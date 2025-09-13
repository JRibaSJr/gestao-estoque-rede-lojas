package com.inventory.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;

/**
 * Entidade JPA para controle de estoque por produto e loja
 */
@Entity
@Table(name = "estoque_produto", 
       uniqueConstraints = @UniqueConstraint(columnNames = {"produto_id", "loja_id"}))
public class EstoqueProduto {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @NotNull(message = "ID do produto é obrigatório")
    @Column(name = "produto_id", nullable = false)
    private Long produtoId;
    
    @NotNull(message = "ID da loja é obrigatório") 
    @Column(name = "loja_id", nullable = false)
    private Long lojaId;
    
    @Min(value = 0, message = "Quantidade não pode ser negativa")
    @Column(name = "quantidade", nullable = false)
    private Integer quantidade;
    
    @Min(value = 0, message = "Quantidade reservada não pode ser negativa")
    @Column(name = "reservado", nullable = false)
    private Integer reservado;
    
    @Min(value = 0, message = "Estoque mínimo não pode ser negativo")
    @Column(name = "estoque_minimo", nullable = false)
    private Integer estoqueMinimo;
    
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Column(name = "ultima_atualizacao")
    private LocalDateTime ultimaAtualizacao;
    
    @Version
    @Column(name = "versao")
    private Long versao; // Para controle de concorrência otimística
    
    // Construtores
    public EstoqueProduto() {
        this.quantidade = 0;
        this.reservado = 0;
        this.estoqueMinimo = 5;
        this.ultimaAtualizacao = LocalDateTime.now();
        this.versao = 1L;
    }
    
    public EstoqueProduto(Long produtoId, Long lojaId, Integer quantidade) {
        this();
        this.produtoId = produtoId;
        this.lojaId = lojaId;
        this.quantidade = quantidade;
    }
    
    // Métodos de negócio
    public Integer getDisponivel() {
        return quantidade - reservado;
    }
    
    public boolean temEstoqueSuficiente(Integer quantidadeDesejada) {
        return getDisponivel() >= quantidadeDesejada;
    }
    
    public boolean isEstoqueBaixo() {
        return quantidade <= estoqueMinimo;
    }
    
    public void reservar(Integer qtd) {
        if (!temEstoqueSuficiente(qtd)) {
            throw new IllegalArgumentException("Estoque insuficiente para reserva");
        }
        this.reservado += qtd;
        this.ultimaAtualizacao = LocalDateTime.now();
        if (this.versao == null) {
            this.versao = 1L;
        } else {
            this.versao++;
        }
    }
    
    public void liberarReserva(Integer qtd) {
        if (this.reservado < qtd) {
            throw new IllegalArgumentException("Quantidade a liberar excede reservado");
        }
        this.reservado -= qtd;
        this.ultimaAtualizacao = LocalDateTime.now();
        if (this.versao == null) {
            this.versao = 1L;
        } else {
            this.versao++;
        }
    }
    
    public void confirmarSaida(Integer qtd) {
        if (this.reservado < qtd) {
            throw new IllegalArgumentException("Quantidade a confirmar excede reservado");
        }
        this.quantidade -= qtd;
        this.reservado -= qtd;
        this.ultimaAtualizacao = LocalDateTime.now();
        if (this.versao == null) {
            this.versao = 1L;
        } else {
            this.versao++;
        }
    }
    
    public void adicionarEstoque(Integer qtd) {
        this.quantidade += qtd;
        this.ultimaAtualizacao = LocalDateTime.now();
        if (this.versao == null) {
            this.versao = 1L;
        } else {
            this.versao++;
        }
    }
    
    // Getters e Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public Long getProdutoId() { return produtoId; }
    public void setProdutoId(Long produtoId) { this.produtoId = produtoId; }
    
    public Long getLojaId() { return lojaId; }
    public void setLojaId(Long lojaId) { this.lojaId = lojaId; }
    
    public Integer getQuantidade() { return quantidade; }
    public void setQuantidade(Integer quantidade) { 
        this.quantidade = quantidade;
        this.ultimaAtualizacao = LocalDateTime.now();
        if (this.versao == null) {
            this.versao = 1L;
        } else {
            this.versao++;
        }
    }
    
    public Integer getReservado() { return reservado; }
    public void setReservado(Integer reservado) { 
        this.reservado = reservado;
        this.ultimaAtualizacao = LocalDateTime.now();
        if (this.versao == null) {
            this.versao = 1L;
        } else {
            this.versao++;
        }
    }
    
    public Integer getEstoqueMinimo() { return estoqueMinimo; }
    public void setEstoqueMinimo(Integer estoqueMinimo) { this.estoqueMinimo = estoqueMinimo; }
    
    public LocalDateTime getUltimaAtualizacao() { return ultimaAtualizacao; }
    public void setUltimaAtualizacao(LocalDateTime ultimaAtualizacao) { this.ultimaAtualizacao = ultimaAtualizacao; }
    
    public Long getVersao() { return versao; }
    public void setVersao(Long versao) { this.versao = versao; }
    
    @Override
    public String toString() {
        return "EstoqueProduto{" +
                "produtoId=" + produtoId +
                ", lojaId=" + lojaId +
                ", quantidade=" + quantidade +
                ", reservado=" + reservado +
                ", disponivel=" + getDisponivel() +
                ", versao=" + versao +
                '}';
    }
}