import { useMutation, useQueryClient } from "@tanstack/react-query";
import { updateLearnAlgorithm } from "../../services/deck.service";
import type { UpdateLearnAlgorithmRequest } from "../../types";
import { qk } from "@/lib/queryKeys";

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
      queryClient.invalidateQueries({
        queryKey: qk.deck.detail(variables.deckId),
      });
      queryClient.invalidateQueries({
        queryKey: qk.deck.details(variables.deckId),
      });
    },
  });
};
