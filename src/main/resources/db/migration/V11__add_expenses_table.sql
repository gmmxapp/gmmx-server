-- V11: Add Expenses Table
CREATE TABLE expenses (
    id UUID PRIMARY KEY,
    tenant_id UUID NOT NULL REFERENCES tenants(id),
    title VARCHAR(255) NOT NULL,
    description TEXT,
    amount DECIMAL(19, 2) NOT NULL,
    date DATE NOT NULL,
    category VARCHAR(50) NOT NULL,
    payment_method VARCHAR(50),
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP,
    version BIGINT DEFAULT 0
);

CREATE INDEX idx_expenses_tenant_id ON expenses(tenant_id);
CREATE INDEX idx_expenses_date ON expenses(date);
