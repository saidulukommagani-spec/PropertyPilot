CREATE TABLE evidence (
    evidence_id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    visit_id UUID NOT NULL REFERENCES visits(visit_id) ON DELETE CASCADE,
    evidence_type VARCHAR(80) NOT NULL,
    file_url TEXT NOT NULL,
    media_type VARCHAR(40),
    captured_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    uploaded_by VARCHAR(150)
);

CREATE TABLE tasks (
    task_id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    service_request_id UUID NOT NULL REFERENCES service_requests(service_request_id) ON DELETE CASCADE,
    task_name VARCHAR(150) NOT NULL,
    task_status VARCHAR(30) NOT NULL DEFAULT 'open',
    assignee_type VARCHAR(40),
    assignee_id UUID,
    due_at TIMESTAMPTZ,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW()
);
