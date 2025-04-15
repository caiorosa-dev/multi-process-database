package com.caiorosadev.os.server.ipc;

import com.caiorosadev.os.shared.PipeConstants;
import com.caiorosadev.os.shared.model.DatabaseRequest;
import com.caiorosadev.os.shared.model.DatabaseResponse;
import com.caiorosadev.os.server.service.DatabaseService;
import com.caiorosadev.os.server.worker.RequestProcessor;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.concurrent.*;

public class IPCServer implements Closeable {
    public static final String REQUEST_PIPE = PipeConstants.REQUEST_PIPE;
    public static final String RESPONSE_PIPE = PipeConstants.RESPONSE_PIPE;
    
    private static final int MAX_THREADS = 10;
    
    private final ExecutorService threadPool;
    private final DatabaseService databaseService;
    private volatile boolean running = true;
    private final Path requestPipePath;
    private final Path responsePipePath;
    
    public IPCServer(DatabaseService databaseService) throws IOException {
        this.threadPool = Executors.newFixedThreadPool(MAX_THREADS);
        this.databaseService = databaseService;
        
        requestPipePath = Paths.get(REQUEST_PIPE);
        responsePipePath = Paths.get(RESPONSE_PIPE);
        
        createPipeFiles();
    }
    
    private void createPipeFiles() throws IOException {
        Files.deleteIfExists(requestPipePath);
        Files.deleteIfExists(responsePipePath);
        
        // Criar novos arquivos de pipe
        Files.createFile(requestPipePath);
        Files.createFile(responsePipePath);
        
        System.out.println("Pipes criados em:");
        System.out.println("- Request pipe: " + requestPipePath);
        System.out.println("- Response pipe: " + responsePipePath);
    }
    
    public void start() {
        System.out.println("Servidor iniciado. Escutando requisições no pipe...");
        System.out.println("Pool de threads com " + MAX_THREADS + " threads");
        
        // Iniciar thread para monitorar o pipe de requisições
        new Thread(this::monitorRequestPipe).start();
    }
    
    private void monitorRequestPipe() {
        while (running) {
            try (RandomAccessFile requestPipe = new RandomAccessFile(requestPipePath.toFile(), "rw")) {
                // Aguarda até que haja dados para ler
                while (requestPipe.length() == 0) {
                    if (!running) return;
                    Thread.sleep(100); // Evitar uso excessivo de CPU
                }
                
                // Ler o tamanho dos dados serializados
                int dataSize = requestPipe.readInt();
                if (dataSize <= 0) continue;
                
                // Ler os dados serializados
                byte[] data = new byte[dataSize];
                requestPipe.readFully(data);
                
                // Truncar o arquivo após a leitura
                requestPipe.setLength(0);
                
                // Processar a requisição em uma thread do pool
                processRequest(data);
            } catch (Exception e) {
                if (running) {
                    System.err.println("Erro ao ler do pipe de requisições: " + e.getMessage());
                    e.printStackTrace();
                }
                
                try {
                    Thread.sleep(1000); // Aguardar antes de tentar novamente
                } catch (InterruptedException ie) {
                    Thread.currentThread().interrupt();
                }
            }
        }
    }
    
    private void processRequest(byte[] requestData) {
        CompletableFuture.runAsync(() -> {
            try {
                // Deserializar a requisição
                ByteArrayInputStream bis = new ByteArrayInputStream(requestData);
                ObjectInputStream ois = new ObjectInputStream(bis);
                DatabaseRequest request = (DatabaseRequest) ois.readObject();
                
                System.out.println("Requisição recebida: " + request.getOperation());
                
                // Processar a requisição em uma thread do pool
                Future<DatabaseResponse> future = threadPool.submit(new RequestProcessor(databaseService, request));
                
                // Obter a resposta
                DatabaseResponse response = future.get();
                
                // Serializar e enviar a resposta pelo pipe de respostas
                sendResponse(response);
                
            } catch (Exception e) {
                System.err.println("Erro ao processar requisição: " + e.getMessage());
                e.printStackTrace();
            }
        }, threadPool);
    }
    
    private void sendResponse(DatabaseResponse response) {
        try (RandomAccessFile responsePipe = new RandomAccessFile(responsePipePath.toFile(), "rw")) {
            // Serializar a resposta
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            ObjectOutputStream oos = new ObjectOutputStream(bos);
            oos.writeObject(response);
            oos.flush();
            
            byte[] data = bos.toByteArray();
            
            // Escrever o tamanho e os dados serializados
            responsePipe.setLength(0); // Limpar o arquivo antes de escrever
            responsePipe.writeInt(data.length);
            responsePipe.write(data);
            
            System.out.println("Resposta enviada: " + response.getStatus());
        } catch (IOException e) {
            System.err.println("Erro ao enviar resposta pelo pipe: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    @Override
    public void close() throws IOException {
        running = false;
        
        if (threadPool != null) {
            threadPool.shutdown();
            try {
                if (!threadPool.awaitTermination(5, TimeUnit.SECONDS)) {
                    threadPool.shutdownNow();
                }
            } catch (InterruptedException e) {
                threadPool.shutdownNow();
            }
        }
        
        // Limpar os arquivos de pipe
        Files.deleteIfExists(requestPipePath);
        Files.deleteIfExists(responsePipePath);
        
        System.out.println("Servidor encerrado e pipes removidos");
    }
} 