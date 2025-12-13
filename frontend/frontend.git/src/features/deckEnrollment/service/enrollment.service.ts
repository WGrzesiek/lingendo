import {
    UpdateFlashcardsPerSessionRequest,
    UpdateLearnAlgorithmRequest,
    UpdateReviewScheduleRequest
} from "@/features/deckEnrollment/type/enrollment";
import apiClient from "@/lib/api/axios";

const BASE_URL = "/v1/decks/enrollments";
/**
 * Zmienia algorytm nauki talii
 * /api/v1/decks/enrollments/424463c8-e99e-47e6-8fca-6a31327c6440/algorithm?algorithm=GRZESIEK_ALGORITHM
 */
export const updateLearnAlgorithm = async (
    enrollmentId: string,
    data: UpdateLearnAlgorithmRequest
): Promise<void> => {
    const response = await apiClient.put(
        `${BASE_URL}/${enrollmentId}/algorithm?algorithm=${data.learnAlgorithm}`
    );
    console.log("[Deck Service] Zmieniono algorytm nauki:", data.learnAlgorithm);
    return response.data;
};

/**
 * Zmienia liczbę fiszek na sesję (1-100)
 */
export const updateFlashcardsPerSession = async (
    enrollmentId: string,
    data: UpdateFlashcardsPerSessionRequest
): Promise<void> => {
    const response = await apiClient.put(
        `${BASE_URL}/${enrollmentId}/session-limit?limit=${data.limit}`
    );
    console.log("[Deck Service] Zmieniono limit fiszek na sesję:", data.limit);

    return response.data;
};

export const updateReviewSchedule = async (
    enrollmentId: string,
    reviewSchedule: UpdateReviewScheduleRequest
): Promise<void> => {
    const response = await apiClient.put(
        `${BASE_URL}/${enrollmentId}/review-schedule?mode=${reviewSchedule.reviewSchedule}`
    );
    console.log("[Deck Service] Zmieniono harmonogram powtórek:", reviewSchedule);

    return response.data;
}