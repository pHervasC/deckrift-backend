package com.ausiasmarch.deckrift.api;

import org.springframework.web.bind.annotation.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;

import java.util.Date;
import java.util.Map;

import com.ausiasmarch.deckrift.bean.LogindataBean;
import com.ausiasmarch.deckrift.entity.AuthResponseEntity;
import com.ausiasmarch.deckrift.entity.TipousuarioEntity;
import com.ausiasmarch.deckrift.entity.UsuarioEntity;
import com.ausiasmarch.deckrift.repository.UsuarioRepository;
import com.ausiasmarch.deckrift.repository.TipoUsuarioRepository;
import com.ausiasmarch.deckrift.service.AuthService;
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

    @Autowired
    AuthService oAuthService;

    private final GoogleTokenVerifierService googleTokenVerifierService;
    private final UsuarioRepository usuarioRepository;
    private static final String JWT_SECRET = "clave_secreta";

    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestBody LogindataBean oLogindataBean) {
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

                    return usuarioRepository.save(newUsuario);
                });

        String jwtToken = JWT.create()
                .withSubject(usuario.getCorreo())  // 📌 Ahora usa el mismo "sub" para ambos métodos
                .withClaim("correo", usuario.getCorreo())
                .withClaim("tipoUsuario", usuario.getTipousuario().getId())
                .withExpiresAt(new Date(System.currentTimeMillis() + 3600 * 1000))
                .sign(Algorithm.HMAC256(JWT_SECRET));

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

}
