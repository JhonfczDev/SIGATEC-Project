package com.sigatec.repository.memory;

import java.util.Optional;

import com.sigatec.model.Product;
import com.sigatec.repository.ProductRepository;

public final class InMemoryProductRepository extends InMemoryRepository<Product, String>
        implements ProductRepository {

    @Override
    public Optional<Product> findBySerialNumber(String serialNumber) {
        return findFirstWhere(product -> product.hasSerialNumber(serialNumber));
    }
}