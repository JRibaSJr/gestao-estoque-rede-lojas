package com.inventory.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

/**
 * DTO para requisições de entrada de mercadoria
 */
public class EntradaRequest {
    
    @NotNull(message = "ID do produto é obrigatório")
    private Long produtoId;
    
    @NotNull(message = "ID da loja é obrigatório")
    private Long lojaId;
    
    @Positive(message = "Quantidade deve ser positiva")
    private Integer quantidade;
    
    private String fornecedor;
    private String notaFiscal;
    private String observacoes;
    
    // Construtores
    public EntradaRequest() {}
    
    public EntradaRequest(Long produtoId, Long lojaId, Integer quantidade) {
        this.produtoId = produtoId;
        this.lojaId = lojaId;
        this.quantidade = quantidade;
    }
    
    // Getters e Setters
    public Long getProdutoId() { return produtoId; }
    public void setProdutoId(Long produtoId) { this.produtoId = produtoId; }
    
    public Long getLojaId() { return lojaId; }
    public void setLojaId(Long lojaId) { this.lojaId = lojaId; }
    
    public Integer getQuantidade() { return quantidade; }
    public void setQuantidade(Integer quantidade) { this.quantidade = quantidade; }
    
    public String getFornecedor() { return fornecedor; }
    public void setFornecedor(String fornecedor) { this.fornecedor = fornecedor; }
    
    public String getNotaFiscal() { return notaFiscal; }
    public void setNotaFiscal(String notaFiscal) { this.notaFiscal = notaFiscal; }
    
    public String getObservacoes() { return observacoes; }
    public void setObservacoes(String observacoes) { this.observacoes = observacoes; }
}