package com.ausiasmarch.deckrift.repository;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.ausiasmarch.deckrift.entity.CompraEntity;

@Repository
public interface CompraRepository extends JpaRepository<CompraEntity, Long> {
    Optional<CompraEntity> findByStripeSessionId(String stripeSessionId);
    Page<CompraEntity> findByUsuarioCorreoContaining(String correo, Pageable pageable);
}
