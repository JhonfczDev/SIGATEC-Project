package com.sigatec.service;

import com.sigatec.model.Claim;
import com.sigatec.model.Warranty;

import java.util.List;

/**
 * Basic reports: active warranties, warranties about to expires, and pending claims.
  */
public class ReportService {
    private final WarrantyFinder warranties;
    private final ClaimService claims;

    public ReportService(WarrantyFinder warranties, ClaimService claims) {
        this.warranties = warranties;
        this.claims = claims;
    }

    public List<Warranty> activeWarranties(){
        return warranties.listActive();
    }

    public List<Warranty> warrantiesExpiringWithin(int days){
        return warranties.listExpiringWithin(days);
    }

    public List<Claim> pendingClaims(){
        return claims.listPending();
    }
}
