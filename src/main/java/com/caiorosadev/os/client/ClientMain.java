package com.caiorosadev.os.client;

import java.io.FileWriter;
import java.io.IOException;
import java.util.Scanner;

public class ClientMain {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        System.out.println("=== CLIENTE DO BANCO DE DADOS ===");

        while (true) {
            System.out.print("\n Digite uma query SQL, ou 'exit' para sair: ");
            String query = scanner.nextLine();

            if (query.equalsIgnoreCase("exit")) {
                System.out.println("Encerrando cliente... ");
                break;
            }

            if (!validarQuery(query)) {
                System.out.println("Query inválida. Use INSERT, SELECT, DELETE ou UPDATE. ");
                continue;
            }

            enviarQueryParaServidor(query);
        }
        scanner.close();
    }

    private static boolean validarQuery(String query) {
        query = query.trim().toUpperCase();

        if (query.startsWith("INSERT") || query.startsWith("SELECT") ||
            query.startsWith("DELETE") || query.startsWith("UPDATE")) {
            return true;
        }

        return false;
    }

    private static void enviarQueryParaServidor(String query) {
        try (FileWriter writer = new FileWriter("pipe.txt", false);
        FileWriter logWriter = new FileWriter("client_log.txt", true)) {
            writer.write("[QUERY_INICIO]\n");
            writer.write(query + "\n");
            writer.write("[QUERY_FIM]\n");

            logWriter.write("[QUERY_INICIO]\n");
            logWriter.write(query + "\n");
            logWriter.write("[QUERY_FIM]\n");

            System.out.println("Query enviada com sucesso!");
        } catch (IOException e) {
            System.err.println("Erro ao enviar a query: " + e.getMessage());
        }
    }
}
