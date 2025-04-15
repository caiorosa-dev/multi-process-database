package com.caiorosadev.os.server;

import com.caiorosadev.os.server.database.AsyncDatabase;
import com.caiorosadev.os.server.database.storage.IDatabaseStorage;
import com.caiorosadev.os.server.database.storage.impl.JsonFileDatabaseStorage;
import com.caiorosadev.os.server.ipc.IPCServer;
import com.caiorosadev.os.server.service.DatabaseService;

import java.io.IOException;

public class ServerMain {
    public static void main(String[] args) {
        try {
            IDatabaseStorage storage = new JsonFileDatabaseStorage();
            
            AsyncDatabase database = new AsyncDatabase(storage);
            
            DatabaseService databaseService = new DatabaseService(database);
            
            // Inicializa o servidor IPC que recebe as requisições
            try (IPCServer server = new IPCServer(databaseService)) {
                server.start();
                
                System.out.println("Servidor iniciado com sucesso. Pressione Enter para encerrar...");
                System.in.read(); // Aguarda o usuário pressionar Enter para encerrar
            }
        } catch (IOException e) {
            System.err.println("Erro ao iniciar o servidor: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
