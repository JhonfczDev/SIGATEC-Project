# Entity-relationship model

Reference branch: develop (commit 32785f3). Review date: 2026-11-24. Source: database/SIGATECDB2.sql.

## Entity-relationship diagram

```
PRODUCTS ----- WARRANTIES ----- CUSTOMERS
                  |
                CLAIMS
                  |
        CLAIM_STATUS_HISTORY
```

| Relationship | Cardinality | Key | Rule |
|---|---|---|---|
| A product has zero or one warranty | one to zero-or-one | warranties.product_serial (UNIQUE) | Each product can have only one warranty |
| A customer has zero or many warranties | one to zero-or-many | warranties.customer_document | ON DELETE RESTRICT: a customer with warranties cannot be deleted |
| A warranty has zero or many claims | one to zero-or-many | claims.warranty_id | ON DELETE RESTRICT: a warranty with claims cannot be deleted |
| A claim has zero or many history records | one to zero-or-many | claim_status_history.claim_id | ON DELETE CASCADE: deleting a claim also deletes its history. The application always stores at least one record |