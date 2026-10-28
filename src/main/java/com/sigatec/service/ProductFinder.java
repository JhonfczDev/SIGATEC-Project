package com.sigatec.service;

import com.sigatec.model.Product;

import java.util.Optional;

/**
 * What other services need to know about products. Depending on this interface
 * instead of {@link ProductService} keeps the services loosely coupled.
 */
public interface ProductFinder {

    Optional<Product> findBySerial(String serial);
}