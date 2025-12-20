import { useQuery } from "@tanstack/react-query";
import { getStatistics } from "../services/statistics.service";

export const useStatistics = () =>
  useQuery({
    queryKey: ["statistics"],
    queryFn: getStatistics,
  });
