package com.caiorosadev.os.shared;

/**
 * Classe utilitária que define os caminhos dos pipes para comunicação entre processos.
 */
public class PipeConstants {
    /**
     * Caminho do pipe para requisições do cliente para o servidor.
     */
    public static final String REQUEST_PIPE = System.getProperty("java.io.tmpdir") + "/db_request_pipe";
    
    /**
     * Caminho do pipe para respostas do servidor para o cliente.
     */
    public static final String RESPONSE_PIPE = System.getProperty("java.io.tmpdir") + "/db_response_pipe";
} 