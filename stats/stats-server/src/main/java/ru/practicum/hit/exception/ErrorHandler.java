package ru.practicum.hit.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.Map;

@Slf4j
@RestControllerAdvice
@Validated
public class ErrorHandler {
    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Map<String, String> handleRequestException(final RequestException e) {
        log.error("400 {}", e.getMessage());
        return Map.of("status", "400 - BAD_REQUEST",
                "reason", "Incorrectly made request.",
                "errorMessage", e.getMessage(),
                "timestamp", LocalDateTime.now().toString()
        );
    }
}
