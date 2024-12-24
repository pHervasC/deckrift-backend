package com.ausiasmarch.deckrift.repository;

import com.ausiasmarch.deckrift.entity.AlmacenEntity;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
public interface AlmacenRepository extends JpaRepository<AlmacenEntity, Long> {
    
    Page<AlmacenEntity> findByUsuarioId( Long usuarioId, Pageable oPageable);
}
