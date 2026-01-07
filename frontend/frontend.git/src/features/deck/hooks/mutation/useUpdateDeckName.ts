import { useMutation, useQueryClient } from "@tanstack/react-query";
import { updateDeckName } from "../../services/deck.service";
import type { UpdateDeckNameRequest } from "../../types";
import { qk } from "@/lib/queryKeys";

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
    onSuccess: (_, variables) => {
      queryClient.invalidateQueries({
        queryKey: qk.deck.detail(variables.deckId),
      });
      queryClient.invalidateQueries({ queryKey: qk.deck.all });
    },
  });
};
