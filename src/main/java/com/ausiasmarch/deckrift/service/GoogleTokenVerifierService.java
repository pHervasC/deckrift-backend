package com.ausiasmarch.deckrift.service;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;

import org.springframework.stereotype.Service;
import com.ausiasmarch.deckrift.service.GoogleTokenVerifierService;

import java.util.Collections;

@Service
public class GoogleTokenVerifierService {

    private final GoogleIdTokenVerifier verifier;

    public GoogleTokenVerifierService() {
        JsonFactory jsonFactory = JacksonFactory.getDefaultInstance();
        verifier = new GoogleIdTokenVerifier.Builder(new NetHttpTransport(), jsonFactory)
                .setAudience(Collections.singletonList("642946707903-742gna6lhbktomd5mmk70nj5h4rg02fv.apps.googleusercontent.com"))
                .build();
    }

    public GoogleIdToken.Payload verifyToken(String token) {
        try {
            GoogleIdToken idToken = verifier.verify(token);
            if (idToken != null) {
                System.out.println("✅ Token verificado correctamente.");
                System.out.println("📌 Email: " + idToken.getPayload().getEmail());
                System.out.println("📌 Nombre: " + idToken.getPayload().get("name"));
                return idToken.getPayload();
            } else {
                System.out.println("❌ Token inválido: Verificación fallida.");
                throw new RuntimeException("Token inválido: Verificación fallida.");
            }
        } catch (Exception e) {
            System.out.println("🚨 Error al verificar el token: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Error al verificar el token.", e);
        }
    }
}
