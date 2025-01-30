package com.ausiasmarch.deckrift.api;
import org.springframework.web.bind.annotation.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;

import java.util.Date;
import java.util.Map;


import com.ausiasmarch.deckrift.entity.AuthResponseEntity;
import com.ausiasmarch.deckrift.entity.TipousuarioEntity;
import com.ausiasmarch.deckrift.entity.UsuarioEntity;
import com.ausiasmarch.deckrift.repository.UsuarioRepository;
import com.ausiasmarch.deckrift.repository.TipoUsuarioRepository;
import com.ausiasmarch.deckrift.service.GoogleTokenVerifierService;


import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "*", allowedHeaders = "*", maxAge = 3600)
public class AuthController {

    @Autowired
    private TipoUsuarioRepository tipousuarioRepository;

    private final GoogleTokenVerifierService googleTokenVerifierService;
    private final UsuarioRepository usuarioRepository;
    private static final String JWT_SECRET = "clave_secreta";

    public AuthController(GoogleTokenVerifierService googleTokenVerifierService, UsuarioRepository usuarioRepository) {
        this.googleTokenVerifierService = googleTokenVerifierService;
        this.usuarioRepository = usuarioRepository;
    }

    @PostMapping("/google")
    public ResponseEntity<?> loginWithGoogle(@RequestBody Map<String, String> requestBody) {
        try {
            // Extraer el token del cuerpo de la solicitud
            String token = requestBody.get("token");

            // Verificar el token
            GoogleIdToken.Payload payload = googleTokenVerifierService.verifyToken(token);

            // Extraer el correo electrónico del payload
            String email = payload.getEmail();

            // Buscar o crear el usuario
            UsuarioEntity usuario = usuarioRepository.findByCorreo(email)
    .orElseGet(() -> {
        UsuarioEntity newUsuario = new UsuarioEntity();
        newUsuario.setNombre((String) payload.get("name"));
        newUsuario.setCorreo(email);
        TipousuarioEntity tipoUsuario = tipousuarioRepository.findById(2L)
            .orElseThrow(() -> new RuntimeException("Tipo de usuario no encontrado"));
        newUsuario.setTipousuario(tipoUsuario);

        return usuarioRepository.save(newUsuario);
    });

            // Generar un JWT
            String jwtToken = JWT.create()
                    .withSubject(email)
                    .withClaim("name", usuario.getNombre())
                    .withClaim("email", usuario.getCorreo())
                    .withExpiresAt(new Date(System.currentTimeMillis() + 3600 * 1000)) // 1 hora
                    .sign(Algorithm.HMAC256(JWT_SECRET));

            // Responder al cliente
            return ResponseEntity.ok(new AuthResponseEntity(jwtToken, usuario.getNombre(), usuario.getId()));


        } catch (RuntimeException e) {
            System.err.println("Error durante el proceso de autenticación:");
            e.printStackTrace();
            return ResponseEntity.status(401).body(e.getMessage());
        } catch (Exception e) {
            System.err.println("Error inesperado:");
            e.printStackTrace();
            return ResponseEntity.status(500).body("Error interno en el servidor.");
        }
    }
}
