package com.ausiasmarch.deckrift.api;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.ausiasmarch.deckrift.service.AlmacenService;
import com.ausiasmarch.deckrift.entity.AlmacenEntity;

@CrossOrigin(origins = "*", allowedHeaders = "*", maxAge = 3600)
@RestController
@RequestMapping("/almacen")
public class Almacen {
    
    @Autowired
    AlmacenService oAlmacenService;

    // Obtener todas las cartas
    @GetMapping("")
    public ResponseEntity<Page<AlmacenEntity>> getPage(Pageable oPageable, @RequestParam Optional<Long> filter) {
        return new ResponseEntity<>(oAlmacenService.findByUsuarioId(oPageable, filter), HttpStatus.OK);
    }

    // Agregar cartas a usuario
    @PostMapping("/addCartas/{idUsuario}")
    public ResponseEntity<String> addCartas(
        @PathVariable Long idUsuario,
        @RequestParam(defaultValue = "5") int cantidad) {
        try {
        oAlmacenService.AñadirCartasAUsuario(idUsuario, cantidad);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body("Cartas añadidas correctamente al usuario con ID: " + idUsuario);
    } catch (RuntimeException e) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
    } catch (Exception e) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("Error inesperado: " + e.getMessage());
        }
    }

    // Ver cartas de cada usuario
    @GetMapping("/cartas/{idUsuario}")
    public ResponseEntity<Page<AlmacenEntity>> getCartasByUsuarioId(Pageable oPageable, @PathVariable Long idUsuario) {
        return new ResponseEntity<>(oAlmacenService.findByUsuarioId(oPageable, Optional.of(idUsuario)), HttpStatus.OK);
    }
}
