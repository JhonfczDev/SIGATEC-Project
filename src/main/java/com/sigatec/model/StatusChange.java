package com.sigatec.model;

import java.time.LocalDate;

/**
 * Immutable record of a claim status change (after-sales traceability).
 */
public record StatusChange(ClaimStatus status, LocalDate date) {

    public StatusChange {
        Validations.requireNonNull(status, "El estado");
        Validations.requireNonNull(date, "La fecha del cambio de estado");
    }
}
