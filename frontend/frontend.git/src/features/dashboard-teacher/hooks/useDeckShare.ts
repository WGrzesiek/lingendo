import { useQuery, useMutation, useQueryClient } from "@tanstack/react-query";
import { deckShareApi } from "../services/deckShareApi";
import type {
  ShareDeckRequestBody,
  BatchShareDeckRequestBody,
} from "../types/api";
import { QUERY_KEYS } from "@/lib/queryKeys";

/**
 * Hook do udostępniania talii (pojedynczego celu)
 */
export const useShareDeck = () => {
  const queryClient = useQueryClient();

  return useMutation({
    mutationFn: ({
      deckId,
      request,
    }: {
      deckId: string;
      request: ShareDeckRequestBody;
    }) => deckShareApi.shareDeck(deckId, request),
    onSuccess: async () => {
      await queryClient.refetchQueries({ queryKey: [QUERY_KEYS.DECK_SHARE] });
    },
  });
};

/**
 * Hook do udostępniania talii wielu celom naraz
 */
export const useShareDeckBatch = () => {
  const queryClient = useQueryClient();

  return useMutation({
    mutationFn: ({
      deckId,
      request,
    }: {
      deckId: string;
      request: BatchShareDeckRequestBody;
    }) => deckShareApi.shareDeckBatch(deckId, request),
    onSuccess: async () => {
      await queryClient.refetchQueries({ queryKey: [QUERY_KEYS.DECK_SHARE] });
    },
  });
};

/**
 * Hook do udostępniania talii wszystkim uczniom
 */
export const useShareDeckWithAllStudents = () => {
  const queryClient = useQueryClient();

  return useMutation({
    mutationFn: (deckId: string) =>
      deckShareApi.shareDeckWithAllStudents(deckId),
    onSuccess: async () => {
      await queryClient.refetchQueries({ queryKey: [QUERY_KEYS.DECK_SHARE] });
      await queryClient.refetchQueries({
        queryKey: [QUERY_KEYS.TEACHER_STUDENT],
      });
    },
  });
};

/**
 * Hook do udostępniania talii wszystkim znajomym
 */
export const useShareDeckWithAllFriends = () => {
  const queryClient = useQueryClient();

  return useMutation({
    mutationFn: (deckId: string) =>
      deckShareApi.shareDeckWithAllFriends(deckId),
    onSuccess: async () => {
      await queryClient.refetchQueries({ queryKey: [QUERY_KEYS.DECK_SHARE] });
    },
  });
};

/**
 * Hook do udostępniania talii grupie
 */
export const useShareDeckWithGroup = () => {
  const queryClient = useQueryClient();

  return useMutation({
    mutationFn: ({
      deckId,
      groupId,
      message,
    }: {
      deckId: string;
      groupId: string;
      message?: string;
    }) => deckShareApi.shareDeckWithGroup(deckId, groupId, message),
    onSuccess: async () => {
      await queryClient.refetchQueries({ queryKey: [QUERY_KEYS.DECK_SHARE] });
      await queryClient.refetchQueries({
        queryKey: [QUERY_KEYS.STUDENT_GROUPS],
      });
    },
  });
};

/**
 * Hook do udostępniania talii konkretnemu użytkownikowi
 */
export const useShareDeckWithUser = () => {
  const queryClient = useQueryClient();

  return useMutation({
    mutationFn: ({
      deckId,
      targetUserId,
    }: {
      deckId: string;
      targetUserId: string;
    }) => deckShareApi.shareDeckWithUser(deckId, targetUserId),
    onSuccess: async () => {
      await queryClient.refetchQueries({ queryKey: [QUERY_KEYS.DECK_SHARE] });
    },
  });
};

/**
 * Hook do cofania udostępnienia talii
 */
export const useRevokeShare = () => {
  const queryClient = useQueryClient();

  return useMutation({
    mutationFn: (shareId: string) => deckShareApi.revokeShare(shareId),
    onSuccess: async () => {
      await queryClient.refetchQueries({ queryKey: [QUERY_KEYS.DECK_SHARE] });
    },
  });
};

/**
 * Hook do pobierania listy udostępnień danej talii
 */
export const useDeckShares = (deckId: string) => {
  return useQuery({
    queryKey: [QUERY_KEYS.DECK_SHARE, "deckShares", deckId],
    queryFn: () => deckShareApi.getDeckShares(deckId),
    enabled: !!deckId,
  });
};

/**
 * Hook do pobierania talii udostępnionych przez aktualnego użytkownika
 */
export const useMyShares = (page: number = 0, size: number = 20) => {
  return useQuery({
    queryKey: [QUERY_KEYS.DECK_SHARE, "myShares", page, size],
    queryFn: () => deckShareApi.getMyShares(page, size),
  });
};

/**
 * Hook do pobierania talii udostępnionych aktualnemu użytkownikowi
 */
export const useSharedWithMe = (page: number = 0, size: number = 20) => {
  return useQuery({
    queryKey: [QUERY_KEYS.DECK_SHARE, "sharedWithMe", page, size],
    queryFn: () => deckShareApi.getSharedWithMe(page, size),
  });
};
