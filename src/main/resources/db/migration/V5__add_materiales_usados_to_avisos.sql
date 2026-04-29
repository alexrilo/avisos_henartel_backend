-- V5: Add optional materiales_usados canonical field to avisos

ALTER TABLE avisos
    ADD COLUMN IF NOT EXISTS materiales_usados VARCHAR(1000);
