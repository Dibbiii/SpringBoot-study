package com.example.gem_springboot.config;

import com.example.gem_springboot.modules.posts.PostClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.support.WebClientAdapter;
import org.springframework.web.service.invoker.HttpServiceProxyFactory;

@Configuration
public class ClientConfig {

    // Bean necessario quando si usa WebFlux in un'app MVC
    // Spring Boot non lo crea automaticamente in questa configurazione
    @Bean
    WebClient.Builder webClientBuilder() {
        return WebClient.builder();
    }

    @Bean
    PostClient postClient(WebClient.Builder builder) {
        // Configuro il WebClient base con l'URL del servizio esterno
        // (JsonPlaceholder è un'API fake gratuita per test)
        WebClient webClient = builder
            .baseUrl("https://jsonplaceholder.typicodeSbagliatoTest.com")
            .build();

        // Creo l'adattatore che fa da ponte tra WebClient e le interfacce dichiarative
        WebClientAdapter adapter = WebClientAdapter.create(webClient);

        // Creo la Factory
        HttpServiceProxyFactory factory = HttpServiceProxyFactory.builderFor(
            adapter
        ).build();

        // Genero e restituisco il Bean di PostClient
        return factory.createClient(PostClient.class);
    }
}
