package com.sigatec.model;

/**
 * Thrown when a domain rule or invariant is violated (missing required data,
 * invalid format, forbidden status transition, etc.).
 *
 * <p>It is unchecked: upper layers (Service / UI) can catch it in a single place
 * to show the message to the user. Messages are user-facing, hence in Spanish.</p>
 */
public class DomainException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public DomainException(String message) {
        super(message);
    }
}