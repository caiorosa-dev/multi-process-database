package com.caiorosadev.os.server.database.storage.impl;

import com.caiorosadev.os.server.database.entities.Record;
import com.caiorosadev.os.server.database.storage.IDatabaseStorage;
import com.caiorosadev.os.server.database.mapper.impl.RecordMapper;
import com.google.gson.*;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class JsonFileDatabaseStorage implements IDatabaseStorage {
    private final File dbFile;
    private final RecordMapper recordMapper;
    private final Gson gson;

    public JsonFileDatabaseStorage() {
        String appData = System.getenv("APPDATA");
        if (appData == null || appData.isEmpty()) {
            appData = System.getProperty("user.home");
        }
        File baseDir = new File(appData, "caiorosa-dev/MultiProcessDatabase");
        if (!baseDir.exists()) {
            baseDir.mkdirs();
        }
        dbFile = new File(baseDir, "database.json");

        if (!dbFile.exists()) {
            try (FileWriter writer = new FileWriter(dbFile)) {
                writer.write("[]");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        recordMapper = new RecordMapper();
        gson = new GsonBuilder().setPrettyPrinting().create();
    }

    private synchronized List<Record> readRecords() {
        List<Record> records = new ArrayList<>();

        try (Reader reader = new FileReader(dbFile)) {
            JsonElement element = JsonParser.parseReader(reader);
            if (element != null && element.isJsonArray()) {
                JsonArray array = element.getAsJsonArray();
                for (JsonElement item : array) {
                    if (item.isJsonObject()){
                        records.add(recordMapper.toDomain(item.getAsJsonObject()));
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return records;
    }

    private synchronized void writeRecords(List<Record> records) {
        JsonArray jsonArray = new JsonArray();
        for (Record record : records) {
            jsonArray.add(recordMapper.toPersistence(record));
        }
        try (Writer writer = new FileWriter(dbFile)) {
            gson.toJson(jsonArray, writer);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public synchronized Record insert(String name) {
        List<Record> records = readRecords();

        int newId = records.stream().mapToInt(Record::getId).max().orElse(0) + 1;

        Record newRecord = new Record(newId, name);

        records.add(newRecord);
        writeRecords(records);

        return newRecord;
    }

    @Override
    public synchronized void update(Record update) {
        List<Record> records = readRecords();

        boolean found = false;

        for (int i = 0; i < records.size(); i++) {
            Record record = records.get(i);
            if (record.getId() == update.getId()) {
                records.set(i, update);
                found = true;
                break;
            }
        }

        if (found) {
            writeRecords(records);
        } else {
            throw new RuntimeException("Registro não encontrado para atualização: " + update.getId());
        }
    }

    @Override
    public synchronized List<Record> list() {
        return readRecords();
    }
}
