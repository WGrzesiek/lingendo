// src/main/java/com/learnwords/userservice/controller/JwksController.java
package com.learnwords.userservice.controller;

import com.nimbusds.jose.jwk.RSAKey;
import com.nimbusds.jose.jwk.JWKSet;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
class JwksController {

    private final JWKSet jwkSet;

    JwksController(RSAKey rsaKey) {
        this.jwkSet = new JWKSet(rsaKey.toPublicJWK()); // tylko publiczne
    }

    @GetMapping("/.well-known/jwks.json")
    public String keys() {
        return jwkSet.toJSONObject().toString();
    }
}
