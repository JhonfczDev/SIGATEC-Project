# SIGATEC: diagrama de clases de los servicios (`com.sigatec.service`)

Se omiten los métodos privados y las clases del modelo y de `repository` que solo se usan como tipos (se muestran las cuatro interfaces de repositorio que los servicios guardan en un atributo).


```mermaid
classDiagram
    class ProductFinder {
        <<interface>>
        +findBySerial(serial) Optional~Product~
    }
    class CustomerFinder {
        <<interface>>
        +findByDocument(document) Optional~Customer~
    }
    class WarrantyFinder {
        <<interface>>
        +findById(id) Optional~Warranty~
        +findBySerial(serial) List~Warranty~
        +findByCustomer(document) List~Warranty~
        +listActive() List~Warranty~
        +listExpiringWithin(days) List~Warranty~
    }

    class ProductService {
        -ProductRepository products
        +register(name, brand, model, serial, price) Product
        +findBySerial(serial) Optional~Product~
        +requireBySerial(serial) Product
        +search(text) List~Product~
        +listAll() List~Product~
        +update(serial, name, brand, model, price) Product
        +delete(serial)
    }
    class CustomerService {
        -CustomerRepository customers
        +register(fullName, document, phone, email) Customer
        +findByDocument(document) Optional~Customer~
        +requireByDocument(document) Customer
        +searchByName(text) List~Customer~
        +listAll() List~Customer~
        +update(document, fullName, phone, email) Customer
        +delete(document)
    }
    class WarrantyService {
        -WarrantyRepository warranties
        -ProductFinder products
        -CustomerFinder customers
        -Clock clock
        +register(serial, document, purchaseDate, coverageMonths) Warranty
        +findById(id) Optional~Warranty~
        +findBySerial(serial) List~Warranty~
        +findByCustomer(document) List~Warranty~
        +listActive() List~Warranty~
        +listExpiringWithin(days) List~Warranty~
        +listAll() List~Warranty~
    }
    class ClaimService {
        -ClaimRepository claims
        -WarrantyFinder warranties
        -Clock clock
        +file(warrantyId, description, claimDate, requestedStatus) Claim
        +changeStatus(claimId, newStatus) Claim
        +findById(id) Optional~Claim~
        +listAll() List~Claim~
        +finBYWarranty(warrantyId) List~Claim~
        +listPending() List~Claim~
        +listOpen() List~Claim~
    }
    class RemovalService {
        -ProductService productService
        -CustomerService customerService
        -WarrantyFinder waranties
        +deleteProdut(serial)
        +deleteCustomer(document)
    }
    class ReportService {
        -WarrantyFinder warranties
        -ClaimService claims
        +activeWarranties() List~Warranty~
        +warrantiesExpiringWithin(days) List~Warranty~
        +pendingClaims() List~Claim~
    }

    class ProductRepository {
        <<interface>>
    }
    class CustomerRepository {
        <<interface>>
    }
    class WarrantyRepository {
        <<interface>>
    }
    class ClaimRepository {
        <<interface>>
    }

    ProductFinder <|.. ProductService
    CustomerFinder <|.. CustomerService
    WarrantyFinder <|.. WarrantyService

    ProductService --> ProductRepository
    CustomerService --> CustomerRepository
    WarrantyService --> WarrantyRepository
    ClaimService --> ClaimRepository

    WarrantyService --> ProductFinder
    WarrantyService --> CustomerFinder
    ClaimService --> WarrantyFinder

    RemovalService --> ProductService
    RemovalService --> CustomerService
    RemovalService --> WarrantyFinder

    ReportService --> WarrantyFinder
    ReportService --> ClaimService
```

## Clases

| Clase | Responsabilidad |
|---|---|
| `ProductService` | Casos de uso de productos: registrar, buscar (`findBySerial` devuelve `Optional`; `requireBySerial` falla si no existe), buscar por texto, listar, modificar y eliminar. Implementa `ProductFinder`. |
| `CustomerService` | Lo mismo para clientes, con búsqueda por nombre. Implementa `CustomerFinder`. |
| `WarrantyService` | Registrar garantías (consulta producto y cliente a través de sus `*Finder`) y listar o buscar por id, serie, cliente, vigencia o vencimiento próximo. Usa `Clock`. Implementa `WarrantyFinder`. |
| `ClaimService` | Presentar una reclamación sobre una garantía (`file`), cambiar su estado, buscarla y listarla (todas, pendientes, abiertas, por garantía). Usa `Clock`. |
| `RemovalService` | Eliminar productos y clientes. *Por su dependencia de `WarrantyFinder`, presumiblemente comprueba que no tengan garantías antes de borrar.* |
| `ReportService` | Garantías activas, garantías por vencer en N días y reclamaciones pendientes (estas últimas a través de `ClaimService`). |
| `ProductFinder`, `CustomerFinder`, `WarrantyFinder` | Interfaces de consulta de solo lectura: permiten que un servicio busque una entidad sin depender de la clase concreta de otro. |

## Relaciones
- Cada servicio de entidad implementa su `*Finder`.
- `WarrantyService` consulta productos y clientes por interfaz; `ClaimService`, `RemovalService` y `ReportService` consultan garantías por interfaz.
- Todos usan las entidades y `DomainException` del modelo (los servicios de producto, cliente, garantía, reclamación y eliminación importan esa excepción).
