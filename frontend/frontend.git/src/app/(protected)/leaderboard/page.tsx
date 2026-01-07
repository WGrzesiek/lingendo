"use client";

import { Card, CardContent, CardHeader, CardTitle } from "@/components/ui/card";
import { TrendingUp, Trophy, Medal, Award, Loader2 } from "lucide-react";

// TODO: Podpiąć API leaderboard
// import { useLeaderboard } from "@/features/leaderboard/hooks/useLeaderboard";

/**
 * Strona rankingu użytkowników
 */
export default function LeaderboardPage() {
  // TODO: Podpiąć hook do API
  // const { data, isLoading } = useLeaderboard();
  const isLoading = false;

  if (isLoading) {
    return (
      <div className="min-h-screen bg-background flex items-center justify-center">
        <div className="flex flex-col items-center gap-3">
          <Loader2 className="w-8 h-8 animate-spin text-primary" />
          <p className="text-muted-foreground">Ładowanie rankingu...</p>
        </div>
      </div>
    );
  }

  return (
    <div className="min-h-screen bg-background">
      <div className="container mx-auto p-6 lg:p-8 space-y-8">
        {/* Header */}
        <div className="space-y-1">
          <h1 className="text-3xl font-bold flex items-center gap-3">
            <TrendingUp className="w-8 h-8" />
            Ranking użytkowników
          </h1>
          <p className="text-muted-foreground">
            Zobacz jak wypadasz na tle innych użytkowników
          </p>
        </div>

        {/* Placeholder - do podpięcia API */}
        <Card>
          <CardHeader>
            <CardTitle>Pełny ranking</CardTitle>
          </CardHeader>
          <CardContent>
            <div className="text-center py-12">
              <Trophy className="w-12 h-12 mx-auto text-muted-foreground/50 mb-4" />
              <h3 className="text-lg font-medium mb-2">
                Ranking wkrótce dostępny
              </h3>
              <p className="text-muted-foreground max-w-md mx-auto">
                Funkcja rankingu jest w trakcie implementacji. Wróć wkrótce!
              </p>
            </div>
          </CardContent>
        </Card>
      </div>
    </div>
  );
}
