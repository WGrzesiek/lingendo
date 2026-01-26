import {
  useQuery,
  useMutation,
  useQueryClient,
  useInfiniteQuery,
} from "@tanstack/react-query";
import { deckShareService } from "../services/deckShare.service";
import type {
  ShareDeckRequest,
  BatchShareDeckRequest,
  DeckShareResponse,
  SharedDeckDto,
} from "../types/deckShare.types";
import type { PageResponse } from "@/types/common";
import { qk } from "@/lib/queryKeys";

/**
 * Hook do pobierania udostępnień konkretnej talii
 */
export function useDeckShares(deckId: string) {
  return useQuery({
    queryKey: qk.deckShare.deckShares(deckId),
    queryFn: () => deckShareService.getDeckShares(deckId),
    enabled: !!deckId,
  });
}

/**
 * Hook do pobierania moich udostępnień (stronicowanie)
 */
export function useMyShares(page: number = 0, size: number = 20) {
  return useQuery({
    queryKey: [...qk.deckShare.myShares(), page, size],
    queryFn: () => deckShareService.getMyShares(page, size),
  });
}

/**
 * Hook do pobierania moich udostępnień (infinite scroll)
 */
export function useInfiniteMyShares(pageSize: number = 20) {
  return useInfiniteQuery<PageResponse<DeckShareResponse>, Error>({
    queryKey: [...qk.deckShare.myShares(), "infinite"],
    queryFn: async ({ pageParam = 0 }) =>
      deckShareService.getMyShares(pageParam as number, pageSize),
    initialPageParam: 0,
    getNextPageParam: (lastPage) =>
      lastPage.last ? undefined : lastPage.number + 1,
  });
}

/**
 * Hook do pobierania talii udostępnionych mi (stronicowanie)
 */
export function useSharedWithMe(page: number = 0, size: number = 20) {
  return useQuery({
    queryKey: [...qk.deckShare.sharedWithMe(), page, size],
    queryFn: () => deckShareService.getSharedWithMe(page, size),
  });
}

/**
 * Hook do pobierania talii udostępnionych mi (infinite scroll)
 */
export function useInfiniteSharedWithMe(pageSize: number = 20) {
  return useInfiniteQuery<PageResponse<SharedDeckDto>, Error>({
    queryKey: [...qk.deckShare.sharedWithMe(), "infinite"],
    queryFn: async ({ pageParam = 0 }) =>
      deckShareService.getSharedWithMe(pageParam as number, pageSize),
    initialPageParam: 0,
    getNextPageParam: (lastPage) =>
      lastPage.last ? undefined : lastPage.number + 1,
  });
}

/**
 * Hook do sprawdzania dostępu do talii
 */
export function useHasAccessToDeck(deckId: string) {
  return useQuery({
    queryKey: qk.deckShare.hasAccess(deckId),
    queryFn: () => deckShareService.hasAccessToDeck(deckId),
    enabled: !!deckId,
  });
}

/**
 * Hook do udostępniania talii (generyczne)
 */
export function useShareDeck() {
  const queryClient = useQueryClient();

  return useMutation({
    mutationFn: ({
      deckId,
      request,
    }: {
      deckId: string;
      request: ShareDeckRequest;
    }) => deckShareService.shareDeck(deckId, request),
    onSuccess: (_, { deckId }) => {
      queryClient.invalidateQueries({
        queryKey: qk.deckShare.deckShares(deckId),
      });
      queryClient.invalidateQueries({ queryKey: qk.deckShare.myShares() });
    },
  });
}

/**
 * Hook do batch udostępniania talii
 */
export function useShareDeckBatch() {
  const queryClient = useQueryClient();

  return useMutation({
    mutationFn: ({
      deckId,
      request,
    }: {
      deckId: string;
      request: BatchShareDeckRequest;
    }) => deckShareService.shareDeckBatch(deckId, request),
    onSuccess: (_, { deckId }) => {
      queryClient.invalidateQueries({
        queryKey: qk.deckShare.deckShares(deckId),
      });
      queryClient.invalidateQueries({ queryKey: qk.deckShare.myShares() });
    },
  });
}

/**
 * Hook do udostępniania talii wszystkim uczniom
 */
export function useShareDeckWithAllStudents() {
  const queryClient = useQueryClient();

  return useMutation({
    mutationFn: ({ deckId, message }: { deckId: string; message?: string }) =>
      deckShareService.shareDeckWithAllStudents(deckId, message),
    onSuccess: (_, { deckId }) => {
      queryClient.invalidateQueries({
        queryKey: qk.deckShare.deckShares(deckId),
      });
      queryClient.invalidateQueries({ queryKey: qk.deckShare.myShares() });
    },
  });
}

/**
 * Hook do udostępniania talii wszystkim znajomym
 */
export function useShareDeckWithAllFriends() {
  const queryClient = useQueryClient();

  return useMutation({
    mutationFn: ({ deckId, message }: { deckId: string; message?: string }) =>
      deckShareService.shareDeckWithAllFriends(deckId, message),
    onSuccess: (_, { deckId }) => {
      queryClient.invalidateQueries({
        queryKey: qk.deckShare.deckShares(deckId),
      });
      queryClient.invalidateQueries({ queryKey: qk.deckShare.myShares() });
    },
  });
}

/**
 * Hook do udostępniania talii grupie
 */
export function useShareDeckWithGroup() {
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
    }) => deckShareService.shareDeckWithGroup(deckId, groupId, message),
    onSuccess: (_, { deckId }) => {
      queryClient.invalidateQueries({
        queryKey: qk.deckShare.deckShares(deckId),
      });
      queryClient.invalidateQueries({ queryKey: qk.deckShare.myShares() });
      // Invalidate group stats as well
      queryClient.invalidateQueries({ queryKey: ["groups"] });
    },
  });
}

/**
 * Hook do udostępniania talii użytkownikowi
 */
export function useShareDeckWithUser() {
  const queryClient = useQueryClient();

  return useMutation({
    mutationFn: ({
      deckId,
      targetUserId,
      message,
    }: {
      deckId: string;
      targetUserId: string;
      message?: string;
    }) => deckShareService.shareDeckWithUser(deckId, targetUserId, message),
    onSuccess: (_, { deckId }) => {
      queryClient.invalidateQueries({
        queryKey: qk.deckShare.deckShares(deckId),
      });
      queryClient.invalidateQueries({ queryKey: qk.deckShare.myShares() });
    },
  });
}

/**
 * Hook do wycofywania udostępnienia
 */
export function useRevokeDeckShare() {
  const queryClient = useQueryClient();

  return useMutation({
    mutationFn: (shareId: string) => deckShareService.revokeDeckShare(shareId),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: qk.deckShare.all });
    },
  });
}

/**
 * Hook do wycofywania wszystkich udostępnień talii
 */
export function useRevokeAllDeckShares() {
  const queryClient = useQueryClient();

  return useMutation({
    mutationFn: (deckId: string) =>
      deckShareService.revokeAllDeckShares(deckId),
    onSuccess: (_, deckId) => {
      queryClient.invalidateQueries({
        queryKey: qk.deckShare.deckShares(deckId),
      });
      queryClient.invalidateQueries({ queryKey: qk.deckShare.myShares() });
    },
  });
}
