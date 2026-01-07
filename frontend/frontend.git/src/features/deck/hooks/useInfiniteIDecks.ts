import { useInfiniteQuery } from "@tanstack/react-query";
import { getIDecks } from "../services/deck.service";
import { PageResponse } from "@/types/common";
import { IDeckListItem } from "../types";
import { qk } from "@/lib/queryKeys";

export const useInfiniteIDecks = (pageSize = 4) => {
  return useInfiniteQuery<PageResponse<IDeckListItem>, Error>({
    queryKey: qk.deck.iDecksInfinite(),
    queryFn: async ({ pageParam = 0 }) => {
      return getIDecks({
        page: pageParam as number,
        size: pageSize,
      });
    },
    initialPageParam: 0,
    getNextPageParam: (lastPage) => {
      if (lastPage.last) return undefined;
      return lastPage.number + 1;
    },
  });
};
