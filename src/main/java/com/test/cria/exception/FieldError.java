package com.test.cria.exception;

public record FieldError(
        String field,
        String message
) {
}
