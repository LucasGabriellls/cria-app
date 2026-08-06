package com.test.cria.exception;

import com.test.cria.exception.userExceptions.InvalidAttributeException;
import com.test.cria.exception.userExceptions.UserAlreadyExistsException;
import com.test.cria.exception.userExceptions.UserNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolationException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.time.LocalDateTime;
import java.util.List;

@RestControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(InvalidAttributeException.class)
    private ResponseEntity<ErrorResponse> invalidAttributeHandler(InvalidAttributeException exception, HttpServletRequest request) {
        ErrorResponse error = buildErrorResponse(
                HttpStatus.BAD_REQUEST,
                exception.getMessage(),
                request.getRequestURI(),
                ErrorCodeEnum.INVALID_USER_ID
        );

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }

    @ExceptionHandler(UserAlreadyExistsException.class)
    private ResponseEntity<ErrorResponse> userAlreadyExistsHandler(UserAlreadyExistsException exception, HttpServletRequest request) {
        ErrorResponse error = buildErrorResponse(
                HttpStatus.BAD_REQUEST,
                exception.getMessage(),
                request.getRequestURI(),
                ErrorCodeEnum.USER_ALREADY_EXISTS
        );

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }

    @ExceptionHandler(UserNotFoundException.class)
    private ResponseEntity<ErrorResponse> userNotFoundHandler(UserNotFoundException exception, HttpServletRequest request) {
        ErrorResponse error = buildErrorResponse(
                HttpStatus.NOT_FOUND,
                exception.getMessage(),
                request.getRequestURI(),
                ErrorCodeEnum.USER_NOT_FOUND
        );

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    private ResponseEntity<ErrorResponse> contrainViolationHandler(ConstraintViolationException exception, HttpServletRequest request) {
        List<ErrorDetail> details = exception.getConstraintViolations()
                .stream()
                .map(violation -> {
                    String propertyPath = violation.getPropertyPath().toString();

                    String fieldName = propertyPath.contains(".")
                            ? propertyPath.substring(propertyPath.lastIndexOf('.') + 1)
                            : propertyPath;

                    return new ErrorDetail(fieldName, violation.getMessage());
                })
                .toList();

        ErrorResponse error = buildErrorResponse(
                HttpStatus.BAD_REQUEST,
                request.getRequestURI(),
                ErrorCodeEnum.VALIDATION_FAILED,
                details
        );

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex, HttpHeaders headers, HttpStatusCode status, WebRequest request) {
        String path = ((ServletWebRequest) request)
                .getRequest()
                .getRequestURI();

        List<ErrorDetail> invalidFields = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(error -> new ErrorDetail(error.getField(), error.getDefaultMessage()))
                .toList();

        ErrorResponse errorResponse = buildErrorResponse(
                status,
                path,
                ErrorCodeEnum.VALIDATION_FAILED,
                invalidFields
        );

        return ResponseEntity.status(status).body(errorResponse);
    }



    private ErrorResponse buildErrorResponse(HttpStatusCode status, String message, String path, ErrorCodeEnum code) {
        return new ErrorResponse(
                status.value(),
                message,
                LocalDateTime.now(),
                path,
                code,
                null
        );
    }

    private ErrorResponse buildErrorResponse(HttpStatusCode status, String path, ErrorCodeEnum code, List<ErrorDetail> details) {
        return new ErrorResponse(
                status.value(),
                "Validation failed for one or more fields",
                LocalDateTime.now(),
                path,
                code,
                details
        );
    }
}