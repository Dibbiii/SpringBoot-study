package com.example.gem_springboot.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import java.util.Date;
import java.util.function.Function;
import javax.crypto.SecretKey;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
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

    // Estrae lo username (subject) dal token
    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    // Metodo generico per estrarre un singolo dato (Claim)
    public <T> T extractClaim(
        String token,
        Function<Claims, T> claimsResolver
    ) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    // Estrae tutto il Payload (il corpo del JSON)
    private Claims extractAllClaims(String token) {
        return Jwts.parser()
            .verifyWith(getSignInKey()) // Usa la stessa chiave per verificare la firma
            .build()
            .parseSignedClaims(token)
            .getPayload();
    }

    // Verifica se il token è valido
    public boolean isTokenValid(String token, UserDetails userDetails) {
        final String username = extractUsername(token);
        // È valido se: lo username coincide E il token non è scaduto
        return (
            (username.equals(userDetails.getUsername())) &&
            !isTokenExpired(token)
        );
    }

    private boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    private Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }
}
