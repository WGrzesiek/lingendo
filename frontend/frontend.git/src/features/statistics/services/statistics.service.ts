/**
 * Service do pobierania statystyk użytkownika i eksportu do PDF
 */
import apiClient from "@/lib/api/axios";
import {
  IStatisticsApiResponse,
  IUserActivityItem,
} from "../types/statistics.types";
import { IPdfExportOptions } from "../types/pdf-export.types";

const BASE_URL = "/v1/stats";

/**
 * Pobiera statystyki użytkownika
 */
export const getStatistics = async (): Promise<IStatisticsApiResponse> => {
  const response = await apiClient.get<IStatisticsApiResponse>(BASE_URL);
  return response.data;
};

/**
 * Eksportuje statystyki użytkownika do pliku PDF
 * @param options - opcje eksportu określające, które sekcje uwzględnić
 * @returns Promise z danymi binarnymi pliku PDF
 */
export const exportStatisticsToPdf = async (
  options: IPdfExportOptions
): Promise<Blob> => {
  const response = await apiClient.post(`${BASE_URL}/export/pdf`, options, {
    responseType: "blob",
    timeout: 30000, // 30 sekund na generowanie PDF
  });

  return response.data;
};
