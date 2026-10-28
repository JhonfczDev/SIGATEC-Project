package com.sigatec.model;

import java.util.regex.Pattern;

/**
 * Store customer, identified by their document number.
 *
 * <p>The document number is the identity and never changes. Full name, phone and
 * email are modified atomically through {@link #updateDetails}.</p>
 */
public final class Customer implements Identifiable<String> {

    private static final Pattern PHONE = Pattern.compile("\\+?\\d{7,15}");
    private static final Pattern EMAIL = Pattern.compile("[\\w.+-]+@[\\w-]+(\\.[\\w-]+)+");

    private final String documentNumber;
    private String fullName;
    private String phone;
    private String email;

    public Customer(String fullName, String documentNumber, String phone, String email) {
        this.documentNumber = Validations.requireText(documentNumber, "El documento");
        this.fullName = Validations.requireText(fullName, "El nombre completo");
        this.phone = normalizePhone(phone);
        this.email = normalizeEmail(email);
    }

    /** Updates the modifiable data. If any value is invalid none is applied. */
    public void updateDetails(String fullName, String phone, String email) {
        String newFullName = Validations.requireText(fullName, "El nombre completo");
        String newPhone = normalizePhone(phone);
        String newEmail = normalizeEmail(email);

        this.fullName = newFullName;
        this.phone = newPhone;
        this.email = newEmail;
    }

    /** Compares with a document number typed by the user (ignores surrounding spaces). */
    public boolean hasDocumentNumber(String document) {
        return document != null && documentNumber.equals(document.strip());
    }

    private static String normalizePhone(String phone) {
        String clean = Validations.requireText(phone, "El teléfono").replaceAll("[\\s\\-()]", "");
        if (!PHONE.matcher(clean).matches()) {
            throw new DomainException("El teléfono debe tener entre 7 y 15 dígitos.");
        }
        return clean;
    }

    private static String normalizeEmail(String email) {
        String clean = Validations.requireText(email, "El email").toLowerCase();
        if (!EMAIL.matcher(clean).matches()) {
            throw new DomainException("El email no tiene un formato válido.");
        }
        return clean;
    }

    @Override
    public String getId() {
        return documentNumber;
    }

    public String getDocumentNumber() {
        return documentNumber;
    }

    public String getFullName() {
        return fullName;
    }

    public String getPhone() {
        return phone;
    }

    public String getEmail() {
        return email;
    }

    @Override
    public boolean equals(Object o) {
        return this == o || (o instanceof Customer other && documentNumber.equals(other.documentNumber));
    }

    @Override
    public int hashCode() {
        return documentNumber.hashCode();
    }

    @Override
    public String toString() {
        return fullName + " (doc. " + documentNumber + ")";
    }
}

