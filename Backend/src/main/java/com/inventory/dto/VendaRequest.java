package com.inventory.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

/**
 * DTO para requisições de venda
 */
public class VendaRequest {
    
    @NotNull(message = "ID do produto é obrigatório")
    private Long produtoId;
    
    @NotNull(message = "ID da loja é obrigatório")
    private Long lojaId;
    
    @Positive(message = "Quantidade deve ser positiva")
    private Integer quantidade;
    
    @NotBlank(message = "ID do cliente é obrigatório")
    private String clienteId;
    
    private String vendedorId;
    private String observacoes;
    
    // Construtores
    public VendaRequest() {}
    
    public VendaRequest(Long produtoId, Long lojaId, Integer quantidade, String clienteId) {
        this.produtoId = produtoId;
        this.lojaId = lojaId;
        this.quantidade = quantidade;
        this.clienteId = clienteId;
    }
    
    // Getters e Setters
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
    
    public String getObservacoes() { return observacoes; }
    public void setObservacoes(String observacoes) { this.observacoes = observacoes; }
}