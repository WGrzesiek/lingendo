-- V2__fix_outbox_columns.sql
-- Naprawienie literówek w nazwach kolumn tabeli outbox

ALTER TABLE koog_outbox RENAME COLUMN agregate_type TO aggregate_type;
ALTER TABLE koog_outbox RENAME COLUMN agregate_id TO aggregate_id;
