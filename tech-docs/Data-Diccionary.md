# Data dictionary

Reference branch: develop (commit 32785f3). Review date: 2026-11-24. Source: database/SIGATECDB2.sql.

## products

| Column | Type | Constraints | Description |
|---|---|---|---|
| serial_number | VARCHAR(100) | Primary key | Serial number, in uppercase |
| name | VARCHAR(150) | NOT NULL | Product name |
| brand | VARCHAR(100) | NOT NULL | Brand |
| model | VARCHAR(100) | NOT NULL | Model |
| price | NUMERIC(14,2) | NOT NULL, CHECK price > 0 | Price |

## customers

| Column | Type | Constraints | Description |
|---|---|---|---|
| document_number | VARCHAR(50) | Primary key | Identity document number |
| full_name | VARCHAR(150) | NOT NULL | Full name |
| phone | VARCHAR(20) | NOT NULL | Phone number |
| email | VARCHAR(150) | NOT NULL | Email address |

## warranties

| Column | Type | Constraints | Description |
|---|---|---|---|
| id | VARCHAR(30) | Primary key | Identifier, for example GAR-0001 |
| product_serial | VARCHAR(100) | NOT NULL, UNIQUE, foreign key to products | Covered product |
| customer_document | VARCHAR(50) | NOT NULL, foreign key to customers | Customer who owns the warranty |
| purchase_date | DATE | NOT NULL | Purchase date |
| coverage_months | INT | NOT NULL, CHECK coverage_months > 0 | Coverage in months |

Index: idx_warranties_customer (customer_document).

## claims

| Column | Type | Constraints | Description |
|---|---|---|---|
| id | VARCHAR(30) | Primary key | Identifier, for example REC-0001 |
| warranty_id | VARCHAR(30) | NOT NULL, foreign key to warranties | Claimed warranty |
| description | TEXT | NOT NULL | Problem description |
| claim_date | DATE | NOT NULL | Claim date |
| current_status | VARCHAR(20) | NOT NULL, CHECK in PENDING, IN_REPAIR, RESOLVED, REJECTED | Current status |

Indexes: idx_claims_warranty (warranty_id) and idx_claims_status (current_status).

## claim_status_history

| Column | Type | Constraints | Description |
|---|---|---|---|
| id | SERIAL | Primary key | Order of the changes |
| claim_id | VARCHAR(30) | NOT NULL, foreign key to claims, ON DELETE CASCADE | Claim the change belongs to |
| status | VARCHAR(20) | NOT NULL, CHECK on the four statuses | Status after the change |
| change_date | DATE | NOT NULL | Change date |

Index: idx_history_claim (claim_id).