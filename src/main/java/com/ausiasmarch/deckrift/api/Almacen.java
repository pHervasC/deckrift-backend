package com.ausiasmarch.deckrift.api;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
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
    @GetMapping("/cartas/{usuarioId}")
    public ResponseEntity<Page<AlmacenEntity>> getCartasByUsuarioId(
            @PathVariable Long usuarioId,
            Pageable pageable,
            @RequestParam Optional<String> filter) {
    
        return ResponseEntity.ok(oAlmacenService.findByUsuarioId(usuarioId, pageable, filter));
    }
    


    @DeleteMapping("/delete/{usuarioId}/{cartaId}")
    public ResponseEntity<Map<String, String>> deleteCarta(
        @PathVariable Long usuarioId, 
        @PathVariable Long cartaId) {
    try {
        oAlmacenService.deleteByUsuarioAndCarta(usuarioId, cartaId);
        return ResponseEntity.ok(Collections.singletonMap("message", "Carta eliminada correctamente."));
    } catch (RuntimeException e) {
        System.err.println("Error al eliminar la carta: " + e.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(Collections.singletonMap("error", e.getMessage()));
    }
}

@PostMapping("/add/{usuarioId}/{cartaId}")
public ResponseEntity<Map<String, Object>> addCarta(
        @PathVariable Long usuarioId, 
        @PathVariable Long cartaId) {

    Map<String, Object> response = new HashMap<>();

    try {
        oAlmacenService.addCarta(usuarioId, cartaId);
        response.put("message", "Carta añadida correctamente.");
        response.put("status", HttpStatus.CREATED.value());
        return ResponseEntity.status(HttpStatus.CREATED).body(response);

    } catch (RuntimeException e) {
        response.put("error", e.getMessage());
        response.put("status", HttpStatus.BAD_REQUEST.value());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);

    } catch (Exception e) {
        response.put("error", "Error inesperado: " + e.getMessage());
        response.put("status", HttpStatus.INTERNAL_SERVER_ERROR.value());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    }
}


}
