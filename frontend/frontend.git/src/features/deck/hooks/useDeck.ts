import { useQuery } from "@tanstack/react-query";
import { getDeckById } from "../services/deck.service";

/**
 * Hook do pobierania pojedynczej talii po ID
 */
export const useDeck = (deckId: string) => {
  return useQuery({
    queryKey: ["deck", deckId],
    queryFn: () => getDeckById(deckId),
    enabled: !!deckId,
  });
};
