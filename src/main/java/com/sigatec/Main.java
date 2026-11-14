
package com.sigatec;

import com.sigatec.repository.file.FileStorage;
import com.sigatec.service.ClaimService;
import com.sigatec.service.CustomerService;
import com.sigatec.service.ProductService;
import com.sigatec.service.WarrantyService;


import java.nio.file.Path;
import java.time.Clock;
import java.time.LocalDate;

public class Main {

    public static void main(String[] args) {
        
        FileStorage storage = FileStorage.open(Path.of("data"), LocalDate.now());
        
        ProductService productService = new ProductService(storage.products());
        CustomerService customerService = new CustomerService(storage.customers());
        
        WarrantyService warrantyService = new WarrantyService(storage.warranties(), productService, customerService, Clock.systemDefaultZone());
        ClaimService claimService = new ClaimService(storage.claims(), warrantyService, Clock.systemDefaultZone());
    }
}
