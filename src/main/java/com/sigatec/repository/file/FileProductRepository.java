package com.sigatec.repository.file;

import java.math.BigDecimal;
import java.nio.file.Path;

import com.sigatec.model.Product;
import com.sigatec.repository.memory.InMemoryProductRepository;

/**
 * Product repository persisted in a text file.
 * Format: {@code serialNumber|name|brand|model|price}.
 * Loads the file on creation and rewrites it after every change.
 */
public final class FileProductRepository extends InMemoryProductRepository {

    private final TextFileStore store;

    public FileProductRepository(Path file) {
        this.store = new TextFileStore(file, "products: serialNumber|name|brand|model|price");
        store.load(FileProductRepository::parse, this::registerUnique);
    }

    @Override
    protected void afterChange() {
        store.save(findAll(), FileProductRepository::toFields);
    }

    private static Product parse(String[] f) {
        TextFileStore.requireFieldCount(f, 5);
        return new Product(f[1], f[2], f[3], f[0], new BigDecimal(f[4]));
    }

    private static String[] toFields(Product p) {
        return new String[] {p.getSerialNumber(), p.getName(), p.getBrand(), p.getModel(),
                p.getPrice().toPlainString()};
    }
}
