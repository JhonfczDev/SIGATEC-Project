# SIGATEC: descripción de las capas

Sistema de Control de Garantías para tiendas de tecnología. Arquitectura en capas con la regla de dependencia apuntando **hacia adentro**: las capas externas conocen a las internas, nunca al revés. El modelo no depende de nada.

Documentos relacionados: `01-diagrama-de-paquetes.md`, `02-diagrama-de-clases-modelo.md`, `03-diagrama-de-clases-repositorios.md`, `04-diagrama-de-clases-servicios.md` y `05-diagrama-de-clases-ui.md`.

---

## Capas y su responsabilidad

| Capa | Paquetes | Responsabilidad | Lo que NO debe hacer |
|---|---|---|---|
| **Presentación** | `ui`, `ui.view` | Dos interfaces sobre los mismos servicios: consola (`ConsoleUI`) y gráfica JavaFX (`MainApp` + una pantalla por área). Leen datos del usuario, llaman a los servicios y muestran resultados y errores. | Reglas de negocio. |
| **Servicios** | `service` | Reglas de negocio y casos de uso: registrar sin duplicar, comprobar que existan producto y cliente antes de crear una garantía, registrar reclamaciones y cambiar su estado, eliminar sin romper relaciones (`RemovalService`) y reportes (`ReportService`). Usan `Clock` para la fecha actual. | Conocer la interfaz o cómo se guardan los datos. |
| **Modelo (dominio)** | `model` | Entidades (`Product`, `Customer`, `Warranty`, `Claim`) y sus invariantes: datos válidos, vigencia de la garantía, transiciones de estado permitidas. | Depender de otras capas, del reloj del sistema o del almacenamiento. |
| **Repositorios: contratos** | `repository` | Definen **qué** se guarda y se busca: `Repository<T,K>` y una interfaz por entidad. Incluye `SequentialIdGenerator`, `PersistenceException` y `SeedData`. | Reglas de negocio. |
| **Repositorios: implementaciones** | `repository.memory`, `.file`, `.jdbc` | Definen **cómo** se guarda: en memoria, en archivos de texto o en base de datos SQL. Son intercambiables. | Decidir reglas de negocio. |
| **Arranque** | `com.sigatec` (`Main`, `Launcher`) y `ui.MainApp` | Crear los objetos concretos y conectarlos. `Main` arranca la consola; `Launcher` arranca `MainApp` (JavaFX). | Lógica de negocio. |
| **Datos y recursos** | `../data`, `../database`, `resources/` | `data/*.txt`: persistencia en texto. `database/*.sql`: scripts SQL. `resources/com/sigatec/ui`: `app.css` e `icon.png` de la interfaz gráfica. | — |

---

## Detalle por capa

### Modelo
Las cuatro entidades protegen sus propias reglas, de modo que un objeto nunca queda en estado inválido.
- `Product` (identidad: número de serie) y `Customer` (identidad: documento) se modifican de forma atómica con `updateDetails`.
- `Warranty` es inmutable; su vencimiento y su vigencia se calculan, no se guardan.
- `Claim` cambia de estado solo con `changeStatus`, que valida la transición y registra un `StatusChange` en el historial.
- Transiciones: `PENDING` → cualquier otro estado; `IN_REPAIR` → `RESOLVED` o `REJECTED`; `RESOLVED` y `REJECTED` son finales.

### Repositorios
- Los cuatro contratos extienden `Repository<T,K>` (`save`, `findById`, `findAll`, `existsById`, `deleteById`) y agregan sus búsquedas.
- **Memoria:** `InMemoryRepository` implementa el CRUD una vez; su gancho `afterChange()` permite reaccionar a cada cambio.
- **Archivos:** extienden los de memoria. `FileStorage` los abre en orden y siembra `SeedData` en la primera ejecución; `TextFileStore` es la única clase que toca archivos.
- **SQL:** `Jdbc*Repository` implementan los contratos directamente (no heredan de memoria) y `Database` entrega la conexión desde una configuración en `Properties`. Garantías y reclamaciones resuelven sus referencias a través de otros repositorios, igual que las versiones de archivo.

### Servicios
- Cada entidad tiene su servicio (`ProductService`, `CustomerService`, `WarrantyService`, `ClaimService`).
- Las interfaces `ProductFinder`, `CustomerFinder` y `WarrantyFinder` permiten que un servicio consulte entidades sin depender de la clase concreta de otro. `WarrantyService` usa `ProductFinder` y `CustomerFinder`; `ClaimService`, `RemovalService` y `ReportService` usan `WarrantyFinder`.
- `RemovalService` coordina la eliminación de productos y clientes (por su dependencia de `WarrantyFinder`, presumiblemente comprueba que no tengan garantías).
- `ReportService` entrega garantías activas, garantías por vencer y reclamaciones pendientes.

### Presentación
- **Gráfica (JavaFX):** `MainApp` construye todo y navega entre seis pantallas que implementan `Screen` (`build()` y `refresh()`). `UiSupport` reúne los ayudantes visuales compartidos (tablas, diálogos, títulos).
- **Consola:** `ConsoleUI` trabaja con los servicios de producto, cliente, garantía y reclamación.

---

## Principios que sostienen la separación
- **Inversión de dependencias:** los servicios dependen de las interfaces de repositorio, no de memoria, archivos o SQL.
- **Responsabilidad única:** cada capa y cada clase tiene una sola razón de cambio.
- **Abierto/cerrado:** agregar una persistencia nueva es crear otro paquete; los servicios no cambian (así entró `jdbc`).
- **Segregación de interfaces:** cada repositorio y cada `*Finder` expone solo lo que sus clientes necesitan.
- **Ley de Demeter:** el modelo ofrece preguntas directas (`belongsToCustomer`, `isForWarranty`) para que las capas superiores no encadenen getters.

---

## Observaciones sobre el código actual

Hallazgos de revisar la estructura; ninguno impide que el sistema funcione.

1. **Dependencia circular `ui` ↔ `ui.view`.** Las vistas importan `Screen` y `UiSupport` (de `ui`), y `MainApp` (de `ui`) importa las vistas. Una salida simple: mover `Screen` y `UiSupport` a un paquete propio (por ejemplo `ui.support`) que no dependa de `ui.view`.
2. **`MainApp` también es raíz de composición.** Importa `FileStorage`, los cuatro `Jdbc*Repository` y los servicios, es decir, el paquete `ui` conoce implementaciones de persistencia. Lo habitual es dejar ese cableado en `com.sigatec`.
3. **Errores de escritura en nombres:** `ClaimService.finBYWarranty` (¿`findByWarranty`?), `RemovalService.deleteProdut` (¿`deleteProduct`?) y el campo `waranties` de `RemovalService`. Los diagramas usan los nombres reales; si se renombran, hay que actualizarlos.
4. **Uso desigual de interfaces.** Casi todos los servicios dependen de `*Finder`, pero `ReportService` depende de `ClaimService` concreto y `RemovalService` de `ProductService` y `CustomerService` concretos.
5. **`ConsoleUI` no recibe `RemovalService`.** Si ofrece eliminar productos o clientes, no pasaría por la regla de eliminación. Conviene verificarlo.
6. **Configuración de la base de datos.** `Database.loadConfig` lee un `Properties`, pero `resources` solo contiene `app.css` e `icon.png`; el origen de esa configuración no es visible.
