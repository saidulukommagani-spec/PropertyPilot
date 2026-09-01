CREATE TABLE auth_otp_challenges (
    otp_challenge_id UUID PRIMARY KEY DEFAULT gen_random_uuid(), user_id UUID REFERENCES users(user_id) ON DELETE CASCADE,
    identifier VARCHAR(255) NOT NULL, purpose VARCHAR(50) NOT NULL, delivery_channel VARCHAR(30) NOT NULL, otp_hash VARCHAR(255) NOT NULL,
    expires_at TIMESTAMPTZ NOT NULL, attempt_count INTEGER NOT NULL DEFAULT 0, verified_at TIMESTAMPTZ,
    created_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP, updated_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by UUID REFERENCES users(user_id) ON DELETE SET NULL, updated_by UUID REFERENCES users(user_id) ON DELETE SET NULL, version BIGINT NOT NULL DEFAULT 0,
    CONSTRAINT ck_otp_identifier CHECK(length(btrim(identifier))>0), CONSTRAINT ck_otp_purpose CHECK(purpose IN ('LOGIN','REGISTRATION','RECOVERY','KYC')),
    CONSTRAINT ck_otp_channel CHECK(delivery_channel IN ('SMS','EMAIL','WHATSAPP')), CONSTRAINT ck_otp_hash CHECK(length(otp_hash)>=32),
    CONSTRAINT ck_otp_expiry CHECK(expires_at>created_at), CONSTRAINT ck_otp_attempts CHECK(attempt_count BETWEEN 0 AND 20),
    CONSTRAINT ck_otp_verification CHECK(verified_at IS NULL OR verified_at<=expires_at), CONSTRAINT ck_otp_audit CHECK(updated_at>=created_at AND version>=0)
);
CREATE INDEX idx_otp_identifier_purpose_expiry ON auth_otp_challenges(identifier,purpose,expires_at DESC);
CREATE INDEX idx_otp_user_verified ON auth_otp_challenges(user_id,verified_at DESC) WHERE user_id IS NOT NULL;
CREATE TRIGGER trg_otp_audit BEFORE UPDATE ON auth_otp_challenges FOR EACH ROW EXECUTE FUNCTION set_audit_columns();

CREATE TABLE auth_sessions (
    session_id UUID PRIMARY KEY DEFAULT gen_random_uuid(), user_id UUID NOT NULL REFERENCES users(user_id) ON DELETE CASCADE,
    device_id VARCHAR(255) NOT NULL, refresh_token_hash VARCHAR(255) NOT NULL, issued_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    expires_at TIMESTAMPTZ NOT NULL, revoked_at TIMESTAMPTZ, risk_status VARCHAR(30) NOT NULL DEFAULT 'NORMAL',
    created_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP, updated_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by UUID REFERENCES users(user_id) ON DELETE SET NULL, updated_by UUID REFERENCES users(user_id) ON DELETE SET NULL, version BIGINT NOT NULL DEFAULT 0,
    CONSTRAINT ck_sessions_device CHECK(length(btrim(device_id))>0), CONSTRAINT ck_sessions_hash CHECK(length(refresh_token_hash)>=32),
    CONSTRAINT ck_sessions_expiry CHECK(expires_at>issued_at), CONSTRAINT ck_sessions_revocation CHECK(revoked_at IS NULL OR revoked_at>=issued_at),
    CONSTRAINT ck_sessions_risk CHECK(risk_status IN ('NORMAL','REVIEW','BLOCKED')), CONSTRAINT ck_sessions_audit CHECK(updated_at>=created_at AND version>=0)
);
CREATE UNIQUE INDEX uq_auth_sessions_active_token ON auth_sessions(refresh_token_hash) WHERE revoked_at IS NULL;
CREATE INDEX idx_auth_sessions_user_expiry ON auth_sessions(user_id,expires_at DESC);
CREATE INDEX idx_auth_sessions_device_revoked ON auth_sessions(device_id,revoked_at);
CREATE TRIGGER trg_auth_sessions_audit BEFORE UPDATE ON auth_sessions FOR EACH ROW EXECUTE FUNCTION set_audit_columns();

COMMENT ON TABLE auth_otp_challenges IS 'Stores hashed, single-use OTP authentication challenges.';
COMMENT ON TABLE auth_sessions IS 'Stores hashed refresh-token sessions bound to users and devices.';
COMMENT ON COLUMN auth_otp_challenges.otp_challenge_id IS 'Primary key.'; COMMENT ON COLUMN auth_otp_challenges.user_id IS 'Known user when resolved.'; COMMENT ON COLUMN auth_otp_challenges.identifier IS 'Delivery identifier.'; COMMENT ON COLUMN auth_otp_challenges.purpose IS 'Challenge purpose.'; COMMENT ON COLUMN auth_otp_challenges.delivery_channel IS 'OTP delivery channel.'; COMMENT ON COLUMN auth_otp_challenges.otp_hash IS 'Salted OTP hash.'; COMMENT ON COLUMN auth_otp_challenges.expires_at IS 'Challenge expiry.'; COMMENT ON COLUMN auth_otp_challenges.attempt_count IS 'Verification attempts.'; COMMENT ON COLUMN auth_otp_challenges.verified_at IS 'Successful verification time.';
COMMENT ON COLUMN auth_sessions.session_id IS 'Primary key.'; COMMENT ON COLUMN auth_sessions.user_id IS 'Authenticated user.'; COMMENT ON COLUMN auth_sessions.device_id IS 'Device context.'; COMMENT ON COLUMN auth_sessions.refresh_token_hash IS 'One-way refresh-token hash.'; COMMENT ON COLUMN auth_sessions.issued_at IS 'Session issue time.'; COMMENT ON COLUMN auth_sessions.expires_at IS 'Session expiry.'; COMMENT ON COLUMN auth_sessions.revoked_at IS 'Revocation time.'; COMMENT ON COLUMN auth_sessions.risk_status IS 'Session risk state.';
COMMENT ON COLUMN auth_otp_challenges.created_at IS 'Creation time.'; COMMENT ON COLUMN auth_otp_challenges.updated_at IS 'Update time.'; COMMENT ON COLUMN auth_otp_challenges.created_by IS 'Creating user.'; COMMENT ON COLUMN auth_otp_challenges.updated_by IS 'Updating user.'; COMMENT ON COLUMN auth_otp_challenges.version IS 'Lock version.';
COMMENT ON COLUMN auth_sessions.created_at IS 'Creation time.'; COMMENT ON COLUMN auth_sessions.updated_at IS 'Update time.'; COMMENT ON COLUMN auth_sessions.created_by IS 'Creating user.'; COMMENT ON COLUMN auth_sessions.updated_by IS 'Updating user.'; COMMENT ON COLUMN auth_sessions.version IS 'Lock version.';
