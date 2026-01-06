"use client";

import { Trophy, Medal, Award, TrendingUp } from "lucide-react";
import { Card, CardContent, CardHeader, CardTitle } from "@/components/ui/card";
import { Skeleton } from "@/components/ui/skeleton";
import { cn } from "@/lib/utils";
import type { GroupLeaderboardEntry } from "../types/group.types";

interface GroupLeaderboardProps {
  entries: GroupLeaderboardEntry[] | undefined;
  isLoading: boolean;
  title?: string;
  showRank?: boolean;
}

const rankIcons = [Trophy, Medal, Award];
const rankColors = ["text-yellow-500", "text-slate-400", "text-amber-600"];

/**
 * Ranking członków grupy
 */
export function GroupLeaderboard({
  entries,
  isLoading,
  title = "Ranking",
  showRank = true,
}: GroupLeaderboardProps) {
  if (isLoading) {
    return (
      <Card>
        <CardHeader>
          <CardTitle className="text-lg">{title}</CardTitle>
        </CardHeader>
        <CardContent className="space-y-3">
          {[...Array(5)].map((_, i) => (
            <div key={i} className="flex items-center gap-3">
              <Skeleton className="size-10 rounded-full" />
              <div className="flex-1">
                <Skeleton className="h-4 w-32 mb-1" />
                <Skeleton className="h-3 w-20" />
              </div>
              <Skeleton className="h-6 w-16" />
            </div>
          ))}
        </CardContent>
      </Card>
    );
  }

  if (!entries || entries.length === 0) {
    return (
      <Card>
        <CardHeader>
          <CardTitle className="text-lg">{title}</CardTitle>
        </CardHeader>
        <CardContent>
          <div className="text-center py-8 text-muted-foreground">
            <TrendingUp className="size-8 mx-auto mb-2 opacity-50" />
            <p>Brak danych do wyświetlenia</p>
          </div>
        </CardContent>
      </Card>
    );
  }

  return (
    <Card>
      <CardHeader>
        <CardTitle className="text-lg">{title}</CardTitle>
      </CardHeader>
      <CardContent className="space-y-1">
        {entries.map((entry, index) => {
          const RankIcon = rankIcons[index] || null;
          const rankColor = rankColors[index] || "text-muted-foreground";

          return (
            <div
              key={entry.studentId}
              className={cn(
                "flex items-center gap-3 p-2 rounded-lg transition-colors",
                index < 3 && "bg-muted/50"
              )}
            >
              {/* Pozycja w rankingu */}
              {showRank && (
                <div className="flex items-center justify-center size-8 min-w-8">
                  {RankIcon ? (
                    <RankIcon className={cn("size-5", rankColor)} />
                  ) : (
                    <span className="text-sm font-medium text-muted-foreground">
                      {index + 1}
                    </span>
                  )}
                </div>
              )}

              {/* Avatar */}
              <div
                className={cn(
                  "flex size-10 items-center justify-center rounded-full font-medium text-sm",
                  index === 0
                    ? "bg-yellow-100 text-yellow-700 dark:bg-yellow-900/30 dark:text-yellow-400"
                    : index === 1
                    ? "bg-slate-100 text-slate-700 dark:bg-slate-800 dark:text-slate-300"
                    : index === 2
                    ? "bg-amber-100 text-amber-700 dark:bg-amber-900/30 dark:text-amber-400"
                    : "bg-primary/10 text-primary"
                )}
              >
                {entry.studentName.charAt(0).toUpperCase()}
              </div>

              {/* Informacje o uczniu */}
              <div className="flex-1 min-w-0">
                <div className="font-medium truncate">{entry.studentName}</div>
                <div className="text-xs text-muted-foreground flex items-center gap-2">
                  <span>{entry.accuracy.toFixed(0)}%</span>
                  <span className="text-muted-foreground/50">•</span>
                  <span>{entry.sessions} sesji</span>
                </div>
              </div>

              {/* Punkty/wynik */}
              <div className="text-right shrink-0">
                <div className="font-semibold">{entry.correctAnswers}</div>
                <div className="text-xs text-muted-foreground">pkt</div>
              </div>
            </div>
          );
        })}
      </CardContent>
    </Card>
  );
}
