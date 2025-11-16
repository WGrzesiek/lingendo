import { Card } from "@/components/ui/card";
import { Button } from "@/components/ui/button";
import { Badge } from "@/components/ui/badge";
import { Trophy, Clock, Target, TrendingUp, ArrowRight } from "lucide-react";

interface SessionSummaryProps {
  session: {
    courseTitle: string;
    sessionNumber: number;
    words: Array<{ id: string }>;
  };
  results: Array<{
    wordId: string;
    difficulty: "easy" | "medium" | "hard";
  }>;
  onContinue: () => void;
  onBackToCourse: () => void;
}

/**
 * Podsumowanie ukończonej sesji nauki
 * Pokazuje statystyki i następne kroki
 */
export const SessionSummary = ({
  session,
  results,
  onContinue,
  onBackToCourse,
}: SessionSummaryProps) => {
  const easyCount = results.filter((r) => r.difficulty === "easy").length;
  const mediumCount = results.filter((r) => r.difficulty === "medium").length;
  const hardCount = results.filter((r) => r.difficulty === "hard").length;

  const totalWords = session.words.length;
  const averageScore = Math.round(
    ((easyCount * 100 + mediumCount * 50 + hardCount * 20) / totalWords / 100) *
      100
  );

  const pointsEarned = easyCount * 10 + mediumCount * 7 + hardCount * 5;

  return (
    <div className="min-h-screen bg-background flex items-center justify-center p-4">
      <div className="max-w-2xl w-full space-y-6">
        <div className="text-center space-y-4">
          <div className="flex justify-center">
            <div className="w-20 h-20 bg-primary/10 rounded-full flex items-center justify-center">
              <Trophy className="w-10 h-10 text-primary" />
            </div>
          </div>
          <h1 className="text-4xl font-bold">Gratulacje! 🎉</h1>
          <p className="text-xl text-muted-foreground">
            Ukończyłeś sesję {session.sessionNumber} z kursu &ldquo;
            {session.courseTitle}&rdquo;
          </p>
        </div>

        <Card className="p-8">
          <div className="grid grid-cols-2 md:grid-cols-4 gap-4 mb-8">
            <div className="text-center p-4 bg-accent/50 rounded-lg">
              <Target className="w-6 h-6 mx-auto mb-2 text-primary" />
              <p className="text-2xl font-bold">{totalWords}</p>
              <p className="text-sm text-muted-foreground">Słówek</p>
            </div>

            <div className="text-center p-4 bg-green-500/10 rounded-lg">
              <div className="text-2xl mb-2">😊</div>
              <p className="text-2xl font-bold text-green-600">{easyCount}</p>
              <p className="text-sm text-muted-foreground">Łatwych</p>
            </div>

            <div className="text-center p-4 bg-yellow-500/10 rounded-lg">
              <div className="text-2xl mb-2">🤔</div>
              <p className="text-2xl font-bold text-yellow-600">
                {mediumCount}
              </p>
              <p className="text-sm text-muted-foreground">Średnich</p>
            </div>

            <div className="text-center p-4 bg-red-500/10 rounded-lg">
              <div className="text-2xl mb-2">😰</div>
              <p className="text-2xl font-bold text-red-600">{hardCount}</p>
              <p className="text-sm text-muted-foreground">Trudnych</p>
            </div>
          </div>

          <div className="space-y-4 pt-6 border-t">
            <div className="flex items-center justify-between">
              <div className="flex items-center gap-2">
                <TrendingUp className="w-5 h-5 text-muted-foreground" />
                <span className="text-sm font-medium">Średni wynik</span>
              </div>
              <Badge variant="secondary" className="text-lg">
                {averageScore}%
              </Badge>
            </div>

            <div className="flex items-center justify-between">
              <div className="flex items-center gap-2">
                <Trophy className="w-5 h-5 text-muted-foreground" />
                <span className="text-sm font-medium">Zdobyte punkty</span>
              </div>
              <Badge className="text-lg">+{pointsEarned} pkt</Badge>
            </div>

            <div className="flex items-center justify-between">
              <div className="flex items-center gap-2">
                <Clock className="w-5 h-5 text-muted-foreground" />
                <span className="text-sm font-medium">Następna powtórka</span>
              </div>
              <span className="text-sm text-muted-foreground">
                {hardCount > 0
                  ? "Za 1 minutę (trudne słówka)"
                  : mediumCount > 0
                  ? "Za 10 minut"
                  : "Za 1 dzień"}
              </span>
            </div>
          </div>
        </Card>

        {hardCount > 0 && (
          <Card className="p-6 bg-orange-500/5 border-orange-500/20">
            <div className="flex items-start gap-4">
              <div className="p-2 bg-orange-500/10 rounded-lg">
                <Clock className="w-5 h-5 text-orange-600" />
              </div>
              <div className="flex-1">
                <h3 className="font-semibold mb-1">
                  Masz {hardCount} trudnych słówek do powtórki
                </h3>
                <p className="text-sm text-muted-foreground">
                  Zalecamy powtórzenie ich teraz, aby lepiej je zapamiętać
                </p>
              </div>
            </div>
          </Card>
        )}

        <div className="flex gap-3">
          <Button
            variant="outline"
            size="lg"
            className="flex-1"
            onClick={onBackToCourse}
          >
            Wróć do kursu
          </Button>
          {hardCount > 0 && (
            <Button size="lg" className="flex-1 gap-2" onClick={onContinue}>
              Powtórz trudne słówka
              <ArrowRight className="w-4 h-4" />
            </Button>
          )}
        </div>
      </div>
    </div>
  );
};
