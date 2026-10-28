package com.sigatec.service;

import com.sigatec.model.Customer;
import com.sigatec.model.DomainException;
import com.sigatec.model.Product;
import com.sigatec.model.Warranty;
import com.sigatec.repository.WarrantyRepository;

import java.time.Clock;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * Warranty rules. Depends on lookup interfaces for products and customers,
 * and gets the current date from an injected {@link Clock}.
 */
public class WarrantyService implements WarrantyFinder {

    private final WarrantyRepository warranties;
    private final ProductFinder products;
    private final CustomerFinder customers;
    private final Clock clock;

    public WarrantyService(WarrantyRepository warranties,
                           ProductFinder products,
                           CustomerFinder customers,
                           Clock clock) {
        this.warranties = warranties;
        this.products = products;
        this.customers = customers;
        this.clock = clock;
    }

    /** Registers a warranty for a product sold to a customer. A product can have only one. */
    public Warranty register(String serial, String document, LocalDate purchaseDate, int coverageMonths) {
        if (purchaseDate == null) {
            throw new DomainException("La fecha de compra es obligatoria.");
        }
        if (purchaseDate.isAfter(today())) {
            throw new DomainException("La fecha de compra no puede ser futura.");
        }

        Product product = products.findBySerial(serial).orElseThrow(() ->
                new DomainException("No existe un producto con el número de serie '" + serial + "'."));
        Customer customer = customers.findByDocument(document).orElseThrow(() ->
                new DomainException("No existe un cliente con documento '" + document + "'."));

        if (!warranties.findByProductSerial(product.getSerialNumber()).isEmpty()) {
            throw new DomainException("El producto ya tiene una garantía registrada.");
        }

        Warranty warranty = new Warranty(warranties.nextId(), product, customer, purchaseDate, coverageMonths);
        warranties.save(warranty);
        return warranty;
    }

    @Override
    public Optional<Warranty> findById(String id) {
        return warranties.findById(id);
    }

    @Override
    public List<Warranty> findBySerial(String serial) {
        return warranties.findByProductSerial(serial);
    }

    @Override
    public List<Warranty> findByCustomer(String document) {
        return warranties.findByCustomerDocument(document);
    }

    @Override
    public List<Warranty> listActive() {
        LocalDate today = today();
        return warranties.findAll().stream()
                .filter(w -> w.isActive(today))
                .toList();
    }

    @Override
    public List<Warranty> listExpiringWithin(int days) {
        LocalDate today = today();
        return warranties.findAll().stream()
                .filter(w -> w.expiresWithin(days, today))
                .toList();
    }

    private LocalDate today() {
        return LocalDate.now(clock);
    }
}