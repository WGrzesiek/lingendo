import { Card, CardContent } from "@/components/ui/card";
import { Badge } from "@/components/ui/badge";
import { IFriend } from "@/features/friends/types/friend.types";
import { Trophy, TrendingUp, User } from "lucide-react";
import { cn } from "@/lib/utils";

interface FriendCardProps {
  friend: IFriend;
}

/**
 * Komponent karty znajomego z informacjami o rankingu i punktach
 */
export const FriendCard = ({ friend }: FriendCardProps) => {
  // Kolor dla top 3 w rankingu
  const getRankColor = (position: number) => {
    if (position === 1) return "text-yellow-500";
    if (position === 2) return "text-gray-400";
    if (position === 3) return "text-amber-600";
    return "text-muted-foreground";
  };

  // Ikona dla top 3
  const showTrophy = friend.rankPosition <= 3;

  return (
    <Card className="hover:shadow-md transition-shadow">
      <CardContent className="p-4">
        <div className="flex items-center justify-between gap-4">
          {/* Avatar i nazwa użytkownika */}
          <div className="flex items-center gap-3 min-w-0 flex-1">
            <div className="w-12 h-12 rounded-full bg-primary/10 flex items-center justify-center flex-shrink-0">
              <User className="w-6 h-6 text-primary" />
            </div>
            <div className="min-w-0 flex-1">
              <div className="flex items-center gap-2">
                <h3 className="font-semibold text-lg truncate">
                  {friend.username}
                </h3>
                {!friend.isActive && (
                  <Badge variant="outline" className="text-xs">
                    Nieaktywny
                  </Badge>
                )}
              </div>
              <p className="text-sm text-muted-foreground">
                Znajomi od{" "}
                {new Date(friend.friendsSince).toLocaleDateString("pl-PL", {
                  year: "numeric",
                  month: "long",
                })}
              </p>
            </div>
          </div>

          {/* Statystyki */}
          <div className="flex items-center gap-6 flex-shrink-0">
            {/* Pozycja w rankingu */}
            <div className="flex flex-col items-center">
              <div className="flex items-center gap-1">
                {showTrophy && (
                  <Trophy
                    className={cn("w-4 h-4", getRankColor(friend.rankPosition))}
                  />
                )}
                <span
                  className={cn(
                    "text-2xl font-bold",
                    getRankColor(friend.rankPosition)
                  )}
                >
                  #{friend.rankPosition}
                </span>
              </div>
              <span className="text-xs text-muted-foreground">Ranking</span>
            </div>

            {/* Punkty */}
            <div className="flex flex-col items-center">
              <div className="flex items-center gap-1">
                <TrendingUp className="w-4 h-4 text-green-500" />
                <span className="text-2xl font-bold text-green-500">
                  {friend.totalPoints.toLocaleString("pl-PL")}
                </span>
              </div>
              <span className="text-xs text-muted-foreground">Punkty</span>
            </div>
          </div>
        </div>
      </CardContent>
    </Card>
  );
};
