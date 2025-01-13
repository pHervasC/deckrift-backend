package com.ausiasmarch.deckrift.api;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;

import java.util.Date;

import com.ausiasmarch.deckrift.entity.AuthResponseEntity;
import com.ausiasmarch.deckrift.service.GoogleTokenVerifierService;
import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "*", allowedHeaders = "*", maxAge = 3600)
public class AuthController {
     
    private final GoogleTokenVerifierService googleTokenVerifierService;
    private static final String JWT_SECRET = "clave_secreta"; // Cambia por una clave más segura

    public AuthController(GoogleTokenVerifierService googleTokenVerifierService) {
        this.googleTokenVerifierService = googleTokenVerifierService;
    }

    @PostMapping("/google")
    public ResponseEntity<?> loginWithGoogle(@RequestBody String token) {
        try {
            // Verificar el token de Google
            GoogleIdToken.Payload payload = googleTokenVerifierService.verifyToken(token);

            // Generar un JWT propio
            String jwtToken = JWT.create()
                    .withSubject(payload.getEmail())
                    .withClaim("name", (String) payload.get("name"))
                    .withClaim("email", payload.getEmail())
                    .withExpiresAt(new Date(System.currentTimeMillis() + 3600 * 1000)) // 1 hora
                    .sign(Algorithm.HMAC256(JWT_SECRET));

            // Crear la respuesta usando tu AuthResponseEntity
            AuthResponseEntity response = new AuthResponseEntity(jwtToken, (String) payload.get("name"));

            // Responder al cliente
            return ResponseEntity.ok().body(response);
        } catch (Exception e) {
            return ResponseEntity.status(401).body("Token inválido");
        }
    }
}