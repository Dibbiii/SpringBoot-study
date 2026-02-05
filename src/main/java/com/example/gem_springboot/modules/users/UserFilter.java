package com.example.gem_springboot.modules.users;

import java.time.LocalDateTime;

// Contiene i filtri che arrivano dall'interfaccia
public record UserFilter(
    String username,
    String email,
    LocalDateTime createdAfter, // Per filtrare "utenti creati dopo una data"
    LocalDateTime createdBefore // Per filtrare "utenti creati prima di una data"
) {}