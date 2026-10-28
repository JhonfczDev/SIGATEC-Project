package com.sigatec.service;

import com.sigatec.model.Customer;

import java.util.Optional;

/**
 * What other services need to know about customers.
 */
public interface CustomerFinder {

    Optional<Customer> findByDocument(String document);
}