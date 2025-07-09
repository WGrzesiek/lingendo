package com.learnwords.userservice.controller;

import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.jwk.KeyUse;
import com.nimbusds.jose.jwk.RSAKey;
import com.nimbusds.jose.jwk.JWKSet;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.KeyPair;
import java.security.interfaces.RSAPublicKey;
import java.util.UUID;

@RestController
class JwksController {

    private final KeyPair keyPair;
    private final String  jwksJson;

    JwksController(KeyPair keyPair) {
        this.keyPair = keyPair;

        RSAKey jwk = new RSAKey.Builder((RSAPublicKey) keyPair.getPublic())
                .keyID(UUID.randomUUID().toString())
                .algorithm(JWSAlgorithm.RS256)
                .keyUse(KeyUse.SIGNATURE)
                .build();

        this.jwksJson = new JWKSet(jwk).toString();
    }

    @GetMapping(value = "/.well-known/jwks.json", produces = "application/json")
//    @GetMapping(value = "/.well-known/jwks.json")
    public String keys() {
        return jwksJson;
    }
}

