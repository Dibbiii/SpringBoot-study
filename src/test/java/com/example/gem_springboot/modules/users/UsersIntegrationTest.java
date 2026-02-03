package com.example.gem_springboot.modules.users;

import static org.assertj.core.api.Assertions.assertThat;

import com.example.gem_springboot.IntegrationTestBase;
import com.example.gem_springboot.modules.users.internal.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;

class UsersIntegrationTest extends IntegrationTestBase {

    // INIEZIONE DIRETTA DEL REPO PER PULIZIA
    // Non lo usiamo per testare (usiamo WebTestClient per quello),
    // ma solo per preparare il terreno (Pattern: Test Fixture).
    @Autowired
    private UserRepository userRepository;

    @BeforeEach // Eseguito PRIMA di ogni singolo @Test
    void setUp() {
        // CANCELLA TUTTO.
        // Fondamentale perché il container Docker è condiviso (static)
        // e vogliamo partire da uno stato pulito ("Tabula Rasa").
        userRepository.deleteAll();
    }

    @Test
    void shouldCreateUser_Login_AndGetProfile() {
        // Dati del nuovo utente
        String username = "integrationUser";
        String password = "securePassword123";
        String email = "integration@test.com";

        UserRequest signupRequest = new UserRequest(username, email, password);

        // 1. REGISTRAZIONE
        webTestClient
            .post()
            .uri("/users")
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(signupRequest)
            .exchange()
            .expectStatus()
            .isOk()
            .expectBody()
            .jsonPath("$.username")
            .isEqualTo(username)
            .jsonPath("$.email")
            .isEqualTo(email);

        // 2. LOGIN
        LoginRequest loginRequest = new LoginRequest(username, password);

        String token = webTestClient
            .post()
            .uri("/auth/login")
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(loginRequest)
            .exchange()
            .expectStatus()
            .isOk()
            .returnResult(String.class)
            .getResponseBody()
            .blockFirst();

        assertThat(token).isNotNull().isNotEmpty();

        // 3. ACCESSO PROTETTO
        webTestClient
            .get()
            .uri(
                "/users/" +
                    userRepository.findByUsername(username).get().getId()
            ) // Uso ID dinamico
            .header("Authorization", "Bearer " + token)
            .exchange()
            .expectStatus()
            .isOk()
            .expectBody()
            .jsonPath("$.username")
            .isEqualTo(username);
    }
}
