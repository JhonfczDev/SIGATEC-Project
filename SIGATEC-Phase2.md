# FASE 2: Definición de la arquitectura del proyecto
**Proyecto:** SIGATEC – Sistema de Seguimiento y Control de Garantías para Tiendas de Tecnología  
**Equipo:** Líder | Dev1 | Dev2

---

## 1. Definición de la arquitectura

El sistema SIGATEC se desarrolla bajo una **arquitectura en capas** (Layered Architecture), con el objetivo de lograr una clara separación de responsabilidades, facilitar el mantenimiento y permitir que cada integrante del equipo trabaje de forma independiente en su capa asignada.

### Capas del sistema

| Capa            | Responsabilidad principal                                      | Responsable |
|-----------------|----------------------------------------------------------------|-----------|
| **UI**          | Interfaz de consola, menús, captura y visualización de datos   | Líder     |
| **Service**     | Lógica de negocio y reglas del dominio                         | Dev2      |
| **Repository**  | Acceso y persistencia de datos                                 | Dev1 / Dev2 |
| **Model**       | Entidades del dominio (POJOs)                                  | Dev1      |

### Diagrama de paquetes organizados por capas

```text
sigatec/
│
├── ui/                     ← Líder
│   ├── MainMenu.java
│   ├── ProductMenu.java
│   ├── ClientMenu.java
│   ├── WarrantyMenu.java
│   ├── ClaimMenu.java
│   ├── ReportMenu.java
│   └── ConsoleUtils.java
│
├── service/                ← Dev2
│   ├── ProductService.java
│   ├── ClientService.java
│   ├── WarrantyService.java
│   └── ClaimService.java
│
├── repository/             ← Dev1 (principal) / Dev2 (apoyo)
│   ├── ProductRepository.java
│   ├── ClientRepository.java
│   ├── WarrantyRepository.java
│   └── ClaimRepository.java
│
├── model/                  ← Dev1
│   ├── Product.java
│   ├── Client.java
│   ├── Warranty.java
│   └── Claim.java
│
└── Main.java               ← Líder (clase integradora)
```

---

## 2. Diagrama de clases – Identificación de clases

| #  | Nombre de la Clase       | Tipo     | Descripción y Rol en el Sistema                                      | Responsable |
|----|--------------------------|----------|----------------------------------------------------------------------|-----------|
| 1  | Product                  | Concreta | Representa un producto tecnológico con sus datos básicos             | Dev1      |
| 2  | Client                   | Concreta | Representa al cliente que adquiere un producto                       | Dev1      |
| 3  | Warranty                 | Concreta | Representa la garantía asociada a un producto y un cliente           | Dev1      |
| 4  | Claim                    | Concreta | Representa un reclamo realizado sobre una garantía                   | Dev1      |
| 5  | ProductRepository        | Concreta | Gestiona el almacenamiento y consulta de productos                   | Dev1      |
| 6  | ClientRepository         | Concreta | Gestiona el almacenamiento y consulta de clientes                    | Dev1      |
| 7  | WarrantyRepository       | Concreta | Gestiona el almacenamiento y consulta de garantías                   | Dev1      |
| 8  | ClaimRepository          | Concreta | Gestiona el almacenamiento y consulta de reclamaciones               | Dev1      |
| 9  | ProductService           | Concreta | Contiene la lógica de negocio relacionada con productos              | Dev2      |
| 10 | ClientService            | Concreta | Contiene la lógica de negocio relacionada con clientes               | Dev2      |
| 11 | WarrantyService          | Concreta | Contiene la lógica de negocio de garantías (vigencia, registro…)     | Dev2      |
| 12 | ClaimService             | Concreta | Contiene la lógica de negocio de reclamaciones                       | Dev2      |
| 13 | MainMenu                 | Concreta | Punto de entrada de la interfaz de consola y navegación principal    | Líder     |
| 14 | ConsoleUtils             | Concreta | Métodos auxiliares para lectura y validación de datos por consola    | Líder     |
| 15 | Main                     | Concreta | Clase integradora que inicializa repositorios, servicios y la UI     | Líder     |

---

## 3. Definición de Atributos, Métodos y Responsabilidades

### Clase: Product
- **Tipo:** Concreta  
- **Responsabilidad principal:** Representar un producto tecnológico vendido en la tienda.  
- **Responsable:** Dev1

| Atributo       | Tipo de Dato | Descripción                          |
|----------------|--------------|--------------------------------------|
| id             | int          | Identificador único del producto     |
| name           | String       | Nombre del producto                  |
| brand          | String       | Marca del producto                   |
| model          | String       | Modelo del producto                  |
| serialNumber   | String       | Número de serie único                |
| price          | double       | Precio de venta                      |

**Métodos principales:**  
constructors, getters, setters, `toString()`

---

### Clase: Client
- **Tipo:** Concreta  
- **Responsabilidad principal:** Representar al cliente que compra un producto y puede reclamar una garantía.  
- **Responsable:** Dev1

| Atributo   | Tipo de Dato | Descripción                       |
|------------|--------------|-----------------------------------|
| id         | int          | Identificador único del cliente   |
| name       | String       | Nombre completo                   |
| document   | String       | Documento de identidad            |
| phone      | String       | Teléfono de contacto              |
| email      | String       | Correo electrónico                |

**Métodos principales:**  
constructors, getters, setters, `toString()`

---

### Clase: Warranty
- **Tipo:** Concreta  
- **Responsabilidad principal:** Representar la garantía de un producto asociada a un cliente, incluyendo fechas y estado.  
- **Responsable:** Dev1

| Atributo          | Tipo de Dato     | Descripción                              |
|-------------------|------------------|------------------------------------------|
| id                | int              | Identificador único de la garantía       |
| product           | Product          | Producto asociado                        |
| client            | Client           | Cliente asociado                         |
| purchaseDate      | LocalDate        | Fecha en que se realizó la compra        |
| expirationDate    | LocalDate        | Fecha en que vence la garantía           |
| warrantyMonths    | int              | Duración de la garantía en meses         |
| status            | String           | ACTIVE, EXPIRED, CLAIMED                 |

**Métodos principales:**  
constructors, getters, setters, `isActive()`, `toString()`

---

### Clase: Claim
- **Tipo:** Concreta  
- **Responsabilidad principal:** Representar un reclamo realizado sobre una garantía.  
- **Responsable:** Dev1

| Atributo             | Tipo de Dato | Descripción                                  |
|----------------------|--------------|----------------------------------------------|
| id                   | int          | Identificador único de la reclamación        |
| warranty             | Warranty     | Garantía sobre la que se reclama             |
| claimDate            | LocalDate    | Fecha en que se registró el reclamo          |
| problemDescription   | String       | Descripción del problema reportado           |
| status               | String       | PENDING, IN_REPAIR, RESOLVED, REJECTED       |
| observations         | String       | Observaciones adicionales                    |

**Métodos principales:**  
constructors, getters, setters, `toString()`

---

### Clase: WarrantyService (ejemplo de Service)
- **Tipo:** Concreta  
- **Responsabilidad principal:** Contener la lógica de negocio relacionada con el registro, consulta y validación de garantías.  
- **Responsable:** Dev2

**Métodos principales:**
- `registerWarranty(Product product, Client client, int months)`
- `findBySerialNumber(String serial)`
- `findByClient(int clientId)`
- `checkValidity(int warrantyId)`
- `listActiveWarranties()`
- `listExpiringSoon(int days)`

---

### Clase: MainMenu (ejemplo de UI)
- **Tipo:** Concreta  
- **Responsabilidad principal:** Mostrar el menú principal y coordinar la navegación hacia los demás menús del sistema.  
- **Responsable:** Líder

**Métodos principales:**
- `show()`
- `processOption(int option)`
- `startSystem()`

---

### Clase: Main
- **Tipo:** Concreta  
- **Responsabilidad principal:** Clase integradora que instancia los repositorios, servicios y la interfaz de usuario, e inicia la aplicación.  
- **Responsable:** Líder

---

## 4. Relaciones entre clases

| Relación                              | Tipo de relación        | Descripción |
|---------------------------------------|-------------------------|-----------|
| Warranty → Product                    | Asociación / Agregación | Una garantía está asociada a un producto |
| Warranty → Client                     | Asociación / Agregación | Una garantía está asociada a un cliente |
| Claim → Warranty                      | Asociación / Agregación | Una reclamación pertenece a una garantía |
| WarrantyService → WarrantyRepository  | Dependencia             | El servicio utiliza el repositorio |
| MainMenu → WarrantyService            | Dependencia             | La UI utiliza los servicios |
| Main → todos los servicios y menús    | Dependencia             | Main crea e integra todos los componentes |

**Nota:** No se utiliza herencia entre las entidades del dominio. Se prefiere composición y asociación para mantener bajo acoplamiento.

---

## 5. Incorporación de herencia, interfaces y clases abstractas

En esta versión del sistema **no se implementan clases abstractas ni herencia** entre las entidades del dominio, con el fin de mantener un diseño simple y adecuado al alcance del proyecto académico.

Sin embargo, se deja preparada la posibilidad de introducir en el futuro:

- Una interfaz `IRepository<T>` genérica para los repositorios.
- Una clase abstracta `BaseEntity` con atributos comunes (`id`, `createdAt`).

Actualmente todas las clases son **concretas**.

---

## 6. Diseño preliminar de la interfaz (Consola)

El sistema utiliza **interfaz de consola**. A continuación se muestran los mockups principales:

### Menú Principal
```text
=============================================================
       SIGATEC - SISTEMA DE CONTROL DE GARANTÍAS
              Tiendas de Tecnología
=============================================================

1. Gestión de Productos
2. Gestión de Clientes
3. Gestión de Garantías
4. Gestión de Reclamaciones
5. Reportes
6. Salir

Seleccione una opción: _
```

### Gestión de Garantías
```text
=============================================================
                 GESTIÓN DE GARANTÍAS
=============================================================

1. Registrar Garantía
2. Buscar Garantía por N° de Serie
3. Buscar Garantías por Cliente
4. Listar Garantías
5. Verificar Vigencia
6. Volver al Menú Principal

Seleccione una opción: _
```

### Gestión de Reclamaciones
```text
=============================================================
               GESTIÓN DE RECLAMACIONES
=============================================================

1. Registrar Reclamación
2. Actualizar Estado de Reclamación
3. Buscar Reclamación
4. Listar Reclamaciones
5. Volver al Menú Principal

Seleccione una opción: _
```

---

## 7. Aplicación de principios de diseño orientado a objetos

### 7.1 Principios GRASP

| Patrón GRASP       | Clase donde se aplica          | Justificación |
|--------------------|--------------------------------|-------------|
| **Creator**        | `WarrantyService`              | Es la clase que tiene la información necesaria para crear objetos `Warranty` |
| **Controller**     | `MainMenu` / `Main`            | Actúan como controladores que reciben la solicitud del usuario y la delegan |
| **Expert**         | `Warranty`                     | La propia clase `Warranty` sabe si está vigente (`isActive()`) |
| **Low Coupling**   | Todas las capas                | Las capas solo dependen de la capa inferior inmediata |
| **High Cohesion**  | `WarrantyService`, `ProductService` | Cada servicio agrupa únicamente la lógica de su entidad |
| **Pure Fabrication** | `ConsoleUtils`               | Clase creada para agrupar operaciones de entrada/salida por consola |

### 7.2 Principios SOLID

| Principio SOLID | Clase / Relación donde se observa | Justificación |
|-----------------|-----------------------------------|-------------|
| **SRP** (Single Responsibility) | Cada clase de Service y Repository | Cada clase tiene una única razón de cambio |
| **OCP** (Open/Closed) | Arquitectura en capas | Se pueden agregar nuevos servicios o repositorios sin modificar el código existente |
| **LSP** (Liskov) | No aplica actualmente | No existe herencia |
| **ISP** (Interface Segregation) | Preparado para futuras interfaces de repositorio | Se evitarán interfaces grandes |
| **DIP** (Dependency Inversion) | `Main` y Services | Los servicios dependen de abstracciones (repositorios), no de implementaciones concretas de alto nivel |

---

## Resumen de responsabilidades por integrante

| Integrante     | Capas / Clases principales                              | Ramas Git sugeridas              |
|----------------|---------------------------------------------------------|----------------------------------|
| **Líder**      | UI (todos los menús), `Main`, `ConsoleUtils`            | `feature/ui-lider`               |
| **Dev1**       | Model (Product, Client, Warranty, Claim) + Repositories | `feature/modelos-dev1`           |
| **Dev2**       | Services (ProductService, ClientService, WarrantyService, ClaimService) | `feature/servicios-dev2` |

---

**Fin de la FASE 2**
