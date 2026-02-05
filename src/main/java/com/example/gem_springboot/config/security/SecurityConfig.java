package com.example.gem_springboot.config.security;

import com.example.gem_springboot.security.JwtAuthenticationFilter;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(
        HttpSecurity http,
        JwtAuthenticationFilter jwtAuthFilter
    ) throws Exception {
        http
            // Abilito il Cors
            // Spring cercherà un bean chiamato "corsConfigurationSource" (definito sotto)
            .cors(cors -> cors.configurationSource(corsConfigurationSource()))
            // Disabilito CSRF (Cross-Site Request Forgery)
            // Perché nelle API REST Stateless non serve e bloccherebbe le POST da Postman.
            .csrf(AbstractHttpConfigurer::disable)
            // Permetti gli header necessari per WebSocket upgrade
            .headers(headers ->
                headers.frameOptions(frame -> frame.sameOrigin())
            )
            // Stateless Session Management
            // Dico a Spring di non creare mai una HttpSession in RAM
            // Voglio che ogni richiesta sia indipendente (usando poi il JWT).
            .sessionManagement(customizer ->
                customizer.sessionCreationPolicy(
                    SessionCreationPolicy.STATELESS
                )
            )
            // Regole di Accesso (Authorization)
            .authorizeHttpRequests(auth ->
                auth
                    // Permetto OPTIONS (Preflight) esplicitamente a tutti, anche se CORS lo gestisce
                    // è buona norma di difesa in profondità
                    .requestMatchers(HttpMethod.OPTIONS, "/**")
                    .permitAll()
                    // Permetto a tutti di registrarsi (POST /users)
                    .requestMatchers(HttpMethod.POST, "/users")
                    .permitAll()
                    // Permetto il login
                    .requestMatchers("/auth/login")
                    .permitAll()
                    // Permetto accesso agli endpoint di Actuator (Prometheus, Health, ecc.)
                    .requestMatchers("/actuator/**")
                    .permitAll()
                    // Permetto la connessione WebSocket
                    .requestMatchers("/ws-gem/**")
                    .permitAll()
                    // Tutto il resto richiede autenticazione
                    .anyRequest()
                    .authenticated()
            )
            .addFilterBefore(
                jwtAuthFilter,
                UsernamePasswordAuthenticationFilter.class
            );

        return http.build();
    }

    // Definisco le policy cors
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();

        // Permetto il frontend in angular e anche localhost diretto per i test
        configuration.setAllowedOrigins(
            List.of(
                "http://localhost:4200",
                "http://localhost:8080",
                "http://127.0.0.1:8080"
            )
        );

        // Permetto anche file:// per i test HTML locali
        configuration.setAllowedOriginPatterns(List.of("*"));

        // Gli do la possibilità di fare queste chiamate
        configuration.setAllowedMethods(
            List.of("GET", "POST", "PUT", "DELETE", "OPTIONS", "HEAD")
        );

        // Definisco quali header sono permessi
        // Authorization serve per il Bearer Token
        // Content-Type serve per il JSON
        // Header WebSocket necessari per l'upgrade
        configuration.setAllowedHeaders(
            List.of(
                "Authorization",
                "Content-Type",
                "Upgrade",
                "Connection",
                "Sec-WebSocket-Key",
                "Sec-WebSocket-Version",
                "Sec-WebSocket-Extensions",
                "Sec-WebSocket-Accept",
                "Sec-WebSocket-Protocol"
            )
        );

        // Permetti credentials (importante per CORS con WebSocket)
        configuration.setAllowCredentials(true);

        // Applico queste regole a TUTTI gli endpoint (/**)
        UrlBasedCorsConfigurationSource source =
            new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

    // Uso BCrypt come algoritmo di hashing per le password
    // Spring lo userà automaticamente quando dovrà verificare le password
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(
        AuthenticationConfiguration config
    ) throws Exception {
        return config.getAuthenticationManager();
    }

    // UserDetailsService è ora fornito da UserDetailsServiceImpl nel modulo users
    // Non serve più definirlo qui, Spring lo troverà automaticamente tramite @Service
}
