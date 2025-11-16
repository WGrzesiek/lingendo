import { useMutation, useQueryClient } from "@tanstack/react-query";
import { createDeck } from "@/features/deck/services/deck.service";

export const useCreateDeck = () => {
  const queryClient = useQueryClient();

  return useMutation({
    mutationFn: createDeck,
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ["decks"] });
      queryClient.invalidateQueries({ queryKey: ["user-decks"] });
    },
  });
};
