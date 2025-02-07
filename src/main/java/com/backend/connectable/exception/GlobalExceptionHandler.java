package com.backend.connectable.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Arrays;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(ConnectableException.class)
    public ResponseEntity<ExceptionResponse> handleConnectableException(ConnectableException e) {
        return ResponseEntity.status(e.getHttpStatus()).body(e.getBody());
    }

    @ExceptionHandler(KasException.class)
    public ResponseEntity<KasExceptionResponse> handleKasException(KasException e) {
        KasExceptionResponse kasExceptionResponse = e.getKasExceptionResponse();
        log.error("KAS Request ID: " + kasExceptionResponse.getRequestId());
        log.error("KAS Code: " + kasExceptionResponse.getCode());
        log.error("KAS Message: " + kasExceptionResponse.getMessage());
        return ResponseEntity.internalServerError().body(kasExceptionResponse);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<MethodArgumentNotValidExceptionResponse> handleMethodArgumentNotValidException(MethodArgumentNotValidException e) {
        MethodArgumentNotValidExceptionResponse errorResponse =  MethodArgumentNotValidExceptionResponse.of(ErrorType.INVALID_REQUEST_ERROR);
        for (FieldError fieldError: e.getFieldErrors()) {
            errorResponse.addValidation(fieldError.getField(), fieldError.getDefaultMessage());
        }
        return ResponseEntity.badRequest().body(errorResponse);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<String> handleException(Exception e) {
        String errorMessage = e.getMessage();
        Arrays.stream(e.getStackTrace())
                .map(StackTraceElement::toString)
                .forEach(log::error);
        return ResponseEntity.internalServerError().body(errorMessage);
    }
}
