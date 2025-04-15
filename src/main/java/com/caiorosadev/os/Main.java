package com.caiorosadev.os;

import com.caiorosadev.os.client.ClientMain;
import com.caiorosadev.os.server.ServerMain;

import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        
        System.out.println("===== Sistema de Banco de Dados Multi-Processo =====");
        System.out.println("1. Iniciar Servidor");
        System.out.println("2. Iniciar Cliente");
        System.out.print("Escolha uma opção: ");
        
        int option = scanner.nextInt();
        scanner.nextLine(); // Consume newline
        
        switch (option) {
            case 1:
                System.out.println("Iniciando servidor...");
                ServerMain.main(args);
                break;
            case 2:
                System.out.println("Iniciando cliente...");
                ClientMain.main(args);
                break;
            default:
                System.out.println("Opção inválida!");
        }
    }
}