import type { WordDto } from "@/types/word";

/**
 * Typy interakcji z fiszką (mapowane z backendu)
 */
export type InteractionType =
  | "PRESENTATION"
  | "TYPING_INPUT_FROM"
  | "TYPING_INPUT_TO"
  | "QUIZ_CHOICE"
  | "REMEMBER_CHECK_FROM"
  | "REMEMBER_CHECK_TO";

/**
 * DTO rekomendacji następnej fiszki (mapowane z backendu)
 * Odpowiada NextFlashcardRecommendation z backendu
 */
export interface NextFlashcardRecommendation {
  flashcardId: string;
  content: WordDto;
  interactionType: InteractionType;
  quizOptions: string[];
}

export interface RememberAnswer {
    type: 'remembered';
    remembered: boolean;
}

export interface TypingAnswer {
  type: 'text';
  text: string
}
export interface QuizAnswer {
    type: 'choice';
  selectedOption: string
}

export type SubmitAnswerRequest = RememberAnswer | QuizAnswer | TypingAnswer;

/**
 * Wynik interakcji użytkownika z fiszką
 */
export interface FlashcardInteractionResult {
    isCorrect: boolean;
    result: {
        currentState: string
        reason: string;
    };
}

export interface LearnHeaderProgress {
  sessionId: string;
  progressPercent: number;
}