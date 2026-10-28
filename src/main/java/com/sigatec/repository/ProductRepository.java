package com.sigatec.repository;

import java.util.Optional;

import com.sigatec.model.Product;

/** Storage contract for {@link Product}s (id = serial number). */
public interface ProductRepository extends Repository<Product, String> {

    /** Finds by a serial number as typed by the user (case and surrounding spaces are ignored). */
    Optional<Product> findBySerialNumber(String serialNumber);
}

