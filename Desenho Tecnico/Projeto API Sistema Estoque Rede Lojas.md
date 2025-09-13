# 🌐 Diagrama Visual das APIs - Sistema de Inventário

## 📊 **Mapa Completo das APIs**

```mermaid
graph TB
    %% Estilo dos nós
    classDef apiGet fill:#e1f5fe,stroke:#01579b,stroke-width:2px
    classDef apiPost fill:#f3e5f5,stroke:#4a148c,stroke-width:2px
    classDef apiPut fill:#fff3e0,stroke:#e65100,stroke-width:2px
    classDef apiDelete fill:#ffebee,stroke:#b71c1c,stroke-width:2px
    classDef database fill:#e8f5e8,stroke:#2e7d32,stroke-width:2px
    classDef service fill:#fff9c4,stroke:#f57f17,stroke-width:2px

    %% Banco de Dados
    H2[(H2 Database<br/>inventorydb)]:::database

    %% Serviços
    EstoqueService[EstoqueServiceJpa<br/>Business Logic]:::service
    ReservaService[ReservaServiceJpa<br/>Business Logic]:::service

    %% APIs de Consulta - GET
    subgraph "📋 APIs de Consulta"
        GET1["GET /estoque/produto/{id}?lojaId={id}<br/>🔍 Produto em Loja Específica"]:::apiGet
        GET2["GET /estoque/produto?produtoId={id}<br/>🏪 Produto em Todas as Lojas"]:::apiGet
        GET3["GET /estoque/loja/{id}<br/>📦 Produtos de Uma Loja"]:::apiGet
        GET4["GET /estoque/loja<br/>🌐 Produtos de Todas as Lojas"]:::apiGet
        GET5["GET /estoque/baixo<br/>⚠️ Produtos com Estoque Baixo"]:::apiGet
    end

    %% APIs de Movimentação - POST
    subgraph "📦 APIs de Movimentação"
        POST1["POST /entrada<br/>📥 Entrada de Mercadoria"]:::apiPost
        POST2["POST /saida<br/>📤 Saída Manual"]:::apiPost
        POST3["POST /ajuste<br/>⚖️ Ajuste de Inventário"]:::apiPost
    end

    %% APIs de Venda - POST/PUT/DELETE
    subgraph "🛒 APIs de Venda"
        POST4["POST /venda<br/>🛍️ Criar Reserva de Venda"]:::apiPost
        PUT1["PUT /venda/{id}/confirmar<br/>✅ Confirmar Venda"]:::apiPut
        DELETE1["DELETE /venda/{id}<br/>❌ Cancelar Venda"]:::apiDelete
    end

    %% Fluxos de Dados
    GET1 --> EstoqueService
    GET2 --> EstoqueService
    GET3 --> EstoqueService
    GET4 --> EstoqueService
    GET5 --> EstoqueService

    POST1 --> EstoqueService
    POST2 --> EstoqueService
    POST3 --> EstoqueService

    POST4 --> EstoqueService
    POST4 --> ReservaService
    PUT1 --> EstoqueService
    PUT1 --> ReservaService
    DELETE1 --> EstoqueService
    DELETE1 --> ReservaService

    EstoqueService --> H2
    ReservaService --> H2
```

## 🔄 **Fluxo de Processos de Negócio**

```mermaid
sequenceDiagram
    participant Cliente
    participant API
    participant EstoqueService
    participant ReservaService
    participant H2DB as H2 Database

    %% Fluxo de Consulta Simples
    Note over Cliente,H2DB: 📋 Consulta de Estoque
    Cliente->>API: GET /estoque/produto?produtoId=101
    API->>EstoqueService: consultarProdutoEmTodasLojas(101)
    EstoqueService->>H2DB: SELECT * FROM estoque_produto WHERE produto_id=101
    H2DB-->>EstoqueService: Lista de estoques por loja
    EstoqueService-->>API: Lista formatada com totalLojas
    API-->>Cliente: JSON com produto em todas as lojas

    %% Fluxo de Venda Completa
    Note over Cliente,H2DB: 🛒 Processo de Venda Completa
    Cliente->>API: POST /venda {produtoId:101,lojaId:1,quantidade:5}
    API->>EstoqueService: processarVenda(101,1,5)
    EstoqueService->>H2DB: Verificar estoque disponível
    H2DB-->>EstoqueService: Estoque suficiente: true
    EstoqueService->>ReservaService: criarReserva()
    ReservaService->>H2DB: INSERT INTO reserva
    EstoqueService->>H2DB: UPDATE estoque_produto SET reservado=reservado+5
    H2DB-->>EstoqueService: Reserva criada com sucesso
    EstoqueService-->>API: reservaId: "RES-12345"
    API-->>Cliente: 201 Created {reservaId}

    %% Confirmação de Venda
    Cliente->>API: PUT /venda/RES-12345/confirmar
    API->>EstoqueService: confirmarVenda("RES-12345")
    EstoqueService->>ReservaService: buscarReserva("RES-12345")
    ReservaService->>H2DB: SELECT * FROM reserva WHERE id='RES-12345'
    H2DB-->>ReservaService: Dados da reserva
    EstoqueService->>H2DB: UPDATE estoque_produto SET quantidade=quantidade-5, reservado=reservado-5
    ReservaService->>H2DB: UPDATE reserva SET status='CONFIRMADA'
    EstoqueService-->>API: Venda confirmada
    API-->>Cliente: 200 OK {mensagem: "Venda confirmada"}
```

## 🏗️ **Arquitetura de Dados**

```mermaid
erDiagram
    ESTOQUE_PRODUTO {
        bigint id PK
        bigint produto_id
        bigint loja_id
        integer quantidade
        integer reservado
        integer estoque_minimo
        timestamp ultima_atualizacao
        bigint versao
    }

    RESERVA {
        varchar id PK
        bigint produto_id
        bigint loja_id
        integer quantidade
        varchar cliente_id
        varchar vendedor_id
        varchar status
        timestamp criada_em
        timestamp expira_em
        varchar observacoes
    }

    ESTOQUE_PRODUTO ||--o{ RESERVA : "produto_id, loja_id"
```

## 📊 **Matriz de Operações por Endpoint**

```mermaid
graph LR
    subgraph "🔍 Consultas (READ)"
        C1[Produto + Loja Específica<br/>GET /produto/101?lojaId=1]
        C2[Produto em Todas Lojas<br/>GET /produto?produtoId=101]
        C3[Loja Específica<br/>GET /loja/1]
        C4[Todas as Lojas<br/>GET /loja]
        C5[Estoque Baixo<br/>GET /baixo]
    end

    subgraph "✏️ Movimentações (WRITE)"
        W1[Entrada<br/>POST /entrada]
        W2[Saída<br/>POST /saida]
        W3[Ajuste<br/>POST /ajuste]
    end

    subgraph "🛒 Vendas (TRANSACTION)"
        T1[Reservar<br/>POST /venda]
        T2[Confirmar<br/>PUT /venda/ID/confirmar]
        T3[Cancelar<br/>DELETE /venda/ID]
    end

    %% Classificação por complexidade
    C1 -.-> Simple[Operação Simples]
    C2 -.-> Medium[Operação Média]
    C3 -.-> Simple
    C4 -.-> Complex[Operação Complexa]
    C5 -.-> Medium

    W1 -.-> Medium
    W2 -.-> Medium
    W3 -.-> Medium

    T1 -.-> Complex
    T2 -.-> Complex
    T3 -.-> Complex
```

## 🚦 **Estados e Transições de Reserva**

```mermaid
stateDiagram-v2
    [*] --> ATIVA : POST /venda<br/>(criar reserva)
    
    ATIVA --> CONFIRMADA : PUT /venda/ID/confirmar<br/>(venda efetivada)
    ATIVA --> CANCELADA : DELETE /venda/ID<br/>(cancelamento manual)
    ATIVA --> EXPIRADA : Timeout (30min)<br/>(job automático)
    
    CONFIRMADA --> [*]
    CANCELADA --> [*]
    EXPIRADA --> [*]
    
    note right of ATIVA
        Produto reservado
        Estoque reduzido
        TTL = 30 minutos
    end note
    
    note right of CONFIRMADA
        Venda finalizada
        Estoque definitivamente reduzido
        Reserva arquivada
    end note
```

## 📈 **Performance e Volumetria**

```mermaid
graph TB
    subgraph "⚡ Performance Esperada"
        P1[Consultas Simples<br/>< 50ms]
        P2[Consultas Complexas<br/>< 200ms]
        P3[Operações de Escrita<br/>< 100ms]
        P4[Transações de Venda<br/>< 500ms]
    end

    subgraph "📊 Capacidade do Sistema"
        V1[5 Lojas Simultâneas]
        V2[10.000 SKUs]
        V3[1.000 Trans/Hora]
        V4[100 Usuários Concorrentes]
    end

    subgraph "🗄️ Dados Iniciais"
        D1[55 Registros de Estoque]
        D2[15 Produtos Cadastrados]
        D3[5 Lojas Configuradas]
        D4[Jobs a cada 5min]
    end
```

## 🛠️ **Exemplo de Uso Completo**

```mermaid
journey
    title Jornada Completa do Sistema de Inventário
    section Preparação
      Iniciar Sistema: 5: Sistema
      Carregar Dados Iniciais: 4: H2DB
      Verificar APIs: 5: Swagger
    section Consultas
      Consultar Produto 101: 5: Usuario
      Ver Todas as Lojas: 4: Usuario
      Verificar Estoque Baixo: 3: Usuario
    section Movimentação
      Entrada de Mercadoria: 5: Fornecedor
      Ajuste de Inventário: 4: Gerente
    section Venda
      Criar Reserva: 5: Cliente
      Confirmar Pagamento: 5: Cliente
      Finalizar Venda: 5: Sistema
    section Manutenção
      Job Limpeza Reservas: 4: Sistema
      Monitorar Performance: 5: Admin
```

---

## 🎯 **Resumo Visual**

Este diagrama Mermaid apresenta uma visão completa do sistema de inventário, incluindo:

- **13 endpoints** organizados por função
- **Fluxos de processo** com sequência de operações  
- **Modelo de dados** com relacionamentos
- **Estados de reserva** e transições
- **Métricas de performance** esperadas
- **Jornada do usuário** completa

O sistema está otimizado para **consistência**, **performance** e **facilidade de uso**, com APIs intuitivas e processos de negócio bem definidos.