package com.sigatec.service;

import com.sigatec.model.Customer;
import com.sigatec.model.DomainException;
import com.sigatec.repository.CustomerRepository;

import java.util.List;
import java.util.Optional;

/**
 * Customer rules: registration, lookup and changes to customer data.
 */
public class CustomerService implements CustomerFinder {

    private final CustomerRepository customers;

    public CustomerService(CustomerRepository customers) {
        this.customers = customers;
    }

    /** Registers a customer. The document number must not already exist. */
    public Customer register(String fullName, String document, String phone, String email) {
        Customer customer = new Customer(fullName, document, phone, email);
        if (customers.existsById(customer.getId())) {
            throw new DomainException("Ya existe un cliente con el documento "
                    + customer.getDocumentNumber() + ".");
        }
        customers.save(customer);
        return customer;
    }

    @Override
    public Optional<Customer> findByDocument(String document) {
        return customers.findByDocumentNumber(document);
    }

    /** Like {@link #findByDocument}, but throws if the customer does not exist. */
    public Customer requireByDocument(String document) {
        return findByDocument(document).orElseThrow(() ->
                new DomainException("No existe un cliente con documento '" + document + "'."));
    }

    /** Searches by full name, ignoring case. */
    public List<Customer> searchByName(String text) {
        String wanted = text == null ? "" : text.strip().toLowerCase();
        return customers.findAll().stream()
                .filter(c -> c.getFullName().toLowerCase().contains(wanted))
                .toList();
    }

    public List<Customer> listAll() {
        return customers.findAll();
    }

    /** Updates name, phone and email. The document number cannot change. */
    public Customer update(String document, String fullName, String phone, String email) {
        Customer customer = requireByDocument(document);
        customer.updateDetails(fullName, phone, email);
        customers.save(customer);
        return customer;
    }

    /** Removes the customer without checking warranties. Use {@link RemovalService} from outside. */
    void delete(String document) {
        Customer customer = requireByDocument(document);
        customers.deleteById(customer.getId());
    }
}