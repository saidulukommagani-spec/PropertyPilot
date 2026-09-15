CREATE TABLE documents
(
    document_id UUID PRIMARY KEY,

    property_id UUID NOT NULL,

    document_name VARCHAR(255) NOT NULL,

    document_type VARCHAR(100) NOT NULL,

    status VARCHAR(30) NOT NULL,

    created_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,

    updated_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT fk_documents_property
        FOREIGN KEY (property_id)
        REFERENCES properties(property_id)
);



CREATE TABLE document_versions
(
    version_id UUID PRIMARY KEY,

    document_id UUID NOT NULL,

    version_number INTEGER NOT NULL,

    file_name VARCHAR(255) NOT NULL,

    file_path VARCHAR(500) NOT NULL,

    mime_type VARCHAR(100),

    file_size BIGINT,

    current_version BOOLEAN NOT NULL DEFAULT TRUE,

    uploaded_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT fk_document_versions_document
        FOREIGN KEY (document_id)
        REFERENCES documents(document_id)
);


ALTER TABLE documents
ADD CONSTRAINT ck_documents_status
CHECK (
    status IN (
        'ACTIVE',
        'ARCHIVED'
    )
);


CREATE INDEX idx_documents_property_id
ON documents(property_id);

CREATE INDEX idx_document_versions_document_id
ON document_versions(document_id);