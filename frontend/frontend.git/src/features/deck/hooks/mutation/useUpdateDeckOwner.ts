import { useMutation, useQueryClient } from "@tanstack/react-query";
import { updateDeckOwner } from "../../services/deck.service";
import type { UpdateDeckOwnerRequest } from "../../types";

/**
 * Hook do zmiany właściciela talii
 */
export const useUpdateDeckOwner = () => {
  const queryClient = useQueryClient();
  return useMutation({
    mutationFn: ({
      deckId,
      data,
    }: {
      deckId: string;
      data: UpdateDeckOwnerRequest;
    }) => updateDeckOwner(deckId, data),
    onSuccess: (_, variables) => {
      queryClient.invalidateQueries({ queryKey: ["deck", variables.deckId] });
      queryClient.invalidateQueries({ queryKey: ["decks"] });
    },
  });
};
