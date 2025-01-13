package com.ausiasmarch.deckrift.entity;

public class AuthResponseEntity {

        private String jwtToken;
        private String name;

        public AuthResponseEntity(String jwtToken, String name) {
            this.jwtToken = jwtToken;
            this.name = name;
        }

        public String getJwtToken() {
            return jwtToken;
        }

        public String getName() {
            return name;
        }
    }

