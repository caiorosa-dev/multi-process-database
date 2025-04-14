package com.caiorosadev.os.server.database.storage;

import com.caiorosadev.os.server.database.entities.Record;

import java.util.List;

public interface IDatabaseStorage {
    Record insert(String name);
    void update(Record update);
    List<Record> list();
}
