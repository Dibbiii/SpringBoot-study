package com.example.gem_springboot;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.context.ApplicationContext;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

// @SpringBootTest(webEnvironment = RANDOM_PORT): Avvia il server vero su una porta casuale
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Testcontainers
public abstract class IntegrationTestBase {

    @Container
    @ServiceConnection
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>(
        "postgres:16-alpine"
    );

    @LocalServerPort
    private int port;

    @Autowired
    private ApplicationContext applicationContext;

    protected WebTestClient webTestClient;

    @Autowired
    public void setWebTestClient(ApplicationContext applicationContext) {
        // Configura WebTestClient per applicazioni MVC usando bindToServer
        // per fare vere chiamate HTTP al server in esecuzione
        this.webTestClient = WebTestClient.bindToServer()
            .baseUrl("http://localhost:" + port)
            .build();
    }

    // Qui potremmo aggiungere metodi di utility per i test, es. pulire il DB
}
