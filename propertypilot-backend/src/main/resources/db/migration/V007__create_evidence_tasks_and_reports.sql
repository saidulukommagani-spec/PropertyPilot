CREATE TABLE evidence (
    evidence_id UUID PRIMARY KEY DEFAULT gen_random_uuid(), visit_id UUID NOT NULL REFERENCES visits(visit_id) ON DELETE CASCADE,
    evidence_type VARCHAR(80) NOT NULL, file_url TEXT NOT NULL, media_type VARCHAR(100), captured_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    checksum_sha256 CHAR(64),
    created_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP, updated_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by UUID REFERENCES users(user_id) ON DELETE SET NULL, updated_by UUID REFERENCES users(user_id) ON DELETE SET NULL, version BIGINT NOT NULL DEFAULT 0,
    CONSTRAINT ck_evidence_type CHECK (length(btrim(evidence_type)) > 0), CONSTRAINT ck_evidence_url CHECK (length(btrim(file_url)) > 0),
    CONSTRAINT ck_evidence_checksum CHECK (checksum_sha256 IS NULL OR checksum_sha256 ~ '^[0-9a-f]{64}$'),
    CONSTRAINT ck_evidence_audit CHECK (updated_at >= created_at AND version >= 0)
);

CREATE TABLE tasks (
    task_id UUID PRIMARY KEY DEFAULT gen_random_uuid(), service_request_id UUID NOT NULL REFERENCES service_requests(service_request_id) ON DELETE CASCADE,
    task_name VARCHAR(150) NOT NULL, task_status VARCHAR(30) NOT NULL DEFAULT 'OPEN', assignee_user_id UUID REFERENCES users(user_id) ON DELETE SET NULL,
    due_at TIMESTAMPTZ, completed_at TIMESTAMPTZ,
    created_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP, updated_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by UUID REFERENCES users(user_id) ON DELETE SET NULL, updated_by UUID REFERENCES users(user_id) ON DELETE SET NULL, version BIGINT NOT NULL DEFAULT 0,
    CONSTRAINT ck_tasks_name CHECK (length(btrim(task_name)) > 0), CONSTRAINT ck_tasks_status CHECK (task_status IN ('OPEN','IN_PROGRESS','BLOCKED','COMPLETED','CANCELLED')),
    CONSTRAINT ck_tasks_completion CHECK ((task_status = 'COMPLETED' AND completed_at IS NOT NULL) OR (task_status <> 'COMPLETED' AND completed_at IS NULL)),
    CONSTRAINT ck_tasks_audit CHECK (updated_at >= created_at AND version >= 0)
);

CREATE TABLE reports (
    report_id UUID PRIMARY KEY DEFAULT gen_random_uuid(), service_request_id UUID NOT NULL REFERENCES service_requests(service_request_id) ON DELETE CASCADE,
    report_type VARCHAR(80) NOT NULL, report_status VARCHAR(30) NOT NULL DEFAULT 'DRAFT', summary TEXT, document_url TEXT,
    submitted_at TIMESTAMPTZ, approved_at TIMESTAMPTZ, delivered_at TIMESTAMPTZ,
    created_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP, updated_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by UUID REFERENCES users(user_id) ON DELETE SET NULL, updated_by UUID REFERENCES users(user_id) ON DELETE SET NULL, version BIGINT NOT NULL DEFAULT 0,
    CONSTRAINT ck_reports_type CHECK (length(btrim(report_type)) > 0),
    CONSTRAINT ck_reports_status CHECK (report_status IN ('DRAFT','SUBMITTED','UNDER_REVIEW','APPROVED','DELIVERED','REJECTED','ARCHIVED')),
    CONSTRAINT ck_reports_submission CHECK (report_status = 'DRAFT' OR submitted_at IS NOT NULL OR report_status = 'ARCHIVED'),
    CONSTRAINT ck_reports_approval CHECK (report_status NOT IN ('APPROVED','DELIVERED') OR approved_at IS NOT NULL),
    CONSTRAINT ck_reports_delivery CHECK (report_status <> 'DELIVERED' OR delivered_at IS NOT NULL),
    CONSTRAINT ck_reports_timestamps CHECK ((approved_at IS NULL OR submitted_at IS NULL OR approved_at >= submitted_at) AND (delivered_at IS NULL OR approved_at IS NULL OR delivered_at >= approved_at)),
    CONSTRAINT ck_reports_audit CHECK (updated_at >= created_at AND version >= 0)
);

CREATE INDEX idx_evidence_visit_captured ON evidence(visit_id,captured_at DESC);
CREATE INDEX idx_evidence_type ON evidence(evidence_type);
CREATE INDEX idx_tasks_request_status ON tasks(service_request_id,task_status);
CREATE INDEX idx_tasks_assignee_status ON tasks(assignee_user_id,task_status) WHERE assignee_user_id IS NOT NULL;
CREATE INDEX idx_tasks_due_open ON tasks(due_at) WHERE task_status IN ('OPEN','IN_PROGRESS','BLOCKED');
CREATE INDEX idx_reports_request_created ON reports(service_request_id,created_at DESC);
CREATE INDEX idx_reports_status_created ON reports(report_status,created_at DESC);

CREATE TRIGGER trg_evidence_audit BEFORE UPDATE ON evidence FOR EACH ROW EXECUTE FUNCTION set_audit_columns();
CREATE TRIGGER trg_tasks_audit BEFORE UPDATE ON tasks FOR EACH ROW EXECUTE FUNCTION set_audit_columns();
CREATE TRIGGER trg_reports_audit BEFORE UPDATE ON reports FOR EACH ROW EXECUTE FUNCTION set_audit_columns();

COMMENT ON TABLE evidence IS 'Stores tamper-evident metadata for visit evidence retained in protected object storage.';
COMMENT ON TABLE tasks IS 'Stores actionable work items belonging to a service request.';
COMMENT ON TABLE reports IS 'Stores versioned service-delivery report records and publication state.';
COMMENT ON COLUMN evidence.evidence_id IS 'System-generated UUID primary key.'; COMMENT ON COLUMN evidence.visit_id IS 'Visit documented by the evidence.'; COMMENT ON COLUMN evidence.evidence_type IS 'Business-defined evidence classification.'; COMMENT ON COLUMN evidence.file_url IS 'Protected object-storage reference.'; COMMENT ON COLUMN evidence.media_type IS 'MIME media type.'; COMMENT ON COLUMN evidence.captured_at IS 'Timestamp when the evidence was captured.'; COMMENT ON COLUMN evidence.checksum_sha256 IS 'Lowercase SHA-256 digest of the stored object.';
COMMENT ON COLUMN tasks.task_id IS 'System-generated UUID primary key.'; COMMENT ON COLUMN tasks.service_request_id IS 'Parent service request.'; COMMENT ON COLUMN tasks.task_name IS 'Short actionable task description.'; COMMENT ON COLUMN tasks.task_status IS 'Current task lifecycle state.'; COMMENT ON COLUMN tasks.assignee_user_id IS 'User currently responsible for the task.'; COMMENT ON COLUMN tasks.due_at IS 'Optional task deadline.'; COMMENT ON COLUMN tasks.completed_at IS 'Timestamp when the task was completed.';
COMMENT ON COLUMN reports.report_id IS 'System-generated UUID primary key.'; COMMENT ON COLUMN reports.service_request_id IS 'Service request documented by the report.'; COMMENT ON COLUMN reports.report_type IS 'Business-defined report classification.'; COMMENT ON COLUMN reports.report_status IS 'Current governed report state.'; COMMENT ON COLUMN reports.summary IS 'Human-readable report summary.'; COMMENT ON COLUMN reports.document_url IS 'Protected reference to the rendered report artifact.'; COMMENT ON COLUMN reports.submitted_at IS 'Timestamp when the report was submitted.'; COMMENT ON COLUMN reports.approved_at IS 'Timestamp when the report was approved.'; COMMENT ON COLUMN reports.delivered_at IS 'Timestamp when the report was delivered.';
COMMENT ON COLUMN evidence.created_at IS 'Creation timestamp.'; COMMENT ON COLUMN evidence.updated_at IS 'Last-update timestamp.'; COMMENT ON COLUMN evidence.created_by IS 'Creating user.'; COMMENT ON COLUMN evidence.updated_by IS 'Last-updating user.'; COMMENT ON COLUMN evidence.version IS 'Optimistic-lock version.';
COMMENT ON COLUMN tasks.created_at IS 'Creation timestamp.'; COMMENT ON COLUMN tasks.updated_at IS 'Last-update timestamp.'; COMMENT ON COLUMN tasks.created_by IS 'Creating user.'; COMMENT ON COLUMN tasks.updated_by IS 'Last-updating user.'; COMMENT ON COLUMN tasks.version IS 'Optimistic-lock version.';
COMMENT ON COLUMN reports.created_at IS 'Creation timestamp.'; COMMENT ON COLUMN reports.updated_at IS 'Last-update timestamp.'; COMMENT ON COLUMN reports.created_by IS 'Creating user.'; COMMENT ON COLUMN reports.updated_by IS 'Last-updating user.'; COMMENT ON COLUMN reports.version IS 'Optimistic-lock version.';
