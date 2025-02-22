package com.ausiasmarch.deckrift.repository;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.ausiasmarch.deckrift.entity.CompraEntity;

@Repository
public interface CompraRepository extends JpaRepository<CompraEntity, Long> {
    Optional<CompraEntity> findByStripeSessionId(String stripeSessionId);
    
    @Query("SELECT c FROM CompraEntity c WHERE " +
       "(:correo IS NULL OR c.usuario.correo LIKE %:correo%) AND " +
       "(:estado IS NULL OR c.estado = :estado)")
        Page<CompraEntity> findByUsuarioCorreoAndEstado(
       @Param("correo") String correo,
       @Param("estado") String estado,
       Pageable pageable);

}
