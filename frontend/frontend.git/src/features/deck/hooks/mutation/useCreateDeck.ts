import { useMutation, useQueryClient } from "@tanstack/react-query";
import { createDeck } from "@/features/deck/services/deck.service";
import { CreateDeckDto, ResponseDeckDto } from "../../types";
import { ApiError } from "next/dist/server/api-utils";
import { ApiErrorResponse } from "@/types/common";

export const useCreateDeck = () => {
  const queryClient = useQueryClient();

  return useMutation<ResponseDeckDto, ApiErrorResponse, CreateDeckDto>({
    mutationFn: createDeck,
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ["decks"] });
      queryClient.invalidateQueries({ queryKey: ["user-decks"] });
    },
  });
};
