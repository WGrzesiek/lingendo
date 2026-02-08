import {FlashcardAnswersStats} from "@/features/course/types/stats.types";
import apiClient from "@/lib/api/axios";

const BASE_URL = "/v1/courses";

export const getFlashcardAnswersStats = async (enrollmentId: string):Promise<FlashcardAnswersStats> => {
    const response = await apiClient.get<FlashcardAnswersStats>(
        `${BASE_URL}/${enrollmentId}/flashcards/stats`);
    console.log("[Stats Service] Fetched flashcard answers stats for enrollmentId:", enrollmentId);
    return response.data;

}