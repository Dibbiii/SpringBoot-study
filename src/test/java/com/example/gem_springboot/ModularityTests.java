package com.example.gem_springboot;

import org.junit.jupiter.api.Test;
import org.springframework.modulith.core.ApplicationModules;
import org.springframework.modulith.docs.Documenter;

class ModularityTests {

    // Definisce l'insieme dei moduli basandosi sulla classe principale
    ApplicationModules modules = ApplicationModules.of(GemSpringbootApplication.class);

    @Test
    void verifyModularity() {
        // Verifica le regole:
        // - Nessun ciclo tra moduli
        // - Nessun accesso a package .internal da altri moduli
        modules.verify();
    }

    @Test
    void createDocumentation() {
        // Genera diagrammi C4 (PlantUML) nella cartella target/spring-modulith-docs
        // Utile per vedere visivamente le dipendenze
        new Documenter(modules).writeDocumentation();
    }
}