-- V15: Normalize all existing mobile numbers to digits only, handling Indian country codes
-- This fixes the issue where logins fail because of +91 prefixes or formatting

UPDATE user_accounts 
SET mobile_number = CASE 
    -- If it starts with +91, remove it
    WHEN mobile_number LIKE '+91%' THEN SUBSTRING(REGEXP_REPLACE(mobile_number, '[^0-9]', '', 'g') FROM 3)
    -- If it starts with 91 and has 12 digits, remove the 91
    WHEN REGEXP_REPLACE(mobile_number, '[^0-9]', '', 'g') LIKE '91%' AND LENGTH(REGEXP_REPLACE(mobile_number, '[^0-9]', '', 'g')) = 12 THEN SUBSTRING(REGEXP_REPLACE(mobile_number, '[^0-9]', '', 'g') FROM 3)
    -- If it starts with 0 and has 11 digits, remove the 0
    WHEN REGEXP_REPLACE(mobile_number, '[^0-9]', '', 'g') LIKE '0%' AND LENGTH(REGEXP_REPLACE(mobile_number, '[^0-9]', '', 'g')) = 11 THEN SUBSTRING(REGEXP_REPLACE(mobile_number, '[^0-9]', '', 'g') FROM 2)
    -- Otherwise just strip all non-digits
    ELSE REGEXP_REPLACE(mobile_number, '[^0-9]', '', 'g')
END
WHERE mobile_number IS NOT NULL AND mobile_number != '';
