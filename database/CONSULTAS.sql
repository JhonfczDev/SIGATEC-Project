SELECT 'products' AS tabla, COUNT(*) AS filas FROM products
UNION ALL
SELECT 'customers', COUNT(*) FROM customers
UNION ALL
SELECT 'warranties', COUNT(*) FROM warranties
UNION ALL
SELECT 'claims', COUNT(*) FROM claims
UNION ALL
SELECT 'claim_status_history', COUNT(*) FROM claim_status_history;