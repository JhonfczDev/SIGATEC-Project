package com.sigatec.model;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

/**
 * Warranty linking a {@link Product} with a {@link Customer}.
 *
 * <p>It is immutable: once registered it is not modified. The expiration date and the
 * validity are derived from the purchase date and the coverage months.</p>
 *
 * <p>Time-dependent queries receive the reference date ({@code today}) as a parameter,
 * so the model does not depend on the system clock and is easy to test.
 * A warranty is considered active up to and including its expiration day.</p>
 */
public final class Warranty implements Identifiable<String> {

    private final String id;
    private final Product product;
    private final Customer customer;
    private final LocalDate purchaseDate;
    private final int coverageMonths;
    private final LocalDate expirationDate;

    /**
     * @param id             unique identifier; assigned by whoever creates the warranty (Service/Repository)
     * @param coverageMonths months of coverage counted from the purchase date (&gt; 0)
     */
    public Warranty(String id, Product product, Customer customer, LocalDate purchaseDate, int coverageMonths) {
        this.id = Validations.requireText(id, "El identificador de la garantía");
        this.product = Validations.requireNonNull(product, "El producto");
        this.customer = Validations.requireNonNull(customer, "El cliente");
        this.purchaseDate = Validations.requireNonNull(purchaseDate, "La fecha de compra");
        this.coverageMonths = Validations.requirePositive(coverageMonths, "Los meses de garantía");
        this.expirationDate = purchaseDate.plusMonths(coverageMonths);
    }

    public boolean isActive(LocalDate today) {
        Validations.requireNonNull(today, "La fecha de referencia");
        return !today.isAfter(expirationDate);
    }

    public WarrantyStatus statusOn(LocalDate today) {
        return isActive(today) ? WarrantyStatus.ACTIVE : WarrantyStatus.EXPIRED;
    }

    /** Days left until expiration; 0 if already expired. */
    public long remainingDays(LocalDate today) {
        return isActive(today) ? ChronoUnit.DAYS.between(today, expirationDate) : 0;
    }

    /** Active today and expiring within the next {@code days} days (for the "about to expire" report). */
    public boolean expiresWithin(int days, LocalDate today) {
        if (days < 0) {
            throw new DomainException("Los días no pueden ser negativos.");
        }
        return isActive(today) && !expirationDate.isAfter(today.plusDays(days));
    }

    /** Asks "does it belong to this customer?" without exposing internals (Law of Demeter). */
    public boolean belongsToCustomer(String documentNumber) {
        return customer.hasDocumentNumber(documentNumber);
    }

    /** Asks "does it cover the product with this serial?" without exposing internals. */
    public boolean coversProductWithSerial(String serialNumber) {
        return product.hasSerialNumber(serialNumber);
    }

    @Override
    public String getId() {
        return id;
    }

    public Product getProduct() {
        return product;
    }

    public Customer getCustomer() {
        return customer;
    }

    public LocalDate getPurchaseDate() {
        return purchaseDate;
    }

    public int getCoverageMonths() {
        return coverageMonths;
    }

    public LocalDate getExpirationDate() {
        return expirationDate;
    }

    @Override
    public boolean equals(Object o) {
        return this == o || (o instanceof Warranty other && id.equals(other.id));
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }

    @Override
    public String toString() {
        return "Warranty " + id + " [" + product.getSerialNumber() + " / " + customer.getDocumentNumber()
                + ", expires " + expirationDate + "]";
    }
}
