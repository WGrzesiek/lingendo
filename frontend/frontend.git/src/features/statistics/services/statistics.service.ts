/**
 * Service do pobierania statystyk użytkownika
 */
import apiClient from "@/lib/api/axios";
import {
  IStatisticsApiResponse,
  IUserActivityItem,
} from "../types/statistics.types";

const BASE_URL = "/v1/stats";

/**
 * Pobiera statystyki użytkownika
 */
export const getStatistics = async (): Promise<IStatisticsApiResponse> => {
  const response = await apiClient.get<IStatisticsApiResponse>(BASE_URL);
  return response.data;
};

