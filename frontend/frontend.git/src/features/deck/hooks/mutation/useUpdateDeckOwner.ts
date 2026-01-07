import { useMutation, useQueryClient } from "@tanstack/react-query";
import { updateDeckOwner } from "../../services/deck.service";
import type { UpdateDeckOwnerRequest } from "../../types";
import { qk } from "@/lib/queryKeys";

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
      queryClient.invalidateQueries({
        queryKey: qk.deck.detail(variables.deckId),
      });
      queryClient.invalidateQueries({ queryKey: qk.deck.all });
    },
  });
};
