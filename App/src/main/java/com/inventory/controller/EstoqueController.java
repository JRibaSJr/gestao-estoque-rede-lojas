package com.inventory.controller;

import com.inventory.dto.EntradaRequest;
import com.inventory.dto.SaidaRequest;
import com.inventory.dto.VendaRequest;
import com.inventory.dto.AjusteRequest;
import com.inventory.exception.ConcorrenciaException;
import com.inventory.exception.EstoqueInsuficienteException;
import com.inventory.model.EstoqueProduto;
import com.inventory.service.EstoqueServiceJpa;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Controller REST para operações de estoque
 * Implementa as principais APIs definidas no design
 */
@RestController
@RequestMapping("/api/v1/inventario")
@Tag(name = "Estoque", description = "APIs para gerenciamento de estoque")
@CrossOrigin(origins = "*")
public class EstoqueController {
    
    private static final Logger logger = LoggerFactory.getLogger(EstoqueController.class);
    
    private final EstoqueServiceJpa estoqueService;
    
    public EstoqueController(EstoqueServiceJpa estoqueService) {
        this.estoqueService = estoqueService;
    }
    
    /**
     * Consulta estoque por produto e loja (produto opcional)
     */
    @GetMapping("/estoque/produto/{produtoId}")
    @Operation(summary = "Consultar estoque por produto", 
               description = "Consulta o estoque de um produto específico em uma loja")
    public ResponseEntity<?> consultarEstoquePorProduto(
            @Parameter(description = "ID do produto") @PathVariable Long produtoId,
            @Parameter(description = "ID da loja") @RequestParam Long lojaId) {
        
        try {
            logger.info("Consultando estoque - Produto: {}, Loja: {}", produtoId, lojaId);
            
            Optional<EstoqueProduto> estoque = estoqueService.consultarEstoque(produtoId, lojaId);
            
            if (estoque.isPresent()) {
                EstoqueProduto e = estoque.get();
                return ResponseEntity.ok(Map.of(
                    "produtoId", e.getProdutoId(),
                    "lojaId", e.getLojaId(),
                    "quantidade", e.getQuantidade(),
                    "disponivel", e.getDisponivel(),
                    "reservado", e.getReservado(),
                    "estoqueMinimo", e.getEstoqueMinimo(),
                    "isEstoqueBaixo", e.isEstoqueBaixo(),
                    "ultimaAtualizacao", e.getUltimaAtualizacao(),
                    "versao", e.getVersao()
                ));
            } else {
                return ResponseEntity.ok(Map.of(
                    "produtoId", produtoId,
                    "lojaId", lojaId,
                    "quantidade", 0,
                    "disponivel", 0,
                    "reservado", 0,
                    "mensagem", "Produto não encontrado no estoque"
                ));
            }
            
        } catch (Exception e) {
            logger.error("Erro ao consultar estoque", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("erro", "Erro interno do servidor", "detalhes", e.getMessage()));
        }
    }
    
    /**
     * Consulta estoque - quando só é fornecido produtoId, retorna todas as lojas que têm o produto
     */
    @GetMapping("/estoque/produto")
    @Operation(summary = "Consultar produto em todas as lojas", 
               description = "Quando fornecido apenas produtoId, retorna todas as lojas onde o produto existe")
    public ResponseEntity<?> consultarProdutoEmTodasLojas(
            @Parameter(description = "ID do produto (obrigatório)") 
            @RequestParam Long produtoId) {
        
        try {
            logger.info("Consultando produto {} em todas as lojas", produtoId);
            
            List<EstoqueProduto> estoques = estoqueService.listarEstoquePorProduto(produtoId);
            
            if (estoques.isEmpty()) {
                return ResponseEntity.ok(Map.of(
                    "produtoId", produtoId,
                    "totalLojas", 0,
                    "lojas", List.of(),
                    "mensagem", "Produto não encontrado em nenhuma loja"
                ));
            }
            
            return ResponseEntity.ok(Map.of(
                "produtoId", produtoId,
                "totalLojas", estoques.size(),
                "lojas", estoques.stream().map(e -> Map.of(
                    "lojaId", e.getLojaId(),
                    "quantidade", e.getQuantidade(),
                    "disponivel", e.getDisponivel(),
                    "reservado", e.getReservado(),
                    "estoqueMinimo", e.getEstoqueMinimo(),
                    "isEstoqueBaixo", e.isEstoqueBaixo(),
                    "ultimaAtualizacao", e.getUltimaAtualizacao()
                )).toList()
            ));
            
        } catch (Exception e) {
            logger.error("Erro ao consultar produto em todas as lojas: {}", produtoId, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("erro", "Erro interno do servidor", "detalhes", e.getMessage()));
        }
    }
    
    /**
     * Lista estoque de uma loja específica
     */
    @GetMapping("/estoque/loja/{lojaId}")
    @Operation(summary = "Listar estoque por loja", 
               description = "Lista todos os produtos em estoque de uma loja específica")
    public ResponseEntity<?> listarEstoquePorLojaEspecifica(
            @Parameter(description = "ID da loja") @PathVariable Long lojaId) {
        
        try {
            logger.info("Listando estoque da loja: {}", lojaId);
            
            List<EstoqueProduto> estoques = estoqueService.listarEstoquePorLoja(lojaId);
            
            return ResponseEntity.ok(Map.of(
                "lojaId", lojaId,
                "totalProdutos", estoques.size(),
                "produtos", estoques
            ));
            
        } catch (Exception e) {
            logger.error("Erro ao listar estoque da loja: {}", lojaId, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("erro", "Erro interno do servidor", "detalhes", e.getMessage()));
        }
    }
    
    /**
     * Lista estoque de todas as lojas (quando lojaId não é fornecido)
     */
    @GetMapping("/estoque/loja")
    @Operation(summary = "Listar estoque de todas as lojas", 
               description = "Quando lojaId não é fornecido, retorna estoque de todas as lojas")
    public ResponseEntity<?> listarEstoqueTodasLojas() {
        
        try {
            logger.info("Listando estoque de todas as lojas");
            
            // Vou buscar todas as lojas distintas do estoque
            List<EstoqueProduto> todosEstoques = estoqueService.listarTodosEstoques();
            
            // Agrupar por loja para estatísticas
            Map<Long, List<EstoqueProduto>> estoquesPorLoja = todosEstoques.stream()
                .collect(java.util.stream.Collectors.groupingBy(EstoqueProduto::getLojaId));
            
            return ResponseEntity.ok(Map.of(
                "totalLojas", estoquesPorLoja.size(),
                "totalProdutos", todosEstoques.size(),
                "lojas", estoquesPorLoja.entrySet().stream().map(entry -> Map.of(
                    "lojaId", entry.getKey(),
                    "totalProdutos", entry.getValue().size(),
                    "produtos", entry.getValue()
                )).toList()
            ));
            
        } catch (Exception e) {
            logger.error("Erro ao listar estoque de todas as lojas", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("erro", "Erro interno do servidor", "detalhes", e.getMessage()));
        }
    }
    
    /**
     * Produtos com estoque baixo
     */
    @GetMapping("/estoque/baixo")
    @Operation(summary = "Produtos com estoque baixo", 
               description = "Lista produtos que estão com estoque abaixo do mínimo")
    public ResponseEntity<?> produtosComEstoqueBaixo() {
        try {
            logger.info("Consultando produtos com estoque baixo");
            
            List<EstoqueProduto> produtosBaixos = estoqueService.produtosComEstoqueBaixo();
            
            return ResponseEntity.ok(Map.of(
                "totalAlertas", produtosBaixos.size(),
                "produtos", produtosBaixos
            ));
            
        } catch (Exception e) {
            logger.error("Erro ao consultar produtos com estoque baixo", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("erro", "Erro interno do servidor", "detalhes", e.getMessage()));
        }
    }
    
    /**
     * Registra entrada de mercadoria
     */
    @PostMapping("/entrada")
    @Operation(summary = "Entrada de mercadoria", 
               description = "Registra entrada de produtos no estoque")
    public ResponseEntity<?> registrarEntrada(@Valid @RequestBody EntradaRequest request) {
        try {
            logger.info("Registrando entrada - Produto: {}, Loja: {}, Quantidade: {}", 
                       request.getProdutoId(), request.getLojaId(), request.getQuantidade());
            
            EstoqueProduto estoque = estoqueService.adicionarEntrada(
                request.getProdutoId(), 
                request.getLojaId(), 
                request.getQuantidade()
            );
            
            return ResponseEntity.status(HttpStatus.CREATED).body(Map.of(
                "mensagem", "Entrada registrada com sucesso",
                "movimentacaoId", "MOV-" + System.currentTimeMillis(),
                "estoque", estoque
            ));
            
        } catch (ConcorrenciaException e) {
            logger.warn("Conflito de concorrência na entrada", e);
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(Map.of("erro", "Conflito de concorrência", "detalhes", e.getMessage()));
        } catch (IllegalArgumentException e) {
            logger.warn("Dados inválidos para entrada", e);
            return ResponseEntity.badRequest()
                    .body(Map.of("erro", "Dados inválidos", "detalhes", e.getMessage()));
        } catch (Exception e) {
            logger.error("Erro ao registrar entrada", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("erro", "Erro interno do servidor", "detalhes", e.getMessage()));
        }
    }
    
    /**
     * Registra saída manual de mercadoria
     */
    @PostMapping("/saida")
    @Operation(summary = "Saída de mercadoria", 
               description = "Registra saída manual de produtos do estoque")
    public ResponseEntity<?> registrarSaida(@Valid @RequestBody SaidaRequest request) {
        try {
            logger.info("Registrando saída - Produto: {}, Loja: {}, Quantidade: {}", 
                       request.getProdutoId(), request.getLojaId(), request.getQuantidade());
            
            EstoqueProduto estoque = estoqueService.processarSaida(
                request.getProdutoId(), 
                request.getLojaId(), 
                request.getQuantidade(),
                request.getMotivo()
            );
            
            return ResponseEntity.ok(Map.of(
                "mensagem", "Saída registrada com sucesso",
                "movimentacaoId", "MOV-" + System.currentTimeMillis(),
                "estoque", estoque
            ));
            
        } catch (EstoqueInsuficienteException e) {
            logger.warn("Estoque insuficiente para saída", e);
            return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY)
                    .body(Map.of(
                        "erro", "Estoque insuficiente", 
                        "detalhes", e.getMessage(),
                        "disponivel", e.getDisponivel(),
                        "solicitado", e.getSolicitado()
                    ));
        } catch (IllegalArgumentException e) {
            logger.warn("Dados inválidos para saída", e);
            return ResponseEntity.badRequest()
                    .body(Map.of("erro", "Dados inválidos", "detalhes", e.getMessage()));
        } catch (Exception e) {
            logger.error("Erro ao registrar saída", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("erro", "Erro interno do servidor", "detalhes", e.getMessage()));
        }
    }
    
    /**
     * Processa venda (cria reserva)
     */
    @PostMapping("/venda")
    @Operation(summary = "Processar venda", 
               description = "Inicia processo de venda criando reserva temporária")
    public ResponseEntity<?> processarVenda(@Valid @RequestBody VendaRequest request) {
        try {
            logger.info("Processando venda - Produto: {}, Loja: {}, Quantidade: {}", 
                       request.getProdutoId(), request.getLojaId(), request.getQuantidade());
            
            String reservaId = estoqueService.processarVenda(
                request.getProdutoId(),
                request.getLojaId(),
                request.getQuantidade(),
                request.getClienteId()
            );
            
            return ResponseEntity.status(HttpStatus.CREATED).body(Map.of(
                "mensagem", "Venda iniciada com sucesso",
                "reservaId", reservaId,
                "status", "RESERVADO",
                "validadeReserva", "30 minutos"
            ));
            
        } catch (EstoqueInsuficienteException e) {
            logger.warn("Estoque insuficiente para venda", e);
            return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY)
                    .body(Map.of(
                        "erro", "Estoque insuficiente", 
                        "detalhes", e.getMessage(),
                        "disponivel", e.getDisponivel(),
                        "solicitado", e.getSolicitado()
                    ));
        } catch (Exception e) {
            logger.error("Erro ao processar venda", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("erro", "Erro interno do servidor", "detalhes", e.getMessage()));
        }
    }
    
    /**
     * Confirma venda (após pagamento)
     */
    @PutMapping("/venda/{reservaId}/confirmar")
    @Operation(summary = "Confirmar venda", 
               description = "Confirma venda após aprovação do pagamento")
    public ResponseEntity<?> confirmarVenda(@PathVariable String reservaId) {
        try {
            logger.info("Confirmando venda - Reserva: {}", reservaId);
            
            boolean confirmada = estoqueService.confirmarVenda(reservaId);
            
            if (confirmada) {
                return ResponseEntity.ok(Map.of(
                    "mensagem", "Venda confirmada com sucesso",
                    "reservaId", reservaId,
                    "status", "CONFIRMADA"
                ));
            } else {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .body(Map.of("erro", "Falha ao confirmar venda"));
            }
            
        } catch (IllegalArgumentException | IllegalStateException e) {
            logger.warn("Erro de validação ao confirmar venda", e);
            return ResponseEntity.badRequest()
                    .body(Map.of("erro", "Dados inválidos", "detalhes", e.getMessage()));
        } catch (Exception e) {
            logger.error("Erro ao confirmar venda", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("erro", "Erro interno do servidor", "detalhes", e.getMessage()));
        }
    }
    
    /**
     * Cancela venda
     */
    @DeleteMapping("/venda/{reservaId}")
    @Operation(summary = "Cancelar venda", 
               description = "Cancela venda e libera reserva")
    public ResponseEntity<?> cancelarVenda(@PathVariable String reservaId) {
        try {
            logger.info("Cancelando venda - Reserva: {}", reservaId);
            
            boolean cancelada = estoqueService.cancelarVenda(reservaId);
            
            if (cancelada) {
                return ResponseEntity.ok(Map.of(
                    "mensagem", "Venda cancelada com sucesso",
                    "reservaId", reservaId,
                    "status", "CANCELADA"
                ));
            } else {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .body(Map.of("erro", "Falha ao cancelar venda"));
            }
            
        } catch (IllegalArgumentException | IllegalStateException e) {
            logger.warn("Erro de validação ao cancelar venda", e);
            return ResponseEntity.badRequest()
                    .body(Map.of("erro", "Dados inválidos", "detalhes", e.getMessage()));
        } catch (Exception e) {
            logger.error("Erro ao cancelar venda", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("erro", "Erro interno do servidor", "detalhes", e.getMessage()));
        }
    }
    
    /**
     * Ajuste manual de estoque
     */
    @PostMapping("/ajuste")
    @Operation(summary = "Ajustar estoque", 
               description = "Realiza ajuste manual de estoque (inventário)")
    public ResponseEntity<?> ajustarEstoque(@Valid @RequestBody AjusteRequest request) {
        try {
            logger.info("Ajustando estoque - Produto: {}, Loja: {}, Nova quantidade: {}", 
                       request.getProdutoId(), request.getLojaId(), request.getNovaQuantidade());
            
            EstoqueProduto estoque = estoqueService.ajustarEstoque(
                request.getProdutoId(),
                request.getLojaId(),
                request.getNovaQuantidade(),
                request.getMotivo()
            );
            
            return ResponseEntity.ok(Map.of(
                "mensagem", "Estoque ajustado com sucesso",
                "movimentacaoId", "ADJ-" + System.currentTimeMillis(),
                "estoque", estoque
            ));
            
        } catch (IllegalArgumentException e) {
            logger.warn("Dados inválidos para ajuste", e);
            return ResponseEntity.badRequest()
                    .body(Map.of("erro", "Dados inválidos", "detalhes", e.getMessage()));
        } catch (Exception e) {
            logger.error("Erro ao ajustar estoque", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("erro", "Erro interno do servidor", "detalhes", e.getMessage()));
        }
    }
}