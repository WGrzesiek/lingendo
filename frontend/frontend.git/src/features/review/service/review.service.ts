import {ReviewHeader} from "@/features/review/types/review.types";
import apiClient from "@/lib/api/axios";
import type {PageResponse} from "@/types/common";
import {CourseContentItem, CourseWord} from "@/features/course/types/words.types";
import {
    FlashcardInteractionResult,
    NextFlashcardRecommendation,
    TypingAnswer
} from "@/features/learning/types/learning.types";

const BASE_URL = "/v1/enrollments"
const BASE_URL2 = "/v1/decks/enrollments";

export const getReviewHeader = async(enrollmentId: string): Promise<ReviewHeader> => {
    const result = await apiClient.get<ReviewHeader>(`${BASE_URL}/${enrollmentId}/review-header`)
    console.log("Pobrano nagłówek powtórek:", result.data)
    return result.data
}


export const getReviewWordsView = async (
    enrollmentId: string,
    params?: { page?: number; size?: number }
): Promise<PageResponse<CourseWord>> => {
    const response = await apiClient.get<PageResponse<CourseContentItem>>(
        `${BASE_URL2}/${enrollmentId}/review-words-view`,
        { params }
    );

    const words: CourseWord[] = (response.data.content || []).map((item) => {
        const flashcard = item.flashcard;
        const progress = item.userFlashcardProgress;
        const session = item.sessionNumber ?? 0;

        return {
            id: flashcard.wordDto.id,
            flashcardId: flashcard.id,
            word: flashcard.wordDto.word,
            translations: flashcard.wordDto.translations,
            sentences: flashcard.wordDto.sentences,
            sentencesAI: flashcard.wordDto.sentencesAI,
            phase: progress?.phase || "NEW",
            isLearned: progress?.isLearned || false,
            isSkipped: progress?.isSkipped || false,
            repetitionCount: progress?.repetitionCount || 0,
            nextReviewAt: progress?.nextReviewAt || null,
            algorithmState: progress?.algorithmState || "",
            sessionNumber: session,
        };
    });
    return {
        content: words,
        totalElements: response.data.totalElements,
        totalPages: response.data.totalPages,
        number: response.data.number,
        size: response.data.size,
        first: response.data.first,
        last: response.data.last,
        empty: response.data.empty,
    };
};

const BASE_URL3 = "/v1/decks/reviews";

export const getNextFlashcardReview = async (
    enrollmentId: string
): Promise<NextFlashcardRecommendation> => {
    const response = await apiClient.get<NextFlashcardRecommendation>(
        `${BASE_URL3}/enrollments/${enrollmentId}/next`
    );
    console.log("[Review Service] Pobieranie następnej fiszki do powtórki:", enrollmentId);
    return response.data;
}

export const submitAnswerReview = async (
    flashcardId: string,
    answer: TypingAnswer
): Promise<FlashcardInteractionResult> => {
    const response = await apiClient.post(
        `${BASE_URL3}/flashcards/${flashcardId}/answer`,
        answer
    );
    console.log("[Review Service] Złożono odpowiedź dla fiszki do powtórki:", flashcardId);
    return response.data;
}
