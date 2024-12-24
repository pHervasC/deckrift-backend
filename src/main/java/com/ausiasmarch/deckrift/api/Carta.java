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

import com.ausiasmarch.deckrift.entity.CartaEntity;
import com.ausiasmarch.deckrift.service.CartaService;

@CrossOrigin(origins = "*", allowedHeaders = "*", maxAge = 3600)
@RestController
@RequestMapping("/carta")
public class Carta {

    @Autowired
    CartaService oCartaService;

    // Obtener todas las cartas (con filtro opcional)
    @GetMapping("")
    public ResponseEntity<Page<CartaEntity>> getPage(
            Pageable oPageable,
            @RequestParam Optional<String> filter) {
        return new ResponseEntity<>(oCartaService.findAll(oPageable, filter), HttpStatus.OK);
    }

    // Obtener una carta por ID
    @GetMapping("/{id}")
    public ResponseEntity<CartaEntity> getCarta(@PathVariable Long id) {
        return new ResponseEntity<>(oCartaService.findById(id), HttpStatus.OK);
    }
    // Eliminar una carta por ID
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        oCartaService.delete(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    // Crear una nueva carta
    @PostMapping("")
    public ResponseEntity<CartaEntity> create(@RequestBody CartaEntity oCartaEntity) {
        return new ResponseEntity<>(oCartaService.create(oCartaEntity), HttpStatus.CREATED);
    }

    // Actualizar una carta existente
    @PutMapping("/{id}")
    public ResponseEntity<CartaEntity> update(@PathVariable Long id, @RequestBody CartaEntity oCartaEntity) {
        return new ResponseEntity<>(oCartaService.update(id, oCartaEntity), HttpStatus.OK);
    }

}
