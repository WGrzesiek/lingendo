import { useMutation, useQueryClient } from "@tanstack/react-query";
import { updateDeckVisibility } from "../../services/deck.service";
import type { UpdateDeckVisibilityRequest } from "../../types";

/**
 * Hook do zmiany widoczności talii (publiczna/prywatna)
 */
export const useUpdateDeckVisibility = () => {
  const queryClient = useQueryClient();
  return useMutation({
    mutationFn: ({
      deckId,
      data,
    }: {
      deckId: string;
      data: UpdateDeckVisibilityRequest;
    }) => updateDeckVisibility(deckId, data),
    onSuccess: (_, variables) => {
      queryClient.invalidateQueries({ queryKey: ["deck", variables.deckId] });
      queryClient.invalidateQueries({ queryKey: ["decks"] });
      queryClient.invalidateQueries({ queryKey: ["user-decks"] });
    },
  });
};
