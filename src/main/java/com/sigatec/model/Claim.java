package com.sigatec.model;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Claim filed against a {@link Warranty}.
 *
 * <p>It protects its own life cycle: status changes are only made through
 * {@link #changeStatus}, which validates the transition (see {@link ClaimStatus}) and
 * records it in the history.</p>
 *
 * <p>It does not check the warranty validity on creation: that is a business rule of the
 * Service (a claim on an expired warranty may be registered and end up rejected).</p>
 */
public final class Claim implements Identifiable<String> {

    private final String id;
    private final Warranty warranty;
    private final String description;
    private final LocalDate claimDate;
    private ClaimStatus status;
    private final List<StatusChange> history = new ArrayList<>();

    /** Creates a claim in PENDING status. */
    public Claim(String id, Warranty warranty, String description, LocalDate claimDate) {
        this(id, warranty, description, claimDate, ClaimStatus.PENDING);
    }

    /** Creates a claim with an initial status (the UI lets the user pick it when registering). */
    public Claim(String id, Warranty warranty, String description, LocalDate claimDate,
                 ClaimStatus initialStatus) {
        this.id = Validations.requireText(id, "El identificador de la reclamación");
        this.warranty = Validations.requireNonNull(warranty, "La garantía");
        this.description = Validations.requireText(description, "La descripción del problema");
        this.claimDate = Validations.requireNonNull(claimDate, "La fecha de la reclamación");
        this.status = Validations.requireNonNull(initialStatus, "El estado");

        if (claimDate.isBefore(warranty.getPurchaseDate())) {
            throw new DomainException("La reclamación no puede ser anterior a la fecha de compra.");
        }
        history.add(new StatusChange(initialStatus, claimDate));
    }

    /**
     * Changes the status if the transition is valid.
     *
     * @throws DomainException if the transition is not allowed or the date is earlier than the last change
     */
    public void changeStatus(ClaimStatus newStatus, LocalDate date) {
        Validations.requireNonNull(newStatus, "El nuevo estado");
        Validations.requireNonNull(date, "La fecha del cambio");

        if (!status.canTransitionTo(newStatus)) {
            throw new DomainException("No se puede pasar de '" + status.getLabel()
                    + "' a '" + newStatus.getLabel() + "'.");
        }
        if (date.isBefore(lastChange().date())) {
            throw new DomainException("La fecha del cambio no puede ser anterior al último cambio de estado.");
        }
        status = newStatus;
        history.add(new StatusChange(newStatus, date));
    }

    public boolean isPending() {
        return status == ClaimStatus.PENDING;
    }

    /** Open = not yet resolved nor rejected (pending or in repair). */
    public boolean isOpen() {
        return !status.isFinal();
    }

    private StatusChange lastChange() {
        return history.get(history.size() - 1);
    }

    @Override
    public String getId() {
        return id;
    }

    public Warranty getWarranty() {
        return warranty;
    }

    public String getDescription() {
        return description;
    }

    public LocalDate getClaimDate() {
        return claimDate;
    }

    public ClaimStatus getStatus() {
        return status;
    }

    /** Status history in chronological order (read-only view). */
    public List<StatusChange> getHistory() {
        return Collections.unmodifiableList(history);
    }

    @Override
    public boolean equals(Object o) {
        return this == o || (o instanceof Claim other && id.equals(other.id));
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }

    @Override
    public String toString() {
        return "Claim " + id + " [" + status.getLabel() + ", warranty " + warranty.getId() + "]";
    }
}
