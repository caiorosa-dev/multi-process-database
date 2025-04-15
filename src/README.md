# Multi-Process Database System

Este é um sistema de banco de dados multi-processo que utiliza IPC (Comunicação entre Processos) para permitir que múltiplos clientes se comuniquem com um servidor compartilhado. O servidor usa threads e mecanismos de sincronização para garantir acesso seguro aos dados.

## Visão Geral da Arquitetura

O sistema está dividido em três componentes principais:

1. **Servidor**: Gerencia o armazenamento de dados e processa requisições concorrentes usando um pool de threads.
2. **Cliente**: Interface para os usuários, permitindo operações CRUD (Create, Read, Update, Delete) no banco de dados.
3. **Pipes IPC**: Mecanismo de comunicação entre processos através de pipe.

## Tecnologias Utilizadas

- **Java**: Linguagem de programação principal
- **Threads**: Para processamento paralelo de requisições
- **Semaphores**: Para sincronização de acesso aos dados
- **Named Pipes**: Para comunicação entre processos (IPC)
- **JSON**: Para armazenamento de dados persistente

## Estrutura do Projeto

```
main/
└── java/
    └── com/
        └── caiorosadev/
            └── os/
                ├── client/             # Cliente
                │   ├── database/       # Operações e parsers do cliente
                │   ├── ipc/            # Comunicação IPC do lado do cliente
                │   ├── ui/             # Interface de usuário
                │   └── ClientMain.java # Ponto de entrada do cliente
                │
                ├── server/             # Servidor
                │   ├── database/       # Operações de banco de dados com sincronização
                │   ├── ipc/            # Comunicação IPC do lado do servidor
                │   ├── service/        # Serviços para processar requisições
                │   ├── worker/         # Workers para o pool de threads
                │   └── ServerMain.java # Ponto de entrada do servidor
                │
                ├── shared/             # Componentes compartilhados
                │   ├── enums/          # Enumerações compartilhadas
                │   ├── model/          # Modelos de dados e DTOs
                │   └── PipeConstants.java # Constantes de caminhos de pipes
                │
                └── Main.java           # Ponto de entrada da aplicação
```

## Principais Funcionalidades

### Operações CRUD

- **INSERT**: Adiciona novos registros ao banco de dados
- **SELECT**: Busca registros por ID ou lista todos
- **UPDATE**: Atualiza informações de registros existentes
- **DELETE**: Remove registros do banco de dados

### Mecanismos de Controle de Concorrência

- **Semáforos**: Garante exclusão mútua para acesso à base de dados
- **Sincronização**: Previne condições de corrida durante operações

### Comunicação entre Processos

- **Named Pipes**: Utiliza arquivos como canais para transferência de dados entre processos
- **Serialização**: Converte objetos Java em sequências de bytes para transferência

### Interface do Usuário

- **Menu Interativo**: Interface de linha de comando para operações no banco
- **Parser SQL**: Suporte para consultas SQL-like, permitindo operações via comandos SQL

## Como Executar

1. Compile o projeto e execute a classe `Main`
2. Selecione uma opção:
   - **Iniciar Servidor**: Inicia o processo servidor que aguarda requisições
   - **Iniciar Cliente**: Inicia um processo cliente para enviar operações ao servidor

## Fluxo de Comunicação

```
[Cliente]                       [Servidor]
    |                               |
    |-- Solicita operação --------->|
    |                               |-- Recebe solicitação
    |                               |-- Aloca thread do pool
    |                               |-- Adquire semáforo 
    |                               |-- Executa operação no banco
    |                               |-- Libera semáforo
    |<-- Recebe resposta ---------- |-- Retorna resultado
```

## Implementação de IPC com Pipes

A comunicação entre processos é feita através de pipes nomeados implementados como arquivos:

1. **REQUEST_PIPE**: Canal para envio de requisições do cliente para o servidor
2. **RESPONSE_PIPE**: Canal para envio de respostas do servidor para o cliente

Cada mensagem contém:
- Tamanho dos dados (inteiro)
- Dados serializados do objeto de requisição/resposta

## Detalhamento dos Componentes

### Cliente

- **IPCClient**: Gerencia a comunicação com o servidor via pipes
- **DatabaseClientService**: Provê métodos de alto nível para operações no banco
- **Menu**: Interface com o usuário
- **QueryParser**: Interpreta consultas SQL-like 

### Servidor

- **AsyncDatabase**: Acesso thread-safe ao banco de dados usando semáforos
- **JsonFileDatabaseStorage**: Implementação de armazenamento em arquivo JSON
- **IPCServer**: Processamento de mensagens via pipes nomeados
- **RequestProcessor**: Executa requisições em threads do pool

### Modelos Compartilhados

- **Record**: Entidade principal armazenada no banco
- **DatabaseRequest**: Modelo para requisições
- **DatabaseResponse**: Modelo para respostas

## Conclusão

Este sistema demonstra conceitos importantes de sistemas operacionais e programação concorrente:
- Comunicação entre processos (IPC)
- Sincronização de threads (semáforos)
- Acesso concorrente a recursos compartilhados
- Processamento paralelo com pool de threads

A arquitetura modular permite fácil extensão com novos recursos e tipos de armazenamento. 