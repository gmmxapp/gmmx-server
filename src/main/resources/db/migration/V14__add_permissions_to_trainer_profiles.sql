-- Add permissions column to trainer_profiles and its audit table
ALTER TABLE trainer_profiles ADD COLUMN IF NOT EXISTS permissions TEXT NOT NULL DEFAULT '';
ALTER TABLE trainer_profiles_aud ADD COLUMN IF NOT EXISTS permissions TEXT;
