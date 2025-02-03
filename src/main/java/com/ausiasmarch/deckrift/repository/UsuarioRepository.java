package com.ausiasmarch.deckrift.repository;


import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.ausiasmarch.deckrift.entity.UsuarioEntity;
import java.util.Optional;


public interface UsuarioRepository extends JpaRepository<UsuarioEntity, Long> {

   @Query(value = "SELECT * FROM usuario WHERE nombre LIKE %:nombre%", nativeQuery = true)
    Page<UsuarioEntity> findByNombreContaining(String nombre, Pageable oPageable);

    Optional<UsuarioEntity> findByCorreo(String correo);

    Page<UsuarioEntity> findByNombreContainingOrCorreoContaining(String filter1, String filter2, Pageable oPageable);

    Optional<UsuarioEntity> findByCorreoAndPassword(String correo, String password);

}
