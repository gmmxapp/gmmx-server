-- V8: Add Membership Plans, Equipment, and Messaging

-- 1. Membership Plans Table (Gym-specific)
CREATE TABLE membership_plans (
    id UUID PRIMARY KEY,
    tenant_id UUID NOT NULL REFERENCES tenants(id),
    name VARCHAR(255) NOT NULL,
    duration_days INTEGER NOT NULL,
    price DECIMAL(19, 2) NOT NULL,
    description TEXT,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP,
    version BIGINT DEFAULT 0
);

CREATE INDEX idx_membership_plan_tenant_id ON membership_plans(tenant_id);

-- 2. Equipment Table
CREATE TABLE equipment (
    id UUID PRIMARY KEY,
    tenant_id UUID NOT NULL REFERENCES tenants(id),
    name VARCHAR(255) NOT NULL,
    quantity INTEGER NOT NULL DEFAULT 1,
    condition VARCHAR(50) NOT NULL, -- NEW, GOOD, FAIR, POOR, BROKEN
    last_maintenance_date DATE,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP,
    version BIGINT DEFAULT 0
);

CREATE INDEX idx_equipment_tenant_id ON equipment(tenant_id);

-- 3. Chat Messages Table
CREATE TABLE chat_messages (
    id UUID PRIMARY KEY,
    tenant_id UUID NOT NULL REFERENCES tenants(id),
    sender_id UUID NOT NULL REFERENCES user_accounts(id),
    recipient_id UUID NOT NULL REFERENCES user_accounts(id),
    message TEXT NOT NULL,
    is_read BOOLEAN NOT NULL DEFAULT FALSE,
    created_at TIMESTAMP NOT NULL,
    version BIGINT DEFAULT 0
);

CREATE INDEX idx_chat_msg_tenant_id ON chat_messages(tenant_id);
CREATE INDEX idx_chat_msg_sender ON chat_messages(sender_id);
CREATE INDEX idx_chat_msg_recipient ON chat_messages(recipient_id);

-- 4. FCM Tokens Table
CREATE TABLE fcm_tokens (
    user_id UUID PRIMARY KEY REFERENCES user_accounts(id),
    token TEXT NOT NULL,
    updated_at TIMESTAMP NOT NULL
);

-- 5. Update Member Profile to link to Membership Plan
ALTER TABLE member_profiles ADD COLUMN membership_plan_id UUID REFERENCES membership_plans(id);
ALTER TABLE member_profiles_aud ADD COLUMN membership_plan_id UUID;
