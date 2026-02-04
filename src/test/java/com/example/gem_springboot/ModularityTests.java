package com.example.gem_springboot;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.modulith.core.ApplicationModules;
import org.springframework.modulith.docs.Documenter;

class ModularityTests {

    @Test
    void verifyModularity() {
        // Definisce l'insieme dei moduli basandosi sulla classe principale
        ApplicationModules modules = ApplicationModules.of(
            GemSpringbootApplication.class
        );

        // Verifica le regole:
        // - Nessun ciclo tra moduli
        // - Nessun accesso a package .internal da altri moduli
        // Risolti i problemi architetturali:
        // - Spostato DuplicateResourceException in shared
        // - Spostato JwtService e JwtAuthenticationFilter in security
        // - Spostato ClientConfig nel modulo posts
        // - Creato UserDetailsServiceImpl per evitare esporre UserRepository
        modules.verify();
    }

    @Test
    void createDocumentation() {
        // Definisce l'insieme dei moduli basandosi sulla classe principale
        ApplicationModules modules = ApplicationModules.of(
            GemSpringbootApplication.class
        );

        // Genera diagrammi C4 (PlantUML) nella cartella target/spring-modulith-docs
        // Utile per vedere visivamente le dipendenze
        new Documenter(modules).writeDocumentation();
    }
}
