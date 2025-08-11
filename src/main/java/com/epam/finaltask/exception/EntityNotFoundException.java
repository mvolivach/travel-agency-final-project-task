package com.epam.finaltask.exception;

import lombok.Getter;

@Getter
public class EntityNotFoundException extends RuntimeException {
    private final StatusCodes statusCode;

    public EntityNotFoundException(String message) {
        super(message);
        this.statusCode = StatusCodes.ENTITY_NOT_FOUND;
    }
}
