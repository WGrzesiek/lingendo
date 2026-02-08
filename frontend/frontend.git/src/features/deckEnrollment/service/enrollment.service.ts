import {
  CreateEnrollmentRequest,
  UpdateFlashcardsPerSessionRequest,
  UpdateLearnAlgorithmRequest,
  UpdateReviewScheduleRequest,
} from "@/features/deckEnrollment/type/enrollment";
import apiClient from "@/lib/api/axios";

const BASE_URL = "/v1/decks/enrollments";
const DECKS_URL = "/v1/decks";

/**
 * Zapisuje użytkownika na talię (kurs)
 * Body jest opcjonalne - jeśli nie podano, backend użyje wartości domyślnych z talii.
 */
export const enrollToDeck = async (
  deckId: string,
  data: CreateEnrollmentRequest = {}
): Promise<void> => {
  await apiClient.post(`${DECKS_URL}/${deckId}/enrollments`, data);
  console.log("[Enrollment Service] Zapisano na talię:", deckId);
};

/**
 * Zmienia algorytm nauki talii
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
};
