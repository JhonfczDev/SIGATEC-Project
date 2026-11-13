package com.sigatec.ui;


import com.sigatec.model.Claim;
import com.sigatec.model.ClaimStatus;
import com.sigatec.model.Customer;
import com.sigatec.model.DomainException;
import com.sigatec.model.Product;
import com.sigatec.model.Warranty;
import com.sigatec.service.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import java.util.Scanner;

public class ConsoleUI {
    private final ProductService productService;
    private final CustomerService customerService;
    private final WarrantyService warrantyService;
    private final ClaimService claimService;
    private final Scanner scanner;

    public ConsoleUI(ProductService productService, CustomerService customerService, 
                     WarrantyService warrantyService, ClaimService claimService) {
        this.productService = productService;
        this.customerService = customerService;
        this.warrantyService = warrantyService;
        this.claimService = claimService;
        this.scanner = new Scanner(System.in);
    }

    public void start() {
        int option;
        do {
            printMenu();
            option = scanner.nextInt();
            scanner.nextLine();

            switch (option) {
                case 1 ->
                    handleProducts();
                case 2 ->
                    handleCustomers();
                case 3 ->
                    handleWarranties();
                case 4 ->
                    handleClaims();
                case 0 ->
                    System.out.println("¡Saliendo del sistema. Hasta luego!");
                default ->
                    System.out.println("Opción inválida, intenta de nuevo.");
            }
            System.out.println();
        } while (option != 0);
    }

    private void printMenu() {
        System.out.println("        SIGATEC");
        System.out.println("=== MENÚ PRINCIPAL ===");
        System.out.println("1. Gestionar Productos");
        System.out.println("2. Gestionar Clientes");
        System.out.println("3. Gestionar Garantías");
        System.out.println("4. Gestionar Reclamos");
        System.out.println("0. Salir");
        System.out.print("Elige una opción: ");
    }

    private void handleProducts() {

        int option;
        do {
            System.out.println("\n-- Módulo de Productos --");
            System.out.println("1. Registrar producto");
            System.out.println("2. Consultar producto");
            System.out.println("3. Modificar producto");
            System.out.println("4. Eliminar producto");
            System.out.println("0. Volver al menú principal");
            System.out.print("Elige una opción: ");

            option = scanner.nextInt();
            scanner.nextLine(); // Limpiar el buffer

            switch (option) {
                case 1 -> {
                    System.out.println("\n--- Registrar Producto ---");
                    System.out.print("Nombre: ");
                    String name = scanner.nextLine();
                    System.out.print("Marca: ");
                    String brand = scanner.nextLine();
                    System.out.print("Modelo: ");
                    String model = scanner.nextLine();
                    System.out.print("Número de serie: ");
                    String serial = scanner.nextLine();
                    System.out.print("Precio: ");
                    BigDecimal price = scanner.nextBigDecimal();
                    scanner.nextLine(); // Limpiar buffer

                    productService.register(name, brand, model, serial, price);
                    System.out.println("¡Producto registrado con éxito!");
                }
                case 2 -> {
                    System.out.println("\n--- Consultar Producto ---");
                    System.out.println("\n--- Consultar Productos ---");
                    System.out.println("1. Listar todos");
                    System.out.println("2. Buscar por número de serie");
                    System.out.println("3. Buscar por texto (nombre/marca/modelo)");
                    System.out.print("Elige una opción de consulta: ");
                    int subOpt = scanner.nextInt();
                    scanner.nextLine();

                    if (subOpt == 1) {
                        List<Product> products = productService.listAll();
                        if (products.isEmpty()) {
                            System.out.println("No hay productos registrados.");
                        } else {
                            products.forEach(p -> System.out.println("- " + p));
                        }
                    } else if (subOpt == 2) {
                        System.out.print("Ingresa el número de serie: ");
                        String serial = scanner.nextLine();
                        Product p = productService.requireBySerial(serial);
                        System.out.println("Encontrado: " + p);
                    } else if (subOpt == 3) {
                        System.out.print("Ingresa el texto de búsqueda: ");
                        String text = scanner.nextLine();
                        List<Product> matches = productService.search(text);
                        matches.forEach(p -> System.out.println("- " + p));
                    }
                }
                case 3 -> {
                    System.out.println("\n--- Modificar Producto ---");
                    System.out.print("Ingresa el número de serie del producto a modificar: ");
                    String serial = scanner.nextLine();
                    
                    // Verificamos que exista primero
                    productService.requireBySerial(serial);

                    System.out.print("Nuevo nombre: ");
                    String name = scanner.nextLine();
                    System.out.print("Nueva marca: ");
                    String brand = scanner.nextLine();
                    System.out.print("Nuevo modelo: ");
                    String model = scanner.nextLine();
                    System.out.print("Nuevo precio: ");
                    BigDecimal price = scanner.nextBigDecimal();
                    scanner.nextLine();

                    productService.update(serial, name, brand, model, price);
                    System.out.println("¡Producto modificado con éxito!");
                    }
                    case 4 -> {
                        System.out.println("\n--- Eliminar Producto ---");
                        System.out.println("\n--- Eliminar Producto ---");
                    System.out.print("Ingresa el número de serie del producto a eliminar: ");
                    String serial = scanner.nextLine();
                    
                    productService.delete(serial);
                    System.out.println("¡Producto eliminado con éxito!");
                    }
                    case 0 ->
                        System.out.println("Volviendo al menú principal...");
                    default ->
                        System.out.println("Opción inválida, intenta de nuevo.");
                }
            } while (option != 0);
        }
    

    private void handleCustomers() {
      
        int option;
        do {
            System.out.println("\n-- Módulo de Clientes --");
            System.out.println("1. Registrar cliente");
            System.out.println("2. Consultar cliente");
            System.out.println("3. Modificar cliente");
            System.out.println("4. Eliminar cliente");
            System.out.println("0. Volver al menú principal");
            System.out.print("Elige una opción: ");

            option = scanner.nextInt();
            scanner.nextLine(); // Limpiar el buffer

            try {
                switch (option) {
                    case 1 -> {
                        System.out.println("\n--- Registrar Cliente ---");
                        System.out.print("Nombre completo: ");
                        String fullName = scanner.nextLine();
                        System.out.print("Número de documento: ");
                        String document = scanner.nextLine();
                        System.out.print("Teléfono: ");
                        String phone = scanner.nextLine();
                        System.out.print("Correo electrónico: ");
                        String email = scanner.nextLine();

                        customerService.register(fullName, document, phone, email);
                        System.out.println("¡Cliente registrado con éxito!");
                    }
                    case 2 -> {
                        System.out.println("\n--- Consultar Clientes ---");
                        System.out.println("1. Listar todos");
                        System.out.println("2. Buscar por número de documento");
                        System.out.println("3. Buscar por nombre");
                        System.out.print("Elige una opción de consulta: ");
                        int subOpt = scanner.nextInt();
                        scanner.nextLine();

                        if (subOpt == 1) {
                            List<Customer> customers = customerService.listAll();
                            if (customers.isEmpty()) {
                                System.out.println("No hay clientes registrados.");
                            } else {
                                customers.forEach(c -> System.out.println("- " + c));
                            }
                        } else if (subOpt == 2) {
                            System.out.print("Ingresa el número de documento: ");
                            String document = scanner.nextLine();
                            Customer c = customerService.requireByDocument(document);
                            System.out.println("Encontrado: " + c);
                        } else if (subOpt == 3) {
                            System.out.print("Ingresa el texto de búsqueda (nombre): ");
                            String text = scanner.nextLine();
                            List<Customer> matches = customerService.searchByName(text);
                            if (matches.isEmpty()) {
                                System.out.println("No se encontraron coincidencias.");
                            } else {
                                matches.forEach(c -> System.out.println("- " + c));
                            }
                        }
                    }
                    case 3 -> {
                        System.out.println("\n--- Modificar Cliente ---");
                        System.out.print("Ingresa el número de documento del cliente a modificar: ");
                        String document = scanner.nextLine();

                        // Verificamos que exista primero
                        customerService.requireByDocument(document);

                        System.out.print("Nuevo nombre completo: ");
                        String fullName = scanner.nextLine();
                        System.out.print("Nuevo teléfono: ");
                        String phone = scanner.nextLine();
                        System.out.print("Nuevo correo electrónico: ");
                        String email = scanner.nextLine();

                        customerService.update(document, fullName, phone, email);
                        System.out.println("¡Cliente modificado con éxito!");
                    }
                    case 4 -> {
                        System.out.println("\n--- Eliminar Cliente ---");
                        System.out.print("Ingresa el número de documento del cliente a eliminar: ");
                        String document = scanner.nextLine();

                        customerService.delete(document);
                        System.out.println("¡Cliente eliminado con éxito!");
                    }
                    case 0 -> System.out.println("Volviendo al menú principal...");
                    default -> System.out.println("Opción inválida, intenta de nuevo.");
                }
            } catch (Exception e) {
                System.out.println("Error: " + e.getMessage());
            }
        } while (option != 0);
    }

    private void handleWarranties() {
        
        int option;
        do {
            System.out.println("\n-- Módulo de Garantías --");
            System.out.println("1. Registrar garantía");
            System.out.println("2. Consultar garantías");
            System.out.println("0. Volver al menú principal");
            System.out.print("Elige una opción: ");

            option = scanner.nextInt();
            scanner.nextLine(); // Limpiar el buffer

            try {
                switch (option) {
                    case 1 -> {
                        System.out.println("\n--- Registrar Garantía ---");
                        System.out.print("Número de serie del producto: ");
                        String serial = scanner.nextLine();
                        System.out.print("Número de documento del cliente: ");
                        String document = scanner.nextLine();
                        System.out.print("Fecha de compra (YYYY-MM-DD): ");
                        LocalDate purchaseDate = LocalDate.parse(scanner.nextLine());
                        System.out.print("Meses de cobertura: ");
                        int coverageMonths = scanner.nextInt();
                        scanner.nextLine(); // Limpiar buffer

                        warrantyService.register(serial, document, purchaseDate, coverageMonths);
                        System.out.println("¡Garantía registrada con éxito!");
                    }
                    case 2 -> {
                        System.out.println("\n--- Consultar Garantías ---");
                        System.out.println("1. Buscar por ID de garantía");
                        System.out.println("2. Buscar por número de serie de producto");
                        System.out.println("3. Buscar por documento de cliente");
                        System.out.println("4. Listar garantías activas");
                        System.out.println("5. Listar garantías próximas a vencer");
                        System.out.print("Elige una opción de consulta: ");
                        int subOpt = scanner.nextInt();
                        scanner.nextLine();

                        if (subOpt == 1) {
                            System.out.print("Ingresa el ID de la garantía: ");
                            String id = scanner.nextLine();
                            warrantyService.findById(id).ifPresentOrElse(
                                w -> System.out.println("Encontrada: " + w),
                                () -> System.out.println("No se encontró ninguna garantía con ese ID.")
                            );
                        } else if (subOpt == 2) {
                            System.out.print("Ingresa el número de serie: ");
                            String serial = scanner.nextLine();
                            List<Warranty> warranties = warrantyService.findBySerial(serial);
                            if (warranties.isEmpty()) {
                                System.out.println("No hay garantías registradas para este producto.");
                            } else {
                                warranties.forEach(w -> System.out.println("- " + w));
                            }
                        } else if (subOpt == 3) {
                            System.out.print("Ingresa el documento del cliente: ");
                            String document = scanner.nextLine();
                            List<Warranty> warranties = warrantyService.findByCustomer(document);
                            if (warranties.isEmpty()) {
                                System.out.println("No hay garantías registradas para este cliente.");
                            } else {
                                warranties.forEach(w -> System.out.println("- " + w));
                            }
                        } else if (subOpt == 4) {
                            List<Warranty> active = warrantyService.listActive();
                            if (active.isEmpty()) {
                                System.out.println("No hay garantías activas actualmente.");
                            } else {
                                active.forEach(w -> System.out.println("- " + w));
                            }
                        } else if (subOpt == 5) {
                            System.out.print("Ingresa el número de días límite para el vencimiento: ");
                            int days = scanner.nextInt();
                            scanner.nextLine();
                            List<Warranty> expiring = warrantyService.listExpiringWithin(days);
                            if (expiring.isEmpty()) {
                                System.out.println("No hay garantías próximas a vencer en ese rango de días.");
                            } else {
                                expiring.forEach(w -> System.out.println("- " + w));
                            }
                        }
                    }
                    case 0 -> System.out.println("Volviendo al menú principal...");
                    default -> System.out.println("Opción inválida, intenta de nuevo.");
                }
            } catch (Exception e) {
                System.out.println("Error: " + e.getMessage());
            }
        } while (option != 0);
    }

    private void handleClaims() {
        
        int option;
        do {
            System.out.println("\n-- Módulo de Reclamos --");
            System.out.println("1. Radicar / Registrar reclamo");
            System.out.println("2. Cambiar estado de reclamo");
            System.out.println("3. Consultar reclamos");
            System.out.println("0. Volver al menú principal");
            System.out.print("Elige una opción: ");

            option = scanner.nextInt();
            scanner.nextLine(); // Limpiar el buffer

            try {
                switch (option) {
                    case 1 -> {
                        System.out.println("\n--- Radicar Reclamo ---");
                        System.out.print("ID de la garantía: ");
                        String warrantyId = scanner.nextLine();
                        System.out.print("Descripción del reclamo: ");
                        String description = scanner.nextLine();
                        System.out.print("Fecha del reclamo (YYYY-MM-DD): ");
                        LocalDate claimDate = LocalDate.parse(scanner.nextLine());
                        
                        System.out.print("¿Deseas asignar un estado inicial específico? (1: PENDIENTE, 2: Otro / Dejar por defecto): ");
                        int statusOpt = scanner.nextInt();
                        scanner.nextLine(); // Limpiar buffer

                        ClaimStatus requestedStatus = null;
                        if (statusOpt == 1) {
                            requestedStatus = ClaimStatus.PENDING;
                        } // Si deseas más opciones de estado inicial puedes agregarlas aquí, o dejarlo nulo para que tome el por defecto.

                        claimService.file(warrantyId, description, claimDate, requestedStatus);
                        System.out.println("¡Reclamo radicado con éxito!");
                    }
                    case 2 -> {
                        System.out.println("\n--- Cambiar Estado de Reclamo ---");
                        System.out.print("ID del reclamo: ");
                        String claimId = scanner.nextLine();
                        
                        System.out.println("Selecciona el nuevo estado:");
                        System.out.println("1. PENDIENTE (PENDING)");
                        System.out.println("2. APROBADO (APPROVED)");
                        System.out.println("3. RECHAZADO (REJECTED)");
                        System.out.println("4. RESUELTO (RESOLVED)");
                        System.out.print("Opción de estado: ");
                        int stateOpt = scanner.nextInt();
                        scanner.nextLine();

                        ClaimStatus newStatus = switch (stateOpt) {
                            case 1 -> ClaimStatus.PENDING;
                            case 2 -> ClaimStatus.RESOLVED;
                            case 3 -> ClaimStatus.REJECTED;
                            case 4 -> ClaimStatus.RESOLVED;
                            default -> throw new DomainException("Estado no válido.");
                        };

                        claimService.changeStatus(claimId, newStatus);
                        System.out.println("¡Estado del reclamo actualizado con éxito!");
                    }
                    case 3 -> {
                        System.out.println("\n--- Consultar Reclamos ---");
                        System.out.println("1. Listar todos");
                        System.out.println("2. Buscar por ID de reclamo");
                        System.out.println("3. Buscar por ID de garantía");
                        System.out.println("4. Listar pendientes");
                        System.out.println("5. Listar abiertos");
                        System.out.print("Elige una opción de consulta: ");
                        int subOpt = scanner.nextInt();
                        scanner.nextLine();

                        if (subOpt == 1) {
                            List<Claim> claims = claimService.listAll();
                            if (claims.isEmpty()) {
                                System.out.println("No hay reclamos registrados.");
                            } else {
                                claims.forEach(c -> System.out.println("- " + c));
                            }
                        } else if (subOpt == 2) {
                            System.out.print("Ingresa el ID del reclamo: ");
                            String id = scanner.nextLine();
                            claimService.findById(id).ifPresentOrElse(
                                c -> System.out.println("Encontrado: " + c),
                                () -> System.out.println("No se encontró ningún reclamo con ese ID.")
                            );
                        } else if (subOpt == 3) {
                            System.out.print("Ingresa el ID de la garantía: ");
                            String warrantyId = scanner.nextLine();
                            List<Claim> claims = claimService.finBYWarranty(warrantyId);
                            if (claims.isEmpty()) {
                                System.out.println("No hay reclamos para esta garantía.");
                            } else {
                                claims.forEach(c -> System.out.println("- " + c));
                            }
                        } else if (subOpt == 4) {
                            List<Claim> pending = claimService.listPending();
                            if (pending.isEmpty()) {
                                System.out.println("No hay reclamos pendientes.");
                            } else {
                                pending.forEach(c -> System.out.println("- " + c));
                            }
                        } else if (subOpt == 5) {
                            List<Claim> open = claimService.listOpen();
                            if (open.isEmpty()) {
                                System.out.println("No hay reclamos abiertos.");
                            } else {
                                open.forEach(c -> System.out.println("- " + c));
                            }
                        }
                    }
                    case 0 -> System.out.println("Volviendo al menú principal...");
                    default -> System.out.println("Opción inválida, intenta de nuevo.");
                }
            } catch (Exception e) {
                System.out.println("Error: " + e.getMessage());
            }
        } while (option != 0);
    }
}
