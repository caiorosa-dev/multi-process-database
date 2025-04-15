package com.caiorosadev.os.server.worker;

import com.caiorosadev.os.shared.model.DatabaseRequest;
import com.caiorosadev.os.shared.model.DatabaseResponse;
import com.caiorosadev.os.server.service.DatabaseService;
import java.util.concurrent.Callable;

public class RequestProcessor implements Callable<DatabaseResponse> {
    private final DatabaseService service;
    private final DatabaseRequest request;

    public RequestProcessor(DatabaseService service, DatabaseRequest request) {
        this.service = service;
        this.request = request;
    }

    @Override
    public DatabaseResponse call() {
        try {
            System.out.println(Thread.currentThread().getName() + " processando " + request.getOperation());
            return service.processRequest(request);
        } catch (Exception e) {
            e.printStackTrace();
            return DatabaseResponse.error("Erro ao processar requisição: " + e.getMessage());
        }
    }
} 