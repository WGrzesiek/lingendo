import { useMutation, useQueryClient } from "@tanstack/react-query";
import { createDeck } from "@/features/deck/services/deck.service";
import type { CreateDeckDto, ResponseDeckDto } from "../../types";
import type { ApiErrorResponse } from "@/types/common";
import type { AxiosError } from "axios";

export const useCreateDeck = () => {
  const queryClient = useQueryClient();

  return useMutation<
    ResponseDeckDto,
    AxiosError<ApiErrorResponse>,
    CreateDeckDto
  >({
    mutationFn: createDeck,
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ["decks"] });
      queryClient.invalidateQueries({ queryKey: ["user-decks"] });
    },
  });
};
