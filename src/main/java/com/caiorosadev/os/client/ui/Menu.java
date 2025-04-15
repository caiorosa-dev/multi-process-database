package com.caiorosadev.os.client.ui;

import com.caiorosadev.os.client.database.DatabaseClientService;
import com.caiorosadev.os.client.database.QueryParser;
import com.caiorosadev.os.shared.model.Record;

import java.io.IOException;
import java.util.List;
import java.util.Scanner;

/**
 * Menu interativo para interação com o banco de dados.
 */
public class Menu {
    private final Scanner scanner;
    private final DatabaseClientService databaseService;
    private boolean running = true;

    public Menu() {
        this.scanner = new Scanner(System.in);
        this.databaseService = new DatabaseClientService();
    }

    /**
     * Exibe o menu principal e processa as opções do usuário.
     */
    public void show() {
        while (running) {
            clearScreen();
            displayMainMenu();

            try {
                int option = scanner.nextInt();
                scanner.nextLine();
                System.out.println();
                processOption(option);
                System.out.println();
            } catch (Exception e) {
                System.err.println("Erro ao processar opção: " + e.getMessage());
                scanner.nextLine();
            }
        }
    }

    /**
     * Exibe as opções do menu principal com melhor estética.
     */
    private void displayMainMenu() {
        System.out.println("  __  __      _ _   _   ___                         ___  ___ ___  ___ ");
        System.out.println(" |  \\/  |_  _| | |_(_) | _ \\_ _ ___  __ ___ ______ / __|/ __|   \\| _ )");
        System.out.println(" | |\\/| | || | |  _| | |  _/ '_/ _ \\/ _/ -_|_-<_-< \\__ \\ (_ | |) | _ \\");
        System.out.println(" |_|  |_|\\_,_|_|\\__|_| |_| |_| \\___/\\__\\___/__/__/ |___/\\___|___/|___/");
        System.out.println(" Java Edition - v1.0.0");
        System.out.println();
        System.out.println("=================================================");
        System.out.println("1. Inserir registro");
        System.out.println("2. Buscar registro por ID");
        System.out.println("3. Listar todos os registros");
        System.out.println("4. Atualizar registro");
        System.out.println("5. Remover registro");
        System.out.println("6. Executar consulta RAW");
        System.out.println("0. Sair");
        System.out.println("=================================================");
        System.out.print("Escolha uma opção: ");
    }

    private void clearScreen() {
        System.out.println("Aperte ENTER para continuar...");
        scanner.nextLine();
        for (int i = 0; i < 50; i++) {
            System.out.println();
        }
    }

    /**
     * Processa a opção escolhida pelo usuário.
     *
     * @param option Opção escolhida
     */
    private void processOption(int option) {
        try {
            switch (option) {
                case 0:
                    exit();
                    break;
                case 1:
                    showInsertRecord();
                    break;
                case 2:
                    showFindRecord();
                    break;
                case 3:
                    showListRecords();
                    break;
                case 4:
                    showUpdateRecord();
                    break;
                case 5:
                    showDeleteRecord();
                    break;
                case 6:
                    showRawQuery();
                    break;
                default:
                    System.out.println("Opção inválida!");
            }
        } catch (Exception e) {
            System.err.println("Erro: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Encerra o menu.
     */
    private void exit() {
        System.out.println("Saindo do programa...");
        running = false;
    }

    /**
     * Exibe a interface para inserir um registro.
     */
    private void showInsertRecord() throws IOException, ClassNotFoundException {
        System.out.print("Digite o nome para o novo registro: ");
        String name = scanner.nextLine();

        Record inserted = databaseService.insertRecord(name);

        if (inserted != null) {
            System.out.println("Registro inserido com sucesso: ID=" + inserted.getId() + ", Nome=" + inserted.getName());
        }
    }

    /**
     * Exibe a interface para buscar um registro por ID.
     */
    private void showFindRecord() throws IOException, ClassNotFoundException {
        System.out.print("Digite o ID do registro: ");
        int id = scanner.nextInt();
        scanner.nextLine(); // Consome o newline

        Record record = databaseService.findRecordById(id);

        if (record != null) {
            System.out.println("Registro encontrado: ID=" + record.getId() + ", Nome=" + record.getName());
        } else {
            System.out.println("Nenhum registro encontrado para o ID informado.");
        }
    }

    /**
     * Exibe a interface para listar todos os registros.
     */
    private void showListRecords() throws IOException, ClassNotFoundException {
        List<Record> records = databaseService.listAllRecords();

        if (records != null) {
            System.out.println("Registros encontrados: " + records.size());
            for (Record record : records) {
                System.out.println("ID=" + record.getId() + ", Nome=" + record.getName());
            }
        }
    }

    /**
     * Exibe a interface para atualizar um registro.
     */
    private void showUpdateRecord() throws IOException, ClassNotFoundException {
        System.out.print("Digite o ID do registro a atualizar: ");
        int id = scanner.nextInt();
        scanner.nextLine(); // Consome o newline

        System.out.print("Digite o novo nome: ");
        String name = scanner.nextLine();

        Record updated = databaseService.updateRecord(id, name);

        if (updated != null) {
            System.out.println("Registro atualizado com sucesso: ID=" + updated.getId() + ", Nome=" + updated.getName());
        }
    }

    /**
     * Exibe a interface para remover um registro.
     */
    private void showDeleteRecord() throws IOException, ClassNotFoundException {
        System.out.print("Digite o ID do registro a remover: ");
        int id = scanner.nextInt();
        scanner.nextLine(); // Consome o newline

        databaseService.deleteRecord(id);
        System.out.println("Registro removido com sucesso, se existia.");
    }

    /**
     * Exibe a interface para executar uma consulta RAW.
     */
    private void showRawQuery() {
        System.out.println("\nDigite a consulta que deseja executar (suporta comandos SELECT, INSERT, UPDATE, DELETE):");
        String query = scanner.nextLine();

        QueryParser parser = new QueryParser(databaseService);
        parser.executeQuery(query);
    }
}
