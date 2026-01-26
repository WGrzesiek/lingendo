import { useQuery } from "@tanstack/react-query";
import { getStudentStatistics } from "../services/statistics.service";

/**
 * Hook do pobierania statystyk studenta
 */
export const useStudentStatistics = () => {
  return useQuery({
    queryKey: ["studentStatistics"],
    queryFn: () => getStudentStatistics(),
  });
};
