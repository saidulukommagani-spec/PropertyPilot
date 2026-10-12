ALTER TABLE user_roles
    ADD COLUMN change_reason VARCHAR(500);

COMMENT ON COLUMN user_roles.change_reason IS
    'Administrator-provided explanation for an access assignment or revocation.';
