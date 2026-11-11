package com.sigatec.service;


import com.sigatec.model.Claim;
import com.sigatec.model.ClaimStatus;
import com.sigatec.model.DomainException;
import com.sigatec.model.Warranty;
import com.sigatec.repository.ClaimRepository;

import java.time.Clock;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * Warrranty claim rules
 */
public class ClaimService {
    private final ClaimRepository claims;
    private final WarrantyFinder warranties;
    private final Clock clock;

    public ClaimService(ClaimRepository claims, WarrantyFinder warranties, Clock clock) {
        this.claims = claims;
        this.warranties = warranties;
        this.clock = clock;
    }

    /**
     * Files a claim on a warranty.
     *
     * <p>If the warranty was not valid on the claim date, the claim is still recorded,
     * but directly as {@link ClaimStatus#REJECTED}. Otherwise it gets {@code requestedStatus},
     * or {@link ClaimStatus#PENDING} when none is given.</p>
     */
    public Claim file(String warrantyId, String description, LocalDate claimDate, ClaimStatus requestedStatus) {
        if (claimDate == null) {
            throw new DomainException("La fecha de la reclamacion es obligatoria.");
        }
        if (claimDate.isAfter(today())) {
            throw new DomainException("La fecha de reclamacion no puede ser futura.");
        }

        Warranty warranty = warranties.findById(warrantyId).orElseThrow(() ->
                new DomainException("No existe ina garantia con identificador '" + warrantyId + "'."));

        ClaimStatus initial;
        if (!warranty.isActive(claimDate)) {
            initial = ClaimStatus.REJECTED;

        } else {
            initial = requestedStatus != null ? requestedStatus : ClaimStatus.PENDING;
        }
        Claim claim = new Claim(claims.nextId(), warranty, description, claimDate, initial);
        claims.save(claim);
        return claim;
    }

    /** Moves a claim to a new status. The model validates that the transition is allowed. */
    public Claim changeStatus(String claimId, ClaimStatus newStatus){
        Claim claim = findById(claimId).orElseThrow(()->
                new DomainException("No existe reclamacion con identificador '" + claimId + "'."));

       claim.changeStatus(newStatus, today());
       claims.save(claim);
       return claim;
    }

    public Optional<Claim> findById(String id){
        return claims.findById(id);
    }

    public List<Claim> listAll(){
        return claims.findAll();
    }

    /** All claims field on one warranty.*/
    public List<Claim> finBYWarranty(String warrantyId){
        return claims.findByWarrantyId(warrantyId);
    }

    /** Claims in PENDING status. */
    public List<Claim> listPending(){
        return claims.findByStatus(ClaimStatus.PENDING);
    }

    /** Clains that are not yet resolved or rejected. */
    public List<Claim> listOpen(){
        return claims.findAll().stream().filter(Claim::isOpen).toList();
    }

    private LocalDate today(){
        return LocalDate.now(clock);
    }
}
