import { useInfiniteQuery } from "@tanstack/react-query";
import {
  getDeckFlashcardsPage,
  DeckFlashcard,
} from "../services/flashcard.service";
import { PageResponse } from "@/types/common";
import { QUERY_KEYS } from "@/lib/queryKeys";

/**
 * Hook do pobierania fiszek z infinite scroll
 */
export const useInfiniteDeckFlashcards = (deckId: string, pageSize = 20) => {
  return useInfiniteQuery<PageResponse<DeckFlashcard>, Error>({
    queryKey: [QUERY_KEYS.FLASHCARDS, deckId, "infinite"],
    queryFn: async ({ pageParam = 0 }) => {
      return getDeckFlashcardsPage({
        deckId,
        page: pageParam as number,
        size: pageSize,
      });
    },
    initialPageParam: 0,
    getNextPageParam: (lastPage) => {
      if (lastPage.last) return undefined;
      return lastPage.number + 1;
    },
    enabled: !!deckId,
  });
};
