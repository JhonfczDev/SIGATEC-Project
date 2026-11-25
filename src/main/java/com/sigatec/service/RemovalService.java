package com.sigatec.service;

import com.sigatec.model.Customer;
import com.sigatec.model.DomainException;
import com.sigatec.model.Product;

/**
 * Deletes products and customers only when no warranty depends on them.
 *
 * <p>This rule lives here rather than in the catalog services, which keeps
 * {@link ProductService} and {@link CustomerService} free of warranty knowledge and
 * avoids a circular dependency with {@link WarrantyService}.</p>
 */
public class RemovalService {
    private final ProductService productService;
    private final CustomerService customerService;
    private final WarrantyFinder waranties;

    public RemovalService(ProductService productService, CustomerService customerService, WarrantyFinder waranties) {
        this.productService = productService;
        this.customerService = customerService;
        this.waranties = waranties;
    }
    public void deleteProdut(String serial){
        Product product = productService.requireBySerial(serial);

        if (!waranties.findBySerial(product.getSerialNumber()).isEmpty()){
            throw new DomainException("No se puede eliminar el producto: tiene garantias registradas.");
        }
        productService.delete(serial);
    }
    public void deleteCustomer(String document){
        Customer customer = customerService.requireByDocument(document);

        if (!waranties.findByCustomer(customer.getDocumentNumber()).isEmpty()){
            throw new DomainException("No se puede eliminar el cliente: tine garantias registradas.");
        }
        customerService.delete(document);
    }
}
