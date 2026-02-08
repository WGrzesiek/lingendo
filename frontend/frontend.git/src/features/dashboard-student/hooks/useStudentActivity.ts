import { useQuery } from "@tanstack/react-query";
import { getStudentActivity } from "../services/statistics.service";
import { QUERY_KEYS } from "@/lib/queryKeys";

export const useStudentActivity = () =>
  useQuery({
    queryKey: [QUERY_KEYS.DASHBOARD, 'activity'],
    queryFn: getStudentActivity,
  });
