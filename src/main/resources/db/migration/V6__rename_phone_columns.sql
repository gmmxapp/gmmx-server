-- Fix missing columns in user_accounts_aud
DO $$ 
BEGIN 
    IF EXISTS (SELECT 1 FROM information_schema.columns WHERE table_name='user_accounts_aud' AND column_name='mobile') THEN
        ALTER TABLE user_accounts_aud RENAME COLUMN mobile TO mobile_number;
    END IF;
END $$;

-- Fix missing columns in tenants_aud
DO $$ 
BEGIN 
    IF EXISTS (SELECT 1 FROM information_schema.columns WHERE table_name='tenants_aud' AND column_name='contact_phone') THEN
        ALTER TABLE tenants_aud RENAME COLUMN contact_phone TO contact_mobile_number;
    END IF;
END $$;

-- Ensure country_code exists in audit tables (V4 missed them)
ALTER TABLE user_accounts_aud ADD COLUMN IF NOT EXISTS country_code VARCHAR(10);
ALTER TABLE tenants_aud ADD COLUMN IF NOT EXISTS country_code VARCHAR(10);
