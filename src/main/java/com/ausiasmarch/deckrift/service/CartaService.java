package com.ausiasmarch.deckrift.service;

import java.io.IOException;
import java.util.Collections;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

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

    public ResponseEntity<?> createCarta(String nombre, String tipo, String rareza, MultipartFile imagen) {
        try {
            if (!oAuthService.isAdmin()) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(Collections.singletonMap("error", "No tienes permisos para crear cartas."));
            }
            if (nombre == null || nombre.trim().isEmpty() || 
                tipo == null || tipo.trim().isEmpty() || 
                rareza == null || rareza.trim().isEmpty()) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(Collections.singletonMap("error", "Todos los campos son obligatorios."));
            }

            CartaEntity nuevaCarta = new CartaEntity();
            nuevaCarta.setNombre(nombre);
            nuevaCarta.setTipo(tipo);
            nuevaCarta.setRareza(rareza);

            if (imagen != null && !imagen.isEmpty()) {
                nuevaCarta.setImagen(imagen.getBytes());
            } else {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(Collections.singletonMap("error", "Debe incluirse una imagen para la carta."));
            }

            CartaEntity cartaCreada = oCartaRepository.save(nuevaCarta);
            return ResponseEntity.status(HttpStatus.CREATED).body(cartaCreada);

        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Collections.singletonMap("error", "Error al procesar la imagen."));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Collections.singletonMap("error", "Ocurrió un error inesperado."));
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

    public ResponseEntity<?> updateCarta(Long id, String nombre, String tipo, String rareza, MultipartFile imagen) {
        try {
            // Verificar permisos de administrador
            if (!oAuthService.isAdmin()) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(Collections.singletonMap("error", "No tienes permisos para modificar cartas."));
            }

            // Buscar la carta en la BD
            CartaEntity cartaExistente = oCartaRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Carta no encontrada con ID: " + id));

            // Actualizar los datos
            cartaExistente.setNombre(nombre);
            cartaExistente.setTipo(tipo);
            cartaExistente.setRareza(rareza);

            // Si hay imagen, actualizarla
            if (imagen != null && !imagen.isEmpty()) {
                cartaExistente.setImagen(imagen.getBytes());
            }

            // Guardar cambios
            CartaEntity cartaActualizada = oCartaRepository.save(cartaExistente);
            return ResponseEntity.ok(cartaActualizada);

        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Collections.singletonMap("error", "Error al procesar la imagen."));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Collections.singletonMap("error", "Ocurrió un error inesperado."));
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
