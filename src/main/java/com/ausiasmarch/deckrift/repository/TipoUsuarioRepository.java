package com.ausiasmarch.deckrift.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.ausiasmarch.deckrift.entity.TipousuarioEntity;

public interface TipoUsuarioRepository extends JpaRepository<TipousuarioEntity, Long> {

    Page<TipousuarioEntity> findByDescripcionContaining(String filter, Pageable pageable);
}
