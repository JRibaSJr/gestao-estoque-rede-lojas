package com.inventory.service;

import com.inventory.exception.EstoqueInsuficienteException;
import com.inventory.model.EstoqueProduto;
import com.inventory.model.Reserva;
import com.inventory.repository.EstoqueJpaRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Serviço de negócio para operações de estoque usando JPA
 * Substitui a implementação com arquivos JSON
 */
@Service
@Transactional
public class EstoqueServiceJpa {
    
    private static final Logger logger = LoggerFactory.getLogger(EstoqueServiceJpa.class);
    
    private final EstoqueJpaRepository estoqueRepository;
    private final ReservaServiceJpa reservaService;
    
    public EstoqueServiceJpa(EstoqueJpaRepository estoqueRepository, ReservaServiceJpa reservaService) {
        this.estoqueRepository = estoqueRepository;
        this.reservaService = reservaService;
    }
    
    /**
     * Consulta estoque por produto e loja
     */
    @Transactional(readOnly = true)
    public Optional<EstoqueProduto> consultarEstoque(Long produtoId, Long lojaId) {
        logger.debug("Consultando estoque - Produto: {}, Loja: {}", produtoId, lojaId);
        return estoqueRepository.findByProdutoIdAndLojaId(produtoId, lojaId);
    }
    
    /**
     * Lista estoque por loja
     */
    @Transactional(readOnly = true)
    public List<EstoqueProduto> listarEstoquePorLoja(Long lojaId) {
        logger.debug("Listando estoque da loja: {}", lojaId);
        return estoqueRepository.findByLojaId(lojaId);
    }
    
    /**
     * Produtos com estoque baixo
     */
    @Transactional(readOnly = true)
    public List<EstoqueProduto> produtosComEstoqueBaixo() {
        logger.debug("Consultando produtos com estoque baixo");
        return estoqueRepository.findEstoqueBaixo();
    }
    
    /**
     * Produtos com estoque baixo por loja
     */
    @Transactional(readOnly = true)
    public List<EstoqueProduto> produtosComEstoqueBaixoPorLoja(Long lojaId) {
        logger.debug("Consultando produtos com estoque baixo na loja: {}", lojaId);
        return estoqueRepository.findEstoqueBaixoByLojaId(lojaId);
    }
    
    /**
     * Adiciona entrada de mercadoria
     */
    public EstoqueProduto adicionarEntrada(Long produtoId, Long lojaId, Integer quantidade) {
        logger.info("Adicionando entrada - Produto: {}, Loja: {}, Quantidade: {}", 
                   produtoId, lojaId, quantidade);
        
        if (quantidade <= 0) {
            throw new IllegalArgumentException("Quantidade deve ser positiva");
        }
        
        Optional<EstoqueProduto> estoqueOpt = estoqueRepository.findByProdutoIdAndLojaId(produtoId, lojaId);
        
        EstoqueProduto estoque;
        if (estoqueOpt.isPresent()) {
            estoque = estoqueOpt.get();
            estoque.adicionarEstoque(quantidade);
        } else {
            estoque = new EstoqueProduto(produtoId, lojaId, quantidade);
            estoque.setUltimaAtualizacao(LocalDateTime.now());
        }
        
        EstoqueProduto resultado = estoqueRepository.save(estoque);
        logger.info("Entrada processada - Produto: {}, Loja: {}, Novo estoque: {}", 
                   produtoId, lojaId, resultado.getQuantidade());
        
        return resultado;
    }
    
    /**
     * Processa saída manual de mercadoria
     */
    public EstoqueProduto processarSaida(Long produtoId, Long lojaId, Integer quantidade, String motivo) {
        logger.info("Processando saída - Produto: {}, Loja: {}, Quantidade: {}, Motivo: {}", 
                   produtoId, lojaId, quantidade, motivo);
        
        if (quantidade <= 0) {
            throw new IllegalArgumentException("Quantidade deve ser positiva");
        }
        
        EstoqueProduto estoque = estoqueRepository.findByProdutoIdAndLojaId(produtoId, lojaId)
                .orElseThrow(() -> new IllegalArgumentException("Produto não encontrado no estoque"));
        
        if (estoque.getQuantidade() < quantidade) {
            throw new EstoqueInsuficienteException(produtoId, lojaId, estoque.getQuantidade(), quantidade);
        }
        
        estoque.setQuantidade(estoque.getQuantidade() - quantidade);
        estoque.setUltimaAtualizacao(LocalDateTime.now());
        EstoqueProduto resultado = estoqueRepository.save(estoque);
        
        logger.info("Saída processada - Produto: {}, Loja: {}, Estoque restante: {}", 
                   produtoId, lojaId, resultado.getQuantidade());
        
        return resultado;
    }
    
    /**
     * Processa venda com reserva prévia
     * FLUXO: Reserva → Confirmação → Baixa no estoque
     */
    public String processarVenda(Long produtoId, Long lojaId, Integer quantidade, String clienteId) {
        logger.info("Iniciando processo de venda - Produto: {}, Loja: {}, Quantidade: {}, Cliente: {}", 
                   produtoId, lojaId, quantidade, clienteId);
        
        // 1. Verifica estoque disponível
        EstoqueProduto estoque = estoqueRepository.findByProdutoIdAndLojaId(produtoId, lojaId)
                .orElseThrow(() -> new IllegalArgumentException("Produto não encontrado no estoque"));
        
        if (!estoque.temEstoqueSuficiente(quantidade)) {
            throw new EstoqueInsuficienteException(produtoId, lojaId, estoque.getDisponivel(), quantidade);
        }
        
        // 2. Cria reserva
        Reserva reserva = new Reserva(produtoId, lojaId, quantidade, clienteId);
        reserva = reservaService.criarReserva(reserva);
        
        // 3. Efetua reserva no estoque usando query otimística
        int linhasAfetadas = estoqueRepository.reservarProduto(
            produtoId, lojaId, quantidade, estoque.getVersao()
        );
        
        if (linhasAfetadas == 0) {
            reservaService.cancelarReserva(reserva.getId());
            throw new RuntimeException("Falha ao efetuar reserva no estoque - possível conflito de concorrência");
        }
        
        logger.info("Venda iniciada com sucesso - Reserva: {}", reserva.getId());
        return reserva.getId();
    }
    
    /**
     * Confirma venda (após pagamento aprovado)
     */
    public boolean confirmarVenda(String reservaId) {
        logger.info("Confirmando venda - Reserva: {}", reservaId);
        
        // 1. Busca reserva
        Reserva reserva = reservaService.buscarReserva(reservaId)
                .orElseThrow(() -> new IllegalArgumentException("Reserva não encontrada: " + reservaId));
        
        if (!reserva.isAtiva()) {
            throw new IllegalStateException("Reserva não está ativa para confirmação");
        }
        
        // 2. Confirma saída no estoque
        int linhasAfetadas = estoqueRepository.confirmarSaida(
            reserva.getProdutoId(), reserva.getLojaId(), reserva.getQuantidade()
        );
        
        if (linhasAfetadas > 0) {
            // 3. Marca reserva como confirmada
            reservaService.confirmarReserva(reservaId);
            logger.info("Venda confirmada com sucesso - Reserva: {}", reservaId);
            return true;
        } else {
            logger.error("Falha ao confirmar saída no estoque - Reserva: {}", reservaId);
            return false;
        }
    }
    
    /**
     * Cancela venda
     */
    public boolean cancelarVenda(String reservaId) {
        logger.info("Cancelando venda - Reserva: {}", reservaId);
        
        // 1. Busca reserva
        Reserva reserva = reservaService.buscarReserva(reservaId)
                .orElseThrow(() -> new IllegalArgumentException("Reserva não encontrada: " + reservaId));
        
        if (reserva.getStatus() == Reserva.StatusReserva.CONFIRMADA) {
            throw new IllegalStateException("Não é possível cancelar venda já confirmada");
        }
        
        // 2. Libera reserva no estoque
        int linhasAfetadas = estoqueRepository.liberarReserva(
            reserva.getProdutoId(), reserva.getLojaId(), reserva.getQuantidade()
        );
        
        if (linhasAfetadas > 0) {
            // 3. Cancela reserva
            reservaService.cancelarReserva(reservaId);
            logger.info("Venda cancelada com sucesso - Reserva: {}", reservaId);
            return true;
        } else {
            logger.error("Falha ao liberar reserva no estoque - Reserva: {}", reservaId);
            return false;
        }
    }
    
    /**
     * Ajuste manual de estoque (inventário)
     */
    public EstoqueProduto ajustarEstoque(Long produtoId, Long lojaId, Integer novaQuantidade, String motivo) {
        logger.info("Ajustando estoque - Produto: {}, Loja: {}, Nova quantidade: {}, Motivo: {}", 
                   produtoId, lojaId, novaQuantidade, motivo);
        
        if (novaQuantidade < 0) {
            throw new IllegalArgumentException("Quantidade não pode ser negativa");
        }
        
        EstoqueProduto estoque = estoqueRepository.findByProdutoIdAndLojaId(produtoId, lojaId)
                .orElse(new EstoqueProduto(produtoId, lojaId, 0));
        
        Integer quantidadeAnterior = estoque.getQuantidade();
        estoque.setQuantidade(novaQuantidade);
        estoque.setUltimaAtualizacao(LocalDateTime.now());
        
        EstoqueProduto resultado = estoqueRepository.save(estoque);
        
        logger.info("Estoque ajustado - Produto: {}, Loja: {}, De: {} Para: {}", 
                   produtoId, lojaId, quantidadeAnterior, novaQuantidade);
        
        return resultado;
    }
    
    /**
     * Verifica se existe estoque suficiente disponível
     */
    @Transactional(readOnly = true)
    public boolean hasEstoqueSuficiente(Long produtoId, Long lojaId, Integer quantidade) {
        return estoqueRepository.hasEstoqueSuficiente(produtoId, lojaId, quantidade);
    }
    
    /**
     * Consulta quantidade disponível
     */
    @Transactional(readOnly = true)
    public Integer getQuantidadeDisponivel(Long produtoId, Long lojaId) {
        return estoqueRepository.findQuantidadeDisponivel(produtoId, lojaId).orElse(0);
    }
    
    /**
     * Lista produtos disponíveis por loja
     */
    @Transactional(readOnly = true)
    public List<EstoqueProduto> listarProdutosDisponiveis(Long lojaId) {
        return estoqueRepository.findProdutosDisponiveis(lojaId);
    }
    
    /**
     * Lista todos os estoques de todas as lojas
     */
    @Transactional(readOnly = true)
    public List<EstoqueProduto> listarTodosEstoques() {
        return estoqueRepository.findAll();
    }
    
    /**
     * Lista estoque de um produto em todas as lojas
     */
    @Transactional(readOnly = true)
    public List<EstoqueProduto> listarEstoquePorProduto(Long produtoId) {
        logger.debug("Listando estoque do produto {} em todas as lojas", produtoId);
        return estoqueRepository.findByProdutoId(produtoId);
    }
    
    /**
     * Estatísticas de estoque por loja
     */
    @Transactional(readOnly = true)
    public EstoqueStats getEstatisticasLoja(Long lojaId) {
        Long totalProdutos = estoqueRepository.countProdutosByLojaId(lojaId);
        Long quantidadeTotal = estoqueRepository.sumQuantidadeByLojaId(lojaId);
        Long disponivelTotal = estoqueRepository.sumDisponivelByLojaId(lojaId);
        Long produtosBaixo = (long) estoqueRepository.findEstoqueBaixoByLojaId(lojaId).size();
        
        return new EstoqueStats(totalProdutos, quantidadeTotal, disponivelTotal, produtosBaixo);
    }
    
    /**
     * Classe para estatísticas de estoque
     */
    public static class EstoqueStats {
        private Long totalProdutos;
        private Long quantidadeTotal;
        private Long disponivelTotal;
        private Long produtosComEstoqueBaixo;
        
        public EstoqueStats(Long totalProdutos, Long quantidadeTotal, Long disponivelTotal, Long produtosComEstoqueBaixo) {
            this.totalProdutos = totalProdutos;
            this.quantidadeTotal = quantidadeTotal;
            this.disponivelTotal = disponivelTotal;
            this.produtosComEstoqueBaixo = produtosComEstoqueBaixo;
        }
        
        // Getters
        public Long getTotalProdutos() { return totalProdutos; }
        public Long getQuantidadeTotal() { return quantidadeTotal; }
        public Long getDisponivelTotal() { return disponivelTotal; }
        public Long getProdutosComEstoqueBaixo() { return produtosComEstoqueBaixo; }
    }
}