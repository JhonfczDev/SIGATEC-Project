package com.sigatec.model;

import java.math.BigDecimal;

/**
 * Internal helper used by the model to check invariants (DRY).
 * Package-private: it is not part of the public API of the model.
 */
final class Validations {

    private Validations() {
    }

    static <T> T requireNonNull(T value, String field) {
        if (value == null) {
            throw new DomainException(field + " es obligatorio.");
        }
        return value;
    }

    /** Requires non-blank text and returns it stripped. */
    static String requireText(String value, String field) {
        if (value == null || value.isBlank()) {
            throw new DomainException(field + " es obligatorio.");
        }
        return value.strip();
    }

    static BigDecimal requirePositive(BigDecimal value, String field) {
        requireNonNull(value, field);
        if (value.signum() <= 0) {
            throw new DomainException(field + " debe ser mayor que cero.");
        }
        return value;
    }

    static int requirePositive(int value, String field) {
        if (value <= 0) {
            throw new DomainException(field + " debe ser mayor que cero.");
        }
        return value;
    }
}
