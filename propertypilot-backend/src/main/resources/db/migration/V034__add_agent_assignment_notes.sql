ALTER TABLE agent_assignments
    ADD COLUMN assignment_notes TEXT;

COMMENT ON COLUMN agent_assignments.assignment_notes IS
    'Operations notes recorded when the field agent was assigned.';
