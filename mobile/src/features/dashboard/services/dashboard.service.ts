/**
 * Service do pobierania statystyk studenta na dashboardzie
 */
import apiClient from "@/lib/api/axios";
import {
  StudentStatistics,
  StudentActivityItem,
  LeaderboardOverviewDto,
} from "../types/dashboard.type";
import { ENDPOINTS } from '@/constants';

export const dashboardService = {
  getStudentStatistics: async (): Promise<StudentStatistics> => {
    const response = await apiClient.get<StudentStatistics>(ENDPOINTS.DASHBOARD.STUDENT_STATS);
    return response.data;
  },

  getStudentActivity: async (): Promise<StudentActivityItem[]> => {
    const response = await apiClient.get<StudentActivityItem[]>(ENDPOINTS.DASHBOARD.STUDENT_ACTIVITY);
    return response.data;
  },

  getLeaderboardOverview: async (): Promise<LeaderboardOverviewDto> => {
    const response = await apiClient.get(ENDPOINTS.DASHBOARD.STUDENT_LEADERBOARD);
    return response.data;
  },
} ;
