package com.inventory.repository;

import com.inventory.model.Reserva;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Repository JPA para operações de reserva
 */
@Repository
public interface ReservaJpaRepository extends JpaRepository<Reserva, String> {

    /**
     * Busca reservas por produto e loja
     */
    List<Reserva> findByProdutoIdAndLojaId(Long produtoId, Long lojaId);

    /**
     * Busca reservas por cliente
     */
    List<Reserva> findByClienteId(String clienteId);

    /**
     * Busca reservas por status
     */
    List<Reserva> findByStatus(Reserva.StatusReserva status);

    /**
     * Busca reservas ativas (não expiradas)
     */
    @Query("SELECT r FROM Reserva r WHERE r.status = 'ATIVA' AND r.expiraEm > CURRENT_TIMESTAMP")
    List<Reserva> findReservasAtivas();

    /**
     * Busca reservas expiradas que ainda estão com status ATIVA
     */
    @Query("SELECT r FROM Reserva r WHERE r.status = 'ATIVA' AND r.expiraEm <= CURRENT_TIMESTAMP")
    List<Reserva> findReservasExpiradas();

    /**
     * Busca reservas por loja
     */
    List<Reserva> findByLojaId(Long lojaId);

    /**
     * Busca reservas por produto
     */
    List<Reserva> findByProdutoId(Long produtoId);

    /**
     * Busca reservas criadas em um período
     */
    @Query("SELECT r FROM Reserva r WHERE r.criadaEm BETWEEN :inicio AND :fim")
    List<Reserva> findReservasPorPeriodo(@Param("inicio") LocalDateTime inicio, 
                                        @Param("fim") LocalDateTime fim);

    /**
     * Busca reservas ativas por produto e loja
     */
    @Query("SELECT r FROM Reserva r WHERE r.produtoId = :produtoId AND r.lojaId = :lojaId " +
           "AND r.status = 'ATIVA' AND r.expiraEm > CURRENT_TIMESTAMP")
    List<Reserva> findReservasAtivasByProdutoAndLoja(@Param("produtoId") Long produtoId, 
                                                    @Param("lojaId") Long lojaId);

    /**
     * Marca reservas expiradas como EXPIRADA
     */
    @Modifying
    @Query("UPDATE Reserva r SET r.status = 'EXPIRADA' " +
           "WHERE r.status = 'ATIVA' AND r.expiraEm <= CURRENT_TIMESTAMP")
    int marcarReservasExpiradas();

    /**
     * Conta reservas ativas por produto e loja
     */
    @Query("SELECT COUNT(r) FROM Reserva r WHERE r.produtoId = :produtoId AND r.lojaId = :lojaId " +
           "AND r.status = 'ATIVA' AND r.expiraEm > CURRENT_TIMESTAMP")
    Long countReservasAtivas(@Param("produtoId") Long produtoId, @Param("lojaId") Long lojaId);

    /**
     * Soma quantidade reservada ativa por produto e loja
     */
    @Query("SELECT COALESCE(SUM(r.quantidade), 0) FROM Reserva r " +
           "WHERE r.produtoId = :produtoId AND r.lojaId = :lojaId " +
           "AND r.status = 'ATIVA' AND r.expiraEm > CURRENT_TIMESTAMP")
    Long sumQuantidadeReservada(@Param("produtoId") Long produtoId, @Param("lojaId") Long lojaId);

    /**
     * Busca reservas que expiram em breve (nos próximos X minutos)
     */
    @Query("SELECT r FROM Reserva r WHERE r.status = 'ATIVA' " +
           "AND r.expiraEm BETWEEN CURRENT_TIMESTAMP AND :limite")
    List<Reserva> findReservasQuaseExpirando(@Param("limite") LocalDateTime limite);

    /**
     * Conta total de reservas por status
     */
    @Query("SELECT COUNT(r) FROM Reserva r WHERE r.status = :status")
    Long countByStatus(@Param("status") Reserva.StatusReserva status);

    /**
     * Busca últimas reservas por cliente (limitadas)
     */
    @Query("SELECT r FROM Reserva r WHERE r.clienteId = :clienteId " +
           "ORDER BY r.criadaEm DESC")
    List<Reserva> findUltimasReservasByCliente(@Param("clienteId") String clienteId);

    /**
     * Verifica se existe reserva ativa para produto específico
     */
    @Query("SELECT CASE WHEN COUNT(r) > 0 THEN true ELSE false END FROM Reserva r " +
           "WHERE r.produtoId = :produtoId AND r.lojaId = :lojaId " +
           "AND r.status = 'ATIVA' AND r.expiraEm > CURRENT_TIMESTAMP")
    boolean existsReservaAtiva(@Param("produtoId") Long produtoId, @Param("lojaId") Long lojaId);

    /**
     * Remove reservas antigas (mais de X dias)
     */
    @Modifying
    @Query("DELETE FROM Reserva r WHERE r.criadaEm < :dataLimite")
    int removeReservasAntigas(@Param("dataLimite") LocalDateTime dataLimite);
}