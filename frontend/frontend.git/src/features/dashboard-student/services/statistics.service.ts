/**
 * Service do pobierania statystyk studenta na dashboardzie
 */
import apiClient from "@/lib/api/axios";
import {
  StudentStatistics,
  StudentActivityItem,
  LeaderboardOverviewDto,
} from "../types/statistics.type";

const BASE_URL = "/v1/dashboard/student";

export const getStudentStatistics = async (): Promise<StudentStatistics> => {
  const response = await apiClient.get<StudentStatistics>(`${BASE_URL}/stats`);
  return response.data;
};

export const getStudentActivity = async (): Promise<StudentActivityItem[]> => {
  const response = await apiClient.get<StudentActivityItem[]>(
    `${BASE_URL}/activity`
  );
  return response.data;
};

export const getLeaderboardOverview =
  async (): Promise<LeaderboardOverviewDto> => {
    const response = await apiClient.get(`${BASE_URL}/leaderboard`);
    return response.data;
  };
