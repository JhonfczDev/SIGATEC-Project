# Diagrama de clases — Sistema de garantías

Propuesta de diagrama de clases para el proyecto de Programación 3 (Java): sistema de seguimiento y control de garantías para tiendas de tecnología.

Los atributos y métodos son una propuesta; ajústalos a lo definido por el grupo.

```mermaid
classDiagram
    direction TB

    %% ===== Model =====
    class Product {
        -String id
        -String name
        -String brand
        -String model
        -double price
        +getId() String
        +getName() String
    }

    class Client {
        -String id
        -String name
        -String phone
        -String email
        +getId() String
        +getName() String
    }

    class Warranty {
        -String id
        -Product product
        -Client client
        -LocalDate startDate
        -LocalDate endDate
        -String status
        +isActive() boolean
    }

    class Claim {
        -String id
        -Warranty warranty
        -LocalDate date
        -String description
        -String status
    }

    %% ===== Repository =====
    class ProductRepository {
        -List~Product~ products
        +save(Product) void
        +findById(String) Product
        +findAll() List~Product~
        +delete(String) void
    }

    class ClientRepository {
        -List~Client~ clients
        +save(Client) void
        +findById(String) Client
        +findAll() List~Client~
        +delete(String) void
    }

    class WarrantyRepository {
        -List~Warranty~ warranties
        +save(Warranty) void
        +findById(String) Warranty
        +findAll() List~Warranty~
        +findByClient(String) List~Warranty~
    }

    class ClaimRepository {
        -List~Claim~ claims
        +save(Claim) void
        +findById(String) Claim
        +findAll() List~Claim~
        +findByWarranty(String) List~Claim~
    }

    %% ===== Service =====
    class ProductService {
        -ProductRepository productRepository
        +registerProduct(Product) void
        +findProduct(String) Product
        +listProducts() List~Product~
    }

    class ClientService {
        -ClientRepository clientRepository
        +registerClient(Client) void
        +findClient(String) Client
        +listClients() List~Client~
    }

    class WarrantyService {
        -WarrantyRepository warrantyRepository
        -ProductRepository productRepository
        -ClientRepository clientRepository
        +createWarranty(String productId, String clientId) Warranty
        +checkWarranty(String id) boolean
        +listWarranties() List~Warranty~
    }

    class ClaimService {
        -ClaimRepository claimRepository
        -WarrantyRepository warrantyRepository
        +openClaim(String warrantyId, String description) Claim
        +updateClaimStatus(String id, String status) void
        +listClaims() List~Claim~
    }

    %% ===== UI =====
    class ConsoleUtils {
        <<utility>>
        +readInt(String prompt) int
        +readString(String prompt) String
        +readDate(String prompt) LocalDate
        +pause() void
        +clearScreen() void
    }

    class MainMenu {
        -ProductMenu productMenu
        -ClientMenu clientMenu
        -WarrantyMenu warrantyMenu
        -ClaimMenu claimMenu
        -ReportMenu reportMenu
        +show() void
    }

    class ProductMenu {
        -ProductService productService
        +show() void
    }

    class ClientMenu {
        -ClientService clientService
        +show() void
    }

    class WarrantyMenu {
        -WarrantyService warrantyService
        +show() void
    }

    class ClaimMenu {
        -ClaimService claimService
        +show() void
    }

    class ReportMenu {
        -WarrantyService warrantyService
        -ClaimService claimService
        +show() void
    }

    %% ===== Integración =====
    class Main {
        +main(String[] args)$ void
    }

    %% ===== Relaciones de modelo =====
    Client "1" --> "*" Warranty : posee
    Product "1" --> "*" Warranty : cubierta por
    Warranty "1" --> "*" Claim : genera

    %% ===== Repository -> Model =====
    ProductRepository ..> Product
    ClientRepository ..> Client
    WarrantyRepository ..> Warranty
    ClaimRepository ..> Claim

    %% ===== Service -> Repository =====
    ProductService --> ProductRepository
    ClientService --> ClientRepository
    WarrantyService --> WarrantyRepository
    WarrantyService --> ProductRepository
    WarrantyService --> ClientRepository
    ClaimService --> ClaimRepository
    ClaimService --> WarrantyRepository

    %% ===== UI -> Service =====
    ProductMenu --> ProductService
    ClientMenu --> ClientService
    WarrantyMenu --> WarrantyService
    ClaimMenu --> ClaimService
    ReportMenu --> WarrantyService
    ReportMenu --> ClaimService

    MainMenu *-- ProductMenu
    MainMenu *-- ClientMenu
    MainMenu *-- WarrantyMenu
    MainMenu *-- ClaimMenu
    MainMenu *-- ReportMenu

    ProductMenu ..> ConsoleUtils
    ClientMenu ..> ConsoleUtils
    WarrantyMenu ..> ConsoleUtils
    ClaimMenu ..> ConsoleUtils
    ReportMenu ..> ConsoleUtils
    MainMenu ..> ConsoleUtils

    %% ===== Integración =====
    Main ..> MainMenu : inicia
```

## Notas de diseño

- **Capas:** Model → Repository → Service → UI, con `Main` como punto de entrada. Cada capa solo depende de la inmediatamente inferior, lo que permite cambiar el almacenamiento (memoria, archivos, base de datos) sin tocar los menús.
- **`WarrantyService` y `ClaimService`** dependen de varios repositorios porque validan la existencia de producto, cliente o garantía antes de crear el registro.
- **`ConsoleUtils`** concentra la lectura de datos por consola, de modo que los menús no repiten código de entrada.
