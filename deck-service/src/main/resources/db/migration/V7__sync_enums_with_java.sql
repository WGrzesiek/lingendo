-- Migracja: synchronizacja CHECK constraints z enumami Java

-- ============================================
-- 1. Language - dodanie brakujących języków
-- ============================================
ALTER TABLE deck DROP CONSTRAINT IF EXISTS deck_language_from_check;
ALTER TABLE deck ADD CONSTRAINT deck_language_from_check
    CHECK (language_from IN (
        'POLISH', 'ENGLISH', 'SPANISH', 'GERMAN', 'FRENCH', 'ITALIAN',
        'RUSSIAN', 'CHINESE', 'JAPANESE', 'PORTUGUESE', 'ARABIC', 'HINDI', 'OTHER'
    ));

ALTER TABLE deck DROP CONSTRAINT IF EXISTS deck_language_to_check;
ALTER TABLE deck ADD CONSTRAINT deck_language_to_check
    CHECK (language_to IN (
        'POLISH', 'ENGLISH', 'SPANISH', 'GERMAN', 'FRENCH', 'ITALIAN',
        'RUSSIAN', 'CHINESE', 'JAPANESE', 'PORTUGUESE', 'ARABIC', 'HINDI', 'OTHER'
    ));

-- ============================================
-- 2. DeckCategory - pełna synchronizacja
-- ============================================
ALTER TABLE deck DROP CONSTRAINT IF EXISTS deck_category_check;
ALTER TABLE deck ADD CONSTRAINT deck_category_check
    CHECK (category IN (
        'BUSINESS', 'IT', 'BASICS', 'TOURISM', 'CULTURE', 'SCIENCE',
        'HOME', 'WORK', 'OTHER', 'HEALTH', 'SPORTS', 'EDUCATION',
        'COOKING', 'FINANCE', 'ANIMALS', 'TECHNOLOGY', 'EMOTIONS',
        'DAILY_LIFE', 'TRANSPORT', 'LAW', 'HOBBY', 'NATURE',
        'MARKETING', 'GAMING', 'GENERAL'
    ));

-- ============================================
-- 3. DeckEnrollmentRole - synchronizacja
-- ============================================
ALTER TABLE deck_enrollment DROP CONSTRAINT IF EXISTS deck_enrollment_role_check;
ALTER TABLE deck_enrollment ADD CONSTRAINT deck_enrollment_role_check
    CHECK (role IN ('STUDENT', 'OWNER', 'FRIEND_OWNER', 'COMMUNITY_OWNER'));

-- ============================================
-- 4. DeckEnrollmentSource - synchronizacja
-- ============================================
ALTER TABLE deck_enrollment DROP CONSTRAINT IF EXISTS deck_enrollment_source_check;
ALTER TABLE deck_enrollment ADD CONSTRAINT deck_enrollment_source_check
    CHECK (source IN ('I', 'COMMUNITY', 'TEACHER_COURSE', 'FRIEND_SHARED'));

-- ============================================
-- 5. LearningPhase - usunięcie GRADUATED
-- ============================================
ALTER TABLE user_flashcard_progress DROP CONSTRAINT IF EXISTS user_flashcard_progress_phase_check;
ALTER TABLE user_flashcard_progress ADD CONSTRAINT user_flashcard_progress_phase_check
    CHECK (learning_phase IN ('NEW', 'LEARNING', 'REVIEW', 'RELEARNING'));

