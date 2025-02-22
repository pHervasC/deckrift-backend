package com.ausiasmarch.deckrift.repository;

import java.time.LocalDate;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.ausiasmarch.deckrift.entity.SobresAbiertosEntity;

@Repository
public interface SobresAbiertosRepository extends JpaRepository<SobresAbiertosEntity, Long> {

    Optional<SobresAbiertosEntity> findByUsuarioIdAndFecha(Long usuarioId, LocalDate fecha);
}
