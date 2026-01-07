import { useQuery } from "@tanstack/react-query";
import { getDeckById } from "../services/deck.service";
import { qk } from "@/lib/queryKeys";

/**
 * Hook do pobierania pojedynczej talii po ID
 */
export const useDeck = (deckId: string) => {
  return useQuery({
    queryKey: qk.deck.detail(deckId),
    queryFn: () => getDeckById(deckId),
    enabled: !!deckId,
  });
};
