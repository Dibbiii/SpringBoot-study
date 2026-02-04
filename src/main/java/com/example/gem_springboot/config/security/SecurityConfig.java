package com.example.gem_springboot.config.security;

import com.example.gem_springboot.modules.users.internal.UserRepository;
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

        // Permetto il frontend in angular
        configuration.setAllowedOrigins(List.of("http://localhost:4200"));

        // Gli do la possibilità di fare queste chiamate
        configuration.setAllowedMethods(
            List.of("GET", "POST", "PUT", "DELETE", "OPTIONS")
        );

        // Definisco quali header sono permessi 
        // Authorization serve per il Bearer Token
        // Content-Type serve per il JSON
        configuration.setAllowedHeaders(
            List.of("Authorization", "Content-Type")
        );

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

    @Bean
    public UserDetailsService userDetailsService(
        UserRepository userRepository
    ) {
        return username ->
            userRepository
                .findByUsername(username)
                .orElseThrow(() ->
                    new UsernameNotFoundException("User not found")
                );
    }
}
