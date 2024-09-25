package com.fin.spr.exceptions;

/**
 * The {@code EntityAlreadyExistsException} class is a custom runtime exception
 * that is thrown when an attempt is made to create an entity that already exists
 * in the storage.
 */
public class EntityAlreadyExistsException extends RuntimeException {

    /**
     * Constructs a new {@code EntityAlreadyExistsException} with the specified detail message.
     *
     * @param message the detail message explaining the reason for the exception
     */
    public EntityAlreadyExistsException(String message) {
        super(message);
    }
}
