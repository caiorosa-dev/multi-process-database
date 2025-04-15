 # Multi-Process Database System - Documentação Completa

## 1. Introdução

Este projeto implementa um sistema de banco de dados leve, multi-processo e multi-thread, com comunicação entre processos (IPC) via pipes nomeados. O objetivo é simular um SGBD (Sistema de Gerenciamento de Banco de Dados) concorrente, praticando conceitos de sistemas operacionais como IPC, sincronização, exclusão mútua, e processamento paralelo.

O sistema permite múltiplos clientes acessarem e modificarem uma base de dados compartilhada, garantindo integridade e consistência dos dados mesmo sob acesso concorrente.

---

## 2. Arquitetura Geral

O sistema é composto por três grandes módulos:

- **Servidor**: Responsável por receber requisições, processá-las em paralelo (pool de threads), garantir acesso seguro ao banco de dados e responder aos clientes.
- **Cliente**: Interface de linha de comando para o usuário, permitindo operações CRUD e consultas SQL-like.
- **Camada Compartilhada**: Define modelos de dados, enums e constantes utilizadas por cliente e servidor.

A comunicação entre cliente e servidor é feita via **pipes nomeados** (arquivos especiais no sistema operacional), utilizando serialização de objetos Java.

### Diagrama de Componentes

```
+----------------+         Pipes         +----------------+
|    Cliente     | <------------------> |    Servidor    |
+----------------+                     +----------------+
        |                                      |
        | 1. Serializa requisição               |
        | 2. Escreve no REQUEST_PIPE           |
        | 3. Lê resposta do RESPONSE_PIPE      |
        |                                      |
        |                        +-----------------------------+
        |                        | Pool de Threads (Servidor)  |
        |                        +-----------------------------+
        |                                      |
        |                        +-----------------------------+
        |                        | Banco de Dados (Thread-safe)|
        |                        +-----------------------------+
```

---

## 3. Estrutura de Diretórios

```
main/
└── java/
    └── com/
        └── caiorosadev/
            └── os/
                ├── client/
                │   ├── database/       # Lógica de operações e parser SQL-like
                │   ├── ipc/            # Comunicação IPC (pipes)
                │   ├── ui/             # Menu e interação com usuário
                │   └── ClientMain.java # Entrada do cliente
                │
                ├── server/
                │   ├── database/       # AsyncDatabase, entidades, storage
                │   ├── ipc/            # IPCServer (pipes)
                │   ├── service/        # DatabaseService (processamento)
                │   ├── worker/         # RequestProcessor (threads)
                │   └── ServerMain.java # Entrada do servidor
                │
                ├── shared/
                │   ├── enums/          # Enums de operações e status
                │   ├── model/          # Record, DatabaseRequest, DatabaseResponse
                │   └── PipeConstants.java
                │
                └── Main.java           # Menu inicial
```

---

## 4. Operações Suportadas (CRUD)

### 4.1. INSERT
- **Descrição**: Adiciona um novo registro ao banco.
- **Fluxo**:
    1. Cliente solicita nome do registro.
    2. Cria `DatabaseRequest` com operação INSERT.
    3. Envia via pipe para o servidor.
    4. Servidor insere registro (gera novo ID), salva no arquivo JSON e responde com o registro criado.
- **Exemplo de uso**:
    ```
    > 1. Inserir registro
    Digite o nome para o novo registro: Alice
    Registro inserido com sucesso: ID=1, Nome=Alice
    ```

### 4.2. SELECT
- **Descrição**: Busca registros por ID ou lista todos.
- **Fluxo**:
    1. Cliente pode buscar por ID ou listar todos.
    2. Cria `DatabaseRequest` com operação SELECT e ID (ou 0 para todos).
    3. Servidor busca no arquivo JSON e responde com o(s) registro(s).
- **Exemplo de uso**:
    ```
    > 2. Buscar registro por ID
    Digite o ID do registro: 1
    Registro encontrado: ID=1, Nome=Alice
    ```
    ```
    > 3. Listar todos os registros
    Registros encontrados: 2
    ID=1, Nome=Alice
    ID=2, Nome=Bob
    ```

### 4.3. UPDATE
- **Descrição**: Atualiza o nome de um registro existente.
- **Fluxo**:
    1. Cliente informa ID e novo nome.
    2. Cria `DatabaseRequest` com operação UPDATE.
    3. Servidor localiza o registro, atualiza e responde com o registro atualizado.
- **Exemplo de uso**:
    ```
    > 4. Atualizar registro
    Digite o ID do registro a atualizar: 1
    Digite o novo nome: Carol
    Registro atualizado com sucesso: ID=1, Nome=Carol
    ```

### 4.4. DELETE
- **Descrição**: Remove um registro pelo ID.
- **Fluxo**:
    1. Cliente informa o ID.
    2. Cria `DatabaseRequest` com operação DELETE.
    3. Servidor remove o registro e responde com sucesso/erro.
- **Exemplo de uso**:
    ```
    > 5. Remover registro
    Digite o ID do registro a remover: 2
    Registro removido com sucesso, se existia.
    ```

### 4.5. RAW SQL-like Query
- **Descrição**: Permite ao usuário executar comandos SQL-like (SELECT, INSERT, UPDATE, DELETE) via parser.
- **Exemplo**:
    ```
    > 6. Executar consulta RAW
    SELECT * FROM records WHERE id = 1;
    Registro encontrado: ID=1, Nome=Carol
    ```
    ```
    INSERT INTO records (name) VALUES ('Daniel');
    Registro inserido com sucesso: ID=3, Nome=Daniel
    ```

---

## 5. Comunicação entre Processos (IPC)

### 5.1. Pipes Nomeados
- Utiliza arquivos temporários como canais de comunicação.
- Cliente escreve requisições serializadas em `REQUEST_PIPE`.
- Servidor lê, processa e responde via `RESPONSE_PIPE`.
- Cada mensagem contém:
    - Tamanho dos dados (int)
    - Dados serializados (byte[])

### 5.2. Serialização
- Objetos de requisição e resposta são serializados com `ObjectOutputStream`.
- Permite transmitir objetos complexos entre processos Java.

### 5.3. Tentativa Inicial com Sockets
- **Motivação**: Sockets são o método tradicional de IPC em Java, mas o requisito do projeto era usar pipes ou memória compartilhada.
- **Implementação**: Inicialmente, o servidor implementava um `ServerSocket` e o cliente conectava via `Socket`, trocando objetos serializados.
- **Limitação**: Para atender ao requisito acadêmico, a implementação foi migrada para pipes nomeados, mantendo a mesma lógica de serialização e processamento.
- **Vantagem do Pipe**: Simula melhor o ambiente de IPC local, sem dependência de rede, e permite fácil visualização dos arquivos de comunicação.

---

## 6. Controle de Concorrência

### 6.1. Pool de Threads
- O servidor utiliza um pool fixo de threads para processar múltiplas requisições simultaneamente.
- Cada requisição é delegada a um `RequestProcessor` (implementa `Callable`).

### 6.2. Exclusão Mútua
- O acesso ao banco de dados (arquivo JSON) é protegido por um semáforo (`Semaphore`), garantindo que apenas uma thread escreva/leia por vez.
- Previne condições de corrida e corrupção de dados.

---

## 7. Armazenamento Persistente

- Os registros são armazenados em um arquivo JSON localizado em uma pasta do usuário (`APPDATA` ou `user.home`).
- O arquivo é lido e escrito de forma sincronizada.
- Cada registro possui um ID único e um nome.

---

## 8. Modelos de Dados

### 8.1. Record
```java
public class Record implements Serializable {
    private int id;
    private String name;
    // ...
}
```

### 8.2. DatabaseRequest
```java
public class DatabaseRequest implements Serializable {
    private RequestOperationType operation;
    private int id;
    private String name;
    // ...
}
```

### 8.3. DatabaseResponse
```java
public class DatabaseResponse implements Serializable {
    private ResponseStatus status;
    private String message;
    private Record record;
    private List<Record> records;
    private boolean operationSuccess;
    // ...
}
```

---

## 9. Exemplo de Fluxo Completo

1. Usuário executa o cliente e escolhe "Inserir registro".
2. Cliente cria um `DatabaseRequest` com operação INSERT e nome.
3. Cliente serializa e escreve no `REQUEST_PIPE`.
4. Servidor lê, processa, insere no JSON, responde com `DatabaseResponse`.
5. Cliente lê resposta do `RESPONSE_PIPE` e exibe resultado.

---

## 10. Extensões e Possíveis Melhorias

- Suporte a múltiplos clientes simultâneos (pipes por cliente ou multiplexação)
- Implementação de autenticação e controle de acesso
- Suporte a outros tipos de dados e tabelas
- Interface gráfica (GUI)
- Logs de operações e auditoria

---

## 11. Conclusão e Aprendizados

Este projeto demonstra, de forma prática, conceitos fundamentais de sistemas operacionais e programação concorrente:
- IPC real via pipes nomeados
- Serialização de objetos para comunicação
- Exclusão mútua e sincronização com semáforos
- Processamento paralelo com pool de threads
- Separação clara de responsabilidades (camadas)

A arquitetura modular facilita a manutenção, testes e futuras expansões, servindo como base para estudos e experimentos acadêmicos em SO, IPC e programação concorrente.
