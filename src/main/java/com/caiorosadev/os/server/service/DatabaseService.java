package com.caiorosadev.os.server.service;

import com.caiorosadev.os.server.database.AsyncDatabase;
import com.caiorosadev.os.shared.model.Record;
import com.caiorosadev.os.shared.model.DatabaseRequest;
import com.caiorosadev.os.shared.model.DatabaseResponse;

public class DatabaseService {
    private final AsyncDatabase database;
    
    public DatabaseService(AsyncDatabase database) {
        this.database = database;
    }
    
    public DatabaseResponse processRequest(DatabaseRequest request) {
        try {
            switch (request.getOperation()) {
                case INSERT:
                    return handleInsert(request);
                case SELECT:
                    return handleSelect(request);
                case UPDATE:
                    return handleUpdate(request);
                case DELETE:
                    return handleDelete(request);
                default:
                    return DatabaseResponse.error("Operação não suportada");
            }
        } catch (Exception e) {
            return DatabaseResponse.error("Erro ao processar requisição: " + e.getMessage());
        }
    }
    
    private DatabaseResponse handleInsert(DatabaseRequest request) {
        Record inserted = database.insert(request.getName());

        return DatabaseResponse.success(inserted);
    }
    
    private DatabaseResponse handleSelect(DatabaseRequest request) {
        if (request.getId() > 0) {
            // Busca por ID
            Record found = database.findById(request.getId());
            if (found != null) {
                return DatabaseResponse.success(found);
            } else {
                return DatabaseResponse.error("Registro não encontrado");
            }
        } else {
            // Lista todos
            return DatabaseResponse.success(database.list());
        }
    }
    
    private DatabaseResponse handleUpdate(DatabaseRequest request) {
        Record existing = database.findById(request.getId());
        if (existing != null) {
            existing.setName(request.getName());
            database.update(existing);
            return DatabaseResponse.success(existing);
        } else {
            return DatabaseResponse.error("Registro não encontrado para atualização");
        }
    }
    
    private DatabaseResponse handleDelete(DatabaseRequest request) {
        boolean deleted = database.delete(request.getId());
        return DatabaseResponse.successDelete(deleted);
    }
} 