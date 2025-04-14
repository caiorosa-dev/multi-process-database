package com.caiorosadev.os.server.database;

import com.caiorosadev.os.server.database.entities.Record;
import com.caiorosadev.os.server.database.storage.IDatabaseStorage;
import lombok.AllArgsConstructor;

import java.util.List;
import java.util.concurrent.Semaphore;

public class AsyncDatabase {
    public IDatabaseStorage databaseStorage;
    private final Semaphore semaphore;

    public AsyncDatabase(IDatabaseStorage databaseStorage) {
        this.databaseStorage = databaseStorage;
        this.semaphore = new Semaphore(1);
    }

    /**
     * Insere um novo registro de forma thread-safe.
     *
     * @param name Nome do novo registro.
     * @return O registro inserido.
     */
    public Record insert(String name) {
        try {
            semaphore.acquire();

            return databaseStorage.insert(name);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("Thread interrompida ao tentar inserir", e);
        } finally {
            semaphore.release();
        }
    }

    /**
     * Atualiza um registro existente de forma thread-safe.
     *
     * @param update Registro com informações atualizadas.
     */
    public void update(Record update) {
        try {
            semaphore.acquire();

            databaseStorage.update(update);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("Thread interrompida ao tentar atualizar", e);
        } finally {
            semaphore.release();
        }
    }

    /**
     * Retorna a lista de registros armazenados de forma thread-safe.
     *
     * @return Lista de registros.
     */
    public List<Record> list() {
        try {
            semaphore.acquire();

            return databaseStorage.list();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("Thread interrompida ao tentar listar os registros", e);
        } finally {
            semaphore.release();
        }
    }
}
