package com.caiorosadev.os.server.database.entities;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data @AllArgsConstructor
public class Record {
    private int id;
    private String name;

    public Record(String name) {
        this.name = name;
    }
}
