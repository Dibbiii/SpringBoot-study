package com.example.gem_springboot.modules.users;

// È un Record immutabile che contiene solo i dati essenziali, NON l'Entity.
// La differenza con UserResponse, è che questo è un messaggio interno verso altri moduli Java, mentre UserResponse è il DTO in uscita verso l'esterno
public record UserCreatedEvent(Long userId, String email, String username) {} 
