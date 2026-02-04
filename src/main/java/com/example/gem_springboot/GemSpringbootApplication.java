package com.example.gem_springboot;

import com.example.gem_springboot.config.CourseProperties;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.context.annotation.Bean;
import org.springframework.retry.annotation.EnableRetry;

@SpringBootApplication
@ConfigurationPropertiesScan
@EnableRetry
public class GemSpringbootApplication {

    public static void main(String[] args) {
        SpringApplication.run(GemSpringbootApplication.class, args);

        // Verifica rapida: Controlliamo se i Virtual Threads sono attivi nella JVM
        System.out.println(
            "Virtual Threads supportati? " +
                Thread.ofVirtual()
                    .factory()
                    .newThread(() -> {})
                    .isVirtual()
        );
    }

    // @Bean: Insegna a Spring come creare un nuovo oggetto da mettere nel Container.
    // CommandLineRunner: È un Bean speciale che dice "Eseguimi appena hai finito l'avvio".
    @Bean
    public CommandLineRunner demoConfig(CourseProperties props) {
        return args -> {
            System.out.println("-------------------------------------------");
            System.out.println("CONFIGURAZIONE CARICATA CON SUCCESSO");
            System.out.println("Nome Corso: " + props.name());
            System.out.println("Messaggio:  " + props.welcomeMessage());
            System.out.println("Studenti Max: " + props.maxStudents());
            System.out.println("Difficoltà: " + props.difficultyLevel());
            System.out.println("Funzionalità:");
            // Stream API per stampare la lista
            props.features().forEach(f -> System.out.println("   * " + f));
            System.out.println("-------------------------------------------");
        };
    }
}
