package com.inventory.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

/**
 * DTO para requisições de ajuste de estoque
 */
public class AjusteRequest {
    
    @NotNull(message = "ID do produto é obrigatório")
    private Long produtoId;
    
    @NotNull(message = "ID da loja é obrigatório")
    private Long lojaId;
    
    @Min(value = 0, message = "Nova quantidade não pode ser negativa")
    private Integer novaQuantidade;
    
    @NotBlank(message = "Motivo é obrigatório")
    private String motivo;
    
    private String observacoes;
    
    // Construtores
    public AjusteRequest() {}
    
    public AjusteRequest(Long produtoId, Long lojaId, Integer novaQuantidade, String motivo) {
        this.produtoId = produtoId;
        this.lojaId = lojaId;
        this.novaQuantidade = novaQuantidade;
        this.motivo = motivo;
    }
    
    // Getters e Setters
    public Long getProdutoId() { return produtoId; }
    public void setProdutoId(Long produtoId) { this.produtoId = produtoId; }
    
    public Long getLojaId() { return lojaId; }
    public void setLojaId(Long lojaId) { this.lojaId = lojaId; }
    
    public Integer getNovaQuantidade() { return novaQuantidade; }
    public void setNovaQuantidade(Integer novaQuantidade) { this.novaQuantidade = novaQuantidade; }
    
    public String getMotivo() { return motivo; }
    public void setMotivo(String motivo) { this.motivo = motivo; }
    
    public String getObservacoes() { return observacoes; }
    public void setObservacoes(String observacoes) { this.observacoes = observacoes; }
}