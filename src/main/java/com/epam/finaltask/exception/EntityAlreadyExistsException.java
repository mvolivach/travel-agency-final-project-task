package com.epam.finaltask.exception;

import lombok.Getter;

@Getter
public class EntityAlreadyExistsException extends RuntimeException {
    private final StatusCodes errorCode;

    public EntityAlreadyExistsException(String message, StatusCodes errorCode) {
        super(message);
        this.errorCode = errorCode;
    }
}
