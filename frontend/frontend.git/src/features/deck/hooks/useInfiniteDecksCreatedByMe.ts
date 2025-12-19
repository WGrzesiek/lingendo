import { useInfiniteQuery } from "@tanstack/react-query";
import {getDecksCreatedByMe} from "../services/deck.service";
import { PageResponse } from "@/types/common";
import {DeckVisibility, ICreatedDeckListItem} from "@/features/deck/types/created-deck.types";
import {DeckOwnerType} from "@/features/deck/types";

type DecksCreatedByMeFilters = {
  deckVisibility?: DeckVisibility[];
  owner?: DeckOwnerType;
};

export const useInfiniteDecksCreatedByMe = (
    filters: DecksCreatedByMeFilters = {
      deckVisibility: ["PRIVATE", "FRIENDS_ONLY", "STUDENTS_ONLY", "PUBLIC"],
      owner: "I",
    },
    pageSize = 20
) => {
  return useInfiniteQuery<PageResponse<ICreatedDeckListItem>, Error>({
    queryKey: ["i-decks-create", "infinite", filters],
    queryFn: async ({ pageParam = 0 }) =>
        getDecksCreatedByMe({
          ...filters,
          page: pageParam as number,
          size: pageSize,
        }),
    initialPageParam: 0,
    getNextPageParam: (lastPage) => (lastPage.last ? undefined : lastPage.number + 1),
  });
};
