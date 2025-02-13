package com.ausiasmarch.deckrift.api;

import org.springframework.web.bind.annotation.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.HashMap;
import java.util.Map;

import com.ausiasmarch.deckrift.bean.LogindataBean;
import com.ausiasmarch.deckrift.entity.TipousuarioEntity;
import com.ausiasmarch.deckrift.entity.UsuarioEntity;
import com.ausiasmarch.deckrift.repository.UsuarioRepository;
import com.ausiasmarch.deckrift.repository.TipoUsuarioRepository;
import com.ausiasmarch.deckrift.service.AuthService;
import com.ausiasmarch.deckrift.service.GoogleTokenVerifierService;
import com.ausiasmarch.deckrift.service.JWTService;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "*", allowedHeaders = "*", maxAge = 3600)
public class AuthController {

    @Autowired
    private TipoUsuarioRepository tipousuarioRepository;

    @Autowired
    AuthService oAuthService;

    @Autowired
    JWTService jwtService;

    private final GoogleTokenVerifierService googleTokenVerifierService;
    private final UsuarioRepository usuarioRepository;

    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestBody LogindataBean oLogindataBean) {
        UsuarioEntity usuario = usuarioRepository.findByCorreo(oLogindataBean.getCorreo())
            .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
    
        if (!usuario.isEmailVerified()) {
            return ResponseEntity.status(401).body("⚠️ Debes verificar tu email antes de iniciar sesión.");
        }
    
        if (oAuthService.checkLogin(oLogindataBean)) {
            return ResponseEntity.ok("\"" + oAuthService.getToken(oLogindataBean.getCorreo()) + "\"");
        } else {
            return ResponseEntity.status(401).body("\"" + "Error de autenticación" + "\"");
        }
    }

    public AuthController(GoogleTokenVerifierService googleTokenVerifierService, UsuarioRepository usuarioRepository) {
        this.googleTokenVerifierService = googleTokenVerifierService;
        this.usuarioRepository = usuarioRepository;
    }

    @PostMapping("/google")
public ResponseEntity<?> loginWithGoogle(@RequestBody Map<String, String> requestBody) {
    try {
        String token = requestBody.get("token");
        GoogleIdToken.Payload payload = googleTokenVerifierService.verifyToken(token);
        if (payload == null) {
            return ResponseEntity.status(401).body("Token inválido");
        }

        String correo = payload.getEmail();
        UsuarioEntity usuario = usuarioRepository.findByCorreo(correo)
                .orElseGet(() -> {
                    UsuarioEntity newUsuario = new UsuarioEntity();
                    newUsuario.setNombre((String) payload.get("name"));
                    newUsuario.setCorreo(correo);
                    TipousuarioEntity tipoUsuario = tipousuarioRepository.findById(2L)
                            .orElseThrow(() -> new RuntimeException("Tipo de usuario no encontrado"));
                    newUsuario.setTipousuario(tipoUsuario);
                    newUsuario.setEmailVerified(true);
                    return usuarioRepository.save(newUsuario);
                });

        Map<String, String> claims = new HashMap<>();
        claims.put("correo", usuario.getCorreo());
        claims.put("tipoUsuario", String.valueOf(usuario.getTipousuario().getId()));

        String jwtToken = jwtService.generateToken(claims);

        return ResponseEntity.ok(Map.of(
                "token", jwtToken,
                "name", usuario.getNombre(),
                "id", usuario.getId(),
                "correo", usuario.getCorreo(),
                "tipoUsuario", usuario.getTipousuario().getId()
        ));

    } catch (Exception e) {
        return ResponseEntity.status(500).body("Error interno en el servidor.");
    }
}


@GetMapping("/verify-email")
public ResponseEntity<Void> verifyEmail(@RequestParam String email) {
    UsuarioEntity usuario = usuarioRepository.findByCorreo(email)
        .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

    if (usuario.isEmailVerified()) {
        return ResponseEntity.status(HttpStatus.FOUND)
                .header("Location", "http://localhost:4200/verified")
                .build();
    }

    usuario.setEmailVerified(true);
    usuarioRepository.save(usuario);

    return ResponseEntity.status(HttpStatus.FOUND)
            .header("Location", "http://localhost:4200/verified")
            .build();
}



}
