package com.sigatec.repository.file;

import java.nio.file.Path;

import com.sigatec.model.Customer;
import com.sigatec.repository.memory.InMemoryCustomerRepository;

/**
 * Customer repository persisted in a text file.
 * Format: {@code documentNumber|fullName|phone|email}.
 * Loads the file on creation and rewrites it after every change.
 */
public final class FileCustomerRepository extends InMemoryCustomerRepository {

    private final TextFileStore store;

    public FileCustomerRepository(Path file) {
        this.store = new TextFileStore(file, "customers: documentNumber|fullName|phone|email");
        store.load(FileCustomerRepository::parse, this::registerUnique);
    }

    @Override
    protected void afterChange() {
        store.save(findAll(), FileCustomerRepository::toFields);
    }

    private static Customer parse(String[] f) {
        TextFileStore.requireFieldCount(f, 4);
        return new Customer(f[1], f[0], f[2], f[3]);
    }

    private static String[] toFields(Customer c) {
        return new String[] {c.getDocumentNumber(), c.getFullName(), c.getPhone(), c.getEmail()};
    }
}
