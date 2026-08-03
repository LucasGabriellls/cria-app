package com.test.cria.exception;

import com.test.cria.exception.userExceptions.InvalidAttributeException;
import jakarta.servlet.http.HttpServletRequest;
import org.jspecify.annotations.Nullable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingPathVariableException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import java.time.LocalDateTime;

@ControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(InvalidAttributeException.class)
    private ResponseEntity<ErrorResponse> userNotFoundHandler(InvalidAttributeException exception, HttpServletRequest request) {

        ErrorResponse error = new ErrorResponse(
                HttpStatus.NOT_FOUND.value(),
                exception.getMessage(),
                LocalDateTime.now(),
                request.getRequestURI()
        );

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    private ResponseEntity<ErrorResponse> methodArgumentTypeMismatchHandler(MethodArgumentTypeMismatchException exception, HttpServletRequest request) {
        ErrorResponse error = new ErrorResponse(
                HttpStatus.BAD_REQUEST.value(),
                "O dado precisa ser do tipo inteiro!",
                LocalDateTime.now(),
                request.getRequestURI()
        );

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }

    @Override
    protected @Nullable ResponseEntity<Object> handleHttpRequestMethodNotSupported(HttpRequestMethodNotSupportedException ex,
                                                                                   HttpHeaders headers,
                                                                                   HttpStatusCode status,
                                                                                   WebRequest request) {

        String message = String.format("O método HTTP '%s' não é suportado para este endpoint. Métodos suportados: %s",
                ex.getMethod(), ex.getSupportedHttpMethods());

        return buildErrorResponse(HttpStatus.METHOD_NOT_ALLOWED, message);
    }

    @Override
    protected @Nullable ResponseEntity<Object> handleNoResourceFoundException(NoResourceFoundException ex,
                                                                              HttpHeaders headers,
                                                                              HttpStatusCode status,
                                                                              WebRequest request) {

        String message = String.format("O recurso ou endpoint '%s' não foi encontrado no servidor.", ex.getResourcePath());
        return buildErrorResponse(HttpStatus.NOT_FOUND, message);
    }

    @Override
    protected @Nullable ResponseEntity<Object> handleMissingPathVariable(MissingPathVariableException ex,
                                                                         HttpHeaders headers,
                                                                         HttpStatusCode status,
                                                                         WebRequest request) {

        String message = String.format("O parâmetro de URL '%s' é obrigatório e está ausente.", ex.getVariableName());
        return buildErrorResponse(HttpStatus.BAD_REQUEST, message);
    }

    private ResponseEntity<Object> buildErrorResponse(HttpStatus status, String message) {

        ErrorResponse error = ErrorResponse.builder()
                .status(status.value())
                .message(message)
                .timestamp(LocalDateTime.now())
                .build();

        return ResponseEntity.status(status).body(error);
    }
}