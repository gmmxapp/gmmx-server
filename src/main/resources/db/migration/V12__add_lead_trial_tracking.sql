-- V12: Add Trial Tracking to Leads
ALTER TABLE leads ADD COLUMN trial_date DATE;
ALTER TABLE leads ADD COLUMN is_trial_completed BOOLEAN DEFAULT FALSE;

CREATE INDEX idx_leads_trial_date ON leads(trial_date);
