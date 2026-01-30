import { useQuery } from "@tanstack/react-query";
import { getStudentStatistics } from "../services/statistics.service";
import { QUERY_KEYS } from "@/lib/queryKeys";

/**
 * Hook do pobierania statystyk studenta
 */
export const useStudentStatistics = () => {
  return useQuery({
    queryKey: [QUERY_KEYS.DASHBOARD, 'statistics'],
    queryFn: () => getStudentStatistics(),
  });
};
