package com.sigatec.model;

import java.text.Normalizer;

/**
 * Possible statuses of a claim and the transitions allowed between them.
 *
 * <pre>
 * PENDING ──► IN_REPAIR ──► RESOLVED
 *    │            └───────► REJECTED
 *    ├────────────────────► RESOLVED
 *    └────────────────────► REJECTED
 * </pre>
 *
 * RESOLVED and REJECTED are final statuses.
 */
public enum ClaimStatus {
    PENDING("Pendiente"),
    IN_REPAIR("En reparación"),
    RESOLVED("Resuelta"),
    REJECTED("Rechazada");

    private final String label;

    ClaimStatus(String label) {
        this.label = label;
    }

    /** Text to display to the user. */
    public String getLabel() {
        return label;
    }

    /** Whether a claim in this status may move to {@code target}. */
    public boolean canTransitionTo(ClaimStatus target) {
        return switch (this) {
            case PENDING -> target == IN_REPAIR || target == RESOLVED || target == REJECTED;
            case IN_REPAIR -> target == RESOLVED || target == REJECTED;
            case RESOLVED, REJECTED -> false;
        };
    }

    /** A final status admits no further changes. */
    public boolean isFinal() {
        return this == RESOLVED || this == REJECTED;
    }

    /**
     * Converts text typed by the user ("en reparacion", "Resuelta", ...) into a status,
     * ignoring case, accents and extra spaces.
     *
     * @throws DomainException if the text matches no status
     */
    public static ClaimStatus fromLabel(String text) {
        String wanted = normalize(Validations.requireText(text, "El estado"));
        for (ClaimStatus status : values()) {
            if (normalize(status.label).equals(wanted)) {
                return status;
            }
        }
        throw new DomainException("Estado de reclamación no válido: '" + text
                + "'. Use Pendiente, En reparación, Resuelta o Rechazada.");
    }

    private static String normalize(String s) {
        return Normalizer.normalize(s, Normalizer.Form.NFD)
                .replaceAll("\\p{M}", "")
                .replaceAll("\\s+", " ")
                .trim()
                .toLowerCase();
    }
}

