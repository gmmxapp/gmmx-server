-- Change role column from custom ENUM to VARCHAR to resolve Hibernate casting issues
ALTER TABLE user_accounts ALTER COLUMN role TYPE VARCHAR(50) USING role::text;
ALTER TABLE user_accounts_aud ALTER COLUMN role TYPE VARCHAR(50) USING role::text;
