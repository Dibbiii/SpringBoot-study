package com.example.gem_springboot.modules.posts;

import java.util.Collections;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Recover;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.cache.annotation.Cacheable;

@Slf4j
@Service
@RequiredArgsConstructor
public class PostService {

    private final PostClient postClient;

    // @Cacheable
    // value = "posts": È il nome della "cartella" in Redis dove metteremo i dati.
    // key = "#userId": La chiave univoca. Se chiedo userId=1, cerco "posts::1".
    // Se Spring trova i dati in Redis, salta l'esecuzione di getPostsWithRetry.
    @Cacheable(value = "posts", key = "#userId")
    // @Retryable
    // value = Exception.class: Riprova per qualsiasi eccezione (si può fare anche più specifico)
    // backoff: Aspetta 1 secondo tra un tentativo e l'altro
    @Retryable(
        retryFor = Exception.class,
        maxAttempts = 3,
        backoff = @Backoff(delay = 1000)
    )
    public List<PostDTO> getPostsWithRetry(Long userId) {
        log.info("Tentativo di recupero post per user: {}", userId);
        return postClient.getPostsByUser(userId);
    }

    // @Recover: Metodo di fallback (Piano B)
    // Se dopo 3 tentativi fallisce ancora, viene eseguito questo metodo
    // invece di lanciare l'errore al frontend.
    @Recover
    public List<PostDTO> recoverPosts(Exception e, Long userId) {
        log.error(
            "Impossibile recuperare i post dopo 3 tentativi. Errore: {}",
            e.getMessage()
        );
        // Restituisco una lista vuota invece di spaccare la pagina Angular
        return Collections.emptyList();
    }
}
