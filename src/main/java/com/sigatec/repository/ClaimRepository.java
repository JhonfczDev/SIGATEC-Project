package com.sigatec.repository;

import java.util.List;

import com.sigatec.model.Claim;
import com.sigatec.model.ClaimStatus;

/** Storage contract for {@link Claim}s. */
public interface ClaimRepository extends Repository<Claim, String> {

    /** Generates the next free identifier (e.g. {@code REC-0001}). */
    String nextId();

    List<Claim> findByWarrantyId(String warrantyId);

    List<Claim> findByStatus(ClaimStatus status);
}
