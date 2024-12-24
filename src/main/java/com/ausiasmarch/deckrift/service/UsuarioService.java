package com.ausiasmarch.deckrift.service;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.ausiasmarch.deckrift.entity.UsuarioEntity;
import com.ausiasmarch.deckrift.exception.ResourceNotFoundException;
import com.ausiasmarch.deckrift.repository.UsuarioRepository;

@Service
public class UsuarioService implements ServiceInterface<UsuarioEntity> {

    @Autowired
    UsuarioRepository oUsuarioRepository;

    @Autowired
    RandomService oRandomService;

    // Obtener un usuario por ID
    public UsuarioEntity findById(Long id) {
        return oUsuarioRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado con id: " + id));
    }

    public Page<UsuarioEntity> getPage(Pageable oPageable, Optional<String> filter) {

        if (filter.isPresent()) {
            return oUsuarioRepository
                    .findByNombreContaining(filter.get(), oPageable);
        } else {
            return oUsuarioRepository.findAll(oPageable);
        }
    }

    public UsuarioEntity get(Long id) {
        return oUsuarioRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado"));

    }

    public Long count() {
        return oUsuarioRepository.count();
    }

    public UsuarioEntity randomSelection() {
        return oUsuarioRepository.findById((long) oRandomService.getRandomInt(1, (int) (long) this.count())).get();
    }

    // Crear un nuevo usuario
    public UsuarioEntity create(UsuarioEntity oUsuarioEntity) {
        return oUsuarioRepository.save(oUsuarioEntity);
    }

    // Actualizar un usuario existente
    public UsuarioEntity update(UsuarioEntity oUsuarioEntity) {
        UsuarioEntity oUsuarioEntityFromDatabase = oUsuarioRepository.findById(oUsuarioEntity.getId()).get();
        if (oUsuarioEntity.getNombre() != null) {
            oUsuarioEntityFromDatabase.setNombre(oUsuarioEntity.getNombre());
        }
        if (oUsuarioEntity.getCorreo() != null) {
            oUsuarioEntityFromDatabase.setCorreo(oUsuarioEntity.getCorreo());
        }
        if (oUsuarioEntity.getPassword() != null) {
            oUsuarioEntityFromDatabase.setPassword(oUsuarioEntity.getPassword());
        }
        return oUsuarioRepository.save(oUsuarioEntityFromDatabase);
    }

    // Eliminar un usuario por ID
    public Long delete(Long id) {
        oUsuarioRepository.deleteById(id);
        return 1L;
    }

    public Long deleteAll() {
        oUsuarioRepository.deleteAll();
        return this.count();
    }
}
