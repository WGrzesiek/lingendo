import { useQuery } from "@tanstack/react-query";
import { getUserDecks } from "@/features/deck/services/deck.service";
import { QUERY_KEYS } from "@/lib/queryKeys";

export const useUserDecks = () => {
  return useQuery({
    queryKey: [QUERY_KEYS.DECKS, "userDecks"],
    queryFn: getUserDecks,
  });
};
