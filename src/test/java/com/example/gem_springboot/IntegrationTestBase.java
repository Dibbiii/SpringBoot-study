package com.example.gem_springboot;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.utility.DockerImageName;

// @SpringBootTest(webEnvironment = RANDOM_PORT): Avvia il server vero su una porta casuale
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
public abstract class IntegrationTestBase {

    // Singleton Pattern per il container PostgreSQL
    // Garantisce che UN SOLO container venga creato e condiviso tra tutti i test
    private static final PostgreSQLContainer<?> postgres;

    static {
        postgres = new PostgreSQLContainer<>(
            DockerImageName.parse("postgres:16-alpine")
        )
            .withDatabaseName("testdb")
            .withUsername("test")
            .withPassword("test");
        postgres.start();
    }

    // @DynamicPropertySource configura dinamicamente le properties di Spring
    // con i valori del container (porta, host, etc.)
    // Questo approccio è più affidabile di @ServiceConnection per il riutilizzo del contesto
    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
        registry.add("spring.liquibase.url", postgres::getJdbcUrl);
        registry.add("spring.liquibase.user", postgres::getUsername);
        registry.add("spring.liquibase.password", postgres::getPassword);
    }

    @LocalServerPort
    private int port;

    @Autowired
    private ApplicationContext applicationContext;

    protected WebTestClient webTestClient;

    @Autowired
    public void setWebTestClient(ApplicationContext applicationContext) {
        // Configura WebTestClient per applicazioni MVC usando bindToServer
        // per fare vere chiamate HTTP al server in esecuzione
        // Aumenta il timeout a 10 secondi per gestire eventi lenti (es. NotificationListener con sleep di 2s)
        this.webTestClient = WebTestClient.bindToServer()
            .baseUrl("http://localhost:" + port)
            .responseTimeout(java.time.Duration.ofSeconds(10))
            .build();
    }

    // Qui potremmo aggiungere metodi di utility per i test, es. pulire il DB
}
