# SIGATEC: diagrama de clases de la interfaz (`com.sigatec.ui`)

Hay **dos interfaces** sobre los mismos servicios: la de consola (`ConsoleUI`, arrancada por `Main`) y la gráfica JavaFX (`MainApp` y seis pantallas, arrancada por `Launcher`). Se omiten los campos de componentes gráficos (`TextField`, `Button`, `TableView`, ...) y los métodos privados auxiliares.


```mermaid
classDiagram
    class Screen {
        <<interface>>
        +build() Parent
        +refresh()
    }

    class UiSupport {
        <<utility>>
        +textColumn(title, value)$ TableColumn
        +showError(message)$
        +showInfo(message)$
        +confirm(message)$ boolean
        +run(action)$
        +sectionTitle(text)$ Label
        +card(children)$ VBox
        +pageTitle(text)$ Label
        +pageSubtitle(text)$ Label
    }

    class ProductsView {
        -ProductService products
        -RemovalService removal
        -Product selected
        +build() Parent
        +refresh()
        -save()
        -delete()
    }
    class CustomersView {
        -CustomerService customers
        -RemovalService removal
        -Customer selected
        +build() Parent
        +refresh()
        -save()
        -delete()
    }
    class WarrantiesView {
        -WarrantyService warranties
        +build() Parent
        +refresh()
        -register()
    }
    class ClaimsView {
        -ClaimService claims
        +build() Parent
        +refresh()
        -file()
        -changeStatus()
    }
    class ReportsView {
        -ReportService reports
        +build() Parent
        +refresh()
    }
    class DashboardView {
        -ReportService reports
        -ProductService products
        +build() Parent
        +refresh()
    }

    class ConsoleUI {
        -ProductService productService
        -CustomerService customerService
        -WarrantyService warrantyService
        -ClaimService claimService
        -Scanner scanner
        +start()
        -printMenu()
        -handleProducts()
        -handleCustomers()
        -handleWarranties()
        -handleClaims()
    }
    class MainApp {
        <<JavaFX Application>>
        +start(stage)
        +main(args)$
    }
    class Launcher {
        +main(args)$
    }
    class Main {
        +main(args)$
    }

    class ProductService {
        <<service>>
    }
    class CustomerService {
        <<service>>
    }
    class WarrantyService {
        <<service>>
    }
    class ClaimService {
        <<service>>
    }
    class ReportService {
        <<service>>
    }
    class RemovalService {
        <<service>>
    }
    class FileStorage {
        <<repository.file>>
    }
    class JdbcRepositories {
        <<repository.jdbc>>
        JdbcProductRepository
        JdbcCustomerRepository
        JdbcWarrantyRepository
        JdbcClaimRepository
    }

    Screen <|.. ProductsView
    Screen <|.. CustomersView
    Screen <|.. WarrantiesView
    Screen <|.. ClaimsView
    Screen <|.. ReportsView
    Screen <|.. DashboardView

    ProductsView ..> UiSupport
    CustomersView ..> UiSupport
    WarrantiesView ..> UiSupport
    ClaimsView ..> UiSupport
    ReportsView ..> UiSupport
    DashboardView ..> UiSupport

    ProductsView --> ProductService
    ProductsView --> RemovalService
    CustomersView --> CustomerService
    CustomersView --> RemovalService
    WarrantiesView --> WarrantyService
    ClaimsView --> ClaimService
    ReportsView --> ReportService
    DashboardView --> ReportService
    DashboardView --> ProductService

    ConsoleUI --> ProductService
    ConsoleUI --> CustomerService
    ConsoleUI --> WarrantyService
    ConsoleUI --> ClaimService

    MainApp ..> Screen
    MainApp ..> ProductsView
    MainApp ..> CustomersView
    MainApp ..> WarrantiesView
    MainApp ..> ClaimsView
    MainApp ..> ReportsView
    MainApp ..> DashboardView
    MainApp ..> FileStorage
    MainApp ..> JdbcRepositories

    Launcher ..> MainApp
    Main ..> ConsoleUI
    Main ..> FileStorage
```

## Clases

| Clase | Paquete | Responsabilidad |
|---|---|---|
| `Screen` | `ui` | Contrato de una pantalla: `build()` crea su contenido y `refresh()` lo actualiza. |
| `UiSupport` | `ui` | Ayudantes estáticos compartidos por las pantallas: columnas de tabla, diálogos de error, información y confirmación, ejecución protegida de acciones, títulos y tarjetas. |
| `ProductsView`, `CustomersView` | `ui.view` | Formulario, búsqueda y tabla; reciben `RemovalService`, presumiblemente para eliminar con su regla de integridad. |
| `WarrantiesView` | `ui.view` | Registrar garantías y filtrarlas (todas, vigentes, vencidas). |
| `ClaimsView` | `ui.view` | Presentar reclamaciones, filtrar por estado y cambiarlo. |
| `ReportsView` | `ui.view` | Garantías activas, garantías por vencer (días configurables) y reclamaciones pendientes. |
| `DashboardView` | `ui.view` | Panel con indicadores (garantías activas, por vencer, reclamaciones pendientes y total de productos) y una tabla de garantías por vencer en 30 días, según los nombres de sus campos. |
| `ConsoleUI` | `ui` | Interfaz de consola con menús para productos, clientes, garantías y reclamaciones. |
| `MainApp` | `ui` | Aplicación JavaFX: construye las dependencias, las pantallas y la navegación entre ellas. |
| `Launcher` | `com.sigatec` | Punto de entrada que lanza `MainApp`. |
| `Main` | `com.sigatec` | Punto de entrada de la consola: abre `FileStorage`, crea los servicios y arranca `ConsoleUI`. |

## Relaciones
- Las seis pantallas implementan `Screen` y usan `UiSupport`.
- `ProductsView` y `CustomersView` reciben `RemovalService` además de su servicio.
- `ReportsView` y `DashboardView` usan `ReportService`; `DashboardView` agrega `ProductService` para el total de productos.
- `ConsoleUI` **no** usa `Screen`, `ReportService` ni `RemovalService`.
- `MainApp` importa `FileStorage` y los cuatro `Jdbc*Repository`: el origen de la persistencia se decide dentro del paquete `ui`.