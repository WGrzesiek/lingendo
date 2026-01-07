import { useInfiniteQuery } from "@tanstack/react-query";
import { getDecksCreatedByMe } from "../services/deck.service";
import { PageResponse } from "@/types/common";
import {
  DeckVisibility,
  ICreatedDeckListItem,
} from "@/features/deck/types/created-deck.types";
import { DeckOwnerType } from "@/features/deck/types";
import { qk } from "@/lib/queryKeys";

type DecksCreatedByMeFilters = {
  deckVisibility?: DeckVisibility[];
  owner?: DeckOwnerType;
};

export const useInfiniteDecksCreatedByMe = (
  filters: DecksCreatedByMeFilters = {
    deckVisibility: ["PRIVATE", "PUBLIC"],
    owner: "I",
  },
  pageSize = 20
) => {
  return useInfiniteQuery<PageResponse<ICreatedDeckListItem>, Error>({
    queryKey: qk.deck.iDecksCreateInfinite(filters),
    queryFn: async ({ pageParam = 0 }) =>
      getDecksCreatedByMe({
        ...filters,
        page: pageParam as number,
        size: pageSize,
      }),
    initialPageParam: 0,
    getNextPageParam: (lastPage) =>
      lastPage.last ? undefined : lastPage.number + 1,
  });
};
