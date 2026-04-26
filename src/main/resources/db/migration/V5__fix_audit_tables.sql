-- V5__fix_audit_tables.sql

-- 1. Update tenants_aud
ALTER TABLE tenants_aud 
ADD COLUMN IF NOT EXISTS display_name VARCHAR(255),
ADD COLUMN IF NOT EXISTS logo_url VARCHAR(255),
ADD COLUMN IF NOT EXISTS address TEXT,
ADD COLUMN IF NOT EXISTS contact_phone VARCHAR(50);

-- 2. Update user_accounts_aud
ALTER TABLE user_accounts_aud 
ADD COLUMN IF NOT EXISTS failed_login_attempts INT,
ADD COLUMN IF NOT EXISTS account_locked BOOLEAN,
ADD COLUMN IF NOT EXISTS device_id VARCHAR(255);

-- 3. Update member_profiles_aud
ALTER TABLE member_profiles_aud 
ADD COLUMN IF NOT EXISTS join_date DATE,
ADD COLUMN IF NOT EXISTS expiry_date DATE;

-- 4. Update trainer_profiles_aud
ALTER TABLE trainer_profiles_aud 
ADD COLUMN IF NOT EXISTS availability JSONB;

-- 5. Update attendance_aud
ALTER TABLE attendance_aud 
ADD COLUMN IF NOT EXISTS method VARCHAR(50);

-- 6. Update subscriptions_aud
ALTER TABLE subscriptions_aud 
ADD COLUMN IF NOT EXISTS plan_id UUID;
