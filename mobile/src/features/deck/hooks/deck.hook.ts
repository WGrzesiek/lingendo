import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query';
import { QUERY_KEYS, INVALIDATION_GROUPS } from '@/constants';
import { deckService, flashcardService } from '../services';
import type {
  CreateDeckRequest,
  UpdateDeckRequest,
  DeckOwnerType,
  DeckVisibility,
  LearnAlgorithm,
  DeckFilters,
  WordToAdd,
} from '../types';

export const useDeck = () => {
  const queryClient = useQueryClient();

  /**
   * Invaliduje wszystkie klucze z grupy
   */
  const invalidateGroup = (group: readonly string[]) => {
    group.forEach((key) => {
      queryClient.invalidateQueries({ queryKey: [key] });
    });
  };

  // =====================
  // QUERIES - Pobieranie danych
  // =====================

  /**
   * Pobiera zapisane talie użytkownika z paginacją
   */
  const useMyEnrolledDecks = (page: number = 0, size: number = 20) =>
    useQuery({
      queryKey: [QUERY_KEYS.ENROLLMENTS, 'my', { page, size }],
      queryFn: () => deckService.getMyEnrolledDecks({ page, size }),
    });

  /**
   * Pobiera publiczne talie z paginacją
   */
  const usePublicDecks = (page?: number, size?: number, filters?: DeckFilters) =>
    useQuery({
      queryKey: [QUERY_KEYS.DECKS, 'public', { page, size, ...filters }],
      queryFn: () => deckService.getPublicDecks(page, size, filters),
    });

  /**
   * Pobiera talie użytkownika z paginacją
   */
  const useUserDecks = (page?: number, size?: number, filters?: DeckFilters) =>
    useQuery({
      queryKey: [QUERY_KEYS.DECKS, 'user', { page, size, ...filters }],
      queryFn: () => deckService.getUserDecks(page, size, filters),
    });

  /**
   * Pobiera talie użytkownika filtrowane po typie właściciela
   */
  const useUserDecksFiltered = (ownerType: DeckOwnerType) =>
    useQuery({
      queryKey: [QUERY_KEYS.DECKS, 'filtered', ownerType],
      queryFn: () => deckService.getUserDecksFiltered(ownerType),
      enabled: !!ownerType,
    });

  /**
   * Pobiera liczbę talii użytkownika
   */
  const useUserDecksCount = () =>
    useQuery({
      queryKey: [QUERY_KEYS.DECKS, 'count'],
      queryFn: () => deckService.getUserDecksCount(),
    });

  /**
   * Pobiera szczegóły talii z danymi zapisu
   */
  const useDeckWithEnrollment = (deckId: string) =>
    useQuery({
      queryKey: [QUERY_KEYS.DECKS, 'details', deckId],
      queryFn: () => deckService.getDeckWithEnrollment(deckId),
      enabled: !!deckId,
    });

  /**
   * Pobiera talię po ID
   */
  const useDeckById = (deckId: string) =>
    useQuery({
      queryKey: [QUERY_KEYS.DECKS, deckId],
      queryFn: () => deckService.getDeckById(deckId),
      enabled: !!deckId,
    });

  /**
   * Pobiera statystyki talii
   */
  const useDeckStatistics = (deckId: string) =>
    useQuery({
      queryKey: [QUERY_KEYS.DECKS, 'statistics', deckId],
      queryFn: () => deckService.getDeckStatistics(deckId),
      enabled: !!deckId,
    });

  /**
   * Pobiera fiszki talii z paginacją
   */
  const useDeckFlashcards = (deckId: string, page?: number, size?: number) =>
    useQuery({
      queryKey: [QUERY_KEYS.FLASHCARDS, deckId, { page, size }],
      queryFn: () => flashcardService.getDeckFlashcardsPage(deckId, page, size),
      enabled: !!deckId,
    });

  // =====================
  // MUTATIONS - Modyfikacje danych
  // =====================

  /**
   * Tworzy nową talię
   */
  const useCreateDeck = () =>
    useMutation({
      mutationFn: (data: CreateDeckRequest) => deckService.createDeck(data),
      onSuccess: () => {
        invalidateGroup(INVALIDATION_GROUPS.AFTER_DECK_MUTATION);
      },
    });

  /**
   * Aktualizuje talię
   */
  const useUpdateDeck = () =>
    useMutation({
      mutationFn: ({ deckId, data }: { deckId: string; data: UpdateDeckRequest }) =>
        deckService.updateDeck(deckId, data),
      onSuccess: () => {
        queryClient.invalidateQueries({ queryKey: [QUERY_KEYS.DECKS] });
      },
    });

  /**
   * Usuwa talię
   */
  const useDeleteDeck = () =>
    useMutation({
      mutationFn: (deckId: string) => deckService.deleteDeck(deckId),
      onSuccess: () => {
        invalidateGroup(INVALIDATION_GROUPS.AFTER_DECK_MUTATION);
      },
    });

  /**
   * Aktualizuje widoczność talii
   */
  const useUpdateVisibility = () =>
    useMutation({
      mutationFn: ({ deckId, visibility }: { deckId: string; visibility: DeckVisibility }) =>
        deckService.updateVisibility(deckId, visibility),
      onSuccess: () => {
        queryClient.invalidateQueries({ queryKey: [QUERY_KEYS.DECKS] });
      },
    });

  /**
   * Aktualizuje właściciela talii
   */
  const useUpdateOwner = () =>
    useMutation({
      mutationFn: ({ deckId, ownerType }: { deckId: string; ownerType: DeckOwnerType }) =>
        deckService.updateOwner(deckId, ownerType),
      onSuccess: () => {
        queryClient.invalidateQueries({ queryKey: [QUERY_KEYS.DECKS] });
      },
    });

  /**
   * Aktualizuje nazwę talii
   */
  const useUpdateName = () =>
    useMutation({
      mutationFn: ({ deckId, name }: { deckId: string; name: string }) =>
        deckService.updateName(deckId, name),
      onSuccess: () => {
        queryClient.invalidateQueries({ queryKey: [QUERY_KEYS.DECKS] });
      },
    });

  /**
   * Aktualizuje algorytm nauki
   */
  const useUpdateAlgorithm = () =>
    useMutation({
      mutationFn: ({ deckId, algorithm }: { deckId: string; algorithm: LearnAlgorithm }) =>
        deckService.updateAlgorithm(deckId, algorithm),
      onSuccess: () => {
        queryClient.invalidateQueries({ queryKey: [QUERY_KEYS.DECKS] });
      },
    });

  /**
   * Aktualizuje liczbę fiszek na sesję
   */
  const useUpdateFlashcardsPerSession = () =>
    useMutation({
      mutationFn: ({
        deckId,
        flashcardsPerSession,
      }: {
        deckId: string;
        flashcardsPerSession: number;
      }) => deckService.updateFlashcardsPerSession(deckId, flashcardsPerSession),
      onSuccess: () => {
        queryClient.invalidateQueries({ queryKey: [QUERY_KEYS.DECKS] });
      },
    });

  /**
   * Dodaje słowa do talii
   */
  const useAddWordsTooDeck = () =>
    useMutation({
      mutationFn: ({ deckId, words }: { deckId: string; words: WordToAdd[] }) =>
        flashcardService.createBatchWordsForDeck(deckId, words),
      onSuccess: () => {
        invalidateGroup(INVALIDATION_GROUPS.AFTER_DECK_MUTATION);
      },
    });

  /**
   * Waliduje nazwę talii
   */
  const useValidateDeckName = () =>
    useMutation({
      mutationFn: (name: string) => deckService.validateDeckName(name),
    });

  return {
    // Queries
    useMyEnrolledDecks,
    usePublicDecks,
    useUserDecks,
    useUserDecksFiltered,
    useUserDecksCount,
    useDeckWithEnrollment,
    useDeckById,
    useDeckStatistics,
    useDeckFlashcards,
    // Mutations
    useCreateDeck,
    useUpdateDeck,
    useDeleteDeck,
    useUpdateVisibility,
    useUpdateOwner,
    useUpdateName,
    useUpdateAlgorithm,
    useUpdateFlashcardsPerSession,
    useAddWordsTooDeck,
    useValidateDeckName,
  };
};
