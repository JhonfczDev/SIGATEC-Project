package com.sigatec.repository;

/**
 * Thrown when data cannot be read from or written to its storage
 * (I/O failure, corrupt file, broken reference between records).
 * Unchecked, like {@code DomainException}: the UI can catch it in a single place.
 * Messages are user-facing, hence in Spanish.
 */
public class PersistenceException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public PersistenceException(String message) {
        super(message);
    }

    public PersistenceException(String message, Throwable cause) {
        super(message, cause);
    }
}
