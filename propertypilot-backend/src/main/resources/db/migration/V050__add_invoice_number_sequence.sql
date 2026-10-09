CREATE SEQUENCE invoice_number_seq AS BIGINT START WITH 1 INCREMENT BY 1 NO CYCLE;

WITH existing_numbers AS (
    SELECT MAX(SUBSTRING(invoice_number FROM '^PP-INV-([0-9]+)$')::BIGINT) AS last_number
    FROM invoices
)
SELECT setval(
    'invoice_number_seq',
    COALESCE(last_number, 1),
    last_number IS NOT NULL
)
FROM existing_numbers;
