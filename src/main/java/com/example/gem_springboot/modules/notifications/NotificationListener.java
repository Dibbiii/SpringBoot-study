package com.example.gem_springboot.modules.notifications;

import com.example.gem_springboot.modules.users.UserCreatedEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.modulith.events.ApplicationModuleListener;
import org.springframework.stereotype.Component;

@Slf4j // Genera automaticamente un campo logger nella classe durante la compilazione -> evita codice boilerplate per la creazione dei logger
@Component
public class NotificationListener {

    // @ApplicationModuleListener è un'annotazione potenziata di Spring Modulith.
    // Garantisce che il listener giri in una transazione separata e (opzionalmente) asincrona.
    @ApplicationModuleListener
    void onUserCreated(UserCreatedEvent event) throws InterruptedException {
        // Simulo un'operazione lenta (es. invio email SMTP)
        // Grazie ai Virtual Threads questo sleep non blocca la CPU
        Thread.sleep(2000);

        log.info(
            "📧 NOTIFICA: Invio email di benvenuto a {} ({}) - ID Utente: {}",
            event.username(),
            event.email(),
            event.userId()
        );
    }
}
