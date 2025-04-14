package com.caiorosadev.os.server.database.mapper.impl;

import com.caiorosadev.os.server.database.entities.Record;
import com.caiorosadev.os.server.database.mapper.IMapper;
import com.google.gson.JsonObject;

public class RecordMapper implements IMapper<Record, JsonObject> {
    @Override
    public Record toDomain(JsonObject persistenceValue) {
        int id = persistenceValue.get("id").getAsInt();
        String name = persistenceValue.get("name").getAsString();

        return new Record(id, name);
    }

    @Override
    public JsonObject toPersistence(Record domainValue) {
        JsonObject object = new JsonObject();

        object.addProperty("id", domainValue.getId());
        object.addProperty("name", domainValue.getName());

        return object;
    }
}
