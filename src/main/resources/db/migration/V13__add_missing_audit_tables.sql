-- V13: Add missing audit tables for Envers

-- 1. Membership Plans Audit Table
CREATE TABLE IF NOT EXISTS membership_plans_aud (
    id UUID NOT NULL,
    rev INTEGER NOT NULL REFERENCES revinfo(rev),
    revtype SMALLINT,
    tenant_id UUID,
    name VARCHAR(255),
    duration_days INTEGER,
    price DECIMAL(19, 2),
    description TEXT,
    created_at TIMESTAMP,
    updated_at TIMESTAMP,
    PRIMARY KEY (id, rev)
);

-- 2. Equipment Audit Table
CREATE TABLE IF NOT EXISTS equipment_aud (
    id UUID NOT NULL,
    rev INTEGER NOT NULL REFERENCES revinfo(rev),
    revtype SMALLINT,
    tenant_id UUID,
    name VARCHAR(255),
    quantity INTEGER,
    condition VARCHAR(50),
    last_maintenance_date DATE,
    created_at TIMESTAMP,
    updated_at TIMESTAMP,
    PRIMARY KEY (id, rev)
);

-- 3. Expenses Audit Table
CREATE TABLE IF NOT EXISTS expenses_aud (
    id UUID NOT NULL,
    rev INTEGER NOT NULL REFERENCES revinfo(rev),
    revtype SMALLINT,
    tenant_id UUID,
    title VARCHAR(255),
    description TEXT,
    amount DECIMAL(19, 2),
    date DATE,
    category VARCHAR(50),
    payment_method VARCHAR(50),
    created_at TIMESTAMP,
    updated_at TIMESTAMP,
    PRIMARY KEY (id, rev)
);
