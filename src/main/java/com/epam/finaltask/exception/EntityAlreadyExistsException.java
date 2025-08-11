package com.epam.finaltask.exception;

import lombok.Getter;

@Getter
public class EntityAlreadyExistsException extends RuntimeException {
    private final StatusCodes statusCode;

    public EntityAlreadyExistsException(String message) {
        super(message);
        this.statusCode = StatusCodes.DUPLICATE_USERNAME;
    }
}
