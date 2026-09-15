CREATE TABLE document_service_catalog
(
    document_service_id UUID PRIMARY KEY,

    document_code VARCHAR(100) NOT NULL UNIQUE,

    service_available BOOLEAN NOT NULL DEFAULT FALSE,

    service_name VARCHAR(255),

    active BOOLEAN NOT NULL DEFAULT TRUE,

    created_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,

    updated_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP
);