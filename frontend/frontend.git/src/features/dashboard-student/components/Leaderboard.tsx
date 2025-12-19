import { Card } from "@/components/ui/card";
import { Button } from "@/components/ui/button";
import { TrendingUp, Trophy, Target } from "lucide-react";
import { useLeaderBoardOverview } from "../hooks/useLeaderBoardOverview";
import { useRouter } from "next/navigation";

/**
 * Ranking uczniów (leaderboard)
 * Pokazuje jak uczeń wypada na tle innych użytkowników
 */
export const Leaderboard = () => {
  const router = useRouter();
  const { data, isLoading, isError } = useLeaderBoardOverview();
  if (isLoading) {
    return (
      <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-6">
        {[1, 2, 3, 4].map((i) => (
          <div key={i} className="h-32 rounded-xl bg-muted animate-pulse" />
        ))}
      </div>
    );
  }

  if (isError || !data) {
    return (
      <p className="text-destructive text-sm">
        Nie udało się pobrać statystyk.
      </p>
    );
  }

  const pointsToNext = data.aboveYou
    ? data.aboveYou.points - data.you.points
    : 0;

  const getRankIcon = (rank: number) => {
    switch (rank) {
      case 1:
        return <Trophy className="w-5 h-5 text-achievement" />;
      case 2:
        return <Trophy className="w-5 h-5 text-muted-foreground" />;
      case 3:
        return <Trophy className="w-5 h-5 text-intermediate" />;
      default:
        return (
          <div className="w-8 h-8 rounded-full bg-muted flex items-center justify-center text-sm font-semibold">
            {rank}
          </div>
        );
    }
  };

  return (
    <Card className="p-6">
      <div className="sm:flex items-center justify-between mb-6">
        <div className="pb-4">
          <h2 className="text-2xl font-bold">Ranking</h2>
          <p className="text-sm text-muted-foreground mt-1">
            Porównaj swoje wyniki z innymi użytkownikami
          </p>
        </div>
        <Button
          variant="outline"
          size="sm"
          onClick={() => router.push("/leaderboard")}
        >
          <TrendingUp className="w-4 h-4 mr-2" />
          Zobacz pełny ranking
        </Button>
      </div>

      <div className="space-y-3 mb-6">
        {data.top3.map((user) => (
          <div
            key={user.userId}
            className={`flex items-center gap-4 p-3 rounded-lg border transition-all ${
              user.userId === data.you.userId
                ? "bg-primary/10 border-primary"
                : "bg-background border-border hover:border-primary/50"
            }`}
          >
            <div className="flex items-center justify-center w-10">
              {getRankIcon(user.rank)}
            </div>

            <div className="flex-1">
              <h3
                className={`font-semibold ${
                  user.userId === data.you.userId
                    ? "text-primary"
                    : "text-foreground"
                }`}
              >
                {user.displayName || "Użytkownik"}
                {user.userId === data.you.userId && " (Ty)"}
              </h3>
              <p className="text-sm text-muted-foreground">
                {user.completedCourses} ukończonych kursów
              </p>
            </div>

            <div className="text-right">
              <p className="font-bold text-lg">{user.points}</p>
              <p className="text-xs text-muted-foreground">punktów</p>
            </div>
          </div>
        ))}

        {data.you.rank > 3 && (
          <div className="flex items-center gap-4 p-3 rounded-lg border transition-all bg-primary/10 border-primary">
            <div className="flex items-center justify-center w-10">
              {getRankIcon(data.you.rank)}
            </div>

            <div className="flex-1">
              <h3 className="font-semibold text-primary">
                {data.you.displayName || "Ty"}
              </h3>
              <p className="text-sm text-muted-foreground">
                {data.you.completedCourses} ukończonych kursów
              </p>
            </div>

            <div className="text-right">
              <p className="font-bold text-lg">{data.you.points}</p>
              <p className="text-xs text-muted-foreground">punktów</p>
            </div>
          </div>
        )}
      </div>

      <div className="grid grid-cols-2 gap-3 pt-4 border-t">
        <div className="text-center">
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

        <div className="text-center">
          <div className="flex items-center justify-center w-10 h-10 mx-auto mb-2 rounded-full bg-premium/10">
            <TrendingUp className="w-5 h-5 text-premium" />
          </div>
          <p className="text-xs text-muted-foreground mb-1">Twoja pozycja</p>
          <p className="font-bold">#{data.you.rank}</p>
        </div>
      </div>
    </Card>
  );
};
