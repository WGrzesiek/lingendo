-- Migracja: zmiana nazwy algorytmu z LEINER_ALGORITHM na LEITNER_ALGORITHM

UPDATE deck SET learn_algorithm = 'LEITNER_ALGORITHM' WHERE learn_algorithm = 'LEINER_ALGORITHM';

UPDATE deck_enrollment SET preferred_algorithm = 'LEITNER_ALGORITHM' WHERE preferred_algorithm = 'LEINER_ALGORITHM';

ALTER TABLE deck DROP CONSTRAINT IF EXISTS deck_learn_algorithm_check;
ALTER TABLE deck ADD CONSTRAINT deck_learn_algorithm_check
    CHECK (learn_algorithm IN ('GRZESIEK_ALGORITHM', 'LEITNER_ALGORITHM', 'TEST_ALGORITHM'));

ALTER TABLE deck_enrollment DROP CONSTRAINT IF EXISTS deck_enrollment_preferred_algorithm_check;
ALTER TABLE deck_enrollment ADD CONSTRAINT deck_enrollment_preferred_algorithm_check
    CHECK (preferred_algorithm IN ('GRZESIEK_ALGORITHM', 'LEITNER_ALGORITHM', 'TEST_ALGORITHM'));
