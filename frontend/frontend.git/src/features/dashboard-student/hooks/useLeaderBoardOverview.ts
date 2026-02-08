import { useQuery } from "@tanstack/react-query";
import { getLeaderboardOverview } from "../services/statistics.service";
import { QUERY_KEYS } from "@/lib/queryKeys";


export const useLeaderBoardOverview = () =>
  useQuery({
    queryKey: [QUERY_KEYS.DASHBOARD, 'leaderboardOverview'],
    queryFn: getLeaderboardOverview,
  });
