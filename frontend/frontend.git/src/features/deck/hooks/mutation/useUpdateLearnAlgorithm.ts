import { useMutation, useQueryClient } from "@tanstack/react-query";
import { updateLearnAlgorithm } from "../../services/deck.service";
import type { UpdateLearnAlgorithmRequest } from "../../types";
import { REFETCH_GROUPS } from "@/lib/queryKeys";

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
    onSuccess: async () => {
      await Promise.all(
        REFETCH_GROUPS.AFTER_DECK_MUTATION.map((key) =>
          queryClient.refetchQueries({ queryKey: [key] }),
        ),
      );
    },
  });
};
