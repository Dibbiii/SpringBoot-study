package com.example.gem_springboot.modules.users;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

// DTO di input dal frontend -> contiene i dati inviati dall'utente
public record UserRequest(
    @NotBlank(message = "Lo username non può essere vuoto")
    @Size(
        min = 3,
        max = 50,
        message = "Lo username deve avere tra 3 e 50 caratteri"
    )
    String username,

    @NotBlank(message = "L'email è obbligatoria")
    @Email(message = "Il formato dell'email non è valido")
    String email,

    @NotBlank(message = "La password è obbligatoria")
    @Size(min = 8, message = "La password deve avere almeno 8 caratteri")
    String password
) {}
