import { useMutation, useQueryClient } from "@tanstack/react-query";
import { createDeck } from "@/features/deck/services/deck.service";
import type { CreateDeckDto, ResponseDeckDto } from "../../types";
import type { ApiErrorResponse } from "@/types/common";
import type { AxiosError } from "axios";
import { REFETCH_GROUPS } from "@/lib/queryKeys";

export const useCreateDeck = () => {
  const queryClient = useQueryClient();

  return useMutation<
    ResponseDeckDto,
    AxiosError<ApiErrorResponse>,
    CreateDeckDto
  >({
    mutationFn: createDeck,
    onSuccess: async () => {
      await Promise.all(
        REFETCH_GROUPS.AFTER_DECK_MUTATION.map((key) =>
          queryClient.refetchQueries({ queryKey: [key] }),
        ),
      );
    },
  });
};
