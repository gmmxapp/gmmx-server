-- Combined Initial Schema for GMMX Backend
-- Consolidates V1 through V5

-- 0. Extensions and Types
CREATE TYPE user_role AS ENUM ('OWNER', 'TRAINER', 'MEMBER');

-- 1. Tenants Table
CREATE TABLE tenants (
    id UUID PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    subdomain VARCHAR(255) NOT NULL UNIQUE,
    display_name VARCHAR(255),
    logo_url VARCHAR(255),
    address TEXT,
    contact_phone VARCHAR(50),
    plan VARCHAR(50) NOT NULL,
    is_active BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP
);

CREATE INDEX idx_tenant_subdomain ON tenants(subdomain);

-- 2. Plans Table
CREATE TABLE plans (
    id UUID PRIMARY KEY,
    name VARCHAR(50) NOT NULL,
    price DECIMAL(10, 2) NOT NULL,
    max_members INT NOT NULL,
    has_microsite BOOLEAN NOT NULL DEFAULT FALSE,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP,
    version BIGINT DEFAULT 0
);

-- 3. User Accounts Table
CREATE TABLE user_accounts (
    id UUID PRIMARY KEY,
    tenant_id UUID NOT NULL REFERENCES tenants(id),
    email VARCHAR(255) NOT NULL,
    full_name VARCHAR(255) NOT NULL,
    mobile VARCHAR(50),
    password_hash VARCHAR(255) NOT NULL,
    role user_role NOT NULL,
    active BOOLEAN NOT NULL DEFAULT TRUE,
    failed_login_attempts INT DEFAULT 0,
    account_locked BOOLEAN DEFAULT FALSE,
    device_id VARCHAR(255),
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP,
    version BIGINT DEFAULT 0
);

CREATE INDEX idx_user_tenant_id ON user_accounts(tenant_id);
CREATE UNIQUE INDEX uq_user_email_tenant ON user_accounts(email, tenant_id);
CREATE INDEX idx_user_role ON user_accounts(role);

-- 4. Member Profiles Table
CREATE TABLE member_profiles (
    id UUID PRIMARY KEY,
    tenant_id UUID NOT NULL REFERENCES tenants(id),
    user_id UUID NOT NULL REFERENCES user_accounts(id),
    height DOUBLE PRECISION,
    weight DOUBLE PRECISION,
    medical_history TEXT,
    goals TEXT,
    status VARCHAR(50) NOT NULL,
    join_date DATE,
    expiry_date DATE,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP,
    version BIGINT DEFAULT 0
);

CREATE INDEX idx_member_tenant_id ON member_profiles(tenant_id);

-- 5. Trainer Profiles Table
CREATE TABLE trainer_profiles (
    id UUID PRIMARY KEY,
    tenant_id UUID NOT NULL REFERENCES tenants(id),
    user_id UUID NOT NULL REFERENCES user_accounts(id),
    specialization VARCHAR(255),
    experience_years INTEGER,
    availability JSONB,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP,
    version BIGINT DEFAULT 0
);

CREATE INDEX idx_trainer_tenant_id ON trainer_profiles(tenant_id);

-- 6. Attendance Table
CREATE TABLE attendance (
    id UUID PRIMARY KEY,
    tenant_id UUID NOT NULL REFERENCES tenants(id),
    member_id UUID NOT NULL REFERENCES member_profiles(id),
    check_in TIMESTAMP NOT NULL,
    check_out TIMESTAMP,
    date DATE NOT NULL,
    method VARCHAR(50) DEFAULT 'MANUAL',
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP,
    version BIGINT DEFAULT 0
);

CREATE INDEX idx_attendance_tenant_id ON attendance(tenant_id);
CREATE INDEX idx_attendance_member_date ON attendance(member_id, date);

-- 7. Subscriptions Table
CREATE TABLE subscriptions (
    id UUID PRIMARY KEY,
    tenant_id UUID NOT NULL REFERENCES tenants(id),
    plan_type VARCHAR(50) NOT NULL,
    plan_id UUID REFERENCES plans(id),
    price DECIMAL(19, 2) NOT NULL,
    valid_until TIMESTAMP NOT NULL,
    active BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP,
    version BIGINT DEFAULT 0
);

CREATE INDEX idx_subscription_tenant_id ON subscriptions(tenant_id);
CREATE INDEX idx_subscription_active ON subscriptions(active);

-- 8. Payments Table
CREATE TABLE payments (
    id UUID PRIMARY KEY,
    tenant_id UUID NOT NULL REFERENCES tenants(id),
    amount DECIMAL(10, 2) NOT NULL,
    status VARCHAR(50) NOT NULL,
    payment_method VARCHAR(50),
    transaction_id VARCHAR(255),
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP,
    version BIGINT DEFAULT 0
);

CREATE INDEX idx_payment_tenant_id ON payments(tenant_id);

-- 9. Refresh Tokens Table
CREATE TABLE refresh_tokens (
    id UUID PRIMARY KEY,
    tenant_id UUID NOT NULL REFERENCES tenants(id),
    user_id UUID NOT NULL REFERENCES user_accounts(id),
    token VARCHAR(255) NOT NULL UNIQUE,
    expiry_date TIMESTAMP NOT NULL,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP,
    version BIGINT DEFAULT 0
);

CREATE INDEX idx_refresh_token_tenant_id ON refresh_tokens(tenant_id);

-- 10. Audit Tables (Hibernate Envers)
CREATE TABLE revinfo (
    rev INTEGER PRIMARY KEY,
    revtstmp BIGINT
);

CREATE SEQUENCE revinfo_seq START WITH 1 INCREMENT BY 50;

CREATE TABLE tenants_aud (
    id UUID NOT NULL,
    rev INTEGER NOT NULL REFERENCES revinfo(rev),
    revtype SMALLINT,
    name VARCHAR(255),
    subdomain VARCHAR(255),
    display_name VARCHAR(255),
    logo_url VARCHAR(255),
    address TEXT,
    contact_phone VARCHAR(50),
    plan VARCHAR(50),
    is_active BOOLEAN,
    created_at TIMESTAMP,
    updated_at TIMESTAMP,
    PRIMARY KEY (id, rev)
);

CREATE TABLE user_accounts_aud (
    id UUID NOT NULL,
    rev INTEGER NOT NULL REFERENCES revinfo(rev),
    revtype SMALLINT,
    tenant_id UUID,
    email VARCHAR(255),
    full_name VARCHAR(255),
    mobile VARCHAR(50),
    password_hash VARCHAR(255),
    role user_role,
    active BOOLEAN,
    failed_login_attempts INT,
    account_locked BOOLEAN,
    device_id VARCHAR(255),
    created_at TIMESTAMP,
    updated_at TIMESTAMP,
    PRIMARY KEY (id, rev)
);

CREATE TABLE member_profiles_aud (
    id UUID NOT NULL,
    rev INTEGER NOT NULL REFERENCES revinfo(rev),
    revtype SMALLINT,
    tenant_id UUID,
    user_id UUID,
    height DOUBLE PRECISION,
    weight DOUBLE PRECISION,
    medical_history TEXT,
    goals TEXT,
    status VARCHAR(50),
    join_date DATE,
    expiry_date DATE,
    created_at TIMESTAMP,
    updated_at TIMESTAMP,
    PRIMARY KEY (id, rev)
);

CREATE TABLE trainer_profiles_aud (
    id UUID NOT NULL,
    rev INTEGER NOT NULL REFERENCES revinfo(rev),
    revtype SMALLINT,
    tenant_id UUID,
    user_id UUID,
    specialization VARCHAR(255),
    experience_years INTEGER,
    availability JSONB,
    created_at TIMESTAMP,
    updated_at TIMESTAMP,
    PRIMARY KEY (id, rev)
);

CREATE TABLE attendance_aud (
    id UUID NOT NULL,
    rev INTEGER NOT NULL REFERENCES revinfo(rev),
    revtype SMALLINT,
    tenant_id UUID,
    member_id UUID,
    check_in TIMESTAMP,
    check_out TIMESTAMP,
    date DATE,
    method VARCHAR(50),
    created_at TIMESTAMP,
    updated_at TIMESTAMP,
    PRIMARY KEY (id, rev)
);

CREATE TABLE subscriptions_aud (
    id UUID NOT NULL,
    rev INTEGER NOT NULL REFERENCES revinfo(rev),
    revtype SMALLINT,
    tenant_id UUID,
    plan_type VARCHAR(50),
    plan_id UUID,
    price DECIMAL(19, 2),
    valid_until TIMESTAMP,
    active BOOLEAN,
    created_at TIMESTAMP,
    updated_at TIMESTAMP,
    PRIMARY KEY (id, rev)
);
