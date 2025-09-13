# 📋 **Plano de Testes Funcionais - Sistema de Inventário Distribuído**

## 🎯 **Objetivo**
Validar todas as funcionalidades do sistema de inventário, incluindo operações de estoque, reservas, controle de concorrência e tolerância a falhas.

## 📊 **Cobertura de Funcionalidades**

### **1. Gestão de Estoque**
- ✅ Entrada de mercadorias
- ✅ Saída manual de mercadorias
- ✅ Ajuste de inventário
- ✅ Consulta de estoque por produto/loja
- ✅ Listagem de estoque por loja
- ✅ Produtos com estoque baixo

### **2. Processo de Vendas**
- ✅ Criação de reserva (início da venda)
- ✅ Confirmação de venda
- ✅ Cancelamento de venda
- ✅ Controle de TTL das reservas

### **3. Controle de Qualidade**
- ✅ Validação de dados de entrada
- ✅ Controle de concorrência
- ✅ Tratamento de erros
- ✅ Tolerância a falhas

---

## 🏪 **Massa de Dados para Teste**

### **Lojas:**
- Loja 1: Shopping Center (ID: 1)
- Loja 2: Centro da Cidade (ID: 2)  
- Loja 3: Bairro Norte (ID: 3)
- Loja 4: Outlet (ID: 4)
- Loja 5: Online Store (ID: 5)

### **Produtos:**
- Produto 101: Smartphone Samsung Galaxy (Eletrônicos)
- Produto 102: Notebook Dell Inspiron (Eletrônicos)
- Produto 103: Camiseta Nike (Roupas)
- Produto 104: Tênis Adidas (Calçados)
- Produto 105: Perfume Channel (Perfumaria)
- Produto 106: Livro "Clean Code" (Livros)
- Produto 107: Mouse Logitech (Acessórios)
- Produto 108: Fone JBL (Áudio)
- Produto 109: Relógio Casio (Acessórios)
- Produto 110: Mochila Eastpak (Bags)

### **Clientes:**
- CLI-001: João Silva
- CLI-002: Maria Santos
- CLI-003: Pedro Oliveira
- CLI-004: Ana Costa
- CLI-005: Carlos Ferreira

---

## 🧪 **Cenários de Teste Funcionais**

### **CT001 - Entrada de Mercadorias**

#### **CT001.1 - Entrada Normal**
**Objetivo:** Validar entrada básica de mercadorias
**Passos:**
1. POST /api/v1/inventario/entrada
2. Dados: produtoId=101, lojaId=1, quantidade=50
**Resultado Esperado:** 
- Status 201 
- Estoque criado/atualizado
- Quantidade disponível = 50

#### **CT001.2 - Entrada Incremental**
**Objetivo:** Validar soma de estoque existente
**Pré-condição:** Produto 101 já tem 50 unidades na loja 1
**Passos:**
1. POST /api/v1/inventario/entrada
2. Dados: produtoId=101, lojaId=1, quantidade=30
**Resultado Esperado:**
- Status 201
- Quantidade total = 80 unidades

#### **CT001.3 - Entrada com Dados Inválidos**
**Objetivo:** Validar tratamento de dados inválidos
**Passos:**
1. POST /api/v1/inventario/entrada
2. Dados: produtoId=-1, lojaId=1, quantidade=0
**Resultado Esperado:**
- Status 400
- Mensagem de erro descritiva

---

### **CT002 - Consulta de Estoque**

#### **CT002.1 - Consulta Produto Existente**
**Objetivo:** Consultar estoque de produto cadastrado
**Pré-condição:** Produto 101 com 80 unidades na loja 1
**Passos:**
1. GET /api/v1/inventario/estoque/produto/101?lojaId=1
**Resultado Esperado:**
- Status 200
- Dados do estoque corretos

#### **CT002.2 - Consulta Produto Inexistente**
**Objetivo:** Consultar produto sem estoque
**Passos:**
1. GET /api/v1/inventario/estoque/produto/999?lojaId=1
**Resultado Esperado:**
- Status 200
- Quantidade = 0
- Mensagem informativa

#### **CT002.3 - Listar Estoque por Loja**
**Objetivo:** Listar todos os produtos de uma loja
**Pré-condição:** Loja 1 com múltiplos produtos
**Passos:**
1. GET /api/v1/inventario/estoque/loja/1
**Resultado Esperado:**
- Status 200
- Lista com todos os produtos da loja
- Totalizadores corretos

---

### **CT003 - Processo de Vendas**

#### **CT003.1 - Venda Completa (Fluxo Feliz)**
**Objetivo:** Simular processo completo de venda
**Pré-condição:** Produto 101 com 80 unidades disponíveis
**Passos:**
1. POST /api/v1/inventario/venda (criar reserva)
   - Dados: produtoId=101, lojaId=1, quantidade=5, clienteId="CLI-001"
2. PUT /api/v1/inventario/venda/{reservaId}/confirmar
**Resultado Esperado:**
- Passo 1: Status 201, reservaId gerado, quantidade reservada
- Passo 2: Status 200, venda confirmada, estoque reduzido

#### **CT003.2 - Venda Cancelada**
**Objetivo:** Cancelar venda e liberar reserva
**Pré-condição:** Reserva ativa criada
**Passos:**
1. POST /api/v1/inventario/venda (criar reserva)
2. DELETE /api/v1/inventario/venda/{reservaId}
**Resultado Esperado:**
- Passo 2: Status 200, reserva cancelada, estoque liberado

#### **CT003.3 - Venda com Estoque Insuficiente**
**Objetivo:** Tentar vender mais que disponível
**Pré-condição:** Produto com 10 unidades disponíveis
**Passos:**
1. POST /api/v1/inventario/venda
   - Dados: quantidade=15
**Resultado Esperado:**
- Status 422
- Erro de estoque insuficiente
- Valores disponível vs solicitado

---

### **CT004 - Saída Manual**

#### **CT004.1 - Saída Normal**
**Objetivo:** Registrar saída manual de produtos
**Pré-condição:** Produto com 50 unidades
**Passos:**
1. POST /api/v1/inventario/saida
   - Dados: produtoId=102, lojaId=1, quantidade=5, motivo="Devolução"
**Resultado Esperado:**
- Status 200
- Estoque reduzido para 45 unidades

#### **CT004.2 - Saída com Estoque Insuficiente**
**Objetivo:** Tentar saída maior que disponível
**Passos:**
1. POST /api/v1/inventario/saida
   - Dados: quantidade=100 (maior que disponível)
**Resultado Esperado:**
- Status 422
- Erro de estoque insuficiente

---

### **CT005 - Ajuste de Inventário**

#### **CT005.1 - Ajuste para Maior**
**Objetivo:** Ajustar estoque para quantidade maior
**Pré-condição:** Produto com 30 unidades
**Passos:**
1. POST /api/v1/inventario/ajuste
   - Dados: produtoId=103, lojaId=2, novaQuantidade=50, motivo="Contagem física"
**Resultado Esperado:**
- Status 200
- Quantidade ajustada para 50

#### **CT005.2 - Ajuste para Menor**
**Objetivo:** Ajustar estoque para quantidade menor
**Pré-condição:** Produto com 50 unidades
**Passos:**
1. POST /api/v1/inventario/ajuste
   - Dados: novaQuantidade=25, motivo="Produtos danificados"
**Resultado Esperado:**
- Status 200
- Quantidade ajustada para 25

---

### **CT006 - Produtos com Estoque Baixo**

#### **CT006.1 - Consulta Alertas**
**Objetivo:** Listar produtos com estoque baixo
**Pré-condição:** Produtos com estoque abaixo do mínimo
**Passos:**
1. GET /api/v1/inventario/estoque/baixo
**Resultado Esperado:**
- Status 200
- Lista de produtos com flag isEstoqueBaixo = true
- Totalizador de alertas

---

### **CT007 - Cenários de Concorrência**

#### **CT007.1 - Operações Simultâneas**
**Objetivo:** Testar controle de concorrência
**Pré-condição:** Produto com 10 unidades
**Passos:**
1. Executar simultaneamente:
   - Thread 1: Venda de 8 unidades
   - Thread 2: Venda de 8 unidades
**Resultado Esperado:**
- Uma operação sucede (Status 201)
- Outra falha por estoque insuficiente (Status 422)

#### **CT007.2 - Reservas Múltiplas**
**Objetivo:** Múltiplas reservas simultâneas
**Pré-condição:** Produto com 20 unidades
**Passos:**
1. Criar 5 reservas simultâneas de 5 unidades cada
**Resultado Esperado:**
- Apenas 4 reservas criadas (20/5 = 4)
- 5ª reserva falha por estoque insuficiente

---

### **CT008 - Validações e Tratamento de Erros**

#### **CT008.1 - Dados Inválidos**
**Objetivo:** Validar tratamento de dados inválidos
**Cenários:**
- Quantidade negativa
- IDs inválidos
- Campos obrigatórios vazios
- Tipos de dados incorretos

#### **CT008.2 - Reserva Inexistente**
**Objetivo:** Operações com reserva inexistente
**Passos:**
1. PUT /api/v1/inventario/venda/RESERVA-INEXISTENTE/confirmar
**Resultado Esperado:**
- Status 400
- Erro de reserva não encontrada

---

### **CT009 - Cenários de Volume**

#### **CT009.1 - Múltiplas Lojas**
**Objetivo:** Operações em todas as 5 lojas
**Passos:**
1. Criar estoque em todas as lojas
2. Executar operações distribuídas
**Resultado Esperado:**
- Isolamento por loja mantido
- Performance adequada

#### **CT009.2 - Múltiplos Produtos**
**Objetivo:** Operações com todos os 10 produtos
**Passos:**
1. Criar estoque para todos os produtos
2. Executar mix de operações
**Resultado Esperado:**
- Operações executadas corretamente
- Integridade dos dados mantida

---

## 📈 **Critérios de Aceitação**

### **Performance:**
- ✅ Tempo de resposta < 200ms para consultas
- ✅ Tempo de resposta < 500ms para operações de escrita

### **Confiabilidade:**
- ✅ 100% das operações válidas executadas com sucesso
- ✅ 100% das operações inválidas tratadas adequadamente

### **Consistência:**
- ✅ Nunca permitir estoque negativo
- ✅ Controle de concorrência funcionando
- ✅ Reservas respeitando TTL

### **Usabilidade:**
- ✅ Mensagens de erro claras e descritivas
- ✅ Documentação Swagger funcionando
- ✅ Logs detalhados para auditoria

---

## 🚀 **Execução dos Testes**

Os cenários serão executados usando:
1. **Testes Automatizados:** Via deep_testing_backend_v2
2. **Testes Manuais:** Via Swagger UI
3. **Testes de Carga:** Scripts curl paralelos

**Próximos Passos:**
1. ✅ Criar massa de dados inicial
2. ✅ Executar cenários funcionais
3. ✅ Executar testes de concorrência
4. ✅ Validar métricas de performance
5. ✅ Gerar relatório de resultados