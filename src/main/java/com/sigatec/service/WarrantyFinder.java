package com.sigatec.service;

import com.sigatec.model.Warranty;

import java.util.List;
import java.util.Optional;

/**
 * Read access to warranties, used by claims, reports and the removal rules.
 */
public interface WarrantyFinder {

    Optional<Warranty> findById(String id);

    List<Warranty> findBySerial(String serial);

    List<Warranty> findByCustomer(String document);

    /** Warranties valid today. */
    List<Warranty> listActive();

    /** Warranties valid today that expire within the next {@code days} days. */
    List<Warranty> listExpiringWithin(int days);
}