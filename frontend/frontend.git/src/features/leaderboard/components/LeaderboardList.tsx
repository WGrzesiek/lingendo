import { Trophy } from "lucide-react";
import { ILeaderboardEntry } from "@/features/leaderboard/types/leaderboard.types";
import { cn } from "@/lib/utils";

interface LeaderboardListProps {
  entries: ILeaderboardEntry[];
  currentUserId: string;
}

/**
 * Komponent listy rankingu - wzorowany na Leaderboard z dashboard
 */
export const LeaderboardList = ({
  entries,
  currentUserId,
}: LeaderboardListProps) => {
  const getRankIcon = (rank: number) => {
    switch (rank) {
      case 1:
        return <Trophy className="w-5 h-5 text-yellow-500" />;
      case 2:
        return <Trophy className="w-5 h-5 text-gray-400" />;
      case 3:
        return <Trophy className="w-5 h-5 text-amber-600" />;
      default:
        return (
          <div className="w-8 h-8 rounded-full bg-muted flex items-center justify-center text-sm font-semibold">
            {rank}
          </div>
        );
    }
  };

  if (entries.length === 0) {
    return (
      <div className="text-center py-12">
        <p className="text-muted-foreground">Brak wyników do wyświetlenia</p>
      </div>
    );
  }

  return (
    <div className="space-y-2">
      {entries.map((entry) => {
        const isCurrentUser = entry.userId === currentUserId;

        return (
          <div
            key={entry.userId}
            className={cn(
              "flex items-center gap-4 p-4 rounded-lg border transition-all",
              isCurrentUser
                ? "bg-primary/10 border-primary shadow-sm"
                : "bg-background border-border hover:border-primary/50 hover:bg-accent/50"
            )}
          >
            {/* Pozycja */}
            <div className="flex items-center justify-center w-10 flex-shrink-0">
              {getRankIcon(entry.rank)}
            </div>

            {/* Nazwa użytkownika */}
            <div className="flex-1 min-w-0">
              <h3
                className={cn(
                  "font-semibold text-base truncate",
                  isCurrentUser ? "text-primary" : "text-foreground"
                )}
              >
                {entry.displayName}
                {isCurrentUser && " (Ty)"}
              </h3>
              <p className="text-sm text-muted-foreground">
                {entry.completedCourses}{" "}
                {entry.completedCourses === 1
                  ? "ukończony kurs"
                  : entry.completedCourses < 5
                  ? "ukończone kursy"
                  : "ukończonych kursów"}
              </p>
            </div>

            {/* Punkty */}
            <div className="text-right flex-shrink-0">
              <p
                className={cn(
                  "font-bold text-xl",
                  isCurrentUser ? "text-primary" : "text-foreground"
                )}
              >
                {entry.points.toLocaleString("pl-PL")}
              </p>
              <p className="text-xs text-muted-foreground">punktów</p>
            </div>
          </div>
        );
      })}
    </div>
  );
};
