import { useQuery } from "@tanstack/react-query";
import { getDeckDetail } from "../services/deck.service";
import { QUERY_KEYS } from "@/lib/queryKeys";

/**
 * Hook do pobierania szczegółowych informacji o talii (deckdetail)
 * Wykorzystuje endpoint /api/v1/decks/{id}
 */
export const useDeckDetail = (deckId: string) => {
  return useQuery({
    queryKey: [QUERY_KEYS.DECKS, "detail", deckId],
    queryFn: () => getDeckDetail(deckId),
    enabled: !!deckId,
  });
};
