-- ==========================================================
-- SIGATEC: datos de prueba del proyecto (base postgres)
-- Se toman de los archivos de datos de prueba del proyecto sin BD.
-- Este script borra lo que haya en las tablas y carga los datos de nuevo.
-- ==========================================================

TRUNCATE claim_status_history, claims, warranties, customers, products RESTART IDENTITY CASCADE;

-- Productos
INSERT INTO products (serial_number, name, brand, model, price) VALUES
('SN-HP-0001', 'Laptop HP 15-dy',                  'HP',       '15-dy2024LA', 2350000),
('SN-LN-0002', 'Laptop Lenovo IdeaPad 3',          'Lenovo',   '15ITL6',      2100000),
('SN-SG-0003', 'Smartphone Samsung Galaxy A54',    'Samsung',  'SM-A546',     1450000),
('SN-AP-0004', 'iPhone 13',                        'Apple',    'A2633',       3200000),
('SN-LG-0005', 'Monitor LG UltraGear 27"',         'LG',       '27GL650F',     980000),
('SN-SY-0006', 'Audífonos Sony WH-1000XM4',        'Sony',     'WH-1000XM4',  1100000),
('SN-SG-0007', 'Tablet Samsung Galaxy Tab S6 Lite','Samsung',  'SM-P625',     1300000),
('SN-EP-0008', 'Impresora Epson EcoTank L3250',    'Epson',    'L3250',        850000),
('SN-TP-0009', 'Router TP-Link Archer AX23',       'TP-Link',  'AX23',         260000),
('SN-SG-0010', 'Smart TV Samsung 50"',             'Samsung',  'UN50CU7000',  1900000);

-- Clientes
INSERT INTO customers (document_number, full_name, phone, email) VALUES
('1065123456', 'Juan Pérez',                   '3001234567', 'juan.perez@example.com'),
('1065987654', 'María Fernanda Gómez',         '3012345678', 'maria.gomez@example.com'),
('77123456',   'Carlos Andrés Rodríguez',      '3105551234', 'carlos.rodriguez@example.com'),
('1067890123', 'Laura Sofía Martínez',         '3158889999', 'laura.martinez@example.com'),
('1082345678', 'Andrés Felipe Díaz',           '3204447788', 'andres.diaz@example.com'),
('1065456789', 'Valentina Torres Mejía',       '3176663322', 'valentina.torres@example.com');

-- Garantías
INSERT INTO warranties (id, product_serial, customer_document, purchase_date, coverage_months) VALUES
('GAR-0001', 'SN-HP-0001', '1065123456', '2026-07-14', 12),
('GAR-0002', 'SN-LN-0002', '1065987654', '2025-12-04', 12),
('GAR-0003', 'SN-SG-0003', '77123456',   '2025-09-14', 12),
('GAR-0004', 'SN-AP-0004', '1067890123', '2026-10-14', 24),
('GAR-0005', 'SN-LG-0005', '1082345678', '2026-04-14', 12),
('GAR-0006', 'SN-SY-0006', '1065456789', '2026-05-24',  6),
('GAR-0007', 'SN-SG-0007', '1065123456', '2025-03-14', 24),
('GAR-0008', 'SN-EP-0008', '1065987654', '2025-05-14', 12),
('GAR-0009', 'SN-TP-0009', '77123456',   '2026-09-14', 12);

-- Reclamaciones (estado inicial PENDING; el estado actual es el último cambio)
INSERT INTO claims (id, warranty_id, description, claim_date, current_status) VALUES
('REC-0001', 'GAR-0001', 'La laptop no enciende después de una actualización del sistema', '2026-10-25', 'PENDING'),
('REC-0002', 'GAR-0002', 'Pantalla con líneas verticales',                                   '2026-10-30', 'IN_REPAIR'),
('REC-0003', 'GAR-0004', 'La batería se descarga en pocas horas',                           '2026-10-15', 'RESOLVED'),
('REC-0004', 'GAR-0005', 'Píxeles muertos en la parte inferior de la pantalla',             '2026-11-04', 'REJECTED'),
('REC-0005', 'GAR-0003', 'Falla en el puerto de carga',                                     '2026-11-09', 'REJECTED'),
('REC-0006', 'GAR-0006', 'El audífono derecho no emite sonido',                             '2026-11-11', 'PENDING');

-- Historial de estados: primer estado al registrar y luego cada cambio
INSERT INTO claim_status_history (claim_id, status, change_date) VALUES
('REC-0001', 'PENDING',   '2026-10-25'),
('REC-0002', 'PENDING',   '2026-10-30'),
('REC-0002', 'IN_REPAIR', '2026-11-02'),
('REC-0003', 'PENDING',   '2026-10-15'),
('REC-0003', 'IN_REPAIR', '2026-10-20'),
('REC-0003', 'RESOLVED',  '2026-10-27'),
('REC-0004', 'PENDING',   '2026-11-04'),
('REC-0004', 'REJECTED',  '2026-11-07'),
('REC-0005', 'PENDING',   '2026-11-09'),
('REC-0005', 'REJECTED',  '2026-11-11'),
('REC-0006', 'PENDING',   '2026-11-11');