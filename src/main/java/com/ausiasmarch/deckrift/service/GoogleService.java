package com.ausiasmarch.deckrift.service;

import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;

import org.springframework.stereotype.Service;
import com.ausiasmarch.deckrift.entity.TipousuarioEntity;
import com.ausiasmarch.deckrift.entity.UsuarioEntity;
import com.ausiasmarch.deckrift.repository.TipoUsuarioRepository;
import com.ausiasmarch.deckrift.repository.UsuarioRepository;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

@Service
public class GoogleService {

    private final GoogleIdTokenVerifier verifier;
    private final UsuarioRepository usuarioRepository;
    private final TipoUsuarioRepository tipousuarioRepository;
    private final JWTService jwtService;

    public GoogleService(UsuarioRepository usuarioRepository, TipoUsuarioRepository tipousuarioRepository, JWTService jwtService) {
        this.usuarioRepository = usuarioRepository;
        this.tipousuarioRepository = tipousuarioRepository;
        this.jwtService = jwtService;

        JsonFactory jsonFactory = JacksonFactory.getDefaultInstance();
        verifier = new GoogleIdTokenVerifier.Builder(new NetHttpTransport(), jsonFactory)
                .setAudience(Collections.singletonList("642946707903-742gna6lhbktomd5mmk70nj5h4rg02fv.apps.googleusercontent.com"))
                .build();
    }

    public GoogleIdToken.Payload verifyToken(String token) {
        try {
            GoogleIdToken idToken = verifier.verify(token);
            if (idToken != null) {
                return idToken.getPayload();
            } else {
                throw new RuntimeException("Token inválido: Verificación fallida.");
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Error al verificar el token.", e);
        }
    }

    public Map<String, Object> loginWithGoogle(String token) {
        GoogleIdToken.Payload payload = verifyToken(token);
        if (payload == null) {
            throw new RuntimeException("Token inválido");
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

        Map<String, Object> response = new HashMap<>();
        response.put("token", jwtToken);
        response.put("name", usuario.getNombre());
        response.put("id", usuario.getId());
        response.put("correo", usuario.getCorreo());
        response.put("tipoUsuario", usuario.getTipousuario().getId());

        return response;
    }
}
