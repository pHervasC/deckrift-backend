package com.ausiasmarch.deckrift.service;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.ausiasmarch.deckrift.exception.ResourceNotFoundException;
import com.ausiasmarch.deckrift.entity.TipousuarioEntity;
import com.ausiasmarch.deckrift.entity.UsuarioEntity;
import com.ausiasmarch.deckrift.exception.ResourceNotFoundException;
import com.ausiasmarch.deckrift.exception.UnauthorizedAccessException;
import com.ausiasmarch.deckrift.repository.TipoUsuarioRepository;
import com.ausiasmarch.deckrift.repository.UsuarioRepository;

import jakarta.persistence.EntityNotFoundException;

@Service
public class UsuarioService implements ServiceInterface<UsuarioEntity> {

    @Autowired
    UsuarioRepository oUsuarioRepository;

    @Autowired
    RandomService oRandomService;

    @Autowired
    AuthService oAuthService;

    @Autowired
    private TipoUsuarioRepository tipousuarioRepository;

    public UsuarioEntity getByEmail(String correo) {
        UsuarioEntity oUsuarioEntity = oUsuarioRepository.findByCorreo(correo)
                .orElseThrow(() -> new ResourceNotFoundException("El usuario con correo " + correo + " no existe"));
        if (oAuthService.isAdmin() || oAuthService.isAuditorWithItsOwnData(oUsuarioEntity.getId())) {
            return oUsuarioEntity;
        } else {
            throw new UnauthorizedAccessException("No tienes permisos para ver el usuario");
        }
    }

    // Obtener un usuario por ID
    public UsuarioEntity findById(Long id) {
        return oUsuarioRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado con id: " + id));
    }

    public Page<UsuarioEntity> getPage(Pageable oPageable, Optional<String> filter) {
        if (oAuthService.isAdmin()) {
            if (filter.isPresent()) {
                return oUsuarioRepository
                        .findByNombreContainingOrCorreoContaining(
                                filter.get(),filter.get(), oPageable);
            } else {
                return oUsuarioRepository.findAll(oPageable);
            }
        } else {
            throw new UnauthorizedAccessException("No tienes permisos para ver los usuarios");
        }
    }

    public UsuarioEntity get(Long id) {
        if (oAuthService.isAdmin() || oAuthService.isAuditorWithItsOwnData(id)) {
            Optional<UsuarioEntity> usuario = oUsuarioRepository.findById(id);
            if (usuario.isPresent()) {
                return usuario.get();
            } else {
                throw new EntityNotFoundException("Usuario no encontrado con ID: " + id);
            }
        } else {
            throw new UnauthorizedAccessException("No tienes permisos para ver el usuario");
        }
    }

    public Long count() {
        if (!oAuthService.isAdmin()) {
            throw new UnauthorizedAccessException("No tienes permisos para contar los usuarios");
        } else {
            return oUsuarioRepository.count();
        }
    }

    public UsuarioEntity randomSelection() {
        return oUsuarioRepository.findById((long) oRandomService.getRandomInt(1, (int) (long) this.count())).get();
    }

    // Crear un nuevo usuario
    public UsuarioEntity create(UsuarioEntity oUsuarioEntity) {
        oUsuarioEntity.setTipousuario(tipousuarioRepository.findById(2L)
                .orElseThrow(() -> new RuntimeException("Tipo de usuario no encontrado")));
        return oUsuarioRepository.save(oUsuarioEntity);
    }

    public UsuarioEntity adminCreate(UsuarioEntity oUsuarioEntity) {
        // Verificar si el ID de tipo de usuario es válido
        Long tipoId = oUsuarioEntity.getTipousuario().getId();
        TipousuarioEntity tipoUsuario = tipousuarioRepository.findById(tipoId)
                .orElseThrow(() -> new RuntimeException("Tipo de usuario no encontrado con ID: " + tipoId));

        // Asignar el tipo de usuario al usuario
        oUsuarioEntity.setTipousuario(tipoUsuario);

        // Guardar y devolver el usuario creado
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
        if (oAuthService.isAdmin() || oAuthService.isAuditorWithItsOwnData(id)) {
            oUsuarioRepository.deleteById(id);
            return 1L;
        } else {
            throw new UnauthorizedAccessException("No tienes permisos para eliminar la carta");
        }
    }

    public Long deleteAll() {
        if (!oAuthService.isAdmin()) {
            throw new UnauthorizedAccessException("No tienes permisos para borrar todos los usuarios");
        } else {
            oUsuarioRepository.deleteAll();
            return this.count();
        }
    }

}
