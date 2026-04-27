-- Add country_code to user_accounts and its audit table
ALTER TABLE user_accounts ADD COLUMN IF NOT EXISTS country_code VARCHAR(10) DEFAULT '+91';
ALTER TABLE user_accounts_aud ADD COLUMN IF NOT EXISTS country_code VARCHAR(10);

-- Add country_code to tenants and its audit table
ALTER TABLE tenants ADD COLUMN IF NOT EXISTS country_code VARCHAR(10) DEFAULT '+91';
ALTER TABLE tenants_aud ADD COLUMN IF NOT EXISTS country_code VARCHAR(10);
