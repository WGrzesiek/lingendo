import { useQuery } from "@tanstack/react-query";
import { DeckOwnerType } from "@/types/common";
import { getDecks } from "../services/deck.service";

export const useDecks = (params?: {
  isPublic?: boolean;
  owner?: DeckOwnerType;
}) => {
  return useQuery({
    queryKey: ["decks", params],
    queryFn: () => getDecks(params),
  });
};
