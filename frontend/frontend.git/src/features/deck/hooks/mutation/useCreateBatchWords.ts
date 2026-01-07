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
    onSuccess: (_, variables) => {
      queryClient.invalidateQueries({
        queryKey: qk.deck.detail1(variables.deckId),
      });
      queryClient.invalidateQueries({
        queryKey: qk.deck.flashcards(variables.deckId),
      });
      queryClient.invalidateQueries({
        queryKey: qk.deck.details(variables.deckId),
      });
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
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: qk.community.words() });
    },
  });
};
