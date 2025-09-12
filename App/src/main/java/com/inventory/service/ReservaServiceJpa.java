package com.inventory.service;

import com.inventory.model.Reserva;
import com.inventory.repository.ReservaJpaRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Serviço de negócio para operações de reserva usando JPA
 * Substitui a implementação com arquivos JSON
 */
@Service
@Transactional
public class ReservaServiceJpa {
    
    private static final Logger logger = LoggerFactory.getLogger(ReservaServiceJpa.class);
    
    private final ReservaJpaRepository reservaRepository;
    
    public ReservaServiceJpa(ReservaJpaRepository reservaRepository) {
        this.reservaRepository = reservaRepository;
    }
    
    /**
     * Cria nova reserva
     */
    public Reserva criarReserva(Reserva reserva) {
        logger.info("Criando reserva - Produto: {}, Loja: {}, Quantidade: {}, Cliente: {}", 
                   reserva.getProdutoId(), reserva.getLojaId(), reserva.getQuantidade(), reserva.getClienteId());
        
        // Validações básicas
        if (reserva.getProdutoId() == null || reserva.getLojaId() == null) {
            throw new IllegalArgumentException("Produto e loja são obrigatórios");
        }
        
        if (reserva.getQuantidade() == null || reserva.getQuantidade() <= 0) {
            throw new IllegalArgumentException("Quantidade deve ser positiva");
        }
        
        // Configurar campos padrão se não estiverem definidos
        if (reserva.getCriadaEm() == null) {
            reserva.setCriadaEm(LocalDateTime.now());
        }
        
        if (reserva.getExpiraEm() == null) {
            reserva.setExpiraEm(LocalDateTime.now().plusMinutes(30)); // TTL padrão 30 min
        }
        
        if (reserva.getStatus() == null) {
            reserva.setStatus(Reserva.StatusReserva.ATIVA);
        }
        
        Reserva reservaSalva = reservaRepository.save(reserva);
        logger.info("Reserva criada com sucesso - ID: {}", reservaSalva.getId());
        
        return reservaSalva;
    }
    
    /**
     * Busca reserva por ID
     */
    @Transactional(readOnly = true)
    public Optional<Reserva> buscarReserva(String reservaId) {
        logger.debug("Buscando reserva: {}", reservaId);
        return reservaRepository.findById(reservaId);
    }
    
    /**
     * Lista reservas por cliente
     */
    @Transactional(readOnly = true)
    public List<Reserva> listarReservasPorCliente(String clienteId) {
        logger.debug("Listando reservas do cliente: {}", clienteId);
        return reservaRepository.findByClienteId(clienteId);
    }
    
    /**
     * Lista reservas por loja
     */
    @Transactional(readOnly = true)
    public List<Reserva> listarReservasPorLoja(Long lojaId) {
        logger.debug("Listando reservas da loja: {}", lojaId);
        return reservaRepository.findByLojaId(lojaId);
    }
    
    /**
     * Lista reservas por produto
     */
    @Transactional(readOnly = true)
    public List<Reserva> listarReservasPorProduto(Long produtoId) {
        logger.debug("Listando reservas do produto: {}", produtoId);
        return reservaRepository.findByProdutoId(produtoId);
    }
    
    /**
     * Lista reservas ativas (não expiradas)
     */
    @Transactional(readOnly = true)
    public List<Reserva> listarReservasAtivas() {
        logger.debug("Listando reservas ativas");
        return reservaRepository.findReservasAtivas();
    }
    
    /**
     * Lista reservas ativas por produto e loja
     */
    @Transactional(readOnly = true)
    public List<Reserva> listarReservasAtivasPorProdutoLoja(Long produtoId, Long lojaId) {
        logger.debug("Listando reservas ativas - Produto: {}, Loja: {}", produtoId, lojaId);
        return reservaRepository.findReservasAtivasByProdutoAndLoja(produtoId, lojaId);
    }
    
    /**
     * Confirma reserva (marca como CONFIRMADA)
     */
    public void confirmarReserva(String reservaId) {
        logger.info("Confirmando reserva: {}", reservaId);
        
        Reserva reserva = reservaRepository.findById(reservaId)
                .orElseThrow(() -> new IllegalArgumentException("Reserva não encontrada: " + reservaId));
        
        if (!reserva.isAtiva()) {
            throw new IllegalStateException("Reserva não está ativa para confirmação");
        }
        
        reserva.confirmar();
        reservaRepository.save(reserva);
        
        logger.info("Reserva confirmada com sucesso: {}", reservaId);
    }
    
    /**
     * Cancela reserva (marca como CANCELADA)
     */
    public void cancelarReserva(String reservaId) {
        logger.info("Cancelando reserva: {}", reservaId);
        
        Reserva reserva = reservaRepository.findById(reservaId)
                .orElseThrow(() -> new IllegalArgumentException("Reserva não encontrada: " + reservaId));
        
        if (reserva.getStatus() == Reserva.StatusReserva.CONFIRMADA) {
            throw new IllegalStateException("Não é possível cancelar reserva já confirmada");
        }
        
        reserva.cancelar();
        reservaRepository.save(reserva);
        
        logger.info("Reserva cancelada com sucesso: {}", reservaId);
    }
    
    /**
     * Verifica se existe reserva ativa para produto
     */
    @Transactional(readOnly = true)
    public boolean existeReservaAtiva(Long produtoId, Long lojaId) {
        return reservaRepository.existsReservaAtiva(produtoId, lojaId);
    }
    
    /**
     * Conta reservas ativas por produto e loja
     */
    @Transactional(readOnly = true)
    public Long contarReservasAtivas(Long produtoId, Long lojaId) {
        return reservaRepository.countReservasAtivas(produtoId, lojaId);
    }
    
    /**
     * Soma quantidade reservada por produto e loja
     */
    @Transactional(readOnly = true)
    public Long somarQuantidadeReservada(Long produtoId, Long lojaId) {
        return reservaRepository.sumQuantidadeReservada(produtoId, lojaId);
    }
    
    /**
     * Lista reservas que expiram em breve
     */
    @Transactional(readOnly = true)
    public List<Reserva> listarReservasQuaseExpirando(int minutos) {
        LocalDateTime limite = LocalDateTime.now().plusMinutes(minutos);
        return reservaRepository.findReservasQuaseExpirando(limite);
    }
    
    /**
     * Lista reservas por status
     */
    @Transactional(readOnly = true)
    public List<Reserva> listarReservasPorStatus(Reserva.StatusReserva status) {
        return reservaRepository.findByStatus(status);
    }
    
    /**
     * Conta reservas por status
     */
    @Transactional(readOnly = true)
    public Long contarReservasPorStatus(Reserva.StatusReserva status) {
        return reservaRepository.countByStatus(status);
    }
    
    /**
     * Lista últimas reservas por cliente
     */
    @Transactional(readOnly = true)
    public List<Reserva> listarUltimasReservasPorCliente(String clienteId) {
        return reservaRepository.findUltimasReservasByCliente(clienteId);
    }
    
    /**
     * Lista reservas por período
     */
    @Transactional(readOnly = true)
    public List<Reserva> listarReservasPorPeriodo(LocalDateTime inicio, LocalDateTime fim) {
        return reservaRepository.findReservasPorPeriodo(inicio, fim);
    }
    
    /**
     * Job automático para marcar reservas expiradas
     * Executa a cada 5 minutos
     */
    @Scheduled(fixedRate = 300000) // 5 minutos em milissegundos
    public void processarReservasExpiradas() {
        logger.debug("Processando reservas expiradas...");
        
        int reservasExpiradas = reservaRepository.marcarReservasExpiradas();
        
        if (reservasExpiradas > 0) {
            logger.info("Marcadas {} reservas como expiradas", reservasExpiradas);
        }
    }
    
    /**
     * Limpeza de reservas antigas (mais de 30 dias)
     * Executa diariamente à meia-noite
     */
    @Scheduled(cron = "0 0 0 * * *")
    public void limparReservasAntigas() {
        logger.info("Iniciando limpeza de reservas antigas...");
        
        LocalDateTime dataLimite = LocalDateTime.now().minusDays(30);
        int reservasRemovidas = reservaRepository.removeReservasAntigas(dataLimite);
        
        logger.info("Removidas {} reservas antigas (mais de 30 dias)", reservasRemovidas);
    }
    
    /**
     * Estatísticas gerais de reservas
     */
    @Transactional(readOnly = true)
    public ReservaStats getEstatisticas() {
        Long ativas = reservaRepository.countByStatus(Reserva.StatusReserva.ATIVA);
        Long confirmadas = reservaRepository.countByStatus(Reserva.StatusReserva.CONFIRMADA);
        Long canceladas = reservaRepository.countByStatus(Reserva.StatusReserva.CANCELADA);
        Long expiradas = reservaRepository.countByStatus(Reserva.StatusReserva.EXPIRADA);
        Long total = ativas + confirmadas + canceladas + expiradas;
        
        return new ReservaStats(total, ativas, confirmadas, canceladas, expiradas);
    }
    
    /**
     * Classe para estatísticas de reservas
     */
    public static class ReservaStats {
        private Long total;
        private Long ativas;
        private Long confirmadas;
        private Long canceladas;
        private Long expiradas;
        
        public ReservaStats(Long total, Long ativas, Long confirmadas, Long canceladas, Long expiradas) {
            this.total = total;
            this.ativas = ativas;
            this.confirmadas = confirmadas;
            this.canceladas = canceladas;
            this.expiradas = expiradas;
        }
        
        // Getters
        public Long getTotal() { return total; }
        public Long getAtivas() { return ativas; }
        public Long getConfirmadas() { return confirmadas; }
        public Long getCanceladas() { return canceladas; }
        public Long getExpiradas() { return expiradas; }
    }
}