package com.test.cria.exception;

import lombok.*;

import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class ErrorResponse {

    private int status;
    private String message;
    private LocalDateTime timestamp;
    private String path;
    //private String error;
}
