import { useState, useMemo } from "react";
import { Card, CardContent, CardHeader, CardTitle } from "@/components/ui/card";
import { Input } from "@/components/ui/input";
import { Button } from "@/components/ui/button";

import {
  AlertDialog,
  AlertDialogAction,
  AlertDialogCancel,
  AlertDialogContent,
  AlertDialogDescription,
  AlertDialogFooter,
  AlertDialogHeader,
  AlertDialogTitle,
} from "@/components/ui/alert-dialog";
import { Alert, AlertDescription } from "@/components/ui/alert";
import { Skeleton } from "@/components/ui/skeleton";
import { FriendCard } from "./FriendCard";
import { FriendDetails } from "./FriendDetails";
import {
  useFriends,
  useFriendsEnriched,
  useRemoveFriend,
  useBlockUser,
} from "../hooks/useFriends";
import { Search, Users, RefreshCw, UserX } from "lucide-react";

/**
 * Skeleton dla loadingu listy znajomych
 */
const FriendsListSkeleton = () => (
  <div className="space-y-4">
    {[1, 2, 3, 4].map((i) => (
      <div key={i} className="border rounded-lg p-4">
        <div className="flex items-center justify-between gap-4">
          <div className="flex items-center gap-3 flex-1">
            <Skeleton className="w-12 h-12 rounded-full" />
            <div className="space-y-2 flex-1">
              <Skeleton className="h-5 w-32" />
              <Skeleton className="h-4 w-48" />
            </div>
          </div>
          <div className="flex gap-6">
            <Skeleton className="h-10 w-16" />
            <Skeleton className="h-10 w-20" />
          </div>
        </div>
      </div>
    ))}
  </div>
);

/**
 * Stan pustej listy
 */
const EmptyState = ({ searchQuery }: { searchQuery?: string }) => (
  <div className="text-center py-16">
    <div className="inline-flex items-center justify-center w-16 h-16 rounded-full bg-muted mb-4">
      <UserX className="w-8 h-8 text-muted-foreground" />
    </div>
    <h3 className="text-xl font-semibold mb-2">Brak znajomych</h3>
    <p className="text-muted-foreground max-w-md mx-auto">
      {searchQuery
        ? "Nie znaleziono znajomych pasujących do wyszukiwania"
        : "Nie masz jeszcze żadnych znajomych. Dodaj pierwszego znajomego wpisując jego nazwę użytkownika."}
    </p>
  </div>
);

interface FriendsListProps {
  initialSearchQuery?: string;
}

/**
 * Główny komponent listy znajomych
 */
export const FriendsList = ({ initialSearchQuery }: FriendsListProps) => {
  const [searchQuery, setSearchQuery] = useState(initialSearchQuery || "");
  const [selectedFriendId, setSelectedFriendId] = useState<string | null>(null);
  const [friendToRemove, setFriendToRemove] = useState<string | null>(null);
  const [friendToBlock, setFriendToBlock] = useState<string | null>(null);

  const { data: friendsData, isLoading, error, refetch } = useFriends();
  const { data: enrichedData } = useFriendsEnriched();
  const removeFriend = useRemoveFriend();
  const blockUser = useBlockUser();

  // Łączenie danych znajomych z danymi wzbogaconymi (punkty, ranking)
  const enrichedFriends = useMemo(() => {
    const friendsList = friendsData?.content ?? [];
    if (!friendsList.length) return [];
    if (!enrichedData) return friendsList;

    const enrichedMap = new Map(enrichedData.map((e) => [e.friendId, e]));

    return friendsList.map((friend) => {
      const enriched = enrichedMap.get(friend.userId);
      if (enriched) {
        return {
          ...friend,
          totalPoints: enriched.totalPoints,
          rankPosition: enriched.globalRank,
        };
      }
      return friend;
    });
  }, [friendsData, enrichedData]);

  // Filtrowanie po wyszukiwanej frazie
  const filteredFriends = useMemo(() => {
    if (!enrichedFriends || !searchQuery) return enrichedFriends;
    const query = searchQuery.toLowerCase();
    return enrichedFriends.filter((friend) =>
      friend.username.toLowerCase().includes(query)
    );
  }, [enrichedFriends, searchQuery]);

  // Obsługa akcji
  const handleViewDetails = (userId: string) => {
    setSelectedFriendId(userId);
  };

  const handleRemove = async () => {
    if (friendToRemove) {
      await removeFriend.mutateAsync(friendToRemove);
      setFriendToRemove(null);
    }
  };

  const handleBlock = async () => {
    if (friendToBlock) {
      await blockUser.mutateAsync(friendToBlock);
      setFriendToBlock(null);
    }
  };

  // Widok szczegółów znajomego
  if (selectedFriendId) {
    return (
      <FriendDetails
        userId={selectedFriendId}
        onBack={() => setSelectedFriendId(null)}
      />
    );
  }

  return (
    <div className="space-y-6">
      {/* Nagłówek z filtrowaniem */}
      <Card>
        <CardHeader className="pb-4">
          <div className="flex items-center justify-between">
            <CardTitle className="flex items-center gap-2">
              <Users className="w-5 h-5" />
              Twoi znajomi
              {enrichedFriends && (
                <span className="text-sm font-normal text-muted-foreground">
                  ({enrichedFriends.length})
                </span>
              )}
            </CardTitle>
            <Button variant="outline" size="sm" onClick={() => refetch()}>
              <RefreshCw className="w-4 h-4 mr-2" />
              Odśwież
            </Button>
          </div>
        </CardHeader>
        <CardContent>
          <div className="flex flex-col sm:flex-row gap-4">
            {/* Wyszukiwarka */}
            <div className="relative flex-1">
              <Search className="absolute left-3 top-1/2 -translate-y-1/2 w-4 h-4 text-muted-foreground" />
              <Input
                placeholder="Szukaj znajomego..."
                value={searchQuery}
                onChange={(e) => setSearchQuery(e.target.value)}
                className="pl-10"
              />
            </div>
          </div>
        </CardContent>
      </Card>

      {/* Lista znajomych */}
      {isLoading ? (
        <FriendsListSkeleton />
      ) : error ? (
        <Alert variant="destructive">
          <AlertDescription>
            Wystąpił błąd podczas ładowania znajomych
          </AlertDescription>
        </Alert>
      ) : filteredFriends?.length === 0 ? (
        <EmptyState searchQuery={searchQuery} />
      ) : (
        <div className="space-y-3">
          {filteredFriends?.map((friend) => (
            <FriendCard
              key={friend.id}
              friend={friend}
              onViewDetails={handleViewDetails}
              onRemove={setFriendToRemove}
              onBlock={setFriendToBlock}
            />
          ))}
        </div>
      )}

      {/* Dialog potwierdzenia usunięcia */}
      <AlertDialog
        open={!!friendToRemove}
        onOpenChange={() => setFriendToRemove(null)}
      >
        <AlertDialogContent>
          <AlertDialogHeader>
            <AlertDialogTitle>Usuń znajomego</AlertDialogTitle>
            <AlertDialogDescription>
              Czy na pewno chcesz usunąć tego znajomego? Ta osoba zostanie
              usunięta z Twojej listy znajomych i nie będziecie mogli porównywać
              swoich postępów.
            </AlertDialogDescription>
          </AlertDialogHeader>
          <AlertDialogFooter>
            <AlertDialogCancel>Anuluj</AlertDialogCancel>
            <AlertDialogAction
              onClick={handleRemove}
              disabled={removeFriend.isPending}
            >
              {removeFriend.isPending ? "Usuwanie..." : "Usuń"}
            </AlertDialogAction>
          </AlertDialogFooter>
        </AlertDialogContent>
      </AlertDialog>

      {/* Dialog potwierdzenia blokady */}
      <AlertDialog
        open={!!friendToBlock}
        onOpenChange={() => setFriendToBlock(null)}
      >
        <AlertDialogContent>
          <AlertDialogHeader>
            <AlertDialogTitle>Zablokuj użytkownika</AlertDialogTitle>
            <AlertDialogDescription>
              Czy na pewno chcesz zablokować tego użytkownika? Zablokowana osoba
              nie będzie mogła wysyłać Ci zaproszeń do znajomych ani wyświetlać
              Twojego profilu.
            </AlertDialogDescription>
          </AlertDialogHeader>
          <AlertDialogFooter>
            <AlertDialogCancel>Anuluj</AlertDialogCancel>
            <AlertDialogAction
              onClick={handleBlock}
              disabled={blockUser.isPending}
              className="bg-destructive text-destructive-foreground hover:bg-destructive/90"
            >
              {blockUser.isPending ? "Blokowanie..." : "Zablokuj"}
            </AlertDialogAction>
          </AlertDialogFooter>
        </AlertDialogContent>
      </AlertDialog>
    </div>
  );
};
