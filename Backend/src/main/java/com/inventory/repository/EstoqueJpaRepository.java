package com.inventory.repository;

import com.inventory.model.EstoqueProduto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository JPA para operações de estoque
 */
@Repository
public interface EstoqueJpaRepository extends JpaRepository<EstoqueProduto, Long> {

    /**
     * Busca estoque por produto e loja
     */
    Optional<EstoqueProduto> findByProdutoIdAndLojaId(Long produtoId, Long lojaId);

    /**
     * Busca todos os estoques de uma loja
     */
    List<EstoqueProduto> findByLojaId(Long lojaId);
    
    /**
     * Busca estoque de um produto em todas as lojas
     */
    List<EstoqueProduto> findByProdutoId(Long produtoId);

    /**
     * Busca produtos com estoque baixo (quantidade <= estoque mínimo)
     */
    @Query("SELECT e FROM EstoqueProduto e WHERE e.quantidade <= e.estoqueMinimo")
    List<EstoqueProduto> findEstoqueBaixo();

    /**
     * Busca produtos por loja com estoque baixo
     */
    @Query("SELECT e FROM EstoqueProduto e WHERE e.lojaId = :lojaId AND e.quantidade <= e.estoqueMinimo")
    List<EstoqueProduto> findEstoqueBaixoByLojaId(@Param("lojaId") Long lojaId);

    /**
     * Atualização otimística para reservar produto
     */
    @Modifying
    @Query("UPDATE EstoqueProduto e SET e.reservado = e.reservado + :quantidade, e.ultimaAtualizacao = CURRENT_TIMESTAMP " +
           "WHERE e.produtoId = :produtoId AND e.lojaId = :lojaId AND e.versao = :versao " +
           "AND (e.quantidade - e.reservado) >= :quantidade")
    int reservarProduto(@Param("produtoId") Long produtoId, 
                       @Param("lojaId") Long lojaId, 
                       @Param("quantidade") Integer quantidade, 
                       @Param("versao") Long versao);

    /**
     * Confirma saída (reduz quantidade e reservado)
     */
    @Modifying
    @Query("UPDATE EstoqueProduto e SET e.quantidade = e.quantidade - :quantidade, " +
           "e.reservado = e.reservado - :quantidade, e.ultimaAtualizacao = CURRENT_TIMESTAMP " +
           "WHERE e.produtoId = :produtoId AND e.lojaId = :lojaId AND e.reservado >= :quantidade")
    int confirmarSaida(@Param("produtoId") Long produtoId, 
                      @Param("lojaId") Long lojaId, 
                      @Param("quantidade") Integer quantidade);

    /**
     * Libera reserva (reduz apenas reservado)
     */
    @Modifying
    @Query("UPDATE EstoqueProduto e SET e.reservado = e.reservado - :quantidade, " +
           "e.ultimaAtualizacao = CURRENT_TIMESTAMP " +
           "WHERE e.produtoId = :produtoId AND e.lojaId = :lojaId AND e.reservado >= :quantidade")
    int liberarReserva(@Param("produtoId") Long produtoId, 
                      @Param("lojaId") Long lojaId, 
                      @Param("quantidade") Integer quantidade);

    /**
     * Consulta quantidade disponível (quantidade - reservado)
     */
    @Query("SELECT (e.quantidade - e.reservado) FROM EstoqueProduto e " +
           "WHERE e.produtoId = :produtoId AND e.lojaId = :lojaId")
    Optional<Integer> findQuantidadeDisponivel(@Param("produtoId") Long produtoId, 
                                             @Param("lojaId") Long lojaId);

    /**
     * Verifica se existe estoque suficiente disponível
     */
    @Query("SELECT CASE WHEN COUNT(e) > 0 THEN true ELSE false END FROM EstoqueProduto e " +
           "WHERE e.produtoId = :produtoId AND e.lojaId = :lojaId " +
           "AND (e.quantidade - e.reservado) >= :quantidade")
    boolean hasEstoqueSuficiente(@Param("produtoId") Long produtoId, 
                                @Param("lojaId") Long lojaId, 
                                @Param("quantidade") Integer quantidade);

    /**
     * Lista produtos com estoque disponível (não reservado) maior que zero
     */
    @Query("SELECT e FROM EstoqueProduto e WHERE e.lojaId = :lojaId AND (e.quantidade - e.reservado) > 0")
    List<EstoqueProduto> findProdutosDisponiveis(@Param("lojaId") Long lojaId);

    /**
     * Conta total de produtos distintos por loja
     */
    @Query("SELECT COUNT(DISTINCT e.produtoId) FROM EstoqueProduto e WHERE e.lojaId = :lojaId")
    Long countProdutosByLojaId(@Param("lojaId") Long lojaId);

    /**
     * Soma quantidade total em estoque por loja
     */
    @Query("SELECT COALESCE(SUM(e.quantidade), 0) FROM EstoqueProduto e WHERE e.lojaId = :lojaId")
    Long sumQuantidadeByLojaId(@Param("lojaId") Long lojaId);

    /**
     * Soma quantidade disponível por loja
     */
    @Query("SELECT COALESCE(SUM(e.quantidade - e.reservado), 0) FROM EstoqueProduto e WHERE e.lojaId = :lojaId")
    Long sumDisponivelByLojaId(@Param("lojaId") Long lojaId);
}