# Applied principles evidence

Reference branch: develop (commit 32785f3). Review date: 2026-11-24. File paths are relative to src/main/java/com/sigatec/. The project has no automated tests, so the evidence comes from reviewing the code.

Names used in the table:
- SOLID principles: Single responsibility, Open/closed, Liskov substitution, Interface segregation, Dependency inversion.
- GRASP: General Responsibility Assignment Software Patterns.
- Clean code: practices from Robert C. Martin's book "Clean Code".

| Type | Principle or practice | Class and method | File | Line(s) | Evidence in the code |
|---|---|---|---|---|---|
| SOLID | Single responsibility principle | Database.getConnection | repository/jdbc/Database.java | 19 | Database only provides database connections |
| SOLID | Open/closed principle | MainApp.start (wiring) | ui/MainApp.java | 35-41 | JDBC was added without changing the services; only the wiring changed |
| SOLID | Liskov substitution principle | FileProductRepository | repository/file/FileProductRepository.java | 14 | Extends InMemoryProductRepository and honors the ProductRepository contract |
| SOLID | Interface segregation principle | ProductFinder, CustomerFinder, WarrantyFinder | service/ProductFinder.java; service/CustomerFinder.java; service/WarrantyFinder.java | 11; 10; 11 | Interfaces with 1, 1 and 5 methods: each service sees only what it needs |
| SOLID | Dependency inversion principle | WarrantyService and ClaimService constructors | service/WarrantyService.java; service/ClaimService.java | 25; 23 | They receive interfaces through the constructor, not concrete classes |
| GRASP | Information expert | Warranty.isActive and Warranty.remainingDays | model/Warranty.java | 38; 48 | The warranty computes its validity; the service does not compute dates |
| GRASP | Creator | WarrantyService.register; ClaimService.file | service/WarrantyService.java; service/ClaimService.java | 53; 54 | The class that has the required data creates the object |
| GRASP | Polymorphism | ClaimStatus.canTransitionTo | model/ClaimStatus.java | 35 | Valid transitions are defined in the enum with a switch |
| GRASP | Low coupling | ReportService constructor | service/ReportService.java | 15 | Depends on WarrantyFinder and ClaimService, not on repositories |
| GRASP | High cohesion | ProductService, CustomerService, WarrantyService, ClaimService (class declarations) | service/ProductService.java; service/CustomerService.java; service/WarrantyService.java; service/ClaimService.java | 14; 13; 18; 18 | Each service groups operations on a single topic |
| GRASP | Pure fabrication | SequentialIdGenerator; Validations | repository/SequentialIdGenerator.java; model/Validations.java | 10; 9 | Technical support classes that are not domain entities |
| GRASP | Protected variations | Repository interface | repository/Repository.java | 18 | Isolates persistence: files can be replaced by PostgreSQL without touching the services |
| GRASP | Controller | MainApp.start | ui/MainApp.java | 35-54 | A single place creates and connects the dependencies |
| Clean code | Objects that are never invalid | Product constructor | model/Product.java | 19-25 | Constructor validates name, brand, model and price |
| Clean code | Atomic update | Product.updateDetails | model/Product.java | 31-41 | Validates everything before assigning; an error does not leave the object half-updated |
| Clean code | Immutable identity | Product.serialNumber; Customer.documentNumber; Warranty.expirationDate | model/Product.java; model/Customer.java; model/Warranty.java | 13; 16; 23 | Keys do not change after the object is created |
| Clean code | Law of Demeter | Warranty.belongsToCustomer; InMemoryWarrantyRepository.findByCustomerDocument | model/Warranty.java; repository/memory/InMemoryWarrantyRepository.java | 61; 33 | Asks about the customer without exposing its structure |
| Clean code | Resources and transactions | JdbcClaimRepository.save | repository/jdbc/JdbcClaimRepository.java | 88 | try-with-resources and commit or rollback: the claim and its history are saved together |
| Clean code | Injected date | Clock field in WarrantyService and ClaimService | service/WarrantyService.java; service/ClaimService.java | 23; 21 | Date rules can be tested with fixed dates |