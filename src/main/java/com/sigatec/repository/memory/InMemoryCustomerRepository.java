package com.sigatec.repository.memory;

import java.util.Optional;

import com.sigatec.model.Customer;
import com.sigatec.repository.CustomerRepository;

public class InMemoryCustomerRepository extends InMemoryRepository<Customer, String>
        implements CustomerRepository {

    @Override
    public Optional<Customer> findByDocumentNumber(String documentNumber) {
        return findFirstWhere(customer -> customer.hasDocumentNumber(documentNumber));
    }
}
