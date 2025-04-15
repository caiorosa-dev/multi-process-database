package com.caiorosadev.os;

import com.caiorosadev.os.client.ClientMain;
import com.caiorosadev.os.server.ServerMain;

import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        System.out.println("  __  __      _ _   _   ___                         ___  ___ ___  ___ ");
        System.out.println(" |  \\/  |_  _| | |_(_) | _ \\_ _ ___  __ ___ ______ / __|/ __|   \\| _ )");
        System.out.println(" | |\\/| | || | |  _| | |  _/ '_/ _ \\/ _/ -_|_-<_-< \\__ \\ (_ | |) | _ \\");
        System.out.println(" |_|  |_|\\_,_|_|\\__|_| |_| |_| \\___/\\__\\___/__/__/ |___/\\___|___/|___/");
        System.out.println(" Java Edition - v1.0.0");
        System.out.println();
        System.out.println(" Bem vindo!");
        System.out.println();
        System.out.println("1. Iniciar Servidor");
        System.out.println("2. Iniciar Cliente");
        System.out.println("3. Créditos");
        System.out.println();
        System.out.print("Escolha uma opção: ");

        int option = scanner.nextInt();
        scanner.nextLine();
        
        switch (option) {
            case 1:
                System.out.println("Iniciando servidor...");
                ServerMain.main(args);
                break;
            case 2:
                System.out.println("Iniciando cliente...");
                ClientMain.main(args);
                break;
            case 3:
                System.out.println("");
                Main.main(args);
                break;
            default:
                System.out.println("Opção inválida!");
        }
    }
}