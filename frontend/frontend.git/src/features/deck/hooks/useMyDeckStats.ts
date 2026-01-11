import { useQuery } from "@tanstack/react-query";
import { getMyDeckStats } from "@/features/deck/services/deck.service";
import { qk } from "@/lib/queryKeys";

export const useMyDeckStats = (deckIds: string[]) => {
  return useQuery({
    // queryKey: qk.deck.userDecks(),
    queryKey: qk.deck.myDeckStats(deckIds),
    queryFn: () => getMyDeckStats({ deckIds }),
    enabled: deckIds.length > 0,
  });
};
