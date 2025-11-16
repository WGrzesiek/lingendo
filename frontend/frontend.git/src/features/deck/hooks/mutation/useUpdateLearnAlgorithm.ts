import { useMutation, useQueryClient } from "@tanstack/react-query";
import { updateLearnAlgorithm } from "../../services/deck.service";
import type { UpdateLearnAlgorithmRequest } from "../../types";

/**
 * Hook do zmiany algorytmu nauki talii
 */
export const useUpdateLearnAlgorithm = () => {
  const queryClient = useQueryClient();
  return useMutation({
    mutationFn: ({
      deckId,
      data,
    }: {
      deckId: string;
      data: UpdateLearnAlgorithmRequest;
    }) => updateLearnAlgorithm(deckId, data),
    onSuccess: (_, variables) => {
      queryClient.invalidateQueries({ queryKey: ["deck", variables.deckId] });
      queryClient.invalidateQueries({
        queryKey: ["deck-details", variables.deckId],
      });
    },
  });
};
