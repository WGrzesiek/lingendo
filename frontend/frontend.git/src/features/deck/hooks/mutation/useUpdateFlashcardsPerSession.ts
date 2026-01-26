import { useMutation, useQueryClient } from "@tanstack/react-query";
import { updateFlashcardsPerSession } from "../../services/deck.service";
import type { UpdateFlashcardsPerSessionRequest } from "../../types";
import { qk } from "@/lib/queryKeys";

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
