-- ==========================================================
-- BASE DE DATOS: SIGATEC (PostgreSQL)
-- ==========================================================

-- 1. Tabla de Productos
CREATE TABLE products (
    serial_number VARCHAR(100) PRIMARY KEY,
    name          VARCHAR(150) NOT NULL,
    brand         VARCHAR(100) NOT NULL,
    model         VARCHAR(100) NOT NULL,
    price         NUMERIC(14, 2) NOT NULL CHECK (price > 0)
);

-- 2. Tabla de Clientes
CREATE TABLE customers (
    document_number VARCHAR(50) PRIMARY KEY,
    full_name       VARCHAR(150) NOT NULL,
    phone           VARCHAR(20) NOT NULL,
    email           VARCHAR(150) NOT NULL
);

-- 3. Tabla de Garantías
CREATE TABLE warranties (
    id                VARCHAR(30) PRIMARY KEY,
    product_serial    VARCHAR(100) NOT NULL UNIQUE,
    customer_document VARCHAR(50) NOT NULL,
    purchase_date     DATE NOT NULL,
    coverage_months   INT NOT NULL CHECK (coverage_months > 0),

    CONSTRAINT fk_warranty_product
        FOREIGN KEY (product_serial) REFERENCES products (serial_number)
        ON UPDATE CASCADE ON DELETE RESTRICT,

    CONSTRAINT fk_warranty_customer
        FOREIGN KEY (customer_document) REFERENCES customers (document_number)
        ON UPDATE CASCADE ON DELETE RESTRICT
);

-- 4. Tabla de Reclamos
CREATE TABLE claims (
    id             VARCHAR(30) PRIMARY KEY,
    warranty_id    VARCHAR(30) NOT NULL,
    description    TEXT NOT NULL,
    claim_date     DATE NOT NULL,
    current_status VARCHAR(20) NOT NULL CHECK (
        current_status IN ('PENDING', 'IN_REPAIR', 'RESOLVED', 'REJECTED')
    ),

    CONSTRAINT fk_claim_warranty
        FOREIGN KEY (warranty_id) REFERENCES warranties (id)
        ON UPDATE CASCADE ON DELETE RESTRICT
);

-- 5. Tabla de Historial de Estados del Reclamo
CREATE TABLE claim_status_history (
    id          SERIAL PRIMARY KEY,
    claim_id    VARCHAR(30) NOT NULL,
    status      VARCHAR(20) NOT NULL CHECK (
        status IN ('PENDING', 'IN_REPAIR', 'RESOLVED', 'REJECTED')
    ),
    change_date DATE NOT NULL,

    CONSTRAINT fk_history_claim
        FOREIGN KEY (claim_id) REFERENCES claims (id)
        ON UPDATE CASCADE ON DELETE CASCADE
);

-- Índices para acelerar las búsquedas de tus repositorios
CREATE INDEX idx_warranties_customer ON warranties (customer_document);
CREATE INDEX idx_claims_warranty ON claims (warranty_id);
CREATE INDEX idx_claims_status ON claims (current_status);
CREATE INDEX idx_history_claim ON claim_status_history (claim_id);