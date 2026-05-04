-- V17: Add attendance settings to tenants_aud table
ALTER TABLE tenants_aud 
ADD COLUMN attendance_mode VARCHAR(20),
ADD COLUMN latitude DOUBLE PRECISION,
ADD COLUMN longitude DOUBLE PRECISION,
ADD COLUMN attendance_radius DOUBLE PRECISION,
ADD COLUMN qr_secret TEXT;
