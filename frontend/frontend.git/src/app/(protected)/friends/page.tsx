"use client";

import { FriendsPage } from "@/features/friends/components/FriendsPage";
import { useProtectedRoute } from "@/features/auth/hooks/useProtectedRoute";
import { Skeleton } from "@/components/ui/skeleton";

/**
 * Strona "Znajomi" - zarządzanie znajomymi, zaproszeniami i wyszukiwaniem
 */
const FriendsPageRoute = () => {
  const { user, isLoading } = useProtectedRoute();

  if (isLoading) {
    return (
      <div className="min-h-screen bg-background">
        <div className="container mx-auto p-6 lg:p-8 space-y-6">
          <Skeleton className="h-10 w-64" />
          <Skeleton className="h-5 w-96" />
          <Skeleton className="h-14 w-full" />
          <div className="space-y-4">
            {[1, 2, 3, 4].map((i) => (
              <Skeleton key={i} className="h-24 w-full" />
            ))}
          </div>
        </div>
      </div>
    );
  }

  if (!user) {
    return null;
  }

  return (
    <div className="min-h-screen bg-background">
      <FriendsPage />
    </div>
  );
};

export default FriendsPageRoute;
