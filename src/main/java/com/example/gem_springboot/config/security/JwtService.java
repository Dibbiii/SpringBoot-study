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
        byte[] keyBytes = Decoders.BASE64.decode(secretKey); // O usa .getBytes() se non è Base64
        // Per semplicità nel corso usiamo getBytes se la chiave nel yaml è testo semplice:
        // return Keys.hmacShaKeyFor(secretKey.getBytes());
        // Ma per fare i professionisti, usiamo una chiave decodificata correttamente se fosse Base64.
        // Visto che nel yaml abbiamo messo testo semplice, usiamo questo trucco sicuro:
        return Keys.hmacShaKeyFor(secretKey.getBytes());
    }
}
