package com.comma.fit.authentication.service.exception;


import lombok.NoArgsConstructor;

/**
 * 自定义验证异常
 */

@NoArgsConstructor
public class AuthenticationException extends RuntimeException {

    public AuthenticationException(String message) {
        super(message);
    }

    public AuthenticationException(String message, Throwable cause) {
        super(message, cause);
    }

    public AuthenticationException(Throwable cause) {
        super(cause);
    }
}
