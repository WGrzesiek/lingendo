import { useMutation, useQueryClient } from "@tanstack/react-query";
import {
  createBatchWordsForDeck,
  createBatchWordsForCommunity,
  VocabularyWord,
  CreateBatchResponse,
} from "../../services/vocabulary.service";
import type { AxiosError } from "axios";
import { qk } from "@/lib/queryKeys";

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
      await queryClient.invalidateQueries({queryKey: qk.deck.iDecksCreateInfiniteRoot(),});
      await queryClient.refetchQueries({queryKey: qk.deck.iDecksCreateInfiniteRoot(),});


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
      await queryClient.invalidateQueries({ queryKey: qk.community.words() });
      await  queryClient.refetchQueries({ queryKey: qk.community.words() });
    },
  });
};
