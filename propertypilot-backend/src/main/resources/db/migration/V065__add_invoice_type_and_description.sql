ALTER TABLE invoices
    ADD COLUMN invoice_type VARCHAR(255),
    ADD COLUMN description VARCHAR(255);

ALTER TABLE invoices
    ADD CONSTRAINT ck_invoices_invoice_type CHECK (
        invoice_type IS NULL OR invoice_type IN (
            'SERVICE_CHARGE',
            'BUYER_COMMISSION',
            'SELLER_COMMISSION',
            'SUBSCRIPTION_FEE',
            'SUBSCRIPTION_RENEWAL',
            'DOCUMENT_SERVICE',
            'REGISTRATION_SERVICE'
        )
    );
