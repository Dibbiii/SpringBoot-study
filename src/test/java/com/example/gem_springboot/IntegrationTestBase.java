package com.example.gem_springboot;

import com.redis.testcontainers.RedisContainer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.boot.webtestclient.autoconfigure.AutoConfigureWebTestClient;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.utility.DockerImageName;

// @SpringBootTest(webEnvironment = RANDOM_PORT): Avvia il server vero su una porta casuale
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureWebTestClient
@ActiveProfiles("test")
public abstract class IntegrationTestBase {

    @Autowired
    protected WebTestClient webTestClient;

    //Configurazione Postgres (Automatica con @ServiceConnection)
    @ServiceConnection
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>(
        "postgres:16-alpine"
    );

    // Configurazione Redis (Automatica con @ServiceConnection)
    @ServiceConnection
    static RedisContainer redis = new RedisContainer(
        DockerImageName.parse("redis:alpine")
    );

    static {
        postgres.start();
        redis.start();
    }
}
