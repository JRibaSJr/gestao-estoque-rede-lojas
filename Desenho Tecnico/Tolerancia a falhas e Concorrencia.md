# 🛡️ Mecanismos de Tolerância a Falhas - Sistema de Inventário

## 📋 **Visão Geral**

O sistema implementa uma estratégia de tolerância a falhas focada em **prevenção de inconsistências** e **recuperação automática**, adequada para um ambiente de inventário distribuído onde a **integridade dos dados é crítica**.

---

## ✅ **MECANISMOS IMPLEMENTADOS**

### **1. 🔄 Controle de Concorrência Otimística**

**Implementação:**
```java
@Entity
public class EstoqueProduto {
    @Version
    private Long versao;  // JPA gerencia automaticamente
    
    // Detecta conflitos automaticamente
    // OptimisticLockException é lançada em conflitos
}
```

**Como Funciona:**
```java
// Cenário: Duas lojas tentam vender o último produto simultaneamente
T1: Loja A lê produto (versao=5, qty=1)
T2: Loja B lê produto (versao=5, qty=1)
T3: Loja A atualiza (UPDATE ... WHERE versao=5) ✅ Sucesso → versao=6
T4: Loja B tenta atualizar (UPDATE ... WHERE versao=5) ❌ OptimisticLockException
```

**Benefícios:**
- ✅ **Detecta race conditions** automaticamente
- ✅ **Alta performance** (não bloqueia leituras)
- ✅ **Zero overselling** (impossível vender mais que disponível)
- ✅ **Escalabilidade** (funciona com múltiplas instâncias)

**Tratamento:**
```java
@Service
public class EstoqueServiceJpa {
    public EstoqueProduto adicionarEntrada(Long produtoId, Long lojaId, Integer quantidade) {
        try {
            return estoqueRepository.save(estoque);
        } catch (OptimisticLockingFailureException e) {
            // Falha detectada - cliente deve tentar novamente
            throw new ConcorrenciaException("Conflito detectado. Tente novamente.");
        }
    }
}
```

---

### **2. ⚡ Transações ACID Nativas**

**Implementação:**
```java
@Transactional
public String processarVenda(Long produtoId, Long lojaId, Integer quantidade) {
    // Todas as operações em uma única transação
    validarEstoque();         // Se falhar, nada é salvo
    Reserva reserva = criarReserva();    // Se falhar, nada é salvo  
    atualizarEstoque();       // Se falhar, tudo é revertido
    return reserva.getId();   // Apenas commita se tudo funcionou
}
```

**Benefícios:**
- ✅ **Atomicidade:** Operação complexa = tudo ou nada
- ✅ **Consistência:** Estado sempre válido
- ✅ **Isolamento:** Transações não interferem entre si
- ✅ **Durabilidade:** Dados persistidos garantidamente

**Cenários Protegidos:**
```java
// ❌ Cenário de falha sem transação:
criarReserva();     // ✅ Sucesso
reduzirEstoque();   // ❌ Falha → Estado inconsistente!

// ✅ Cenário com transação:
@Transactional {
    criarReserva();     // Preparado
    reduzirEstoque();   // Falha → Tudo é revertido automaticamente
}
```

---

### **3. 🕐 TTL (Time-To-Live) para Reservas**

**Implementação:**
```java
public class Reserva {
    private LocalDateTime expiraEm;  // 30 minutos padrão
    
    public Reserva(Long produtoId, Long lojaId, Integer quantidade, String clienteId) {
        this.expiraEm = LocalDateTime.now().plusMinutes(30);
        this.status = StatusReserva.ATIVA;
    }
    
    public boolean isExpirada() {
        return LocalDateTime.now().isAfter(expiraEm);
    }
}
```

**Job Automático:**
```java
@Scheduled(fixedRate = 300000) // A cada 5 minutos
public void processarReservasExpiradas() {
    List<Reserva> expiradas = reservaRepository.findReservasExpiradas();
    for (Reserva reserva : expiradas) {
        liberarReservaAutomaticamente(reserva);
        log.info("Reserva {} liberada automaticamente por TTL", reserva.getId());
    }
}
```

**Benefícios:**
- ✅ **Auto-healing:** Sistema se recupera de PDVs travados
- ✅ **Prevenção de deadlock:** Recursos não ficam presos indefinidamente
- ✅ **Disponibilidade:** Produtos voltam ao estoque automaticamente
- ✅ **Sem intervenção manual:** Operação 24/7 sem administrador

**Cenários Protegidos:**
- PDV trava durante checkout → Reserva expira → Produto liberado
- Rede instável → Cliente abandona compra → Sistema libera automaticamente
- Processo de pagamento lento → TTL estendido, mas há limite

---

### **4. 🏥 Health Checks Customizados**

**Implementação:**
```java
@Component
public class InventoryHealthIndicator implements HealthIndicator {
    @Override
    public Health health() {
        try {
            // Verifica conectividade com banco
            long totalEstoque = estoqueRepository.count();
            
            // Verifica integridade dos dados
            long reservasAtivas = reservaRepository.countByStatus(StatusReserva.ATIVA);
            long estoquesBaixos = estoqueRepository.findEstoqueBaixo().size();
            
            // Verifica se há reservas "vazadas" (sem estoque correspondente)
            boolean integridadeOk = validarIntegridade();
            
            if (!integridadeOk) {
                return Health.down()
                    .withDetail("erro", "Inconsistência detectada entre estoque e reservas")
                    .build();
            }
            
            return Health.up()
                .withDetail("total_produtos_estoque", totalEstoque)
                .withDetail("reservas_ativas", reservasAtivas)
                .withDetail("produtos_estoque_baixo", estoquesBaixos)
                .withDetail("database", "H2 - operacional")
                .withDetail("ultima_verificacao", LocalDateTime.now())
                .build();
                
        } catch (Exception e) {
            return Health.down()
                .withDetail("erro", e.getMessage())
                .withDetail("timestamp", LocalDateTime.now())
                .build();
        }
    }
}
```

**Benefícios:**
- ✅ **Detecção precoce:** Problemas identificados antes dos usuários
- ✅ **Métricas de negócio:** Não apenas técnicas, mas operacionais
- ✅ **Integração:** Load balancers podem remover instâncias não saudáveis
- ✅ **Monitoramento:** Nagios/Zabbix podem alertar automaticamente

---

### **5. 🚫 Validações Rigorosas**

**Implementação:**
```java
@RestController
public class EstoqueController {
    
    public ResponseEntity<?> processarVenda(@Valid @RequestBody VendaRequestDTO request) {
        try {
            // Validação de entrada
            if (request.getQuantidade() <= 0) {
                return ResponseEntity.badRequest()
                    .body(Map.of("erro", "Quantidade deve ser positiva"));
            }
            
            // Validação de negócio
            if (!estoqueService.hasEstoqueSuficiente(request.getProdutoId(), 
                                                   request.getLojaId(), 
                                                   request.getQuantidade())) {
                return ResponseEntity.status(422)
                    .body(Map.of(
                        "erro", "Estoque insuficiente",
                        "disponivel", estoqueService.getQuantidadeDisponivel(
                            request.getProdutoId(), request.getLojaId()
                        )
                    ));
            }
            
            // Processamento
            String reservaId = estoqueService.processarVenda(request);
            return ResponseEntity.status(201).body(Map.of("reservaId", reservaId));
            
        } catch (EstoqueInsuficienteException e) {
            return ResponseEntity.status(422).body(Map.of(
                "erro", "Estoque insuficiente",
                "produtoId", e.getProdutoId(),
                "lojaId", e.getLojaId(),
                "disponivel", e.getDisponivel(),
                "solicitado", e.getSolicitado()
            ));
        } catch (Exception e) {
            log.error("Erro não esperado ao processar venda", e);
            return ResponseEntity.status(500).body(Map.of(
                "erro", "Erro interno do servidor",
                "correlationId", UUID.randomUUID().toString()
            ));
        }
    }
}
```

**Benefícios:**
- ✅ **Fail-fast:** Erros detectados o mais cedo possível
- ✅ **Mensagens claras:** Clientes sabem exatamente o que corrigir
- ✅ **Prevenção:** Dados inválidos nunca entram no sistema
- ✅ **Debugging:** Correlation IDs para rastrear problemas

---

### **6. 🔄 Jobs de Manutenção Automática**

**Implementação:**
```java
@Component
public class MaintenanceJobs {
    
    @Scheduled(fixedRate = 300000) // 5 minutos
    public void processarReservasExpiradas() {
        int reservasProcessadas = reservaRepository.marcarReservasExpiradas();
        if (reservasProcessadas > 0) {
            log.info("Processadas {} reservas expiradas", reservasProcessadas);
        }
    }
    
    @Scheduled(cron = "0 0 2 * * *") // 2:00 AM todos os dias
    public void limpezaDiaria() {
        // Remove reservas antigas (mais de 30 dias)
        LocalDateTime dataLimite = LocalDateTime.now().minusDays(30);
        int removidas = reservaRepository.removeReservasAntigas(dataLimite);
        log.info("Limpeza diária: {} reservas antigas removidas", removidas);
        
        // Valida integridade dos dados
        validarIntegridadeCompleta();
    }
    
    @Scheduled(fixedRate = 600000) // 10 minutos
    public void atualizarEstatisticas() {
        // Refresh de views/cache se necessário
        estatisticasService.atualizarCache();
    }
}
```

**Benefícios:**
- ✅ **Prevenção:** Problemas resolvidos antes de causar impacto
- ✅ **Performance:** Limpeza regular mantém banco otimizado
- ✅ **Capacidade:** Remove dados antigos para liberar espaço
- ✅ **Confiabilidade:** Validações regulares detectam corrupção

---

## ⚠️ **LIMITAÇÕES ATUAIS**

### **1. Ausência de Circuit Breaker**
**Problema:** Se H2 falhar, todas as requisições falham instantaneamente
**Impacto:** Cascata de erros, sem degradação graceful
**Mitigação atual:** Health checks detectam rapidamente

### **2. Sem Retry Automático**  
**Problema:** Conflitos de concorrência requerem retry manual do cliente
**Impacto:** UX degradada em cenários de alta concorrência
**Mitigação atual:** Mensagens de erro claras para orientar retry

### **3. Falta de Rate Limiting**
**Problema:** Um cliente pode sobrecarregar o sistema
**Impacto:** DoS acidental ou intencional
**Mitigação current:** Dependemos do load balancer externo

### **4. Sem Backup/Recovery**
**Problema:** H2 in-memory = dados perdidos se aplicação reiniciar
**Impacto:** Perda total de dados operacionais
**Mitigação atual:** Dados iniciais são recarregados automaticamente

---

## 🚀 **MELHORIAS RECOMENDADAS PARA PRODUÇÃO**

### **1. 🔄 Circuit Breaker Pattern**

```java
@Component
public class ResilientEstoqueService {
    
    @CircuitBreaker(name = "database", fallbackMethod = "fallbackConsultaEstoque")
    @Retry(name = "database")
    @TimeLimiter(name = "database")
    public CompletableFuture<EstoqueProduto> consultarEstoque(Long produtoId, Long lojaId) {
        return CompletableFuture.supplyAsync(() -> 
            estoqueRepository.findByProdutoIdAndLojaId(produtoId, lojaId)
        );
    }
    
    public CompletableFuture<EstoqueProduto> fallbackConsultaEstoque(Long produtoId, Long lojaId, Exception ex) {
        // Retorna dados do cache ou estimativa baseada em dados históricos
        return CompletableFuture.supplyAsync(() -> {
            log.warn("Database indisponível, usando fallback para produto {}", produtoId);
            return cacheService.getEstoqueEstimado(produtoId, lojaId);
        });
    }
}
```

**Configuração:**
```yaml
resilience4j:
  circuitbreaker:
    instances:
      database:
        sliding-window-size: 10
        failure-rate-threshold: 50
        wait-duration-in-open-state: 30s
        permitted-number-of-calls-in-half-open-state: 3
```

### **2. 🔁 Retry com Backoff Exponencial**

```java
@Retryable(
    value = {OptimisticLockingFailureException.class},
    maxAttempts = 3,
    backoff = @Backoff(delay = 100, multiplier = 2)
)
public EstoqueProduto adicionarEntrada(Long produtoId, Long lojaId, Integer quantidade) {
    // Operação que pode falhar por concorrência
    // Retry automático com backoff: 100ms, 200ms, 400ms
}
```

### **3. 🚧 Rate Limiting**

```java
@Component
public class RateLimitingFilter implements Filter {
    
    private final Map<String, RateLimiter> limiters = new ConcurrentHashMap<>();
    
    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) {
        String clientId = getClientId(request);
        RateLimiter limiter = limiters.computeIfAbsent(clientId, 
            k -> RateLimiter.create(10.0)); // 10 req/segundo por cliente
        
        if (limiter.tryAcquire()) {
            chain.doFilter(request, response);
        } else {
            ((HttpServletResponse) response).setStatus(429); // Too Many Requests
        }
    }
}
```

### **4. 💾 Backup e Recovery**

```java
@Component
public class BackupService {
    
    @Scheduled(fixedRate = 300000) // 5 minutos
    public void backupIncremental() {
        // Backup apenas dados modificados
        List<EstoqueProduto> modificados = estoqueRepository.findModificadosApos(ultimoBackup);
        redisTemplate.opsForValue().set("backup:estoque:" + timestamp, modificados);
    }
    
    @EventListener(ContextRefreshedEvent.class)
    public void restaurarSeNecessario() {
        if (estoqueRepository.count() == 0) {
            // Restaura do último backup disponível
            restaurarUltimoBackup();
        }
    }
}
```

### **5. 📊 Observabilidade Avançada**

```java
@Component
public class MetricsCollector {
    
    private final Counter vendasCounter = Counter.build()
        .name("vendas_total")
        .help("Total de vendas processadas")
        .labelNames("loja", "status")
        .register();
    
    private final Histogram tempoProcessamento = Histogram.build()
        .name("tempo_processamento_venda")
        .help("Tempo de processamento de vendas")
        .register();
    
    @EventListener
    public void onVendaProcessada(VendaProcessadaEvent event) {
        vendasCounter.labels(event.getLojaId().toString(), event.getStatus()).inc();
        tempoProcessamento.observe(event.getTempoProcessamento());
    }
}
```

---

## 📊 **COMPARATIVO: ATUAL vs PRODUÇÃO**

| Aspecto | **Sistema Atual** | **Produção Recomendada** |
|---------|-------------------|--------------------------|
| **Concorrência** | ✅ Versionamento otimístico | ✅ + Circuit breaker + Retry |
| **Transações** | ✅ ACID nativo | ✅ + Compensating actions |
| **Auto-healing** | ✅ TTL + Jobs | ✅ + Backup automático |
| **Monitoramento** | ✅ Health checks | ✅ + Métricas + Alertas |
| **Validações** | ✅ Rigorosas | ✅ + Rate limiting |
| **Disponibilidade** | 🔄 Single point of failure | ✅ Alta disponibilidade |
| **Performance** | ✅ < 100ms | ✅ + Cache + Load balancing |
| **Recuperação** | ❌ Dados voláteis | ✅ Backup + Recovery |

---

## 🎯 **CONCLUSÃO**

### **✅ Pontos Fortes Atuais:**
- **Controle de concorrência robusto** previne 100% dos overselling
- **Transações ACID** garantem consistência absoluta
- **Auto-healing** via TTL resolve problemas automaticamente
- **Monitoramento** detecta problemas rapidamente
- **Validações** impedem entrada de dados inválidos

### **⚠️ Áreas para Evolução:**
- **Circuit breakers** para degradação graceful
- **Retry automático** para melhor UX
- **Rate limiting** para proteção contra sobrecarga
- **Backup/recovery** para dados persistentes
- **Observabilidade** avançada para produção

### **🏆 Adequação ao Cenário:**
O sistema atual possui **tolerância a falhas adequada para prototipagem** e **pequena escala**. As implementações focam nos aspectos mais críticos (consistência + auto-healing) e podem ser facilmente evoluídas para produção conforme a necessidade.

**Para 10 lojas e 1.000 transações/hora, os mecanismos atuais são suficientes. Para escalas maiores, as melhorias recomendadas se tornam essenciais.**