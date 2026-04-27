-- Add assigned_trainer_id to member_profiles and its audit table
ALTER TABLE member_profiles ADD COLUMN IF NOT EXISTS assigned_trainer_id UUID REFERENCES trainer_profiles(id);
ALTER TABLE member_profiles_aud ADD COLUMN IF NOT EXISTS assigned_trainer_id UUID;
