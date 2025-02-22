package com.ausiasmarch.deckrift.api;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.ausiasmarch.deckrift.service.StripeService;

import java.util.Map;

@CrossOrigin(origins = "*", allowedHeaders = "*", maxAge = 3600)
@RestController
@RequestMapping("/stripe")
public class StripeController {
    
    @Autowired
    StripeService stripeService;

    @PostMapping("/crear-sesion")
    public ResponseEntity<Map<String, String>> crearSesionDePago(@RequestBody Map<String, Object> request) {
        Long usuarioId = ((Number) request.get("usuarioId")).longValue();
        int cantidadMonedas = ((Number) request.get("cantidadMonedas")).intValue();
        double precio = ((Number) request.get("precio")).doubleValue();
    
        String sessionId = stripeService.crearSesionDePago(usuarioId, cantidadMonedas, precio);
    
        return ResponseEntity.ok(Map.of("sessionId", sessionId));
    }

    @GetMapping("/confirmar-pago")
    public ResponseEntity<Boolean> confirmarPago(@RequestParam String session_id) {
    boolean confirmado = stripeService.confirmarPago(session_id);
    return ResponseEntity.ok(confirmado);
}

}
