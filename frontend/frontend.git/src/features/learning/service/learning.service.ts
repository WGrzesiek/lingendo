import {
    FlashcardInteractionResult, LearnHeaderProgress,
    NextFlashcardRecommendation,
    QuizAnswer,
    RememberAnswer, SubmitAnswerRequest,
    TypingAnswer
} from "@/features/learning/types/learning.types";
import apiClient from "@/lib/api/axios";

const BASE_URL = "/v1/decks/sessions";




export const getNextFlashcard = async (
  sessionId: string
): Promise<NextFlashcardRecommendation> => {
  const response = await apiClient.get<NextFlashcardRecommendation>(
    `${BASE_URL}/${sessionId}/next`
  );
  console.log("[Learning Service] Pobieranie następnej fiszki dla sesji:", sessionId);
  return response.data;
}

export const submitAnswer = async (
  sessionId: string,
  flashcardId: string,
  answer: SubmitAnswerRequest
): Promise<FlashcardInteractionResult> => {
  const response = await apiClient.post(
    `${BASE_URL}/${sessionId}/flashcards/${flashcardId}/answer`,
    answer
  );
  console.log("[Learning Service] Złożono odpowiedź dla fiszki:", flashcardId);
  return response.data;
}

export const getLearnHeaderProgress = async (
  sessionId: string
): Promise<LearnHeaderProgress> => {
    const response = await apiClient.get<LearnHeaderProgress>(
        `${BASE_URL}/${sessionId}/learn-header`
    );

    console.log("[Learning Service] Pobieranie postępu sesji:", sessionId);
    return response.data;
}

export const completeSession = async (sessionId: string): Promise<void> => {
    await apiClient.put(`${BASE_URL}/${sessionId}/complete`);
    console.log("[Learning Service] Sesja zakończona:", sessionId);

}