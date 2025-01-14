package com.epam.finaltask.exception;

import lombok.Getter;

@Getter
public class EntityAlreadyExistsException extends RuntimeException {
    private final String errorCode;

    public EntityAlreadyExistsException(String message, String errorCode) {
        super(message);
        this.errorCode = errorCode;
    }


}
