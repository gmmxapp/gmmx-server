-- V4: Separate country code and mobile number
ALTER TABLE user_accounts ADD COLUMN country_code VARCHAR(10) DEFAULT '+91';
ALTER TABLE user_accounts RENAME COLUMN mobile TO mobile_number;

ALTER TABLE tenants ADD COLUMN country_code VARCHAR(10) DEFAULT '+91';
ALTER TABLE tenants RENAME COLUMN contact_phone TO contact_mobile_number;

-- For existing users, if mobile_number starts with +, try to split it.
-- This is a simple heuristic: if it's 13 chars and starts with +91, split it.
-- But since we normalized them to remove +, we might just have 10 digits.
-- For now, default +91 is safe for existing Indian users.
UPDATE user_accounts SET country_code = '+91' WHERE country_code IS NULL;
UPDATE tenants SET country_code = '+91' WHERE country_code IS NULL;
