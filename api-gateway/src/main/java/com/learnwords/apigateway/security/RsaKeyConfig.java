package com.learnwords.apigateway.security;

import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.Resource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.nio.charset.StandardCharsets;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

@Slf4j
@Configuration
public class RsaKeyConfig {

    @Value("${jwt.private-key-location}")
    private Resource privateKeyRes;

    @Value("${jwt.public-key-location}")
    private Resource publicKeyRes;

    @Bean
    public KeyPair jwtKeyPair() throws Exception {
        PrivateKey privateKey = readPrivateKey(privateKeyRes);
        PublicKey publicKey  = readPublicKey(publicKeyRes);
        return new KeyPair(publicKey, privateKey);
    }

    private PrivateKey readPrivateKey(Resource pem) throws Exception {
        String text = pem.getContentAsString(StandardCharsets.UTF_8)
                .replaceAll("-----BEGIN PRIVATE KEY-----", "")
                .replaceAll("-----END PRIVATE KEY-----", "")
                .replaceAll("\\s", "");
        byte[] der = Base64.getDecoder().decode(text);
        return KeyFactory.getInstance("RSA")
                .generatePrivate(new PKCS8EncodedKeySpec(der));
    }

    private PublicKey readPublicKey(Resource pem) throws Exception {
        String text = pem.getContentAsString(StandardCharsets.UTF_8)
                .replaceAll("-----BEGIN PUBLIC KEY-----", "")
                .replaceAll("-----END PUBLIC KEY-----", "")
                .replaceAll("\\s", "");
        byte[] der = Base64.getDecoder().decode(text);
        return KeyFactory.getInstance("RSA")
                .generatePublic(new X509EncodedKeySpec(der));
    }
}
