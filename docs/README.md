# SIGATEC – Sistema de Seguimiento y Control de Garantías

**Sistema de Seguimiento y Control de Garantías para Tiendas de Tecnología**

---

## Descripción

SIGATEC es un sistema de información desarrollado para tiendas de tecnología que permite gestionar de forma ordenada y eficiente el control de garantías de los productos vendidos.  

El sistema facilita el registro de productos, clientes y garantías, la consulta del estado de vigencia, el seguimiento de reclamaciones y la generación de reportes básicos, todo a través de una interfaz de consola.

---

## Arquitectura

El proyecto utiliza una **arquitectura en capas** (Layered Architecture):

```
sigatec/
│
├── ui/                 → Interfaz de consola (menús)
├── service/            → Lógica de negocio
├── repository/         → Acceso a datos
├── model/              → Entidades del dominio
└── Main.java           → Clase integradora
```

| Capa          | Responsabilidad                                      |
|---------------|------------------------------------------------------|
| **UI**        | Menús, captura y visualización de datos por consola  |
| **Service**   | Reglas de negocio y validaciones                     |
| **Repository**| Persistencia y consultas de datos                    |
| **Model**     | Entidades: Product, Client, Warranty, Claim         |

---

## Equipo de desarrollo

| Integrante     | Rol              | Responsabilidades                                      | Rama Git                  |
|----------------|------------------|--------------------------------------------------------|---------------------------|
| **Líder**      | Líder de proyecto | UI (menús), Main, ConsoleUtils, integración final     | `feature/ui-lider`        |
| **Dev1**       | Desarrollador    | Model (Product, Client, Warranty, Claim) + Repositories | `feature/modelos-dev1`    |
| **Dev2**       | Desarrollador    | Services (ProductService, ClientService, WarrantyService, ClaimService) | `feature/servicios-dev2` |

---

## Funcionalidades principales

- Registrar, buscar, listar, modificar y eliminar **productos**
- Registrar, buscar, listar, modificar y eliminar **clientes**
- Registrar y consultar **garantías** (por número de serie o cliente)
- Verificar el estado de vigencia de una garantía
- Registrar y actualizar el estado de **reclamaciones**
- Generar **reportes** básicos:
  - Garantías activas
  - Garantías próximas a vencer
  - Reclamaciones pendientes

---

## Clases principales

### Model
- `Product`
- `Client`
- `Warranty`
- `Claim`

### Repository
- `ProductRepository`
- `ClientRepository`
- `WarrantyRepository`
- `ClaimRepository`

### Service
- `ProductService`
- `ClientService`
- `WarrantyService`
- `ClaimService`

### UI
- `MainMenu`
- `ProductMenu`
- `ClientMenu`
- `WarrantyMenu`
- `ClaimMenu`
- `ReportMenu`
- `ConsoleUtils`

### Integración
- `Main`

---

## Estrategia de ramas (Git)

- `main` → Código estable y revisado
- `feature/ui-lider` → Desarrollo de la interfaz de consola (Líder)
- `feature/modelos-dev1` → Modelos y repositorios (Dev1)
- `feature/servicios-dev2` → Servicios / lógica de negocio (Dev2)

---

## Cómo ejecutar

1. Clonar el repositorio:
   ```bash
   git clone https://github.com/tu-usuario/Proyecto-sigatec.git
   cd Proyecto-sigatec
   ```

2. Compilar el proyecto (Java):
   ```bash
   javac -d bin src/**/*.java
   ```

3. Ejecutar:
   ```bash
   java -cp bin Main
   ```

---

## Requisitos

- Java 17 o superior


---

## Estructura del repositorio

```
Proyecto-sigatec/
├── src/
│   ├── model/
│   ├── repository/
│   ├── service/
│   ├── ui/
│   └── Main.java
├── docs/
│   └── FASE2_Definicion_Arquitectura_SIGATEC.pdf
├── README.md
└── .gitignore
```

---

## Principios de diseño aplicados

- **GRASP:** Creator, Controller, Expert, Low Coupling, High Cohesion, Pure Fabrication
- **SOLID:** Single Responsibility (SRP), Open/Closed (OCP), Dependency Inversion (DIP)

---

## Licencia

Proyecto académico – Uso educativo.
