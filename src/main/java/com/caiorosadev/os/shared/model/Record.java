package com.caiorosadev.os.shared.model;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.io.Serializable;

@Data @AllArgsConstructor
public class Record implements Serializable {
    private int id;
    private String name;

    public Record(String name) {
        this.name = name;
    }
}
