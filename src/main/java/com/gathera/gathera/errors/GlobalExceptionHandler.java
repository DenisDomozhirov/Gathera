package com.gathera.gathera.errors;

import jakarta.persistence.EntityNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.time.LocalDateTime;
import java.util.stream.Collectors;

@ControllerAdvice
public class GlobalExceptionHandler {
    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler({
            MethodArgumentNotValidException.class,
            IllegalArgumentException.class
    })
    public ResponseEntity<ServerDto> handleValidationException(
            Exception e
    ){
        log.error("Get validation exception", e);

        String defaultMessage = e instanceof MethodArgumentNotValidException ?
                constructMethodArgumentNotValidException((MethodArgumentNotValidException) e) : e.getMessage();

        var errorMessage = new ServerDto(
                "Ошибка при валидации запроса",
                defaultMessage,
                LocalDateTime.now()
        );

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(errorMessage);
    }

    @ExceptionHandler
    public ResponseEntity<ServerDto> handleNotFoundException(
            Exception e
    ){
        log.error("Server error", e);
        var serverError = new ServerDto(
                "Server error",
                e.getMessage(),
                LocalDateTime.now()
        );
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(serverError);
    }

    @ExceptionHandler(
            EntityNotFoundException.class
    )
    public ResponseEntity<ServerDto> handleGenericException(
            EntityNotFoundException e
    ){
        log.error("Entity not found, server error", e);
        var serverError = new ServerDto(
                "Server error",
                e.getMessage(),
                LocalDateTime.now()
        );
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(serverError);
    }


    private static String constructMethodArgumentNotValidException(
            MethodArgumentNotValidException e
    ){
        return e.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(error -> error.getField() + " " + error.getDefaultMessage())
                .collect(Collectors.joining(", "));
    }

}
