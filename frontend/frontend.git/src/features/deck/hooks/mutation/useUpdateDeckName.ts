import { useMutation, useQueryClient } from "@tanstack/react-query";
import { updateDeckName } from "../../services/deck.service";
import type { UpdateDeckNameRequest } from "../../types";
import { REFETCH_GROUPS } from "@/lib/queryKeys";

/**
 * Hook do zmiany nazwy talii
 */
export const useUpdateDeckName = () => {
  const queryClient = useQueryClient();
  return useMutation({
    mutationFn: ({
      deckId,
      data,
    }: {
      deckId: string;
      data: UpdateDeckNameRequest;
    }) => updateDeckName(deckId, data),
    onSuccess: async () => {
      await Promise.all(
        REFETCH_GROUPS.AFTER_DECK_MUTATION.map((key) =>
          queryClient.refetchQueries({ queryKey: [key] }),
        ),
      );
    },
  });
};
