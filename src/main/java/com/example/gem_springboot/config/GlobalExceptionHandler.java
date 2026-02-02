package com.example.gem_springboot.config;

import java.net.URI;
import java.util.stream.Collectors;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

// Componente che ascolta tutti i Controller. Se c'è un errore lo gestisce.
@RestControllerAdvice
public class GlobalExceptionHandler {

    // Errori di Validazione (@Valid fallito)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ProblemDetail handleValidationErrors(
        MethodArgumentNotValidException ex
    ) {
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(
            HttpStatus.BAD_REQUEST,
            "La richiesta contiene parametri non validi"
        );

        problemDetail.setTitle("Validation Error"); // titolo dell'errore
        // URI che identifica il tipo di errore -> dovrebbe puntare a una documentazione che spiega
        // il significato dell'errore, perchè si verifica e come risolverlo
        problemDetail.setType(
            URI.create("https://example.com/errors/validation")
        );
        // Raccogliamo tutti gli errori di campo in una stringa o lista
        String errors = ex
            .getBindingResult()
            .getFieldErrors()
            .stream()
            .map(error -> error.getField() + ": " + error.getDefaultMessage())
            .collect(Collectors.joining(", "));

        problemDetail.setProperty("errors", errors); // Contiene una lista con gli errori

        return problemDetail;
    }

    // Errori Generici (es. RuntimeException lanciate dal Service)
    @ExceptionHandler(RuntimeException.class)
    public ProblemDetail handleRuntimeException(RuntimeException ex) {
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(
            HttpStatus.INTERNAL_SERVER_ERROR,
            ex.getMessage()
        );
        problemDetail.setTitle("Internal Server Error");
        return problemDetail;
    }

    @ExceptionHandler(DuplicateResourceException.class)
    public ProblemDetail handleDuplicateResource(
        DuplicateResourceException ex
    ) {
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(
            HttpStatus.CONFLICT, // 409 Conflict
            ex.getMessage()
        );
        problemDetail.setTitle("Duplicate Resource");
        problemDetail.setType(
            URI.create("https://example.com/errors/duplicate")
        );
        return problemDetail;
    }
}
