-- V3__add_tenant_details.sql
ALTER TABLE tenants 
ADD COLUMN display_name VARCHAR(255),
ADD COLUMN logo_url VARCHAR(255),
ADD COLUMN address TEXT,
ADD COLUMN contact_phone VARCHAR(50);
