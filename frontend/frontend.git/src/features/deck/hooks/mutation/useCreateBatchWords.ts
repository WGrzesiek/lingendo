import { useMutation, useQueryClient } from "@tanstack/react-query";
import {
  createBatchWordsForDeck,
  createBatchWordsForCommunity,
  VocabularyWord,
  CreateBatchResponse,
} from "../../services/vocabulary.service";
import type { AxiosError } from "axios";

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
        queryKey: ["deck-detail", variables.deckId],
      });
      queryClient.invalidateQueries({
        queryKey: ["deck-flashcards", "infinite", variables.deckId],
      });
      queryClient.invalidateQueries({
        queryKey: ["deck-details", variables.deckId],
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
      queryClient.invalidateQueries({ queryKey: ["community-words"] });
    },
  });
};
