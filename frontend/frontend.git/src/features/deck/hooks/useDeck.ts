import { useQuery } from "@tanstack/react-query";
import { getDeckById } from "../services/deck.service";
import { QUERY_KEYS } from "@/lib/queryKeys";

/**
 * Hook do pobierania pojedynczej talii po ID
 */
export const useDeck = (deckId: string) => {
  return useQuery({
    queryKey: [QUERY_KEYS.DECKS, "byId", deckId],
    queryFn: () => getDeckById(deckId),
    enabled: !!deckId,
  });
};
