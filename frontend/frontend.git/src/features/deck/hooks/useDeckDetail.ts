import { useQuery } from "@tanstack/react-query";
import { getDeckDetail } from "../services/deck.service";
import { qk } from "@/lib/queryKeys";

/**
 * Hook do pobierania szczegółowych informacji o talii (deckdetail)
 * Wykorzystuje endpoint /api/v1/decks/{id}
 */
export const useDeckDetail = (deckId: string) => {
  return useQuery({
    queryKey: qk.deck.detail1(deckId),
    queryFn: () => getDeckDetail(deckId),
    enabled: !!deckId,
  });
};
