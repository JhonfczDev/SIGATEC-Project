package com.sigatec.model;

/**
 * Validity status of a warranty on a given date.
 * It is a <b>derived</b> value (computed from the dates) and is never stored.
 */
public enum WarrantyStatus {
    ACTIVE("Vigente"),
    EXPIRED("Vencida");

    private final String label;

    WarrantyStatus(String label) {
        this.label = label;
    }

    /** Text to display to the user. */
    public String getLabel() {
        return label;
    }
}
