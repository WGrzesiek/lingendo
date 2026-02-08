import { useMutation, useQueryClient } from "@tanstack/react-query";
import { updateDeckVisibility } from "../../services/deck.service";
import type { UpdateDeckVisibilityRequest } from "../../types";
import { REFETCH_GROUPS } from "@/lib/queryKeys";

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
    onSuccess: async () => {
      await Promise.all(
        REFETCH_GROUPS.AFTER_DECK_MUTATION.map((key) =>
          queryClient.refetchQueries({ queryKey: [key] }),
        ),
      );
    },
  });
};
