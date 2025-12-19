import { useMutation, useQueryClient } from "@tanstack/react-query";
import { updateDeckDetails } from "../../services/deck.service";
import {type DeckDetailsDto} from "../../types";

export const useUpdateDeckDetails = () => {
  const queryClient = useQueryClient();

  return useMutation({
    mutationFn: ({
      deckId,
      data,
    }: {
      deckId: string;
      data: DeckDetailsDto;
    }) => updateDeckDetails(deckId, data),
    onSuccess: (_, variables) => {
      queryClient.invalidateQueries({ queryKey: ["deck", variables.deckId] });
    },
  });
};
