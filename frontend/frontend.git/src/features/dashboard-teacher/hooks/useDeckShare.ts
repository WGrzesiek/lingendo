import { useQuery, useMutation, useQueryClient } from "@tanstack/react-query";
import { deckShareApi } from "../services/deckShareApi";
import type {
  ShareDeckRequestBody,
  BatchShareDeckRequestBody,
} from "../types/api";
import { qk } from "@/lib/queryKeys";

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
    onSuccess: (_, { deckId }) => {
      queryClient.invalidateQueries({
        queryKey: qk.deckShare.deckShares(deckId),
      });
      queryClient.invalidateQueries({
        queryKey: qk.deckShare.mySharesPaged(0, 20),
      });
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
    onSuccess: (_, { deckId }) => {
      queryClient.invalidateQueries({
        queryKey: qk.deckShare.deckShares(deckId),
      });
      queryClient.invalidateQueries({
        queryKey: qk.deckShare.mySharesPaged(0, 20),
      });
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
    onSuccess: (_, deckId) => {
      queryClient.invalidateQueries({
        queryKey: qk.deckShare.deckShares(deckId),
      });
      queryClient.invalidateQueries({
        queryKey: qk.deckShare.mySharesPaged(0, 20),
      });
      queryClient.invalidateQueries({
        queryKey: qk.teacherStudent.students(),
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
    onSuccess: (_, deckId) => {
      queryClient.invalidateQueries({
        queryKey: qk.deckShare.deckShares(deckId),
      });
      queryClient.invalidateQueries({
        queryKey: qk.deckShare.mySharesPaged(0, 20),
      });
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
    onSuccess: (_, { deckId, groupId }) => {
      queryClient.invalidateQueries({
        queryKey: qk.deckShare.deckShares(deckId),
      });
      queryClient.invalidateQueries({
        queryKey: qk.deckShare.mySharesPaged(0, 20),
      });
      queryClient.invalidateQueries({
        queryKey: qk.studentGroups.groupDetail(groupId),
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
    onSuccess: (_, { deckId }) => {
      queryClient.invalidateQueries({
        queryKey: qk.deckShare.deckShares(deckId),
      });
      queryClient.invalidateQueries({
        queryKey: qk.deckShare.mySharesPaged(0, 20),
      });
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
    onSuccess: () => {
      queryClient.invalidateQueries({
        queryKey: qk.deckShare.all,
      });
    },
  });
};

/**
 * Hook do pobierania listy udostępnień danej talii
 */
export const useDeckShares = (deckId: string) => {
  return useQuery({
    queryKey: qk.deckShare.deckShares(deckId),
    queryFn: () => deckShareApi.getDeckShares(deckId),
    enabled: !!deckId,
  });
};

/**
 * Hook do pobierania talii udostępnionych przez aktualnego użytkownika
 */
export const useMyShares = (page: number = 0, size: number = 20) => {
  return useQuery({
    queryKey: qk.deckShare.mySharesPaged(page, size),
    queryFn: () => deckShareApi.getMyShares(page, size),
  });
};

/**
 * Hook do pobierania talii udostępnionych aktualnemu użytkownikowi
 */
export const useSharedWithMe = (page: number = 0, size: number = 20) => {
  return useQuery({
    queryKey: qk.deckShare.sharedWithMePaged(page, size),
    queryFn: () => deckShareApi.getSharedWithMe(page, size),
  });
};
