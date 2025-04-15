package com.caiorosadev.os.shared.model;

import com.caiorosadev.os.shared.enums.ResponseStatus;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class DatabaseResponse implements Serializable {
    private static final long serialVersionUID = 1L;
    
    private ResponseStatus status;
    private String message;
    private Record record;
    private List<Record> records;
    private boolean operationSuccess;
    
    public static DatabaseResponse success(String message) {
        DatabaseResponse response = new DatabaseResponse();
        response.status = ResponseStatus.SUCCESS;
        response.message = message;
        return response;
    }
    
    public static DatabaseResponse success(Record record) {
        DatabaseResponse response = new DatabaseResponse();
        response.status = ResponseStatus.SUCCESS;
        response.record = record;
        return response;
    }
    
    public static DatabaseResponse success(List<Record> records) {
        DatabaseResponse response = new DatabaseResponse();
        response.status = ResponseStatus.SUCCESS;
        response.records = records;
        return response;
    }
    
    public static DatabaseResponse successDelete(boolean deleted) {
        DatabaseResponse response = new DatabaseResponse();
        response.status = ResponseStatus.SUCCESS;
        response.operationSuccess = deleted;
        response.message = deleted ? "Registro removido com sucesso" : "Registro não encontrado";
        return response;
    }
    
    public static DatabaseResponse error(String message) {
        DatabaseResponse response = new DatabaseResponse();
        response.status = ResponseStatus.ERROR;
        response.message = message;
        return response;
    }
} 