package com.sigatec.repository.file;

import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.List;
import java.util.Objects;

import com.sigatec.repository.ClaimRepository;
import com.sigatec.repository.CustomerRepository;
import com.sigatec.repository.ProductRepository;
import com.sigatec.repository.SeedData;
import com.sigatec.repository.WarrantyRepository;

/**
 * Opens the four file repositories from a data folder, wiring them in the right order
 * (products and customers first, then warranties, then claims).
 *
 * <p>On the very first run (none of the data files exists) it fills them with {@link SeedData}.
 * From then on the files are the source of truth and survive between runs.</p>
 *
 * <pre>
 * FileStorage storage = FileStorage.open(Path.of("data"), LocalDate.now());
 * ProductRepository products = storage.products();
 * </pre>
 */
public final class FileStorage {

    public static final String PRODUCTS_FILE = "products.txt";
    public static final String CUSTOMERS_FILE = "customers.txt";
    public static final String WARRANTIES_FILE = "warranties.txt";
    public static final String CLAIMS_FILE = "claims.txt";

    private final ProductRepository products;
    private final CustomerRepository customers;
    private final WarrantyRepository warranties;
    private final ClaimRepository claims;

    private FileStorage(Path dataDirectory) {
        this.products = new FileProductRepository(dataDirectory.resolve(PRODUCTS_FILE));
        this.customers = new FileCustomerRepository(dataDirectory.resolve(CUSTOMERS_FILE));
        this.warranties = new FileWarrantyRepository(dataDirectory.resolve(WARRANTIES_FILE), products, customers);
        this.claims = new FileClaimRepository(dataDirectory.resolve(CLAIMS_FILE), warranties);
    }

    /**
     * @param dataDirectory folder holding the data files (created if missing)
     * @param today         reference date used only to build the sample data on the first run
     * @throws com.sigatec.repository.PersistenceException if a data file is unreadable or inconsistent
     */
    public static FileStorage open(Path dataDirectory, LocalDate today) {
        Objects.requireNonNull(dataDirectory, "dataDirectory must not be null");
        Objects.requireNonNull(today, "today must not be null");

        boolean firstRun = List.of(PRODUCTS_FILE, CUSTOMERS_FILE, WARRANTIES_FILE, CLAIMS_FILE).stream()
                .noneMatch(name -> Files.exists(dataDirectory.resolve(name)));

        FileStorage storage = new FileStorage(dataDirectory);
        if (firstRun) {
            SeedData.populate(storage.products, storage.customers, storage.warranties, storage.claims, today);
        }
        return storage;
    }

    public ProductRepository products() {
        return products;
    }

    public CustomerRepository customers() {
        return customers;
    }

    public WarrantyRepository warranties() {
        return warranties;
    }

    public ClaimRepository claims() {
        return claims;
    }
}
