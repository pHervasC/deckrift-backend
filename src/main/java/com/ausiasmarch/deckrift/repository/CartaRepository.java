package com.ausiasmarch.deckrift.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.ausiasmarch.deckrift.entity.CartaEntity;

public interface CartaRepository extends JpaRepository<CartaEntity, Long> {

    @Query(value = "SELECT * FROM cartas WHERE nombre LIKE %:nombre%", nativeQuery = true)
    Page<CartaEntity> findByNombreContaining(String nombre, Pageable oPageable);

    @Query(value = "SELECT * FROM cartas ORDER BY RAND() LIMIT 1", nativeQuery = true)
    CartaEntity GetRandomCard();

}
