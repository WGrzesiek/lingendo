import { Card, CardContent, CardHeader, CardTitle } from "@/components/ui/card";
import { Trophy, Target, TrendingUp } from "lucide-react";
import { ILeaderboardEntry } from "@/features/leaderboard/types/leaderboard.types";

interface CurrentUserPositionProps {
  currentUser: ILeaderboardEntry;
  userAbove?: ILeaderboardEntry;
}

/**
 * Komponent pokazujący pozycję zalogowanego użytkownika w rankingu
 * Wzorowany na mini rankingu z dashboard
 */
export const CurrentUserPosition = ({
  currentUser,
  userAbove,
}: CurrentUserPositionProps) => {
  const pointsToNext = userAbove ? userAbove.points - currentUser.points : 0;

  const getRankIcon = (rank: number) => {
    switch (rank) {
      case 1:
        return <Trophy className="w-6 h-6 text-yellow-500" />;
      case 2:
        return <Trophy className="w-6 h-6 text-gray-400" />;
      case 3:
        return <Trophy className="w-6 h-6 text-amber-600" />;
      default:
        return (
          <div className="w-10 h-10 rounded-full bg-primary/10 flex items-center justify-center text-base font-bold text-primary">
            {rank}
          </div>
        );
    }
  };

  return (
    <Card className="border-primary shadow-md sticky top-6">
      <CardHeader>
        <CardTitle className="flex items-center gap-2">
          <Target className="w-5 h-5" />
          Twoja pozycja
        </CardTitle>
      </CardHeader>
      <CardContent className="space-y-4">
        {/* Główna pozycja użytkownika */}
        <div className="flex items-center gap-4 p-4 rounded-lg bg-primary/10 border border-primary">
          <div className="flex items-center justify-center w-12">
            {getRankIcon(currentUser.rank)}
          </div>

          <div className="flex-1">
            <h3 className="font-semibold text-lg text-primary">
              {currentUser.displayName}
            </h3>
            <p className="text-sm text-muted-foreground">
              {currentUser.completedCourses} ukończonych kursów
            </p>
          </div>

          <div className="text-right">
            <p className="font-bold text-2xl text-primary">
              {currentUser.points}
            </p>
            <p className="text-xs text-muted-foreground">punktów</p>
          </div>
        </div>

        {/* Statystyki */}
        <div className="grid grid-cols-2 gap-3 pt-3 border-t">
          <div className="text-center p-3 rounded-lg bg-muted/50">
            <div className="flex items-center justify-center w-10 h-10 mx-auto mb-2 rounded-full bg-info/10">
              <Target className="w-5 h-5 text-info" />
            </div>
            <p className="text-xs text-muted-foreground mb-1">
              Do następnego miejsca
            </p>
            <p className="font-bold">
              {pointsToNext > 0 ? `${pointsToNext} pkt` : "🏆 Lider!"}
            </p>
          </div>

          <div className="text-center p-3 rounded-lg bg-muted/50">
            <div className="flex items-center justify-center w-10 h-10 mx-auto mb-2 rounded-full bg-green-500/10">
              <TrendingUp className="w-5 h-5 text-green-500" />
            </div>
            <p className="text-xs text-muted-foreground mb-1">Ranking</p>
            <p className="font-bold">#{currentUser.rank}</p>
          </div>
        </div>

        {/* Użytkownik powyżej */}
        {userAbove && (
          <div className="pt-3 border-t">
            <p className="text-xs text-muted-foreground mb-2">
              Użytkownik powyżej:
            </p>
            <div className="flex items-center gap-3 p-3 rounded-lg border bg-background">
              <div className="flex items-center justify-center w-8">
                {getRankIcon(userAbove.rank)}
              </div>
              <div className="flex-1 min-w-0">
                <p className="font-semibold text-sm truncate">
                  {userAbove.displayName}
                </p>
                <p className="text-xs text-muted-foreground">
                  {userAbove.points} pkt
                </p>
              </div>
            </div>
          </div>
        )}
      </CardContent>
    </Card>
  );
};
