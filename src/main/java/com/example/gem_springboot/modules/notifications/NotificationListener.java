package com.example.gem_springboot.modules.notifications;

import com.example.gem_springboot.modules.users.UserCreatedEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.modulith.events.ApplicationModuleListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Slf4j // Genera automaticamente un campo logger nella classe durante la compilazione -> evita codice boilerplate per la creazione dei logger
@Component
public class NotificationListener {

    // SimpMessagingTemplate è lo strumento per inviare messaggi WebSocket
    private final SimpMessagingTemplate messagingTemplate;

    public NotificationListener(SimpMessagingTemplate messagingTemplate) {
        this.messagingTemplate = messagingTemplate;
    }

    // @ApplicationModuleListener è un'annotazione potenziata di Spring Modulith.
    // Garantisce che il listener giri in una transazione separata e (opzionalmente) asincrona.
    // @Async rende l'esecuzione veramente asincrona, liberando subito il thread chiamante
    @Async
    @ApplicationModuleListener
    void onUserCreated(UserCreatedEvent event) throws InterruptedException {
        log.info("Ricevuto evento creazione utente: {}", event.email());
        // Simulo un'operazione lenta (es. invio email SMTP)
        // Grazie ai Virtual Threads questo sleep non blocca la CPU
        Thread.sleep(2000);

        // Invio la notifica Real Time a tutti i client connessi
        String message = "Nuovo utente registrato: " + event.username();

        // Spedisco al topic pubblico "/topic/users"
        messagingTemplate.convertAndSend("/topic/users", message);

        log.info("Notifica WebSocket inviata: {}", message);
    }
}
