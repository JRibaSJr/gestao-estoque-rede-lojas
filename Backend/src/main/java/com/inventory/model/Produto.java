package com.inventory.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Modelo de dados para Produto
 */
public class Produto {
    
    @NotNull(message = "ID do produto é obrigatório")
    private Long id;
    
    @NotBlank(message = "Nome do produto é obrigatório")
    private String nome;
    
    @NotBlank(message = "SKU é obrigatório")
    private String sku;
    
    private String categoria;
    
    @Positive(message = "Valor unitário deve ser positivo")
    private BigDecimal valorUnitario;
    
    private String descricao;
    
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime criadoEm;
    
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime atualizadoEm;
    
    // Construtores
    public Produto() {
        this.criadoEm = LocalDateTime.now();
        this.atualizadoEm = LocalDateTime.now();
    }
    
    public Produto(Long id, String nome, String sku, String categoria, BigDecimal valorUnitario) {
        this();
        this.id = id;
        this.nome = nome;
        this.sku = sku;
        this.categoria = categoria;
        this.valorUnitario = valorUnitario;
    }
    
    // Getters e Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public String getNome() { return nome; }
    public void setNome(String nome) { 
        this.nome = nome;
        this.atualizadoEm = LocalDateTime.now();
    }
    
    public String getSku() { return sku; }
    public void setSku(String sku) { 
        this.sku = sku;
        this.atualizadoEm = LocalDateTime.now();
    }
    
    public String getCategoria() { return categoria; }
    public void setCategoria(String categoria) { 
        this.categoria = categoria;
        this.atualizadoEm = LocalDateTime.now();
    }
    
    public BigDecimal getValorUnitario() { return valorUnitario; }
    public void setValorUnitario(BigDecimal valorUnitario) { 
        this.valorUnitario = valorUnitario;
        this.atualizadoEm = LocalDateTime.now();
    }
    
    public String getDescricao() { return descricao; }
    public void setDescricao(String descricao) { 
        this.descricao = descricao;
        this.atualizadoEm = LocalDateTime.now();
    }
    
    public LocalDateTime getCriadoEm() { return criadoEm; }
    public void setCriadoEm(LocalDateTime criadoEm) { this.criadoEm = criadoEm; }
    
    public LocalDateTime getAtualizadoEm() { return atualizadoEm; }
    public void setAtualizadoEm(LocalDateTime atualizadoEm) { this.atualizadoEm = atualizadoEm; }
    
    @Override
    public String toString() {
        return "Produto{" +
                "id=" + id +
                ", nome='" + nome + '\'' +
                ", sku='" + sku + '\'' +
                ", categoria='" + categoria + '\'' +
                ", valorUnitario=" + valorUnitario +
                '}';
    }
}