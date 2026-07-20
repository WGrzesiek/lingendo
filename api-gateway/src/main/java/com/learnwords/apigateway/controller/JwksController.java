package com.learnwords.apigateway.controller;

import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.jwk.KeyUse;
import com.nimbusds.jose.jwk.RSAKey;
import com.nimbusds.jose.jwk.JWKSet;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.KeyPair;
import java.security.interfaces.RSAPublicKey;
import java.util.UUID;

@RestController
class JwksController {

    private final KeyPair keyPair;
    private final String  jwksJson;

    JwksController(KeyPair keyPair, @Value("${security.jwt.kid:gateway-kid}") String kid) {
        this.keyPair = keyPair;

        RSAKey jwk = new RSAKey.Builder((RSAPublicKey) keyPair.getPublic())
                .keyID(kid)
                .algorithm(JWSAlgorithm.RS256)
                .keyUse(KeyUse.SIGNATURE)
                .build();

        this.jwksJson = new JWKSet(jwk).toString();
    }

    @GetMapping(value = "/.well-known/jwks.json", produces = "application/json")
    public String keys() {
        return jwksJson;
    }
}
