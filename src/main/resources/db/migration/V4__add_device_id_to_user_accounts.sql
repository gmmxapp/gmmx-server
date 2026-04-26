-- V4__add_device_id_to_user_accounts.sql
ALTER TABLE user_accounts 
ADD COLUMN device_id VARCHAR(255);
