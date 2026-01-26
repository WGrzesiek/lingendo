import apiClient from "@/lib/api/axios";
import { PageResponse } from "@/types/common";
import {
  CourseWord,
  CourseContentItem,
} from "@/features/course/types/words.types";

const BASE_URL = "/v1/decks/enrollments";

/**
 * Pobiera paginowaną listę słówek dla kursu na podstawie enrollmentId
 */
export const getCourseWords = async (
  enrollmentId: string,
  params?: { page?: number; size?: number }
): Promise<PageResponse<CourseWord>> => {
  const response = await apiClient.get<PageResponse<CourseContentItem>>(
    `${BASE_URL}/${enrollmentId}/course-view`,
    { params }
  );

  console.log("[Course Words Service] Raw API response:", response.data);

  // Mapowanie elementów content[] na CourseWord
  const words: CourseWord[] = (response.data.content || []).map((item) => {
    const flashcard = item.flashcard;
    const progress = item.userFlashcardProgress;
    const session = item.sessionNumber ?? 0;

    // Null-safe access do wordDto
    const wordDto = flashcard?.wordDto;

    return {
      id: wordDto?.id ?? flashcard?.id ?? "",
      flashcardId: flashcard?.id ?? "",
      word: wordDto?.word ?? "",
      translations: wordDto?.translations ?? [],
      sentences: wordDto?.sentences ?? [],
      sentencesAI: wordDto?.sentencesAI ?? [],
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
