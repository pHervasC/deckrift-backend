package com.ausiasmarch.deckrift.api;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
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
import com.ausiasmarch.deckrift.service.SobresAbiertosService;
import com.ausiasmarch.deckrift.entity.AlmacenEntity;
import com.ausiasmarch.deckrift.entity.CartaEntity;

@CrossOrigin(origins = "*", allowedHeaders = "*", maxAge = 3600)
@RestController
@RequestMapping("/almacen")
public class Almacen {

    @Autowired
    AlmacenService oAlmacenService;

    @Autowired
    SobresAbiertosService sobresAbiertosService;

    // Agregar 5 cartas random a usuario
    @PostMapping("/addCartas/{idUsuario}")
public ResponseEntity<?> addCartas(
        @PathVariable Long idUsuario,
        @RequestParam(defaultValue = "5") int cantidad,
        @RequestParam(defaultValue = "false") boolean usarMonedas) {

    try {
        // Si no usa monedas, validar límite de sobres
        if (!usarMonedas && !sobresAbiertosService.puedeAbrirSobre(idUsuario)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Has alcanzado el límite de 2 sobres por día.");
        }

        List<CartaEntity> cartas = oAlmacenService.AñadirCartasAUsuario(idUsuario, cantidad);

        // Registrar apertura solo si no se usaron monedas
        if (!usarMonedas) {
            sobresAbiertosService.registrarAperturaSobre(idUsuario, false);
        } else {
            sobresAbiertosService.registrarAperturaSobre(idUsuario, true);
        }

        return ResponseEntity.status(HttpStatus.CREATED).body(cartas);
    } catch (RuntimeException e) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Collections.singletonMap("error", e.getMessage()));
    } catch (Exception e) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Collections.singletonMap("error", "Error interno del servidor"));
    }
}


@GetMapping("/puedeAbrir/{idUsuario}")
public ResponseEntity<Boolean> puedeAbrir(@PathVariable Long idUsuario) {
    try {
        boolean puedeAbrir = sobresAbiertosService.puedeAbrirSobre(idUsuario);
        return ResponseEntity.ok(puedeAbrir);
    } catch (Exception e) {
        e.printStackTrace();
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(false);
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

@DeleteMapping("/delete/all/{usuarioId}/{cartaId}")
public ResponseEntity<Map<String, String>> deleteAllCarta(
    @PathVariable Long usuarioId, 
    @PathVariable Long cartaId) {
try {
    oAlmacenService.deleteAllByUsuarioAndCarta(usuarioId, cartaId);
    return ResponseEntity.ok(Collections.singletonMap("message", "Carta eliminada correctamente."));
} catch (RuntimeException e) {
    System.err.println("Error al eliminar la carta: " + e.getMessage());
    return ResponseEntity.status(HttpStatus.BAD_REQUEST)
            .body(Collections.singletonMap("error", e.getMessage()));
}
}


@DeleteMapping("/usuario/{usuarioId}/vaciar")
public ResponseEntity<Long> vaciarColeccion(@PathVariable Long usuarioId) {
    Long eliminadas = oAlmacenService.vaciarColeccion(usuarioId);
    return new ResponseEntity<>(eliminadas, HttpStatus.OK);
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
