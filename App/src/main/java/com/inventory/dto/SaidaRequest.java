package com.inventory.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

/**
 * DTO para requisições de saída manual de mercadoria
 */
public class SaidaRequest {
    
    @NotNull(message = "ID do produto é obrigatório")
    private Long produtoId;
    
    @NotNull(message = "ID da loja é obrigatório")
    private Long lojaId;
    
    @Positive(message = "Quantidade deve ser positiva")
    private Integer quantidade;
    
    @NotBlank(message = "Motivo é obrigatório")
    private String motivo;
    
    private String observacoes;
    
    // Construtores
    public SaidaRequest() {}
    
    public SaidaRequest(Long produtoId, Long lojaId, Integer quantidade, String motivo) {
        this.produtoId = produtoId;
        this.lojaId = lojaId;
        this.quantidade = quantidade;
        this.motivo = motivo;
    }
    
    // Getters e Setters
    public Long getProdutoId() { return produtoId; }
    public void setProdutoId(Long produtoId) { this.produtoId = produtoId; }
    
    public Long getLojaId() { return lojaId; }
    public void setLojaId(Long lojaId) { this.lojaId = lojaId; }
    
    public Integer getQuantidade() { return quantidade; }
    public void setQuantidade(Integer quantidade) { this.quantidade = quantidade; }
    
    public String getMotivo() { return motivo; }
    public void setMotivo(String motivo) { this.motivo = motivo; }
    
    public String getObservacoes() { return observacoes; }
    public void setObservacoes(String observacoes) { this.observacoes = observacoes; }
}