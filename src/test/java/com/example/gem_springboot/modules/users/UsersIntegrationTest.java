package com.example.gem_springboot.modules.users;

import static org.assertj.core.api.Assertions.assertThat;

import com.example.gem_springboot.IntegrationTestBase;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;

// Estendiamo la base così abbiamo il DB Docker già pronto
class UsersIntegrationTest extends IntegrationTestBase {

    @Autowired
    private WebTestClient webTestClient; // Client HTTP per fare le chiamate al nostro server di test

    @Test
    void shouldCreateUser_Login_AndGetProfile() {
        // Dati del nuovo utente
        String username = "integrationUser";
        String password = "securePassword123";
        String email = "integration@test.com";

        UserRequest signupRequest = new UserRequest(username, email, password);

        // CHIAMATA 1: Registrazione (POST /users)
        webTestClient
            .post()
            .uri("/users")
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(signupRequest)
            .exchange() // Esegui la richiesta
            .expectStatus()
            .isOk() // Mi aspetto 200 OK
            .expectBody()
            .jsonPath("$.username")
            .isEqualTo(username) // Verifico il JSON di risposta
            .jsonPath("$.email")
            .isEqualTo(email);

        // CHIAMATA 2: Login (POST /auth/login)
        LoginRequest loginRequest = new LoginRequest(username, password);

        String token = webTestClient
            .post()
            .uri("/auth/login")
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(loginRequest)
            .exchange()
            .expectStatus()
            .isOk()
            .returnResult(String.class) // Prendo il body come stringa (il token)
            .getResponseBody()
            .blockFirst();

        assertThat(token).isNotNull().isNotEmpty(); // Verifico che il token esista

        // CHIAMATA 3: Accesso Protetto (GET /users o un endpoint protetto)
        // Nota: Assumiamo che GET /users sia protetto.
        webTestClient
            .get()
            .uri("/users")
            .header("Authorization", "Bearer " + token) // Uso il token
            .exchange()
            .expectStatus()
            .isOk(); // Se il token funziona, devo ricevere 200 OK, non 403
    }
}
