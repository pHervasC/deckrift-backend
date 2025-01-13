package com.ausiasmarch.deckrift.service;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;

import org.springframework.stereotype.Service;

import java.util.Collections;
@Service
public class GoogleTokenVerifierService {
    private final GoogleIdTokenVerifier verifier;

    public GoogleTokenVerifierService() {
        this.verifier = new GoogleIdTokenVerifier.Builder(
                new NetHttpTransport(),
                JacksonFactory.getDefaultInstance())
                .setAudience(Collections.singletonList("TU_CLIENT_ID"))
                .build();
    }

    public GoogleIdToken.Payload verifyToken(String token) throws Exception {
        GoogleIdToken idToken = verifier.verify(token);
        if (idToken != null) {
            return idToken.getPayload();
        } else {
            throw new IllegalArgumentException("Invalid ID token.");
        }
    }
}
