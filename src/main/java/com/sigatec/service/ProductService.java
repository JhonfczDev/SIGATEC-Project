package com.sigatec.service;

import com.sigatec.model.DomainException;
import com.sigatec.model.Product;
import com.sigatec.repository.ProductRepository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

/**
 * Product catalog rules: registration, lookup and changes to product data.
 */
public class ProductService implements ProductFinder {

    private final ProductRepository products;

    public ProductService(ProductRepository products) {
        this.products = products;
    }

    /** Registers a product. The serial number must not already exist. */
    public Product register(String name, String brand, String model, String serial, BigDecimal price) {
        Product product = new Product(name, brand, model, serial, price);
        if (products.existsById(product.getId())) {
            throw new DomainException("Ya existe un producto con el número de serie "
                    + product.getSerialNumber() + ".");
        }
        products.save(product);
        return product;
    }

    @Override
    public Optional<Product> findBySerial(String serial) {
        return products.findBySerialNumber(serial);
    }

    /** Like {@link #findBySerial}, but throws if the product does not exist. */
    public Product requireBySerial(String serial) {
        return findBySerial(serial).orElseThrow(() ->
                new DomainException("No existe un producto con el número de serie '" + serial + "'."));
    }

    /** Searches by name, brand or model, ignoring case. */
    public List<Product> search(String text) {
        String wanted = text == null ? "" : text.strip().toLowerCase();
        return products.findAll().stream()
                .filter(p -> p.getName().toLowerCase().contains(wanted)
                        || p.getBrand().toLowerCase().contains(wanted)
                        || p.getModel().toLowerCase().contains(wanted))
                .toList();
    }

    public List<Product> listAll() {
        return products.findAll();
    }

    /** Updates the data of an existing product. The serial number cannot change. */
    public Product update(String serial, String name, String brand, String model, BigDecimal price) {
        Product product = requireBySerial(serial);
        product.updateDetails(name, brand, model, price);
        products.save(product);
        return product;
    }

    /** Removes the product without checking warranties. Use {@link RemovalService} from outside. */
    void delete(String serial) {
        Product product = requireBySerial(serial);
        products.deleteById(product.getId());
    }
}