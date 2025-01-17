package com.ausiasmarch.deckrift.entity;

public class AuthResponseEntity {

        private String jwtToken;
        private String name;
        private Long id;

        public AuthResponseEntity(String jwtToken, String name, Long id) {
            this.jwtToken = jwtToken;
            this.name = name;
            this.id = id;
        }

        public String getJwtToken() {
            return jwtToken;
        }

        public String getName() {
            return name;
        }

        public Long getId() {
            return id;
        }

        public Long setId(Long id) {
            this.id = id;
            return id;
        }
    }

