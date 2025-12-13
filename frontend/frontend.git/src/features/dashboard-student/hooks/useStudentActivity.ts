import { useQuery } from "@tanstack/react-query";
import { getStudentActivity } from "../services/statistics.service";

export const useStudentActivity = () =>
  useQuery({
    queryKey: ["studentActivity"],
    queryFn: getStudentActivity,
  });
