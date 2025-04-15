package com.caiorosadev.os.client;

import com.caiorosadev.os.client.ui.Menu;
import com.caiorosadev.os.shared.PipeConstants;

/**
 * Classe principal do cliente.
 */
public class ClientMain {
    
    public static void main(String[] args) {
        System.out.println("Cliente de Banco de Dados iniciado.");
        System.out.println("Utilizando pipes em:");
        System.out.println("- Request pipe: " + PipeConstants.REQUEST_PIPE);
        System.out.println("- Response pipe: " + PipeConstants.RESPONSE_PIPE);
        
        // Inicializa e exibe o menu
        Menu menu = new Menu();
        menu.show();
        
        System.out.println("Cliente encerrado.");
    }
}
