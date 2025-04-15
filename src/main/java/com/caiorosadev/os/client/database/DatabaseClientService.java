package com.caiorosadev.os.client.database;

import com.caiorosadev.os.client.ipc.IPCClient;
import com.caiorosadev.os.shared.enums.RequestOperationType;
import com.caiorosadev.os.shared.enums.ResponseStatus;
import com.caiorosadev.os.shared.model.DatabaseRequest;
import com.caiorosadev.os.shared.model.DatabaseResponse;
import com.caiorosadev.os.shared.model.Record;

import java.io.IOException;
import java.util.List;

/**
 * Serviço para operações no banco de dados pelo cliente.
 */
public class DatabaseClientService {
    private final IPCClient ipcClient;
    
    public DatabaseClientService() {
        this.ipcClient = new IPCClient();
    }
    
    /**
     * Insere um novo registro no banco de dados.
     *
     * @param name Nome do registro a ser inserido
     * @return O registro inserido ou null se ocorrer erro
     * @throws IOException Se ocorrer erro de comunicação
     * @throws ClassNotFoundException Se ocorrer erro de desserialização
     */
    public Record insertRecord(String name) throws IOException, ClassNotFoundException {
        DatabaseRequest request = DatabaseRequest.insert(name);
        DatabaseResponse response = ipcClient.sendRequest(request);
        
        if (response.getStatus() == ResponseStatus.SUCCESS) {
            return response.getRecord();
        } else {
            System.out.println("Erro: " + response.getMessage());
            return null;
        }
    }
    
    /**
     * Busca um registro por ID.
     *
     * @param id ID do registro a ser buscado
     * @return O registro encontrado ou null se não existir ou ocorrer erro
     * @throws IOException Se ocorrer erro de comunicação
     * @throws ClassNotFoundException Se ocorrer erro de desserialização
     */
    public Record findRecordById(int id) throws IOException, ClassNotFoundException {
        DatabaseRequest request = DatabaseRequest.findOrDelete(RequestOperationType.SELECT, id);
        DatabaseResponse response = ipcClient.sendRequest(request);
        
        if (response.getStatus() == ResponseStatus.SUCCESS) {
            return response.getRecord();
        } else {
            System.out.println("Erro: " + response.getMessage());
            return null;
        }
    }
    
    /**
     * Lista todos os registros do banco de dados.
     *
     * @return Lista de registros ou null se ocorrer erro
     * @throws IOException Se ocorrer erro de comunicação
     * @throws ClassNotFoundException Se ocorrer erro de desserialização
     */
    public List<Record> listAllRecords() throws IOException, ClassNotFoundException {
        DatabaseRequest request = DatabaseRequest.findOrDelete(RequestOperationType.SELECT, 0);
        DatabaseResponse response = ipcClient.sendRequest(request);
        
        if (response.getStatus() == ResponseStatus.SUCCESS) {
            return response.getRecords();
        } else {
            System.out.println("Erro: " + response.getMessage());
            return null;
        }
    }
    
    /**
     * Atualiza um registro existente.
     *
     * @param id ID do registro a ser atualizado
     * @param name Novo nome do registro
     * @return O registro atualizado ou null se não existir ou ocorrer erro
     * @throws IOException Se ocorrer erro de comunicação
     * @throws ClassNotFoundException Se ocorrer erro de desserialização
     */
    public Record updateRecord(int id, String name) throws IOException, ClassNotFoundException {
        DatabaseRequest request = DatabaseRequest.update(id, name);
        DatabaseResponse response = ipcClient.sendRequest(request);
        
        if (response.getStatus() == ResponseStatus.SUCCESS) {
            return response.getRecord();
        } else {
            System.out.println("Erro: " + response.getMessage());
            return null;
        }
    }
    
    /**
     * Remove um registro pelo ID.
     *
     * @param id ID do registro a ser removido
     * @return true se o registro foi removido, false caso contrário
     * @throws IOException Se ocorrer erro de comunicação
     * @throws ClassNotFoundException Se ocorrer erro de desserialização
     */
    public boolean deleteRecord(int id) throws IOException, ClassNotFoundException {
        DatabaseRequest request = DatabaseRequest.findOrDelete(RequestOperationType.DELETE, id);
        DatabaseResponse response = ipcClient.sendRequest(request);
        
        if (response.getStatus() == ResponseStatus.SUCCESS) {
            System.out.println(response.getMessage());
            return response.isOperationSuccess();
        } else {
            System.out.println("Erro: " + response.getMessage());
            return false;
        }
    }
} 