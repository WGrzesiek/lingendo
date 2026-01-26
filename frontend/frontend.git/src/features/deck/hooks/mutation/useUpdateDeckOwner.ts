import { useMutation, useQueryClient } from "@tanstack/react-query";
import { updateDeckOwner } from "../../services/deck.service";
import type { UpdateDeckOwnerRequest } from "../../types";
import { REFETCH_GROUPS } from "@/lib/queryKeys";

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
    onSuccess: async () => {
      await Promise.all(
        REFETCH_GROUPS.AFTER_DECK_MUTATION.map((key) =>
          queryClient.refetchQueries({ queryKey: [key] }),
        ),
      );
    },
  });
};
