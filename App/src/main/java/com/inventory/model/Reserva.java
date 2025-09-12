package com.inventory.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Entidade JPA para reserva temporária de produtos
 */
@Entity
@Table(name = "reserva")
public class Reserva {
    
    @Id
    @Column(name = "id", length = 36)
    private String id;
    
    @NotNull(message = "ID do produto é obrigatório")
    @Column(name = "produto_id", nullable = false)
    private Long produtoId;
    
    @NotNull(message = "ID da loja é obrigatório")
    @Column(name = "loja_id", nullable = false)
    private Long lojaId;
    
    @Positive(message = "Quantidade deve ser positiva")
    @Column(name = "quantidade", nullable = false)
    private Integer quantidade;
    
    @Column(name = "cliente_id", length = 50)
    private String clienteId;
    
    @Column(name = "vendedor_id", length = 50)
    private String vendedorId;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    private StatusReserva status;
    
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Column(name = "criada_em", nullable = false)
    private LocalDateTime criadaEm;
    
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Column(name = "expira_em", nullable = false)
    private LocalDateTime expiraEm;
    
    @Column(name = "observacoes", length = 500)
    private String observacoes;
    
    public enum StatusReserva {
        ATIVA,
        CONFIRMADA,
        CANCELADA,
        EXPIRADA
    }
    
    // Construtores
    public Reserva() {
        this.id = UUID.randomUUID().toString();
        this.status = StatusReserva.ATIVA;
        this.criadaEm = LocalDateTime.now();
        this.expiraEm = LocalDateTime.now().plusMinutes(30); // TTL 30 minutos
    }
    
    public Reserva(Long produtoId, Long lojaId, Integer quantidade, String clienteId) {
        this();
        this.produtoId = produtoId;
        this.lojaId = lojaId;
        this.quantidade = quantidade;
        this.clienteId = clienteId;
    }
    
    // Métodos de negócio
    public boolean isExpirada() {
        return LocalDateTime.now().isAfter(expiraEm);
    }
    
    public boolean isAtiva() {
        return status == StatusReserva.ATIVA && !isExpirada();
    }
    
    public void confirmar() {
        if (!isAtiva()) {
            throw new IllegalStateException("Reserva não está ativa para confirmação");
        }
        this.status = StatusReserva.CONFIRMADA;
    }
    
    public void cancelar() {
        if (status == StatusReserva.CONFIRMADA) {
            throw new IllegalStateException("Não é possível cancelar reserva já confirmada");
        }
        this.status = StatusReserva.CANCELADA;
    }
    
    public void marcarComoExpirada() {
        if (status == StatusReserva.ATIVA) {
            this.status = StatusReserva.EXPIRADA;
        }
    }
    
    // Getters e Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    
    public Long getProdutoId() { return produtoId; }
    public void setProdutoId(Long produtoId) { this.produtoId = produtoId; }
    
    public Long getLojaId() { return lojaId; }
    public void setLojaId(Long lojaId) { this.lojaId = lojaId; }
    
    public Integer getQuantidade() { return quantidade; }
    public void setQuantidade(Integer quantidade) { this.quantidade = quantidade; }
    
    public String getClienteId() { return clienteId; }
    public void setClienteId(String clienteId) { this.clienteId = clienteId; }
    
    public String getVendedorId() { return vendedorId; }
    public void setVendedorId(String vendedorId) { this.vendedorId = vendedorId; }
    
    public StatusReserva getStatus() { return status; }
    public void setStatus(StatusReserva status) { this.status = status; }
    
    public LocalDateTime getCriadaEm() { return criadaEm; }
    public void setCriadaEm(LocalDateTime criadaEm) { this.criadaEm = criadaEm; }
    
    public LocalDateTime getExpiraEm() { return expiraEm; }
    public void setExpiraEm(LocalDateTime expiraEm) { this.expiraEm = expiraEm; }
    
    public String getObservacoes() { return observacoes; }
    public void setObservacoes(String observacoes) { this.observacoes = observacoes; }
    
    @Override
    public String toString() {
        return "Reserva{" +
                "id='" + id + '\'' +
                ", produtoId=" + produtoId +
                ", lojaId=" + lojaId +
                ", quantidade=" + quantidade +
                ", status=" + status +
                ", expiraEm=" + expiraEm +
                '}';
    }
}