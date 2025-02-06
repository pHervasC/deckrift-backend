package com.ausiasmarch.deckrift.service;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.ausiasmarch.deckrift.entity.CartaEntity;
import com.ausiasmarch.deckrift.exception.ResourceNotFoundException;
import com.ausiasmarch.deckrift.exception.UnauthorizedAccessException;
import com.ausiasmarch.deckrift.repository.CartaRepository;

@Service
public class CartaService implements ServiceInterface<CartaEntity> {

    @Autowired
    CartaRepository oCartaRepository;

    @Autowired
    RandomService oRandomService;

    @Autowired
    AuthService oAuthService;

    // Obtener una carta por ID
    public CartaEntity findById(Long id) {
        return oCartaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Carta no encontrada con id: " + id));
    }

    // Obtener todas las cartas con paginación (y filtro opcional)
    public Page<CartaEntity> findAll(Pageable oPageable, Optional<String> filter) {
        if (filter.isPresent()) {
            return oCartaRepository.findByNombreContaining(filter.get(), oPageable);
        } else {
            return oCartaRepository.findAll(oPageable);
        }
    }
    
    // Crear una nueva carta
    public CartaEntity create(CartaEntity oCartaEntity) {
        if (oAuthService.isAdmin()) {
            return oCartaRepository.save(oCartaEntity);
        } else {
            throw new UnauthorizedAccessException("No tienes permisos para eliminar la carta");
        }
    }

    // Eliminar una carta por ID
    public Long delete(Long id) {
        if (oAuthService.isAdmin()) {
            oCartaRepository.deleteById(id);
            return 1L;
        } else {
            throw new UnauthorizedAccessException("No tienes permisos para eliminar la carta");
        }
    }

    // Contar todas las cartas
    public Long count() {
        if (!oAuthService.isAdmin()) {
            throw new UnauthorizedAccessException("No tienes permisos para contar los usuarios");
        } else {
            return oCartaRepository.count();
        }
    }

    // Eliminar todas las cartas
    public Long deleteAll() {
        oCartaRepository.deleteAll();
        return this.count();
    }

    public CartaEntity update(CartaEntity oCartaEntity) {
        if (oAuthService.isAdmin()) {
            return oCartaRepository.save(oCartaEntity);
        } else {
            throw new UnauthorizedAccessException("No tienes permisos para modificar la carta");
        }
    }

    public Page<CartaEntity> getPage(Pageable oPageable, Optional<String> filter) {
        if (oAuthService.isAdmin()) {
            if (filter.isPresent()) {
                return oCartaRepository
                        .findByNombreContaining(
                                filter.get(), oPageable);
            } else {
                return oCartaRepository.findAll(oPageable);
            }
        } else {
            throw new UnauthorizedAccessException("No tienes permisos para ver las Cartas");
        }
    }

    public CartaEntity get(Long id) {
        return oCartaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Carta no encontrada con id: " + id));
    }

    public CartaEntity randomSelection() {
        return oCartaRepository.findById((long) oRandomService.getRandomInt(1, (int) (long) this.count())).get();

    }

}
