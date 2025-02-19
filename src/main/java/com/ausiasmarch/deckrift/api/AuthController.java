package com.ausiasmarch.deckrift.api;

import org.springframework.web.bind.annotation.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;

import java.util.Map;

import com.ausiasmarch.deckrift.bean.LogindataBean;
import com.ausiasmarch.deckrift.entity.UsuarioEntity;
import com.ausiasmarch.deckrift.repository.UsuarioRepository;
import com.ausiasmarch.deckrift.service.AuthService;
import com.ausiasmarch.deckrift.service.EmailService;
import com.ausiasmarch.deckrift.service.GoogleService;


@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "*", allowedHeaders = "*", maxAge = 3600)
public class AuthController {

    @Autowired
    private EmailService emailService;

    @Autowired
    private GoogleService googleService;

    @Autowired
    private AuthService oAuthService;

    @Autowired
    private UsuarioRepository usuarioRepository;


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


    @PostMapping("/google")
    public ResponseEntity<?> loginWithGoogle(@RequestBody Map<String, String> requestBody) {
        String token = requestBody.get("token");
        return ResponseEntity.ok(googleService.loginWithGoogle(token));

    }

@GetMapping("/verify-email")
    public ResponseEntity<Void> verifyEmail(@RequestParam String email) {
        return emailService.verifyEmail(email);
    }

}
