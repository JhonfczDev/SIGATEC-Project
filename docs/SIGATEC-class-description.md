# SIGATEC: descripción de las clases

Responsabilidad, atributos principales y métodos relevantes de las 56 clases del proyecto, agrupadas por paquete. Para el diagrama que las relaciona ver `06-diagrama-de-clases-general.md`.

> Se omiten los getters simples, `equals`, `hashCode` y `toString`, salvo que tengan una regla propia.

| Paquete | Clases |
|---|---|
| `model` | 10 |
| `repository` | 8 |
| `repository.memory` | 5 |
| `repository.file` | 7 |
| `repository.jdbc` | 5 |
| `service` | 9 |
| `ui` | 4 |
| `ui.view` | 6 |
| `com.sigatec` | 2 |

---

## 1. Modelo (`com.sigatec.model`)

### `Identifiable<K>` (interfaz)
- **Responsabilidad:** contrato de toda entidad con identidad única; permite escribir repositorios genéricos.
- **Métodos:** `K getId()`.

### `Product`
- **Responsabilidad:** producto individual, identificado por su número de serie. Garantiza que nunca quede con datos inválidos.
- **Atributos:** `serialNumber` (inmutable, se guarda en mayúsculas), `name`, `brand`, `model`, `price` (`BigDecimal`).
- **Métodos relevantes:**
  - `Product(name, brand, model, serialNumber, price)`: valida que los textos no estén vacíos y que el precio sea mayor que cero.
  - `updateDetails(name, brand, model, price)`: modifica los cuatro datos de forma atómica (si alguno es inválido no se aplica ninguno).
  - `hasSerialNumber(serial)`: compara con un texto escrito por el usuario, ignorando mayúsculas y espacios.
- **Igualdad:** por número de serie.

### `Customer`
- **Responsabilidad:** cliente de la tienda, identificado por su documento.
- **Atributos:** `documentNumber` (inmutable), `fullName`, `phone`, `email`; patrones `PHONE` y `EMAIL` para validar.
- **Métodos relevantes:**
  - `Customer(fullName, documentNumber, phone, email)`: limpia el teléfono (quita espacios, guiones y paréntesis; exige de 7 a 15 dígitos) y pasa el email a minúsculas validando su formato.
  - `updateDetails(fullName, phone, email)`: actualización atómica.
  - `hasDocumentNumber(document)`: compara con el texto del usuario, ignorando espacios.
- **Igualdad:** por documento.

### `Warranty`
- **Responsabilidad:** garantía que une un producto con un cliente. Es **inmutable** y calcula su propia vigencia.
- **Atributos:** `id`, `product`, `customer`, `purchaseDate`, `coverageMonths`, `expirationDate` (= compra + meses).
- **Métodos relevantes** (los que dependen del tiempo reciben `today` como parámetro; el modelo nunca consulta el reloj):
  - `isActive(today)`: vigente hasta el día de vencimiento **inclusive**.
  - `statusOn(today)`: devuelve `WarrantyStatus`.
  - `remainingDays(today)`: días que faltan; 0 si ya venció.
  - `expiresWithin(days, today)`: vigente y con vencimiento dentro de los próximos `days` días.
  - `belongsToCustomer(documentNumber)`, `coversProductWithSerial(serialNumber)`: preguntas directas para no encadenar getters.

### `Claim`
- **Responsabilidad:** reclamación sobre una garantía. Es la dueña de su ciclo de vida: nadie puede saltarse las transiciones de estado.
- **Atributos:** `id`, `warranty`, `description`, `claimDate`, `status`, `history` (`List<StatusChange>`).
- **Métodos relevantes:**
  - Dos constructores: uno crea la reclamación en `PENDING`; el otro recibe el estado inicial. Ambos rechazan una fecha anterior a la compra de la garantía y registran el primer elemento del historial.
  - `changeStatus(newStatus, date)`: valida la transición (`ClaimStatus.canTransitionTo`) y que la fecha no sea anterior al último cambio; luego agrega un `StatusChange`.
  - `isPending()`, `isOpen()` (pendiente o en reparación), `isForWarranty(warrantyId)`.
  - `getHistory()`: vista de solo lectura, en orden cronológico.

### `StatusChange` (record)
- **Responsabilidad:** registro inmutable de un cambio de estado (trazabilidad del postventa).
- **Componentes:** `status` (`ClaimStatus`), `date` (`LocalDate`). El constructor compacto exige que ninguno sea nulo.

### `ClaimStatus` (enum)
- **Responsabilidad:** estados de una reclamación y transiciones permitidas.
- **Constantes:** `PENDING` ("Pendiente"), `IN_REPAIR` ("En reparación"), `RESOLVED` ("Resuelta"), `REJECTED` ("Rechazada"). Atributo: `label`.
- **Métodos relevantes:**
  - `canTransitionTo(target)`: `PENDING` → cualquier otro; `IN_REPAIR` → `RESOLVED` o `REJECTED`; los finales no cambian.
  - `isFinal()`: `RESOLVED` o `REJECTED`.
  - `fromLabel(text)` (estático): convierte el texto del usuario en estado, ignorando mayúsculas, tildes y espacios sobrantes; falla con `DomainException` si no coincide.
  - `getLabel()`: texto para mostrar.

### `WarrantyStatus` (enum)
- **Responsabilidad:** vigencia de una garantía en una fecha. Es un valor derivado, nunca se guarda.
- **Constantes:** `ACTIVE` ("Vigente"), `EXPIRED` ("Vencida"). Método: `getLabel()`.

### `Validations`
- **Responsabilidad:** comprobaciones repetidas del modelo (DRY). Es `final` y de ámbito de paquete: no forma parte de la API pública.
- **Métodos (estáticos):** `requireNonNull(value, field)`, `requireText(value, field)` (devuelve el texto sin espacios en los extremos), `requirePositive(BigDecimal, field)` y `requirePositive(int, field)`. Todos lanzan `DomainException`.

### `DomainException`
- **Responsabilidad:** excepción no verificada para reglas de negocio violadas. Hereda de `RuntimeException`.
- **Métodos:** constructor con el mensaje (es el que se muestra al usuario).

---

## 2. Repositorios: contratos y utilidades (`com.sigatec.repository`)

### `Repository<T extends Identifiable<K>, K>` (interfaz)
- **Responsabilidad:** contrato genérico de almacenamiento; los servicios dependen de él y no de cómo se guarda.
- **Métodos:** `save(entity)` (inserta o reemplaza), `findById(id)` (`Optional`), `findAll()`, `existsById(id)`, `deleteById(id)` (devuelve si borró algo).

### `ProductRepository` (interfaz)
- **Responsabilidad:** repositorio de productos (id = número de serie).
- **Método propio:** `findBySerialNumber(serialNumber)`, tolerante a mayúsculas y espacios.

### `CustomerRepository` (interfaz)
- **Responsabilidad:** repositorio de clientes (id = documento).
- **Método propio:** `findByDocumentNumber(documentNumber)`.

### `WarrantyRepository` (interfaz)
- **Responsabilidad:** repositorio de garantías.
- **Métodos propios:** `nextId()` (siguiente id libre, p. ej. `GAR-0001`), `findByProductSerial(serialNumber)`, `findByCustomerDocument(documentNumber)`.

### `ClaimRepository` (interfaz)
- **Responsabilidad:** repositorio de reclamaciones.
- **Métodos propios:** `nextId()` (p. ej. `REC-0001`), `findByWarrantyId(warrantyId)`, `findByStatus(status)`.

### `SequentialIdGenerator`
- **Responsabilidad:** generar ids legibles y consecutivos.
- **Atributos:** `prefix`, `last`.
- **Métodos relevantes:** constructores `(prefix)` y `(prefix, lastUsed)`; `next()` (p. ej. `GAR-0001`, `GAR-0002`); `observe(id)`: si el id tiene el prefijo y un número mayor, la secuencia continúa después de él (así los ids nuevos nunca chocan con los existentes).

### `PersistenceException`
- **Responsabilidad:** error al leer o escribir datos (archivo dañado, fallo de disco, referencia rota). No verificada.
- **Métodos:** constructores `(message)` y `(message, cause)`.

### `SeedData`
- **Responsabilidad:** datos de prueba para que el sistema muestre contenido desde la primera ejecución. Trabaja contra las interfaces, así que rellena cualquier implementación.
- **Métodos relevantes:** `populate(products, customers, warranties, claims, today)`; auxiliares privados `product`, `customer` y `warranty`.
- **Contenido** (según la versión entregada): 10 productos, 6 clientes, 9 garantías y 6 reclamaciones que cubren los cuatro estados; las fechas son relativas a `today`, de modo que siempre hay garantías vigentes, próximas a vencer y vencidas.

---

## 3. Repositorios en memoria (`com.sigatec.repository.memory`)

### `InMemoryRepository<T, K>` (abstracta)
- **Responsabilidad:** implementar el CRUD una sola vez sobre un mapa que conserva el orden de inserción, y ofrecer un gancho para que otras implementaciones reaccionen a los cambios.
- **Atributos:** `store` (`LinkedHashMap`).
- **Métodos relevantes:**
  - `save`, `findById`, `findAll` (copia de solo lectura), `existsById`, `deleteById`.
  - `afterChange()` (gancho, vacío por defecto): se ejecuta tras cada `save` o `deleteById`; si lanza una excepción, el cambio en memoria se **deshace** (`notifyChange`).
  - `register(entity)`: agrega sin disparar el gancho (se usa al cargar datos); `registerUnique(entity)` lo hace rechazando ids duplicados.
  - `findWhere(filter)`, `findFirstWhere(filter)`: base de las búsquedas de las subclases.

### `InMemoryProductRepository` / `InMemoryCustomerRepository`
- **Responsabilidad:** repositorio en memoria de productos / clientes; agregan solo su búsqueda específica, delegando la comparación en el modelo.
- **Métodos:** `findBySerialNumber(serialNumber)` / `findByDocumentNumber(documentNumber)`.

### `InMemoryWarrantyRepository`
- **Responsabilidad:** repositorio en memoria de garantías; genera ids `GAR-####`.
- **Atributos:** `idGenerator` (prefijo `GAR-`).
- **Métodos:** `nextId()`, `register(warranty)` (además informa al generador del id para que la secuencia continúe), `findByProductSerial`, `findByCustomerDocument`.

### `InMemoryClaimRepository`
- **Responsabilidad:** repositorio en memoria de reclamaciones; genera ids `REC-####`.
- **Atributos:** `idGenerator` (prefijo `REC-`).
- **Métodos:** `nextId()`, `register(claim)`, `findByWarrantyId`, `findByStatus`.

---

## 4. Repositorios en archivos (`com.sigatec.repository.file`)

### `FileStorage`
- **Responsabilidad:** punto único para abrir la persistencia en archivos: crea los cuatro repositorios en el orden correcto (productos y clientes, luego garantías, luego reclamaciones) y siembra los datos de prueba solo en la primera ejecución.
- **Atributos:** constantes `PRODUCTS_FILE`, `CUSTOMERS_FILE`, `WARRANTIES_FILE`, `CLAIMS_FILE` (nombres de los `.txt`); los cuatro repositorios.
- **Métodos relevantes:** `open(dataDirectory, today)` (estático; es primera ejecución si no existe ninguno de los cuatro archivos), `products()`, `customers()`, `warranties()`, `claims()`.

### `FileProductRepository`
- **Responsabilidad:** repositorio de productos guardado en `products.txt`; extiende al de memoria. Carga el archivo al crearse y lo reescribe tras cada cambio.
- **Atributos:** `store` (`TextFileStore`).
- **Métodos:** constructor `(Path file)`, `afterChange()`, `parse`, `toFields`.
- **Formato:** `serialNumber|name|brand|model|price`.

### `FileCustomerRepository`
- **Responsabilidad:** igual, para clientes en `customers.txt`.
- **Formato:** `documentNumber|fullName|phone|email`.

### `FileWarrantyRepository`
- **Responsabilidad:** igual, para garantías en `warranties.txt`. Guarda solo las claves del producto y del cliente y las **resuelve** al cargar.
- **Atributos:** `store`, `products`, `customers`.
- **Métodos:** constructor `(Path file, ProductRepository, CustomerRepository)`, `afterChange()`, `parse`, `toFields`.
- **Formato:** `id|productSerial|customerDocument|purchaseDate|coverageMonths`.

### `FileClaimRepository`
- **Responsabilidad:** igual, para reclamaciones en `claims.txt`. Guarda el estado inicial y los cambios posteriores, y al cargar reconstruye el historial pasando por `Claim.changeStatus` (se vuelven a validar las transiciones).
- **Atributos:** `store`, `warranties`.
- **Métodos:** constructor `(Path file, WarrantyRepository)`, `afterChange()`, `parse`, `toFields`.
- **Formato:** `id|warrantyId|description|claimDate|initialStatus|changes` (cambios como `ESTADO@fecha` separados por `;`).

### `TextFileStore` (ámbito de paquete)
- **Responsabilidad:** única clase que toca archivos. Lee y escribe un archivo UTF-8 donde cada línea es un registro.
- **Atributos:** `file`, `header`, `BOM`.
- **Métodos relevantes:**
  - `load(parser, sink)`: ignora líneas vacías y comentarios (`#`) y un BOM inicial; si una línea falla, lanza `PersistenceException` con el nombre del archivo y el número de línea.
  - `save(entities, toFields)`: escribe primero en un archivo temporal y luego lo reemplaza, para no dejar datos a medias.
  - `requireFieldCount(fields, expected)` (estático).

### `RecordCodec` (ámbito de paquete)
- **Responsabilidad:** convertir un registro en una línea y viceversa, escapando los caracteres especiales para que cualquier texto se guarde sin romper el formato.
- **Atributos:** `SEPARATOR` (`|`), `ESCAPE` (`\`).
- **Métodos (estáticos):** `encode(fields...)` y `decode(line)`; escapan `\`, `|` y los saltos de línea.

---

## 5. Repositorios SQL (`com.sigatec.repository.jdbc`)

### `Database`
- **Responsabilidad:** entregar la conexión a la base de datos a partir de una configuración leída en un `Properties`, validando que cada propiedad obligatoria exista. Es `final` y no se instancia.
- **Métodos:** `getConnection()` (estático), `loadConfig()` y `requiredProperty(properties, key)` (privados). Importa `PersistenceException`.

### `JdbcProductRepository`
- **Responsabilidad:** persistir productos con SQL.
- **Atributos:** constantes `UPSERT`, `SELECT_BY_ID`, `SELECT_ALL`, `EXISTS`, `DELETE`.
- **Métodos:** `findBySerialNumber`, `save`, `findById`, `findAll`, `existsById`, `deleteById`; privados `mapRow` (fila → `Product`) y `normalize(serial)`.

### `JdbcCustomerRepository`
- **Responsabilidad:** persistir clientes con SQL.
- **Atributos:** mismas constantes de consulta.
- **Métodos:** `findByDocumentNumber`, `save`, `findById`, `findAll`, `existsById`, `deleteById`; privados `mapRow` y `normalize(documentNumber)`.

### `JdbcWarrantyRepository`
- **Responsabilidad:** persistir garantías con SQL; reconstruye cada garantía consultando producto y cliente a través de sus repositorios.
- **Atributos:** constantes `UPSERT`, `SELECT_BY_ID`, `SELECT_ALL`, `SELECT_BY_PRODUCT`, `SELECT_BY_CUSTOMER`, `EXISTS`, `DELETE`, `MAX_NUMBER`; `products`, `customers`.
- **Métodos:** constructor `(ProductRepository, CustomerRepository)`, `nextId()` (probablemente apoyado en `MAX_NUMBER`), `findByProductSerial`, `findByCustomerDocument`, `save`, `findById`, `findAll`, `existsById`, `deleteById`; privados `query` y `mapRow`.

### `JdbcClaimRepository`
- **Responsabilidad:** persistir reclamaciones y su historial de estados con SQL; reconstruye la garantía a través de su repositorio.
- **Atributos:** constantes `UPSERT_CLAIM`, `DELETE_HISTORY`, `INSERT_HISTORY`, `SELECT_HISTORY`, `SELECT_COLUMNS`, `SELECT_BY_ID`, `SELECT_ALL`, `SELECT_BY_WARRANTY`, `SELECT_BY_STATUS`, `EXISTS`, `DELETE`, `MAX_NUMBER`; `warranties`.
- **Métodos:** constructor `(WarrantyRepository)`, `nextId()`, `findByWarrantyId`, `findByStatus`, `save`, `findById`, `findAll`, `existsById`, `deleteById`; privados `query`, `mapRow` y `loadHistory(claimId)` (carga la lista de `StatusChange`).

---

## 6. Servicios (`com.sigatec.service`)

### `ProductFinder` (interfaz)
- **Responsabilidad:** consulta de solo lectura de productos, para que otros servicios no dependan de `ProductService`.
- **Métodos:** `findBySerial(serial)` → `Optional<Product>`.

### `CustomerFinder` (interfaz)
- **Responsabilidad:** consulta de solo lectura de clientes.
- **Métodos:** `findByDocument(document)` → `Optional<Customer>`.

### `WarrantyFinder` (interfaz)
- **Responsabilidad:** consulta de solo lectura de garantías.
- **Métodos:** `findById(id)`, `findBySerial(serial)`, `findByCustomer(document)`, `listActive()`, `listExpiringWithin(days)`.

### `ProductService` (implementa `ProductFinder`)
- **Responsabilidad:** casos de uso de productos (por los imports, aplica reglas del dominio con `DomainException`).
- **Atributos:** `products` (`ProductRepository`).
- **Métodos relevantes:** `register(name, brand, model, serial, price)`, `findBySerial` (`Optional`), `requireBySerial` (falla si no existe), `search(text)`, `listAll()`, `update(serial, name, brand, model, price)`, `delete(serial)`.

### `CustomerService` (implementa `CustomerFinder`)
- **Responsabilidad:** casos de uso de clientes.
- **Atributos:** `customers` (`CustomerRepository`).
- **Métodos relevantes:** `register(fullName, document, phone, email)`, `findByDocument`, `requireByDocument`, `searchByName(text)`, `listAll()`, `update(document, fullName, phone, email)`, `delete(document)`.

### `WarrantyService` (implementa `WarrantyFinder`)
- **Responsabilidad:** registrar garantías (consultando producto y cliente por interfaz) y consultarlas por distintos criterios.
- **Atributos:** `warranties` (`WarrantyRepository`), `products` (`ProductFinder`), `customers` (`CustomerFinder`), `clock`.
- **Métodos relevantes:** `register(serial, document, purchaseDate, coverageMonths)`, `findById`, `findBySerial`, `findByCustomer`, `listActive()`, `listExpiringWithin(days)`, `listAll()`.

### `ClaimService`
- **Responsabilidad:** presentar reclamaciones sobre garantías, cambiar su estado y consultarlas.
- **Atributos:** `claims` (`ClaimRepository`), `warranties` (`WarrantyFinder`), `clock`.
- **Métodos relevantes:** `file(warrantyId, description, claimDate, requestedStatus)`, `changeStatus(claimId, newStatus)`, `findById`, `listAll()`, `finBYWarranty(warrantyId)` *(nombre tal como está en el código)*, `listPending()`, `listOpen()`.

### `RemovalService`
- **Responsabilidad:** eliminar productos y clientes. Por su dependencia de `WarrantyFinder`, presumiblemente comprueba que no tengan garantías antes de borrar.
- **Atributos:** `productService`, `customerService`, `waranties` (`WarrantyFinder`; nombre tal como está en el código).
- **Métodos:** `deleteProdut(serial)` *(nombre tal como está en el código)*, `deleteCustomer(document)`.

### `ReportService`
- **Responsabilidad:** consultas agregadas para reportes y panel.
- **Atributos:** `warranties` (`WarrantyFinder`), `claims` (`ClaimService`).
- **Métodos:** `activeWarranties()`, `warrantiesExpiringWithin(days)`, `pendingClaims()`.

---

## 7. Interfaz (`com.sigatec.ui` y `com.sigatec.ui.view`)

### `Screen` (interfaz)
- **Responsabilidad:** contrato de una pantalla de la interfaz gráfica.
- **Métodos:** `build()` → `Parent` (crea el contenido), `refresh()` (lo actualiza).

### `UiSupport`
- **Responsabilidad:** ayudantes estáticos compartidos por las pantallas JavaFX. Es `final` y no se instancia.
- **Métodos:** `textColumn(title, value)`, `showError(message)`, `showInfo(message)`, `confirm(message)`, `run(action)`, `sectionTitle(text)`, `card(children...)`, `pageTitle(text)`, `pageSubtitle(text)`.

### `ConsoleUI`
- **Responsabilidad:** interfaz de consola con un menú por área.
- **Atributos:** `productService`, `customerService`, `warrantyService`, `claimService`, `scanner`.
- **Métodos:** `start()`, y privados `printMenu()`, `handleProducts()`, `handleCustomers()`, `handleWarranties()`, `handleClaims()`.

### `MainApp` (extiende `Application` de JavaFX)
- **Responsabilidad:** aplicación gráfica principal: arma las dependencias (importa `FileStorage`, los cuatro `Jdbc*Repository` y los servicios), construye las pantallas y la navegación entre ellas.
- **Métodos:** `start(Stage)`, `main(args)` (estático) y `navButton(text, node, screen, root)` (privado).

### `ProductsView` (implementa `Screen`)
- **Responsabilidad:** pantalla de productos: formulario, búsqueda y tabla.
- **Atributos:** `products` (`ProductService`), `removal` (`RemovalService`), `selected` (producto elegido en la tabla), campos de serie, nombre, marca, modelo, precio y búsqueda; botones "Registrar" y "Eliminar".
- **Métodos:** `build()`, `refresh()`, y privados `fill(product)`, `save()`, `delete()`, `parsePrice()`, `buildTable()`.

### `CustomersView` (implementa `Screen`)
- **Responsabilidad:** pantalla de clientes: formulario, búsqueda y tabla.
- **Atributos:** `customers` (`CustomerService`), `removal` (`RemovalService`), `selected`, campos de documento, nombre, teléfono, email y búsqueda; botones "Registrar" y "Eliminar".
- **Métodos:** `build()`, `refresh()`, y privados `fill(customer)`, `save()`, `delete()`, `buildTable()`.

### `WarrantiesView` (implementa `Screen`)
- **Responsabilidad:** registrar garantías y listarlas con un filtro de vigencia.
- **Atributos:** `warranties` (`WarrantyService`); constantes de filtro `ALL` ("Todas"), `ACTIVE` ("Vigentes"), `EXPIRED` ("Vencidas"); campos de serie, documento, fecha de compra (por defecto hoy) y meses (por defecto 12).
- **Métodos:** `build()`, `refresh()`, y privados `register()`, `parseMonths()`, `buildTable()`.

### `ClaimsView` (implementa `Screen`)
- **Responsabilidad:** presentar reclamaciones, filtrarlas por estado y cambiar su estado.
- **Atributos:** `claims` (`ClaimService`), `filters` (`ToggleGroup`), campos de garantía, descripción y fecha (por defecto hoy), `newStatus` (lista de `ClaimStatus`).
- **Métodos:** `build()`, `refresh()`, y privados `file()`, `changeStatus()`, `buildForm()`, `buildFilterBar()`, `filterButton(text, status)`, `buildStatusBar()`, `buildTable()`, `statusColumn()`.

### `ReportsView` (implementa `Screen`)
- **Responsabilidad:** reportes de garantías activas, garantías por vencer (con un número de días configurable, por defecto 30) y reclamaciones pendientes.
- **Atributos:** `reports` (`ReportService`), `daysField`, tablas `activeTable`, `expiringTable` y `pendingTable`.
- **Métodos:** `build()`, `refresh()`, y privados `parseDays()`, `buildWarrantyTable(table)`, `buildClaimTable(table)`.

### `DashboardView` (implementa `Screen`)
- **Responsabilidad:** panel de inicio con indicadores y una tabla de garantías por vencer (deducido de los nombres de sus campos).
- **Atributos:** constante `EXPIRING_WITHIN_DAYS` (30), `reports` (`ReportService`), `products` (`ProductService`), etiquetas `activeValue`, `expiringValue`, `pendingValue` y `productsValue`, y `expiringTable`.
- **Métodos:** `build()`, `refresh()`, y privados `kpiCard(caption, value)`, `buildExpiringTable()`.

---

## 8. Arranque (`com.sigatec`)

### `Main`
- **Responsabilidad:** punto de entrada de la versión de consola. Importa `FileStorage`, los servicios de producto, cliente, garantía y reclamación, y `ConsoleUI`: abre la persistencia en archivos, crea los servicios y arranca la consola.
- **Métodos:** `main(args)`.

### `Launcher`
- **Responsabilidad:** punto de entrada que lanza la aplicación gráfica; importa `MainApp`.
- **Métodos:** `main(args)`.
