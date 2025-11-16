import { Card } from "@/components/ui/card";
import { Button } from "@/components/ui/button";
import { TrendingUp, Trophy, Target, Zap } from "lucide-react";

interface LeaderboardEntry {
  rank: number;
  name: string;
  points: number;
  coursesCompleted: number;
  isCurrentUser?: boolean;
}

/**
 * Ranking uczniów (leaderboard)
 * Pokazuje jak uczeń wypada na tle innych użytkowników
 */
export const Leaderboard = () => {
  const leaderboard: LeaderboardEntry[] = [
    {
      rank: 1,
      name: "Anna Kowalska",
      points: 12450,
      coursesCompleted: 15,
    },
    {
      rank: 2,
      name: "Jan Nowak",
      points: 11200,
      coursesCompleted: 13,
    },
    {
      rank: 3,
      name: "Maria Wiśniewska",
      points: 10800,
      coursesCompleted: 12,
    },
    {
      rank: 15,
      name: "Ty",
      points: 3420,
      coursesCompleted: 5,
      isCurrentUser: true,
    },
  ];

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
        <Button variant="outline" size="sm">
          <TrendingUp className="w-4 h-4 mr-2" />
          Zobacz pełny ranking
        </Button>
      </div>

      <div className="space-y-3 mb-6">
        {leaderboard.map((entry) => (
          <div
            key={entry.rank}
            className={`flex items-center gap-4 p-3 rounded-lg border transition-all ${
              entry.isCurrentUser
                ? "bg-primary/10 border-primary"
                : "hover:bg-accent/50"
            }`}
          >
            <div className="flex items-center justify-center w-10">
              {getRankIcon(entry.rank)}
            </div>

            <div className="flex-1">
              <h3
                className={`font-semibold ${
                  entry.isCurrentUser ? "text-primary" : ""
                }`}
              >
                {entry.name}
              </h3>
              <p className="text-sm text-muted-foreground">
                {entry.coursesCompleted} ukończonych kursów
              </p>
            </div>

            <div className="text-right">
              <p className="font-bold text-lg">
                {entry.points.toLocaleString()}
              </p>
              <p className="text-xs text-muted-foreground">punktów</p>
            </div>
          </div>
        ))}
      </div>

      <div className="grid grid-cols-3 gap-3 pt-4 border-t">
        <div className="text-center">
          <div className="flex items-center justify-center w-10 h-10 mx-auto mb-2 rounded-full bg-info/10">
            <Target className="w-5 h-5 text-info" />
          </div>
          <p className="text-xs text-muted-foreground mb-1">
            Do następnego miejsca
          </p>
          <p className="font-bold">820 pkt</p>
        </div>

        <div className="text-center">
          <div className="flex items-center justify-center w-10 h-10 mx-auto mb-2 rounded-full bg-success/10">
            <Zap className="w-5 h-5 text-success" />
          </div>
          <p className="text-xs text-muted-foreground mb-1">W tym tygodniu</p>
          <p className="font-bold">+240 pkt</p>
        </div>

        <div className="text-center">
          <div className="flex items-center justify-center w-10 h-10 mx-auto mb-2 rounded-full bg-premium/10">
            <TrendingUp className="w-5 h-5 text-premium" />
          </div>
          <p className="text-xs text-muted-foreground mb-1">Twoja pozycja</p>
          <p className="font-bold">#15</p>
        </div>
      </div>
    </Card>
  );
};
