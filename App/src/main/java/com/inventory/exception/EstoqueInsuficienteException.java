package com.inventory.exception;

/**
 * Exception para casos de estoque insuficiente
 */
public class EstoqueInsuficienteException extends RuntimeException {
    
    private final Long produtoId;
    private final Long lojaId;
    private final Integer disponivel;
    private final Integer solicitado;
    
    public EstoqueInsuficienteException(Long produtoId, Long lojaId, Integer disponivel, Integer solicitado) {
        super(String.format("Estoque insuficiente para produto %d na loja %d. Disponível: %d, Solicitado: %d", 
              produtoId, lojaId, disponivel, solicitado));
        this.produtoId = produtoId;
        this.lojaId = lojaId;
        this.disponivel = disponivel;
        this.solicitado = solicitado;
    }
    
    // Getters
    public Long getProdutoId() { return produtoId; }
    public Long getLojaId() { return lojaId; }
    public Integer getDisponivel() { return disponivel; }
    public Integer getSolicitado() { return solicitado; }
}