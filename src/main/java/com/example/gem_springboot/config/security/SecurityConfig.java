package com.example.gem_springboot.config.security;

import com.example.gem_springboot.modules.users.internal.UserRepository;
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

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http)
        throws Exception {
        http
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
                    // Permetto a tutti di registrarsi (POST /users)
                    .requestMatchers(HttpMethod.POST, "/users")
                    .permitAll()
                    // Permetto il login
                    .requestMatchers("/auth/login")
                    .permitAll()
                    // Tutto il resto richiede autenticazione
                    .anyRequest()
                    .authenticated()
            );

        return http.build();
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
        return email ->
            userRepository
                .findByEmail(email)
                .orElseThrow(() ->
                    new UsernameNotFoundException("User not found")
                );
    }
}
