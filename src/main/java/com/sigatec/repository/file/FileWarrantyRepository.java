package com.sigatec.repository.file;

import java.nio.file.Path;
import java.time.LocalDate;

import com.sigatec.model.Customer;
import com.sigatec.model.Product;
import com.sigatec.model.Warranty;
import com.sigatec.repository.CustomerRepository;
import com.sigatec.repository.PersistenceException;
import com.sigatec.repository.ProductRepository;
import com.sigatec.repository.memory.InMemoryWarrantyRepository;

/**
 * Warranty repository persisted in a text file.
 * Format: {@code id|productSerial|customerDocument|purchaseDate(yyyy-MM-dd)|coverageMonths}.
 *
 * <p>Only the keys of the product and customer are stored; they are resolved against the given
 * repositories when loading, so those must be created (and loaded) first.</p>
 */
public final class FileWarrantyRepository extends InMemoryWarrantyRepository {

    private final TextFileStore store;
    private final ProductRepository products;
    private final CustomerRepository customers;

    public FileWarrantyRepository(Path file, ProductRepository products, CustomerRepository customers) {
        this.products = products;
        this.customers = customers;
        this.store = new TextFileStore(file,
                "warranties: id|productSerial|customerDocument|purchaseDate|coverageMonths");
        store.load(this::parse, this::registerUnique);
    }

    @Override
    protected void afterChange() {
        store.save(findAll(), FileWarrantyRepository::toFields);
    }

    private Warranty parse(String[] f) {
        TextFileStore.requireFieldCount(f, 5);
        Product product = products.findById(f[1])
                .orElseThrow(() -> new PersistenceException("producto inexistente: " + f[1]));
        Customer customer = customers.findById(f[2])
                .orElseThrow(() -> new PersistenceException("cliente inexistente: " + f[2]));
        return new Warranty(f[0], product, customer, LocalDate.parse(f[3]), Integer.parseInt(f[4]));
    }

    private static String[] toFields(Warranty w) {
        return new String[] {w.getId(), w.getProduct().getSerialNumber(), w.getCustomer().getDocumentNumber(),
                w.getPurchaseDate().toString(), Integer.toString(w.getCoverageMonths())};
    }
}
