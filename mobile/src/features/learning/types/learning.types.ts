import type { WordDto } from '@/features/course/types';

/**
 * Typy interakcji z fiszką
 */
export const interactionTypeValues = [
  'PRESENTATION',
  'TYPING_INPUT_FROM',
  'TYPING_INPUT_TO',
  'QUIZ_CHOICE',
  'REMEMBER_CHECK_FROM',
  'REMEMBER_CHECK_TO',
] as const;

export type InteractionType = (typeof interactionTypeValues)[number];

/**
 * Rekomendacja następnej fiszki do nauki
 */
export interface NextFlashcardRecommendation {
  flashcardId: string;
  content: WordDto;
  interactionType: InteractionType;
  quizOptions: string[];
}

/**
 * Odpowiedź typu "pamiętam/nie pamiętam"
 */
export interface RememberAnswer {
  type: 'remembered';
  remembered: boolean;
}

/**
 * Odpowiedź typu wpisywanie tekstu
 */
export interface TypingAnswer {
  type: 'text';
  text: string;
}

/**
 * Odpowiedź typu quiz (wybór)
 */
export interface QuizAnswer {
  type: 'choice';
  selectedOption: string;
}

/**
 * Request do przesłania odpowiedzi
 */
export type SubmitAnswerRequest = RememberAnswer | QuizAnswer | TypingAnswer;

/**
 * Wynik interakcji użytkownika z fiszką
 */
export interface FlashcardInteractionResult {
  isCorrect: boolean;
  result: {
    currentState: string;
    reason: string;
  };
}

/**
 * Postęp w nagłówku nauki (pasek postępu)
 */
export interface LearnHeaderProgress {
  sessionId: string;
  progressPercent: number;
}
