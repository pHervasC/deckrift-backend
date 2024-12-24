package com.ausiasmarch.deckrift.service;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.ausiasmarch.deckrift.entity.CartaEntity;
import com.ausiasmarch.deckrift.exception.ResourceNotFoundException;
import com.ausiasmarch.deckrift.repository.CartaRepository;


@Service
public class CartaService implements ServiceInterface<CartaEntity> {

    @Autowired
    CartaRepository oCartaRepository;

    @Autowired
    RandomService oRandomService;

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
        return oCartaRepository.save(oCartaEntity);
    }

    // Actualizar una carta existente
    public CartaEntity update(Long id, CartaEntity oCartaEntity) {
        CartaEntity oCartaFromDb = findById(id);
        if (oCartaEntity.getNombre() != null) {
            oCartaFromDb.setNombre(oCartaEntity.getNombre());
        }
        if (oCartaEntity.getTipo() != null) {
            oCartaFromDb.setTipo(oCartaEntity.getTipo());
        }
        return oCartaRepository.save(oCartaFromDb);
    }

    // Eliminar una carta por ID
    public Long delete(Long id) {
        oCartaRepository.deleteById(id);
        return 1L;
    }

    // Contar todas las cartas
    public Long count() {
        return oCartaRepository.count();
    }

    // Eliminar todas las cartas
    public Long deleteAll() {
        oCartaRepository.deleteAll();
                return this.count();
    }

    public CartaEntity update(CartaEntity oCartaEntity) {
        CartaEntity oCartaFromDb = findById(oCartaEntity.getId());
        if (oCartaEntity.getNombre() != null) {
            oCartaFromDb.setNombre(oCartaEntity.getNombre());
        }
        if (oCartaEntity.getTipo() != null) {
            oCartaFromDb.setTipo(oCartaEntity.getTipo());
        }
        if (oCartaEntity.getRareza() != null) {
            oCartaFromDb.setRareza(oCartaEntity.getRareza());
        }
        return oCartaRepository.save(oCartaFromDb);
    }

    public Page<CartaEntity> getPage(Pageable oPageable, Optional<String> filter) {
        if (filter.isPresent()) {
            return oCartaRepository
                    .findByNombreContaining(
                            filter.get(), oPageable);
        } else {
            return oCartaRepository.findAll(oPageable);
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
