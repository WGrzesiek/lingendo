import { useQuery } from '@tanstack/react-query';
import { QUERY_KEYS } from '@/constants';
import { dashboardService } from '@/features/dashboard/services/dashboard.service';

export const useDashboard = () => {
  const useStudentStatistics = () =>
    useQuery({
      queryKey: [QUERY_KEYS.DASHBOARD, 'studentStatistics'],
      queryFn: () => dashboardService.getStudentStatistics(),
    });

  const useStudentActivity = () =>
    useQuery({
      queryKey: [QUERY_KEYS.DASHBOARD, 'studentActivity'],
      queryFn: () => dashboardService.getStudentActivity(),
    });

  const useLeaderboardOverview = () =>
    useQuery({
      queryKey: [QUERY_KEYS.DASHBOARD, 'leaderboard'],
      queryFn: () => dashboardService.getLeaderboardOverview(),
    });
  return {
    useStudentStatistics,
    useStudentActivity,
    useLeaderboardOverview
  }
}
