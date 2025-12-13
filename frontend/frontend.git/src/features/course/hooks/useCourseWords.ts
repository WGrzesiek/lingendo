import { useInfiniteQuery } from "@tanstack/react-query";
import { getCourseWords } from "@/features/course/services/words.service";
import { PageResponse } from "@/types/common";
import { CourseWord } from "@/features/course/types/words.types";

export const useInfiniteCourseWords = (
  enrollmentId: string | null,
  pageSize = 10
) => {
  return useInfiniteQuery<PageResponse<CourseWord>, Error>({
    queryKey: ["courseWords", enrollmentId, "infinite"],
    queryFn: async ({ pageParam = 0 }) => {
      return getCourseWords(enrollmentId!, {
        page: pageParam as number,
        size: pageSize,
      });
    },
    initialPageParam: 0,
    getNextPageParam: (lastPage) => {
      if (lastPage.last) return undefined;
      return lastPage.number + 1;
    },
    enabled: !!enrollmentId,
  });
};
