import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query';
import { QUERY_KEYS, INVALIDATION_GROUPS } from '@/constants';
import { deckService } from '@/features/deck';
import { flashcardService } from '@/features/deck';
import type {
  CreateDeckRequest,
  DeckOwnerType,
  DeckVisibility,
  UpdateDeckNameRequest,
  UpdateDeckVisibilityRequest,
  UpdateDeckOwnerRequest,
  UpdateLearnAlgorithmRequest,
  UpdateFlashcardsPerSessionRequest,
  DeckDetailsDto,
  WordToAdd,
} from '../types';

export const useDeck = () => {
  const queryClient = useQueryClient();

  const useMyEnrolledDecks = (params?: { page?: number; size?: number }) =>
    useQuery({
      queryKey: [QUERY_KEYS.DECKS, 'enrolled', params],
      queryFn: () => deckService.getMyEnrolledDecks(params),
    });

  const useDecksCreatedByMe = (params?: {
    deckVisibility?: DeckVisibility[];
    owner?: DeckOwnerType[];
    page?: number;
    size?: number;
  }) =>
    useQuery({
      queryKey: [QUERY_KEYS.DECKS, 'created', params],
      queryFn: () => deckService.getDecksCreatedByMe(params),
    });

  const useAllPublicDecks = (params?: { owner?: DeckOwnerType; page?: number; size?: number }) =>
    useQuery({
      queryKey: [QUERY_KEYS.DECKS, 'public', params],
      queryFn: () => deckService.getAllPublicDecks(params),
    });

  const useDeckById = (deckId: string) =>
    useQuery({
      queryKey: [QUERY_KEYS.DECKS, 'detail', deckId],
      queryFn: () => deckService.getDeckById(deckId),
      enabled: !!deckId,
    });

  const useDeckDetails = (deckId: string) =>
    useQuery({
      queryKey: [QUERY_KEYS.DECKS, 'details', deckId],
      queryFn: () => deckService.getDeckDetails(deckId),
      enabled: !!deckId,
    });

  const useDeckStatistics = (deckId: string) =>
    useQuery({
      queryKey: [QUERY_KEYS.DECKS, 'statistics', deckId],
      queryFn: () => deckService.getDeckStatistics(deckId),
      enabled: !!deckId,
    });

  const useUserDeckCount = () =>
    useQuery({
      queryKey: [QUERY_KEYS.DECKS, 'count'],
      queryFn: () => deckService.getUserDeckCount(),
    });

  const useDeckFlashcards = (params: { deckId: string; page?: number; size?: number }) =>
    useQuery({
      queryKey: [QUERY_KEYS.CARDS, 'page', params],
      queryFn: () => flashcardService.getDeckFlashcardsPage(params),
      enabled: !!params.deckId,
    });

  // =====================
  // MUTATIONS - Modyfikacje danych
  // =====================

  const useCreateDeck = () =>
    useMutation({
      mutationFn: (data: CreateDeckRequest) => deckService.createDeck(data),
      onSuccess: () => {
        queryClient.invalidateQueries({ queryKey: INVALIDATION_GROUPS.AFTER_DECK_MUTATION });
      },
    });

  const useDeleteDeck = () =>
    useMutation({
      mutationFn: (deckId: string) => deckService.deleteDeck(deckId),
      onSuccess: () => {
        queryClient.invalidateQueries({ queryKey: INVALIDATION_GROUPS.AFTER_DECK_MUTATION });
      },
    });

  const useUpdateDeckDetails = () =>
    useMutation({
      mutationFn: ({ deckId, data }: { deckId: string; data: DeckDetailsDto }) =>
        deckService.updateDeckDetails(deckId, data),
      onSuccess: () => {
        queryClient.invalidateQueries({ queryKey: QUERY_KEYS.DECKS });
      },
    });

  const useUpdateDeckVisibility = () =>
    useMutation({
      mutationFn: ({ deckId, data }: { deckId: string; data: UpdateDeckVisibilityRequest }) =>
        deckService.updateDeckVisibility(deckId, data),
      onSuccess: () => {
        queryClient.invalidateQueries({ queryKey: QUERY_KEYS.DECKS });
      },
    });

  const useUpdateDeckOwner = () =>
    useMutation({
      mutationFn: ({ deckId, data }: { deckId: string; data: UpdateDeckOwnerRequest }) =>
        deckService.updateDeckOwner(deckId, data),
      onSuccess: () => {
        queryClient.invalidateQueries({ queryKey: QUERY_KEYS.DECKS });
      },
    });

  const useUpdateDeckName = () =>
    useMutation({
      mutationFn: ({ deckId, data }: { deckId: string; data: UpdateDeckNameRequest }) =>
        deckService.updateDeckName(deckId, data),
      onSuccess: () => {
        queryClient.invalidateQueries({ queryKey: QUERY_KEYS.DECKS });
      },
    });

  const useUpdateLearnAlgorithm = () =>
    useMutation({
      mutationFn: ({ deckId, data }: { deckId: string; data: UpdateLearnAlgorithmRequest }) =>
        deckService.updateLearnAlgorithm(deckId, data),
      onSuccess: () => {
        queryClient.invalidateQueries({ queryKey: QUERY_KEYS.DECKS });
      },
    });

  const useUpdateFlashcardsPerSession = () =>
    useMutation({
      mutationFn: ({ deckId, data }: { deckId: string; data: UpdateFlashcardsPerSessionRequest }) =>
        deckService.updateFlashcardsPerSession(deckId, data),
      onSuccess: () => {
        queryClient.invalidateQueries({ queryKey: QUERY_KEYS.DECKS });
      },
    });

  const useAddWordsTooDeck = () =>
    useMutation({
      mutationFn: ({ deckId, words }: { deckId: string; words: WordToAdd[] }) =>
        flashcardService.createBatchWordsForDeck(deckId, words),
      onSuccess: () => {
        queryClient.invalidateQueries({ queryKey: INVALIDATION_GROUPS.AFTER_DECK_MUTATION });
      },
    });

  const useAddWordsToCommunity = () =>
    useMutation({
      mutationFn: (words: WordToAdd[]) => flashcardService.createBatchWordsForCommunity(words),
    });

  const useValidateDeckName = () =>
    useMutation({
      mutationFn: (deckName: string) => deckService.validateDeckName(deckName),
    });

  return {
    // Queries
    useMyEnrolledDecks,
    useDecksCreatedByMe,
    useAllPublicDecks,
    useDeckById,
    useDeckDetails,
    useDeckStatistics,
    useUserDeckCount,
    useDeckFlashcards,
    // Mutations
    useCreateDeck,
    useDeleteDeck,
    useUpdateDeckDetails,
    useUpdateDeckVisibility,
    useUpdateDeckOwner,
    useUpdateDeckName,
    useUpdateLearnAlgorithm,
    useUpdateFlashcardsPerSession,
    useAddWordsTooDeck,
    useAddWordsToCommunity,
    useValidateDeckName,
  };
};
