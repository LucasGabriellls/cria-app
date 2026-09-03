package com.test.cria.exception.classroom;

public class ClassroomAlreadyExistsException extends RuntimeException {
    public ClassroomAlreadyExistsException(String message) {
        super(message);
    }
}
