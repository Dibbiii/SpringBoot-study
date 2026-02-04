package com.example.gem_springboot.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final UserDetailsService userDetailsService;

    @Override
    protected void doFilterInternal(
        @NonNull HttpServletRequest request,
        @NonNull HttpServletResponse response,
        @NonNull FilterChain filterChain
    ) throws ServletException, IOException {
        // Cerco l'header Authorization
        final String authHeader = request.getHeader("Authorization");
        final String jwt;
        final String userEmail;

        // Se non c'è l'header o non inizia con "Bearer ", lasciamo passare la richiesta
        // Magari è una richiesta pubblica come il login, ci penserà Spring Security dopo a bloccarla se necessario
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        // Estraggo il token togliendo la parte di Baerer che sono 7 caratteri
        jwt = authHeader.substring(7);

        // Estraggo l'email dal token
        // (Qui potrebbe lanciare eccezione se il token è scaduto o manomesso)
        userEmail = jwtService.extractUsername(jwt);

        // Se ho l'email e l'utente non è già autenticato nel contesto attuale
        if (
            userEmail != null &&
            SecurityContextHolder.getContext().getAuthentication() == null
        ) {
            // Cerco i dettagli dell'utente dal DB
            UserDetails userDetails =
                this.userDetailsService.loadUserByUsername(userEmail);

            // Valido il token
            if (jwtService.isTokenValid(jwt, userDetails)) {
                // Creo l'oggetto di Autenticazione per Spring Security
                UsernamePasswordAuthenticationToken authToken =
                    new UsernamePasswordAuthenticationToken(
                        userDetails,
                        null, // Non abbiamo credenziali (password) da passare qui
                        userDetails.getAuthorities()
                    );

                // Aggiungo dettagli extra (es. indirizzo IP della richiesta)
                authToken.setDetails(
                    new WebAuthenticationDetailsSource().buildDetails(request)
                );

                // Timbro finale: metto l'utente nel ContextHolder
                // Da questo momento in poi l'utente è loggato
                SecurityContextHolder.getContext().setAuthentication(authToken);
            }
        }

        // Continuo con il prossimo filtro della catena
        filterChain.doFilter(request, response);
    }
}
