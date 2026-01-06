import { Card, CardContent } from "@/components/ui/card";
import { Button } from "@/components/ui/button";
import {
  DropdownMenu,
  DropdownMenuContent,
  DropdownMenuItem,
  DropdownMenuSeparator,
  DropdownMenuTrigger,
} from "@/components/ui/dropdown-menu";
import { IFriend } from "@/features/friends/types/friend.types";
import {
  Trophy,
  TrendingUp,
  MoreVertical,
  MessageSquare,
  UserMinus,
  Ban,
  Eye,
  Flame,
} from "lucide-react";
import { cn } from "@/lib/utils";

interface FriendCardProps {
  friend: IFriend;
  onViewDetails?: (userId: string) => void;
  onRemove?: (userId: string) => void;
  onBlock?: (userId: string) => void;
}

/**
 * Komponent karty znajomego z informacjami o rankingu i punktach
 */
export const FriendCard = ({
  friend,
  onViewDetails,
  onRemove,
  onBlock,
}: FriendCardProps) => {
  const getRankColor = (position: number) => {
    if (position === 1) return "text-yellow-500";
    if (position === 2) return "text-gray-400";
    if (position === 3) return "text-amber-600";
    return "text-muted-foreground";
  };

  const showTrophy = friend.rankPosition <= 3;

  return (
    <Card className="hover:shadow-md transition-shadow">
      <CardContent className="p-4">
        <div className="flex items-center justify-between gap-4">
          {/* Avatar i nazwa użytkownika */}
          <div
            className="flex items-center gap-3 min-w-0 flex-1 cursor-pointer"
            onClick={() => onViewDetails?.(friend.userId)}
          >
            <div className="w-12 h-12 rounded-full bg-primary/10 flex items-center justify-center flex-shrink-0">
              <span className="text-lg font-semibold text-primary">
                {friend.username.charAt(0).toUpperCase()}
              </span>
            </div>
            <div className="min-w-0 flex-1">
              <div className="flex items-center gap-2">
                <h3 className="font-semibold text-lg truncate">
                  {friend.username}
                </h3>
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
          <div className="hidden sm:flex items-center gap-6 flex-shrink-0">
            {/* Seria dni */}
            {friend.stats?.streakDays !== undefined &&
              friend.stats.streakDays > 0 && (
                <div className="flex flex-col items-center">
                  <div className="flex items-center gap-1">
                    <Flame className="w-4 h-4 text-orange-500" />
                    <span className="text-lg font-bold text-orange-500">
                      {friend.stats.streakDays}
                    </span>
                  </div>
                  <span className="text-xs text-muted-foreground">Seria</span>
                </div>
              )}

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
                    "text-xl font-bold",
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
                <span className="text-xl font-bold text-green-500">
                  {friend.totalPoints.toLocaleString("pl-PL")}
                </span>
              </div>
              <span className="text-xs text-muted-foreground">Punkty</span>
            </div>
          </div>

          {/* Menu akcji */}
          <DropdownMenu>
            <DropdownMenuTrigger asChild>
              <Button variant="ghost" size="icon">
                <MoreVertical className="w-4 h-4" />
              </Button>
            </DropdownMenuTrigger>
            <DropdownMenuContent align="end">
              <DropdownMenuItem onClick={() => onViewDetails?.(friend.userId)}>
                <Eye className="w-4 h-4 mr-2" />
                Zobacz profil
              </DropdownMenuItem>
              <DropdownMenuSeparator />
              <DropdownMenuItem onClick={() => onRemove?.(friend.userId)}>
                <UserMinus className="w-4 h-4 mr-2" />
                Usuń znajomego
              </DropdownMenuItem>
              <DropdownMenuItem
                onClick={() => onBlock?.(friend.userId)}
                className="text-destructive"
              >
                <Ban className="w-4 h-4 mr-2" />
                Zablokuj
              </DropdownMenuItem>
            </DropdownMenuContent>
          </DropdownMenu>
        </div>
      </CardContent>
    </Card>
  );
};
