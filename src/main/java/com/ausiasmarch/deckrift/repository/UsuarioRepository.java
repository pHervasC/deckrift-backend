package com.ausiasmarch.deckrift.repository;


import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.ausiasmarch.deckrift.entity.UsuarioEntity;


public interface UsuarioRepository extends JpaRepository<UsuarioEntity, Long> {

   @Query(value = "SELECT * FROM usuario WHERE nombre LIKE %:nombre%", nativeQuery = true)
    Page<UsuarioEntity> findByNombreContaining(String nombre, org.springframework.data.domain.Pageable oPageable);


}
