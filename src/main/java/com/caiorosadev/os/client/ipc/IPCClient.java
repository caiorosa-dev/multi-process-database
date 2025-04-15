package com.caiorosadev.os.client.ipc;

import com.caiorosadev.os.shared.PipeConstants;
import com.caiorosadev.os.shared.model.DatabaseRequest;
import com.caiorosadev.os.shared.model.DatabaseResponse;

import java.io.*;

/**
 * Cliente de IPC responsável pela comunicação com o servidor via pipes.
 */
public class IPCClient {
    private static final String REQUEST_PIPE = PipeConstants.REQUEST_PIPE;
    private static final String RESPONSE_PIPE = PipeConstants.RESPONSE_PIPE;
    
    /**
     * Envia uma requisição para o servidor e aguarda a resposta.
     *
     * @param request A requisição a ser enviada
     * @return A resposta do servidor
     * @throws IOException Se ocorrer erro de I/O
     * @throws ClassNotFoundException Se ocorrer erro de desserialização
     */
    public DatabaseResponse sendRequest(DatabaseRequest request) throws IOException, ClassNotFoundException {
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
            
            // Verificar se o servidor não cometeu algum erro
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