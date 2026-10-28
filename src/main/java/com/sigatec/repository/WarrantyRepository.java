package com.sigatec.repository;

import java.util.List;

import com.sigatec.model.Warranty;

/**
 * Storage contract for {@link Warranty}s.
 *
 * <p>It only offers lookups the UI needs. Business reports (active, about to expire)
 * belong to the Service, which filters using the model's own methods.</p>
 */
public interface WarrantyRepository extends Repository<Warranty, String> {

    /** Generates the next free identifier (e.g. {@code GAR-0001}). */
    String nextId();

    /** Warranties covering the product with the given serial (a product may have several over time). */
    List<Warranty> findByProductSerial(String serialNumber);

    List<Warranty> findByCustomerDocument(String documentNumber);
}

