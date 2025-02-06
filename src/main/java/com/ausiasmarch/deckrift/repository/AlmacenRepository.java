package com.ausiasmarch.deckrift.repository;

import com.ausiasmarch.deckrift.entity.AlmacenEntity;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
public interface AlmacenRepository extends JpaRepository<AlmacenEntity, Long> {
    
    Page<AlmacenEntity> findByUsuarioId(Long usuarioId, Pageable pageable);

    @Query(value = "SELECT * FROM almacen WHERE usuario_id = :usuarioId", nativeQuery = true)
    Page<AlmacenEntity> findByUsuarioIdNative(@Param("usuarioId") Long usuarioId, Pageable pageable);

    AlmacenEntity findByUsuarioIdAndCartaId(Long usuarioId, Long cartaId);

    @Query(value = "SELECT a.id AS almacen_id, a.usuario_id, a.carta_id, a.cantidad, c.id AS carta_id, c.nombre, c.tipo, c.rareza " +
               "FROM almacen a " +
               "JOIN cartas c ON a.carta_id = c.id " +
               "WHERE a.usuario_id = :usuarioId " +
               "AND c.nombre LIKE %:nombre%",
       countQuery = "SELECT COUNT(*) FROM almacen a JOIN cartas c ON a.carta_id = c.id WHERE a.usuario_id = :usuarioId AND c.nombre LIKE %:nombre%",
       nativeQuery = true)
Page<Object[]> findByUsuarioIdAndCartaNombreContaining(@Param("usuarioId") Long usuarioId, @Param("nombre") String nombre, Pageable pageable);

    
    
}
