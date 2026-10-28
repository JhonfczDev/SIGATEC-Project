package com.sigatec.repository;

import java.math.BigDecimal;
import java.time.LocalDate;

import com.sigatec.model.Claim;
import com.sigatec.model.ClaimStatus;
import com.sigatec.model.Customer;
import com.sigatec.model.Product;
import com.sigatec.model.Warranty;

/**
 * Sample data so the system shows meaningful content (and every report has something to list)
 * from the very first run.
 *
 * <p>Works against the repository interfaces, so it can fill any implementation (memory or file).
 * Dates are relative to {@code today}, which keeps the mix of active, about-to-expire and
 * expired warranties representative whenever the program is first run.</p>
 *
 * <p>Resulting data: 10 products, 6 customers, 9 warranties (7 active, 2 of which expire within
 * 30 days, and 2 expired) and 6 claims covering every status.</p>
 */
public final class SeedData {

    private SeedData() {
    }

    public static void populate(ProductRepository products, CustomerRepository customers,
                                WarrantyRepository warranties, ClaimRepository claims, LocalDate today) {
        // ---- Products ----
        Product hpLaptop = product(products, "Laptop HP 15-dy", "HP", "15-dy2024LA", "SN-HP-0001", "2350000");
        Product lenovoLaptop = product(products, "Laptop Lenovo IdeaPad 3", "Lenovo", "15ITL6", "SN-LN-0002", "2100000");
        Product galaxyPhone = product(products, "Smartphone Samsung Galaxy A54", "Samsung", "SM-A546", "SN-SG-0003", "1450000");
        Product iphone = product(products, "iPhone 13", "Apple", "A2633", "SN-AP-0004", "3200000");
        Product lgMonitor = product(products, "Monitor LG UltraGear 27\"", "LG", "27GL650F", "SN-LG-0005", "980000");
        Product sonyHeadphones = product(products, "Audífonos Sony WH-1000XM4", "Sony", "WH-1000XM4", "SN-SY-0006", "1100000");
        Product galaxyTab = product(products, "Tablet Samsung Galaxy Tab S6 Lite", "Samsung", "SM-P625", "SN-SG-0007", "1300000");
        Product epsonPrinter = product(products, "Impresora Epson EcoTank L3250", "Epson", "L3250", "SN-EP-0008", "850000");
        Product tpLinkRouter = product(products, "Router TP-Link Archer AX23", "TP-Link", "AX23", "SN-TP-0009", "260000");
        product(products, "Smart TV Samsung 50\"", "Samsung", "UN50CU7000", "SN-SG-0010", "1900000"); // no warranty yet

        // ---- Customers ----
        Customer juan = customer(customers, "Juan Pérez", "1065123456", "3001234567", "juan.perez@example.com");
        Customer maria = customer(customers, "María Fernanda Gómez", "1065987654", "3012345678", "maria.gomez@example.com");
        Customer carlos = customer(customers, "Carlos Andrés Rodríguez", "77123456", "3105551234", "carlos.rodriguez@example.com");
        Customer laura = customer(customers, "Laura Sofía Martínez", "1067890123", "3158889999", "laura.martinez@example.com");
        Customer andres = customer(customers, "Andrés Felipe Díaz", "1082345678", "3204447788", "andres.diaz@example.com");
        Customer valentina = customer(customers, "Valentina Torres Mejía", "1065456789", "3176663322", "valentina.torres@example.com");

        // ---- Warranties: 6 active (2 expiring within 30 days: Lenovo and Sony), 2 expired (Galaxy A54, Epson) ----
        Warranty wHp = warranty(warranties, hpLaptop, juan, today.minusMonths(4), 12);
        Warranty wLenovo = warranty(warranties, lenovoLaptop, maria, today.minusMonths(12).plusDays(20), 12);
        Warranty wGalaxy = warranty(warranties, galaxyPhone, carlos, today.minusMonths(14), 12);
        Warranty wIphone = warranty(warranties, iphone, laura, today.minusMonths(1), 24);
        Warranty wLg = warranty(warranties, lgMonitor, andres, today.minusMonths(7), 12);
        Warranty wSony = warranty(warranties, sonyHeadphones, valentina, today.minusMonths(6).plusDays(10), 6);
        warranty(warranties, galaxyTab, juan, today.minusMonths(20), 24);
        warranty(warranties, epsonPrinter, maria, today.minusMonths(18), 12);
        warranty(warranties, tpLinkRouter, carlos, today.minusMonths(2), 12);

        // ---- Claims: one or more in each status ----
        Claim pending = new Claim(claims.nextId(), wHp,
                "La laptop no enciende después de una actualización del sistema", today.minusDays(20));
        claims.save(pending);

        Claim inRepair = new Claim(claims.nextId(), wLenovo,
                "Pantalla con líneas verticales", today.minusDays(15));
        inRepair.changeStatus(ClaimStatus.IN_REPAIR, today.minusDays(12));
        claims.save(inRepair);

        Claim resolved = new Claim(claims.nextId(), wIphone,
                "La batería se descarga en pocas horas", today.minusDays(30));
        resolved.changeStatus(ClaimStatus.IN_REPAIR, today.minusDays(25));
        resolved.changeStatus(ClaimStatus.RESOLVED, today.minusDays(18));
        claims.save(resolved);

        Claim rejectedDamage = new Claim(claims.nextId(), wLg,
                "Píxeles muertos en la parte inferior de la pantalla", today.minusDays(10));
        rejectedDamage.changeStatus(ClaimStatus.REJECTED, today.minusDays(7));
        claims.save(rejectedDamage);

        Claim rejectedExpired = new Claim(claims.nextId(), wGalaxy,
                "Falla en el puerto de carga", today.minusDays(5));
        rejectedExpired.changeStatus(ClaimStatus.REJECTED, today.minusDays(3));
        claims.save(rejectedExpired);

        Claim pendingRecent = new Claim(claims.nextId(), wSony,
                "El audífono derecho no emite sonido", today.minusDays(3));
        claims.save(pendingRecent);
    }

    private static Product product(ProductRepository repository, String name, String brand, String model,
                                   String serial, String price) {
        Product product = new Product(name, brand, model, serial, new BigDecimal(price));
        repository.save(product);
        return product;
    }

    private static Customer customer(CustomerRepository repository, String fullName, String document,
                                     String phone, String email) {
        Customer customer = new Customer(fullName, document, phone, email);
        repository.save(customer);
        return customer;
    }

    private static Warranty warranty(WarrantyRepository repository, Product product, Customer customer,
                                     LocalDate purchaseDate, int months) {
        Warranty warranty = new Warranty(repository.nextId(), product, customer, purchaseDate, months);
        repository.save(warranty);
        return warranty;
    }
}