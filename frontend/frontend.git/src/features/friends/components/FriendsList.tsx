import { IFriend } from "@/features/friends/types/friend.types";
import { FriendCard } from "./FriendCard";
import { Skeleton } from "@/components/ui/skeleton";
import { Alert, AlertDescription } from "@/components/ui/alert";
import { UserX, Users } from "lucide-react";

interface FriendsListProps {
  friends: IFriend[];
  isLoading?: boolean;
  error?: string;
}

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
const EmptyState = () => (
  <div className="text-center py-16">
    <div className="inline-flex items-center justify-center w-16 h-16 rounded-full bg-muted mb-4">
      <UserX className="w-8 h-8 text-muted-foreground" />
    </div>
    <h3 className="text-xl font-semibold mb-2">Brak znajomych</h3>
    <p className="text-muted-foreground max-w-md mx-auto">
      Nie masz jeszcze żadnych znajomych. Dodaj pierwszego znajomego wpisując
      jego nazwę użytkownika.
    </p>
  </div>
);

/**
 * Komponent listy znajomych z obsługą stanów loading/error/empty
 */
export const FriendsList = ({
  friends,
  isLoading = false,
  error,
}: FriendsListProps) => {
  // Loading state
  if (isLoading) {
    return <FriendsListSkeleton />;
  }

  // Error state
  if (error) {
    return (
      <Alert variant="destructive">
        <AlertDescription>{error}</AlertDescription>
      </Alert>
    );
  }

  // Empty state
  if (!friends || friends.length === 0) {
    return <EmptyState />;
  }

  // Lista znajomych
  return (
    <div className="space-y-4">
      <div className="flex items-center gap-2 text-muted-foreground mb-4">
        <Users className="w-5 h-5" />
        <span className="text-sm font-medium">
          {friends.length} {friends.length === 1 ? "znajomy" : "znajomych"}
        </span>
      </div>
      {friends.map((friend) => (
        <FriendCard key={friend.userId} friend={friend} />
      ))}
    </div>
  );
};
