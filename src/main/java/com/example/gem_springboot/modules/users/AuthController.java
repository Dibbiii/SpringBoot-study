package com.example.gem_springboot.modules.users;

import com.example.gem_springboot.config.security.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;

    @PostMapping("/login")
    public String login(@RequestBody LoginRequest request) {
        // Tenta l'autenticazione (Spring controlla password criptata vs DB)
        // Se fallisce lancia automaticamente un'eccezione (403/401)
        authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(request.email(), request.password())
        );

        // Se siamo qui la password è giusta -> Generiamo il token.
        // Nota: stiamo usando l'email come subject del token
        return jwtService.generateToken(request.email());
    }
}