package com.sigatec.repository.memory;

import java.util.List;

import com.sigatec.model.Claim;
import com.sigatec.model.ClaimStatus;
import com.sigatec.repository.ClaimRepository;
import com.sigatec.repository.SequentialIdGenerator;

public final class InMemoryClaimRepository extends InMemoryRepository<Claim, String>
        implements ClaimRepository {

    private final SequentialIdGenerator idGenerator = new SequentialIdGenerator("REC-");

    @Override
    public String nextId() {
        return idGenerator.next();
    }

    @Override
    public List<Claim> findByWarrantyId(String warrantyId) {
        return findWhere(claim -> claim.isForWarranty(warrantyId));
    }

    @Override
    public List<Claim> findByStatus(ClaimStatus status) {
        return findWhere(claim -> claim.getStatus() == status);
    }
}