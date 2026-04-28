-- V10: Add fees details to member profiles
ALTER TABLE member_profiles ADD COLUMN fees_paid DECIMAL(19, 2);
ALTER TABLE member_profiles ADD COLUMN fees_notes TEXT;

ALTER TABLE member_profiles_aud ADD COLUMN fees_paid DECIMAL(19, 2);
ALTER TABLE member_profiles_aud ADD COLUMN fees_notes TEXT;
