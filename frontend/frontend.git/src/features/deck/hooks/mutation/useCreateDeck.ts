import { useMutation, useQueryClient } from "@tanstack/react-query";
import { createDeck } from "@/features/deck/services/deck.service";
import type { CreateDeckDto, ResponseDeckDto } from "../../types";
import type { ApiErrorResponse } from "@/types/common";
import type { AxiosError } from "axios";
import { qk } from "@/lib/queryKeys";

export const useCreateDeck = () => {
  const queryClient = useQueryClient();

  return useMutation<
    ResponseDeckDto,
    AxiosError<ApiErrorResponse>,
    CreateDeckDto
  >({
    mutationFn: createDeck,
    onSuccess: async () => {
      // await queryClient.invalidateQueries({ queryKey: qk.deck.userDecks() });
      // await queryClient.refetchQueries({ queryKey: qk.deck.userDecks() });
      await queryClient.invalidateQueries({queryKey: qk.deck.iDecksCreateInfiniteRoot(),});
      await queryClient.refetchQueries({queryKey: qk.deck.iDecksCreateInfiniteRoot(),});
    },
  });
};
