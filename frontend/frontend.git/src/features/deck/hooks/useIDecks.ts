import { useQuery } from "@tanstack/react-query";
import { getIDecks } from "../services/deck.service";

/**
 * Hook do pobierania statystyk talii
 */
export const useIDecks = (page?: number, size?: number) => {
  return useQuery({
    queryKey: ["deck-statistics", page, size],
    queryFn: () => getIDecks({ page, size }),
  });
};
