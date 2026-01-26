import { useQuery } from "@tanstack/react-query";
import { getDeckDetails } from "../services/deck.service";
import { qk } from "@/lib/queryKeys";

/**
 * Hook do pobierania szczegółowych informacji o talii
 */
export const useDeckDetails = (deckId: string) => {
  return useQuery({
    queryKey: qk.deck.details(deckId),
    queryFn: () => getDeckDetails(deckId),
    enabled: !!deckId,
  });
};
