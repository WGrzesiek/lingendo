import { useQuery } from "@tanstack/react-query";
import { getLeaderboardOverview } from "../services/statistics.service";

export const useLeaderBoardOverview = () =>
  useQuery({
    queryKey: ["leaderBoardOverview"],
    queryFn: getLeaderboardOverview,
  });
