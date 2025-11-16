import { useQuery } from "@tanstack/react-query";
import { getUserDecks } from "@/features/deck/services/deck.service";

export const useUserDecks = () => {
  return useQuery({
    queryKey: ["user-decks"],
    queryFn: getUserDecks,
  });
};
