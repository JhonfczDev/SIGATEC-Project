package com.sigatec.repository;

import java.util.Optional;

import com.sigatec.model.Customer;

/** Storage contract for {@link Customer}s (id = document number). */
public interface CustomerRepository extends Repository<Customer, String> {

    /** Finds by a document number as typed by the user (surrounding spaces are ignored). */
    Optional<Customer> findByDocumentNumber(String documentNumber);
}