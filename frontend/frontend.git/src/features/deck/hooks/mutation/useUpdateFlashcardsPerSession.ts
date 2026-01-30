import { useMutation, useQueryClient } from "@tanstack/react-query";
import { updateFlashcardsPerSession } from "../../services/deck.service";
import type { UpdateFlashcardsPerSessionRequest } from "../../types";
import { REFETCH_GROUPS } from "@/lib/queryKeys";

/**
 * Hook do zmiany liczby fiszek na sesję (1-100)
 */
export const useUpdateFlashcardsPerSession = () => {
  const queryClient = useQueryClient();
  return useMutation({
    mutationFn: ({
      deckId,
      data,
    }: {
      deckId: string;
      data: UpdateFlashcardsPerSessionRequest;
    }) => updateFlashcardsPerSession(deckId, data),
    onSuccess: async () => {
      await Promise.all(
        REFETCH_GROUPS.AFTER_DECK_MUTATION.map((key) =>
          queryClient.refetchQueries({ queryKey: [key] }),
        ),
      );
    },
  });
};
