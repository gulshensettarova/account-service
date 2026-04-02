package com.mybank.account_service.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import jakarta.persistence.OptimisticLockException;
import lombok.Builder;
import lombok.Getter;
import java.time.LocalDateTime;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @Getter
    @Builder
    static class ErrorResponse {
        private String errorCode;
        private String message;
        private LocalDateTime timestamp;
    }

    @ExceptionHandler(BankException.class)
    public ResponseEntity<ErrorResponse> handleBankException(
            BankException ex) {

        return ResponseEntity
                .status(ex.getHttpStatus())
                .body(ErrorResponse.builder()
                        .errorCode(ex.getErrorCode())
                        .message(ex.getMessage())
                        .timestamp(LocalDateTime.now())
                        .build());
    }

    // Optimistic Lock — iki nəfər eyni anda dəyişdi
    @ExceptionHandler(OptimisticLockException.class)
    public ResponseEntity<ErrorResponse> handleOptimisticLock(
            OptimisticLockException ex) {

        return ResponseEntity
                .status(HttpStatus.CONFLICT)
                .body(ErrorResponse.builder()
                        .errorCode("CONCURRENT_UPDATE")
                        .message("Məlumatlar dəyişib. Yenidən cəhd edin.")
                        .timestamp(LocalDateTime.now())
                        .build());
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGeneral(
            Exception ex) {

        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ErrorResponse.builder()
                        .errorCode("INTERNAL_ERROR")
                        .message("Sistem xətası baş verdi")
                        .timestamp(LocalDateTime.now())
                        .build());
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ErrorResponse> handleJsonError(Exception ex) {
        return ResponseEntity
                .badRequest()
                .body(ErrorResponse.builder()
                        .errorCode("INVALID_JSON")
                        .message("Request düzgün formatda deyil")
                        .timestamp(LocalDateTime.now())
                        .build());
    }
}