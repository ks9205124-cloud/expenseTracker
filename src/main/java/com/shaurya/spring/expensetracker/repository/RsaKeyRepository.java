package com.shaurya.spring.expensetracker.repository;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;
import java.util.List;
import java.util.UUID;

@Repository
public class RsaKeyRepository {

    private final JdbcTemplate jdbcTemplate;

    public RsaKeyRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public static class PersistedRsaKey {
        private final String id;
        private final RSAPublicKey publicKey;
        private final RSAPrivateKey privateKey;

        public PersistedRsaKey(String id, RSAPublicKey publicKey, RSAPrivateKey privateKey) {
            this.id = id;
            this.publicKey = publicKey;
            this.privateKey = privateKey;
        }

        public String getId() { return id; }
        public RSAPublicKey getPublicKey() { return publicKey; }
        public RSAPrivateKey getPrivateKey() { return privateKey; }
    }

    public PersistedRsaKey findActiveKey() {
        String sql = "SELECT id, key_id, public_key, private_key FROM key_pair_store WHERE is_active = 1 ORDER BY created_at DESC LIMIT 1";
        List<PersistedRsaKey> keys = jdbcTemplate.query(sql, (rs, rowNum) -> {
            try {
                String id = rs.getString("key_id"); // or rs.getString("id") depending on which acts as your unique identifier
                byte[] pubBytes = Base64.getDecoder().decode(rs.getString("public_key"));
                byte[] privBytes = Base64.getDecoder().decode(rs.getString("private_key"));

                KeyFactory keyFactory = KeyFactory.getInstance("RSA");
                RSAPublicKey publicKey = (RSAPublicKey) keyFactory.generatePublic(new X509EncodedKeySpec(pubBytes));
                RSAPrivateKey privateKey = (RSAPrivateKey) keyFactory.generatePrivate(new PKCS8EncodedKeySpec(privBytes));

                return new PersistedRsaKey(id, publicKey, privateKey);
            } catch (Exception e) {
                throw new IllegalStateException("Failed to parse RSA key pair from database", e);
            }
        });

        if (!keys.isEmpty()) {
            return keys.get(0);
        }

        // If no active key exists in DB, generate and persist one
        return generateAndSaveKey();
    }

    private PersistedRsaKey generateAndSaveKey() {
        try {
            KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");
            keyPairGenerator.initialize(2048);
            KeyPair keyPair = keyPairGenerator.generateKeyPair();

            RSAPublicKey publicKey = (RSAPublicKey) keyPair.getPublic();
            RSAPrivateKey privateKey = (RSAPrivateKey) keyPair.getPrivate();

            String id = UUID.randomUUID().toString();
            String pubBase64 = Base64.getEncoder().encodeToString(publicKey.getEncoded());
            String privBase64 = Base64.getEncoder().encodeToString(privateKey.getEncoded());

            // Make sure your table columns include id, key_id, public_key, private_key, is_active, and created_at
            jdbcTemplate.update(
                    "INSERT INTO key_pair_store (id, key_id, public_key, private_key, is_active) VALUES (?, ?, ?, ?, 1)",
                    UUID.randomUUID().toString(), id, pubBase64, privBase64
            );

            return new PersistedRsaKey(id, publicKey, privateKey);
        } catch (Exception e) {
            throw new IllegalStateException("Failed to generate and store RSA key pair", e);
        }
    }
}