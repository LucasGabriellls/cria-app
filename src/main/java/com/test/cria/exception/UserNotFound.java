package com.test.cria.exception;

public class UserNotFound extends RuntimeException {

    public UserNotFound(String message) {
        super(message);
    }

    public UserNotFound() {
        super("Usuário não encontrado!");
    }
}
