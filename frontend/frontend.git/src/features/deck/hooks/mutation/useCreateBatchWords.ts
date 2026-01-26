import { useMutation, useQueryClient } from "@tanstack/react-query";
import {
  createBatchWordsForDeck,
  createBatchWordsForCommunity,
  VocabularyWord,
  CreateBatchResponse,
} from "../../services/vocabulary.service";
import type { AxiosError } from "axios";
import { REFETCH_GROUPS, QUERY_KEYS } from "@/lib/queryKeys";

/**
 * Hook do dodawania słówek batch do konkretnego decka
 */
export const useCreateBatchWordsForDeck = () => {
  const queryClient = useQueryClient();

  return useMutation<
    CreateBatchResponse,
    AxiosError,
    { deckId: string; words: VocabularyWord[] }
  >({
    mutationFn: ({ deckId, words }) => createBatchWordsForDeck(deckId, words),
    onSuccess: async () => {
      await Promise.all(
        REFETCH_GROUPS.AFTER_DECK_MUTATION.map((key) =>
          queryClient.refetchQueries({ queryKey: [key] }),
        ),
      );
    },
  });
};

/**
 * Hook do dodawania słówek batch do społeczności
 */
export const useCreateBatchWordsForCommunity = () => {
  const queryClient = useQueryClient();

  return useMutation<CreateBatchResponse, AxiosError, VocabularyWord[]>({
    mutationFn: (words) => createBatchWordsForCommunity(words),
    onSuccess: async () => {
      await queryClient.refetchQueries({ queryKey: [QUERY_KEYS.COMMUNITY] });
    },
  });
};
