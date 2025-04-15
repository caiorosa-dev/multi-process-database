package com.caiorosadev.os.server.database.storage;

import com.caiorosadev.os.shared.model.Record;

import java.util.List;

public interface IDatabaseStorage {
    Record insert(String name);
    void update(Record update);
    List<Record> list();
    boolean delete(int id);
}
