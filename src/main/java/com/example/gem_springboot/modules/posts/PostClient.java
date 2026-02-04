package com.example.gem_springboot.modules.posts;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.service.annotation.GetExchange;
import org.springframework.web.service.annotation.HttpExchange;
import java.util.List;

// @HttpExchange: Definisce che questa interfaccia è un client HTTP.
// "/posts" è il path base che verrà aggiunto all'URL del server.
@HttpExchange("/posts")
public interface PostClient {

    // @GetExchange: Mappa una chiamata GET HTTP.
    // Spring converte automaticamente il JSON di risposta in List<PostDTO>.
    // Nota come non c'è nessuna implementazione, ma solo la firma.
    @GetExchange
    List<PostDTO> getAllPosts();

    // Supporta path variables e query params come un Controller!
    @GetExchange("?userId={userId}")
    List<PostDTO> getPostsByUser(@PathVariable("userId") Long userId);
}