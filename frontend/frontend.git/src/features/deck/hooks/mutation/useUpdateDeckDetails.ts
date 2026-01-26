import { useMutation, useQueryClient } from "@tanstack/react-query";
import { updateDeckDetails } from "../../services/deck.service";
import { type DeckDetailsDto } from "../../types";
import { REFETCH_GROUPS } from "@/lib/queryKeys";

export const useUpdateDeckDetails = () => {
  const queryClient = useQueryClient();

  return useMutation({
    mutationFn: ({ deckId, data }: { deckId: string; data: DeckDetailsDto }) =>
      updateDeckDetails(deckId, data),
    onSuccess: async () => {
      await Promise.all(
        REFETCH_GROUPS.AFTER_DECK_MUTATION.map((key) =>
          queryClient.refetchQueries({ queryKey: [key] }),
        ),
      );
    },
  });
};
