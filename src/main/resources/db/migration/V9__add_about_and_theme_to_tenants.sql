-- V9: Add about and theme_primary to tenants
ALTER TABLE tenants ADD COLUMN about TEXT;
ALTER TABLE tenants ADD COLUMN theme_primary VARCHAR(50) DEFAULT '#ef4444';

ALTER TABLE tenants_aud ADD COLUMN about TEXT;
ALTER TABLE tenants_aud ADD COLUMN theme_primary VARCHAR(50);
