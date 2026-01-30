-- V2__fix_outbox_columns.sql
-- Naprawienie literówek w nazwach kolumn tabeli outbox

ALTER TABLE outbox RENAME COLUMN agregate_type TO aggregate_type;
ALTER TABLE outbox RENAME COLUMN agregate_id TO aggregate_id;
