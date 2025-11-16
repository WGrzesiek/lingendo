import { useMutation, useQueryClient } from "@tanstack/react-query";
import { updateDeckDetails } from "../../services/deck.service";
import { UpdateDeckDetailsRequest } from "../../types";

export const useUpdateDeckDetails = () => {
  const queryClient = useQueryClient();

  return useMutation({
    mutationFn: ({
      deckId,
      data,
    }: {
      deckId: string;
      data: UpdateDeckDetailsRequest;
    }) => updateDeckDetails(deckId, data),
    onSuccess: (_, variables) => {
      queryClient.invalidateQueries({ queryKey: ["deck", variables.deckId] });
    },
  });
};
