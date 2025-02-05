package com.ausiasmarch.deckrift.api;

import java.io.IOException;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
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
import org.springframework.web.multipart.MultipartFile;

import com.ausiasmarch.deckrift.entity.CartaEntity;
import com.ausiasmarch.deckrift.exception.UnauthorizedAccessException;
import com.ausiasmarch.deckrift.repository.CartaRepository;
import com.ausiasmarch.deckrift.service.CartaService;

@CrossOrigin(origins = "*", allowedHeaders = "*", maxAge = 3600)
@RestController
@RequestMapping("/carta")
public class Carta {

    @Autowired
    CartaService oCartaService;

    @Autowired
    private CartaRepository oCartaRepository;

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
    public ResponseEntity<?> delete(@PathVariable Long id) {
        oCartaService.delete(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @PostMapping("/carta")
public ResponseEntity<CartaEntity> createCarta(
    @RequestParam("nombre") String nombre,
    @RequestParam("tipo") String tipo,
    @RequestParam("rareza") String rareza,
    @RequestParam("imagen") MultipartFile imagen) {
    try {
        CartaEntity nuevaCarta = new CartaEntity();
        nuevaCarta.setNombre(nombre);
        nuevaCarta.setTipo(tipo);
        nuevaCarta.setRareza(rareza);
        nuevaCarta.setImagen(imagen.getBytes());

        CartaEntity cartaCreada = oCartaService.create(nuevaCarta);
        return ResponseEntity.ok(cartaCreada);
    } catch (IOException e) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
    }
}

    // Actualizar una carta existente
    @PutMapping("/{id}")
    public ResponseEntity<CartaEntity> update(@PathVariable Long id, @RequestBody CartaEntity oCartaEntity) {
        return new ResponseEntity<>(oCartaService.update(id, oCartaEntity), HttpStatus.OK);
    }

    @GetMapping("/{id}/imagen")
    public ResponseEntity<byte[]> obtenerImagen(@PathVariable Long id) {
        CartaEntity oCartaEntity = oCartaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Carta no encontrada"));
        return ResponseEntity.ok()
                .contentType(MediaType.IMAGE_PNG)
                .body(oCartaEntity.getImagen());
    }

}
