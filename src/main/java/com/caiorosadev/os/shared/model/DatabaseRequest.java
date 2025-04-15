package com.caiorosadev.os.shared.model;

import com.caiorosadev.os.shared.enums.RequestOperationType;
import lombok.Data;

import java.io.Serializable;

@Data
public class DatabaseRequest implements Serializable {
    private static final long serialVersionUID = 1L;

    private RequestOperationType operation;
    private int id;
    private String name;
    
    // Construtor para SELECT e DELETE (apenas ID)
    public static DatabaseRequest findOrDelete(RequestOperationType operation, int id) {
        if (operation != RequestOperationType.SELECT && operation != RequestOperationType.DELETE) {
            throw new IllegalArgumentException("Este construtor só deve ser usado para SELECT ou DELETE");
        }
        
        DatabaseRequest request = new DatabaseRequest();
        request.operation = operation;
        request.id = id;
        return request;
    }
    
    // Construtor para INSERT (apenas nome)
    public static DatabaseRequest insert(String name) {
        DatabaseRequest request = new DatabaseRequest();
        request.operation = RequestOperationType.INSERT;
        request.name = name;
        return request;
    }
    
    // Construtor para UPDATE (id e nome)
    public static DatabaseRequest update(int id, String name) {
        DatabaseRequest request = new DatabaseRequest();
        request.operation = RequestOperationType.UPDATE;
        request.id = id;
        request.name = name;
        return request;
    }
} 