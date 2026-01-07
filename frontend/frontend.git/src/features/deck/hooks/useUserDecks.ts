import { useQuery } from "@tanstack/react-query";
import { getUserDecks } from "@/features/deck/services/deck.service";
import { qk } from "@/lib/queryKeys";

export const useUserDecks = () => {
  return useQuery({
    queryKey: qk.deck.userDecks(),
    queryFn: getUserDecks,
  });
};
