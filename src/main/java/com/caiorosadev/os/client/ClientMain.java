package com.caiorosadev.os.client;

import com.caiorosadev.os.shared.PipeConstants;
import com.caiorosadev.os.shared.enums.RequestOperationType;
import com.caiorosadev.os.shared.enums.ResponseStatus;
import com.caiorosadev.os.shared.model.Record;
import com.caiorosadev.os.shared.model.DatabaseRequest;
import com.caiorosadev.os.shared.model.DatabaseResponse;

import java.io.*;
import java.util.List;
import java.util.Scanner;

public class ClientMain {
    // Caminho dos pipes nomeados (obtidos da classe de constantes)
    private static final String REQUEST_PIPE = PipeConstants.REQUEST_PIPE;
    private static final String RESPONSE_PIPE = PipeConstants.RESPONSE_PIPE;
    
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        
        System.out.println("Cliente de Banco de Dados iniciado.");
        System.out.println("Utilizando pipes em:");
        System.out.println("- Request pipe: " + REQUEST_PIPE);
        System.out.println("- Response pipe: " + RESPONSE_PIPE);
        
        while (true) {
            System.out.println("\n===== MENU =====");
            System.out.println("1. Inserir registro");
            System.out.println("2. Buscar registro por ID");
            System.out.println("3. Listar todos os registros");
            System.out.println("4. Atualizar registro");
            System.out.println("5. Remover registro");
            System.out.println("0. Sair");
            System.out.print("Escolha uma opção: ");
            
            int option = scanner.nextInt();
            scanner.nextLine(); // Consume newline
            
            if (option == 0) {
                break;
            }
            
            try {
                switch (option) {
                    case 1:
                        insertRecord(scanner);
                        break;
                    case 2:
                        findRecordById(scanner);
                        break;
                    case 3:
                        listAllRecords();
                        break;
                    case 4:
                        updateRecord(scanner);
                        break;
                    case 5:
                        deleteRecord(scanner);
                        break;
                    default:
                        System.out.println("Opção inválida!");
                }
            } catch (Exception e) {
                System.err.println("Erro: " + e.getMessage());
                e.printStackTrace();
            }
        }
        
        System.out.println("Cliente encerrado.");
    }
    
    private static void insertRecord(Scanner scanner) throws IOException, ClassNotFoundException {
        System.out.print("Digite o nome para o novo registro: ");
        String name = scanner.nextLine();
        
        DatabaseRequest request = DatabaseRequest.insert(name);
        DatabaseResponse response = sendRequest(request);
        
        if (response.getStatus() == ResponseStatus.SUCCESS) {
            Record inserted = response.getRecord();
            System.out.println("Registro inserido com sucesso: ID=" + inserted.getId() + ", Nome=" + inserted.getName());
        } else {
            System.out.println("Erro: " + response.getMessage());
        }
    }
    
    private static void findRecordById(Scanner scanner) throws IOException, ClassNotFoundException {
        System.out.print("Digite o ID do registro: ");
        int id = scanner.nextInt();
        scanner.nextLine(); // Consume newline
        
        DatabaseRequest request = DatabaseRequest.findOrDelete(RequestOperationType.SELECT, id);
        DatabaseResponse response = sendRequest(request);
        
        if (response.getStatus() == ResponseStatus.SUCCESS) {
            Record record = response.getRecord();
            System.out.println("Registro encontrado: ID=" + record.getId() + ", Nome=" + record.getName());
        } else {
            System.out.println("Erro: " + response.getMessage());
        }
    }
    
    private static void listAllRecords() throws IOException, ClassNotFoundException {
        DatabaseRequest request = DatabaseRequest.findOrDelete(RequestOperationType.SELECT, 0);
        DatabaseResponse response = sendRequest(request);
        
        if (response.getStatus() == ResponseStatus.SUCCESS) {
            List<Record> records = response.getRecords();
            System.out.println("Registros encontrados: " + records.size());
            
            for (Record record : records) {
                System.out.println("ID=" + record.getId() + ", Nome=" + record.getName());
            }
        } else {
            System.out.println("Erro: " + response.getMessage());
        }
    }
    
    private static void updateRecord(Scanner scanner) throws IOException, ClassNotFoundException {
        System.out.print("Digite o ID do registro a atualizar: ");
        int id = scanner.nextInt();
        scanner.nextLine(); // Consume newline
        
        System.out.print("Digite o novo nome: ");
        String name = scanner.nextLine();
        
        DatabaseRequest request = DatabaseRequest.update(id, name);
        DatabaseResponse response = sendRequest(request);
        
        if (response.getStatus() == ResponseStatus.SUCCESS) {
            Record updated = response.getRecord();
            System.out.println("Registro atualizado com sucesso: ID=" + updated.getId() + ", Nome=" + updated.getName());
        } else {
            System.out.println("Erro: " + response.getMessage());
        }
    }
    
    private static void deleteRecord(Scanner scanner) throws IOException, ClassNotFoundException {
        System.out.print("Digite o ID do registro a remover: ");
        int id = scanner.nextInt();
        scanner.nextLine(); // Consume newline
        
        DatabaseRequest request = DatabaseRequest.findOrDelete(RequestOperationType.DELETE, id);
        DatabaseResponse response = sendRequest(request);
        
        if (response.getStatus() == ResponseStatus.SUCCESS) {
            System.out.println(response.getMessage());
        } else {
            System.out.println("Erro: " + response.getMessage());
        }
    }
    
    private static DatabaseResponse sendRequest(DatabaseRequest request) throws IOException, ClassNotFoundException {
        // Serializar a requisição
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream(bos);
        oos.writeObject(request);
        oos.flush();
        byte[] data = bos.toByteArray();
        
        // Enviar pelo pipe de requisições
        try (RandomAccessFile requestPipe = new RandomAccessFile(REQUEST_PIPE, "rw")) {
            requestPipe.setLength(0);
            requestPipe.writeInt(data.length);
            requestPipe.write(data);
            System.out.println("Requisição enviada: " + request.getOperation());
        }
        
        // Aguardar e ler a resposta do pipe de respostas
        DatabaseResponse response = null;
        try (RandomAccessFile responsePipe = new RandomAccessFile(RESPONSE_PIPE, "rw")) {
            // Aguardar até que haja dados para ler
            long startTime = System.currentTimeMillis();
            while (responsePipe.length() == 0) {
                Thread.sleep(100);
                
                // Timeout após 10 segundos
                if (System.currentTimeMillis() - startTime > 10000) {
                    throw new IOException("Timeout ao aguardar resposta do servidor");
                }
            }
            
            // Ler o tamanho dos dados da resposta
            int responseSize = responsePipe.readInt();
            if (responseSize <= 0) {
                throw new IOException("Resposta inválida do servidor");
            }
            
            // Ler os dados da resposta
            byte[] responseData = new byte[responseSize];
            responsePipe.readFully(responseData);
            
            // Limpar pipe após comunicação acabar, pra evitar problemas
            responsePipe.setLength(0);
            
            // Deserializar a resposta
            ByteArrayInputStream bis = new ByteArrayInputStream(responseData);
            ObjectInputStream ois = new ObjectInputStream(bis);
            response = (DatabaseResponse) ois.readObject();
            
            System.out.println("Resposta recebida: " + response.getStatus());
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new IOException("Interrompido ao aguardar resposta", e);
        }
        
        return response;
    }
}
