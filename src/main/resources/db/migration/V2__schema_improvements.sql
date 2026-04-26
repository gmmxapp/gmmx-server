-- V2__schema_improvements.sql

-- 1. Create Plans Table
CREATE TABLE plans (
    id UUID PRIMARY KEY,
    name VARCHAR(50) NOT NULL,
    price DECIMAL(10,2) NOT NULL,
    max_members INT NOT NULL,
    has_microsite BOOLEAN NOT NULL DEFAULT FALSE,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP,
    version BIGINT DEFAULT 0
);

-- 2. Create Payments Table
CREATE TABLE payments (
    id UUID PRIMARY KEY,
    tenant_id UUID NOT NULL,
    amount DECIMAL(10,2) NOT NULL,
    status VARCHAR(50) NOT NULL,
    payment_method VARCHAR(50),
    transaction_id VARCHAR(255),
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP,
    version BIGINT DEFAULT 0
);

-- 3. Security & Feature Columns for User Accounts
ALTER TABLE user_accounts 
ADD COLUMN failed_login_attempts INT DEFAULT 0,
ADD COLUMN account_locked BOOLEAN DEFAULT FALSE;

-- 4. Multi-tenant Email Uniqueness
-- Note: We first drop the old global unique constraint. 
-- In V1, the name is likely 'user_accounts_email_key' by default in Postgres if not specified.
ALTER TABLE user_accounts DROP CONSTRAINT IF EXISTS user_accounts_email_key;
DROP INDEX IF EXISTS idx_user_email;
CREATE UNIQUE INDEX uq_user_email_tenant ON user_accounts(email, tenant_id);

-- 5. Role Enum
-- Note: Check if type exists first to avoid errors on retry
DO $$ BEGIN
    CREATE TYPE user_role AS ENUM ('OWNER', 'TRAINER', 'MEMBER');
EXCEPTION
    WHEN duplicate_object THEN null;
END $$;

ALTER TABLE user_accounts 
ALTER COLUMN role TYPE user_role USING role::user_role;

-- 6. Member Feature Columns
ALTER TABLE member_profiles 
ADD COLUMN join_date DATE,
ADD COLUMN expiry_date DATE;

-- 7. Trainer Feature Columns
ALTER TABLE trainer_profiles 
ADD COLUMN availability JSONB;

-- 8. Attendance Design Fix
ALTER TABLE attendance DROP CONSTRAINT IF EXISTS attendance_member_id_fkey;
-- We need to ensure member_id can reference member_profiles(id)
-- For existing data, this might fail if member_id was a user_id. 
-- We'll assume a fresh start or the user handles migration.
ALTER TABLE attendance 
ADD CONSTRAINT fk_attendance_member_profile 
FOREIGN KEY (member_id) REFERENCES member_profiles(id);

ALTER TABLE attendance 
ADD COLUMN method VARCHAR(50) DEFAULT 'MANUAL';

-- 9. Subscription Upgrade
ALTER TABLE subscriptions 
ADD COLUMN plan_id UUID REFERENCES plans(id);

-- 10. Strict Foreign Keys for Tenant Isolation
ALTER TABLE user_accounts ADD CONSTRAINT fk_user_tenant FOREIGN KEY (tenant_id) REFERENCES tenants(id);
ALTER TABLE member_profiles ADD CONSTRAINT fk_member_tenant FOREIGN KEY (tenant_id) REFERENCES tenants(id);
ALTER TABLE trainer_profiles ADD CONSTRAINT fk_trainer_tenant FOREIGN KEY (tenant_id) REFERENCES tenants(id);
ALTER TABLE attendance ADD CONSTRAINT fk_attendance_tenant FOREIGN KEY (tenant_id) REFERENCES tenants(id);
ALTER TABLE subscriptions ADD CONSTRAINT fk_subscription_tenant FOREIGN KEY (tenant_id) REFERENCES tenants(id);
ALTER TABLE payments ADD CONSTRAINT fk_payment_tenant FOREIGN KEY (tenant_id) REFERENCES tenants(id);
ALTER TABLE refresh_tokens ADD CONSTRAINT fk_refresh_token_tenant FOREIGN KEY (tenant_id) REFERENCES tenants(id);

-- 11. Performance Indexes
CREATE INDEX idx_attendance_member_date ON attendance(member_id, date);
CREATE INDEX idx_user_role ON user_accounts(role);
CREATE INDEX idx_subscription_active ON subscriptions(active);
