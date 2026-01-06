import { useQuery, useMutation, useQueryClient } from "@tanstack/react-query";
import {
  friendsApiService,
  friendRequestsApiService,
  userSearchApiService,
  friendsStatsApiService,
} from "../services/friends.service";
import { toast } from "sonner";
import { qk } from "@/lib/queryKeys";

// ============================================
// HOOKI DLA ZNAJOMYCH
// ============================================

/**
 * Hook do pobierania listy znajomych z paginacją
 */
export const useFriends = (page: number = 0, size: number = 20) => {
  return useQuery({
    queryKey: qk.friends.listFiltered({ page, size }),
    queryFn: () => friendsApiService.getFriends(page, size),
  });
};

/**
 * Hook do pobierania wszystkich znajomych
 */
export const useAllFriends = () => {
  return useQuery({
    queryKey: qk.friends.allFriends(),
    queryFn: () => friendsApiService.getAllFriends(),
  });
};

/**
 * Hook do pobierania statystyk znajomych
 */
export const useFriendsStats = () => {
  return useQuery({
    queryKey: qk.friends.stats(),
    queryFn: () => friendsApiService.getStats(),
  });
};

/**
 * Hook do sprawdzania czy użytkownicy są znajomymi
 */
export const useCheckFriendship = (otherUserId: string) => {
  return useQuery({
    queryKey: qk.friends.check(otherUserId),
    queryFn: () => friendsApiService.checkFriendship(otherUserId),
    enabled: !!otherUserId,
  });
};

/**
 * Hook do usuwania znajomego
 */
export const useRemoveFriend = () => {
  const queryClient = useQueryClient();

  return useMutation({
    mutationFn: (friendId: string) => friendsApiService.removeFriend(friendId),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: qk.friends.all });
      toast.success("Usunięto znajomego");
    },
    onError: () => {
      toast.error("Nie udało się usunąć znajomego");
    },
  });
};

/**
 * Hook do blokowania użytkownika
 */
export const useBlockUser = () => {
  const queryClient = useQueryClient();

  return useMutation({
    mutationFn: (userToBlockId: string) =>
      friendsApiService.blockUser(userToBlockId),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: qk.friends.all });
      toast.success("Użytkownik został zablokowany");
    },
    onError: () => {
      toast.error("Nie udało się zablokować użytkownika");
    },
  });
};

/**
 * Hook do odblokowywania użytkownika
 */
export const useUnblockUser = () => {
  const queryClient = useQueryClient();

  return useMutation({
    mutationFn: (userToUnblockId: string) =>
      friendsApiService.unblockUser(userToUnblockId),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: qk.friends.all });
      toast.success("Użytkownik został odblokowany");
    },
    onError: () => {
      toast.error("Nie udało się odblokować użytkownika");
    },
  });
};

/**
 * Hook do pobierania zablokowanych użytkowników
 */
export const useBlockedUsers = () => {
  return useQuery({
    queryKey: qk.friends.blocked(),
    queryFn: () => friendsApiService.getBlockedUsers(),
  });
};

/**
 * Hook do pobierania szczegółowych statystyk użytkownika
 */
export const useFriendDetail = (userId: string) => {
  return useQuery({
    queryKey: qk.friends.userStats(userId),
    queryFn: () => friendsStatsApiService.getUserStats(userId),
    enabled: !!userId,
  });
};

/**
 * Hook do pobierania wzbogaconej listy znajomych (z punktami i rankingiem)
 */
export const useFriendsEnriched = () => {
  return useQuery({
    queryKey: qk.friends.enriched(),
    queryFn: () => friendsStatsApiService.getFriendsEnriched(),
  });
};

// ============================================
// HOOKI DLA ZAPROSZEŃ
// ============================================

/**
 * Hook do wysyłania zaproszenia
 */
export const useSendFriendRequest = () => {
  const queryClient = useQueryClient();

  return useMutation({
    mutationFn: (targetUserId: string) =>
      friendRequestsApiService.sendRequest(targetUserId),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: qk.friends.requests() });
      queryClient.invalidateQueries({ queryKey: qk.friends.stats() });
      toast.success("Zaproszenie zostało wysłane");
    },
    onError: () => {
      toast.error("Nie udało się wysłać zaproszenia");
    },
  });
};

/**
 * Hook do pobierania otrzymanych zaproszeń
 */
export const useReceivedRequests = () => {
  return useQuery({
    queryKey: qk.friends.receivedRequests(),
    queryFn: () => friendRequestsApiService.getReceivedRequests(),
  });
};

/**
 * Hook do pobierania wysłanych zaproszeń
 */
export const useSentRequests = () => {
  return useQuery({
    queryKey: qk.friends.sentRequests(),
    queryFn: () => friendRequestsApiService.getSentRequests(),
  });
};

/**
 * Hook do akceptowania zaproszenia
 */
export const useAcceptRequest = () => {
  const queryClient = useQueryClient();

  return useMutation({
    mutationFn: (friendshipId: string) =>
      friendRequestsApiService.acceptRequest(friendshipId),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: qk.friends.all });
      toast.success("Zaproszenie zostało zaakceptowane");
    },
    onError: () => {
      toast.error("Nie udało się zaakceptować zaproszenia");
    },
  });
};

/**
 * Hook do odrzucania zaproszenia
 */
export const useRejectRequest = () => {
  const queryClient = useQueryClient();

  return useMutation({
    mutationFn: (friendshipId: string) =>
      friendRequestsApiService.rejectRequest(friendshipId),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: qk.friends.requests() });
      queryClient.invalidateQueries({ queryKey: qk.friends.stats() });
      toast.success("Zaproszenie zostało odrzucone");
    },
    onError: () => {
      toast.error("Nie udało się odrzucić zaproszenia");
    },
  });
};

/**
 * Hook do anulowania wysłanego zaproszenia
 */
export const useCancelRequest = () => {
  const queryClient = useQueryClient();

  return useMutation({
    mutationFn: (friendshipId: string) =>
      friendRequestsApiService.cancelRequest(friendshipId),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: qk.friends.requests() });
      queryClient.invalidateQueries({ queryKey: qk.friends.stats() });
      toast.success("Zaproszenie zostało anulowane");
    },
    onError: () => {
      toast.error("Nie udało się anulować zaproszenia");
    },
  });
};

// ============================================
// HOOKI DLA WYSZUKIWANIA
// ============================================

/**
 * Hook do wyszukiwania użytkowników
 */
export const useSearchUsers = (query: string) => {
  return useQuery({
    queryKey: qk.friends.search(query),
    queryFn: () => userSearchApiService.searchUsers(query),
    enabled: query.length >= 2,
    staleTime: 30 * 1000,
  });
};
