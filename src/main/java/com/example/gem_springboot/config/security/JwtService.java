package com.example.gem_springboot.config.security;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import java.util.Date;
import javax.crypto.SecretKey;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class JwtService {

    @Value("${security.jwt.secret-key}")
    private String secretKey;

    @Value("${security.jwt.expiration-time}")
    private Long jwtExpiration;

    public String generateToken(String username) {
        return Jwts.builder()
            .subject(username) // A chi è intestato il token?
            .issuedAt(new Date(System.currentTimeMillis())) // Quando è stato creato?
            .expiration(new Date(System.currentTimeMillis() + jwtExpiration)) // Quando scade?
            .signWith(getSignInKey()) // Firma con la chiave segreta
            .compact(); // Chiudi il pacchetto e fallo diventare una stringa
    }

    // Trasforma la stringa della chiave in un oggetto crittografico SecretKey
    private SecretKey getSignInKey() {
        // Decodifica la stringa Base64 in byte reali
        byte[] keyBytes = Decoders.BASE64.decode(secretKey);
        // Usa i byte decodificati per generare la chiave
        return Keys.hmacShaKeyFor(keyBytes);
    }
}
