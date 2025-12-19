import { Card, CardContent, CardHeader, CardTitle } from "@/components/ui/card";
import { Progress } from "@/components/ui/progress";
import {
  Target,
  CheckCircle2,
  XCircle,
  Clock,
  TrendingUp,
  Timer,
  Zap,
} from "lucide-react";
import { ICourseStudyStatistics } from "@/features/course/types/course-statistics.types";
import {FlashcardAnswersStats} from "@/features/course/types/stats.types";

interface CourseStudyStatsProps {
  statistics: FlashcardAnswersStats;
}

/**
 * Komponent wyświetlający statystyki nauki dla konkretnego kursu
 */
export const CourseStudyStats = ({ statistics}: CourseStudyStatsProps) => {

  const formatDurationMs = (ms: number) => {
    if (!Number.isFinite(ms) || ms <= 0) return "0.0s";
    const seconds = ms / 1000;
    if (seconds < 60) return `${seconds.toFixed(1)}s`;
    const minutes = Math.floor(seconds / 60);
    const secs = Math.floor(seconds % 60);
    return `${minutes}m ${secs}s`;
  };

  const formatStudyTimeMs = (ms: number) => {
    if (!Number.isFinite(ms) || ms <= 0) return "0 min";
    const totalMinutes = Math.floor(ms / 60000);
    if (totalMinutes < 60) return `${totalMinutes} min`;
    const hours = Math.floor(totalMinutes / 60);
    const mins = totalMinutes % 60;
    return `${hours}h ${mins}m`;
  };

  const formatUnixSecondsToPlDate = (unixSeconds?: number | null) => {
    if (!unixSeconds) return "Brak";
    return new Date(unixSeconds * 1000).toLocaleDateString("pl-PL");
  };

  return (
    <div className="space-y-6">
      <Card>
        <CardHeader>
          <CardTitle className="flex items-center gap-2">
            <Target className="w-5 h-5" />
            Celność odpowiedzi
          </CardTitle>
        </CardHeader>
        <CardContent className="space-y-4">
          <div className="text-center">
            <p className="text-5xl font-bold text-primary mb-2">
              {statistics.accuracy}%
            </p>
            <Progress value={statistics.accuracy} className="h-3 mb-2" />
            <p className="text-sm text-muted-foreground">
              {statistics.correctAnswers} poprawnych z {statistics.totalAnswers}{" "}
              odpowiedzi
            </p>
          </div>
        </CardContent>
      </Card>

      <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
        <Card className="border-green-500/20 bg-green-500/5">
          <CardContent className="p-6">
            <div className="flex items-center gap-4">
              <div className="w-12 h-12 rounded-full bg-green-500/10 flex items-center justify-center flex-shrink-0">
                <CheckCircle2 className="w-6 h-6 text-green-500" />
              </div>
              <div>
                <p className="text-sm text-muted-foreground">
                  Poprawne odpowiedzi
                </p>
                <p className="text-2xl font-bold">
                  {statistics.correctAnswers}
                </p>
              </div>
            </div>
          </CardContent>
        </Card>

        <Card className="border-red-500/20 bg-red-500/5">
          <CardContent className="p-6">
            <div className="flex items-center gap-4">
              <div className="w-12 h-12 rounded-full bg-red-500/10 flex items-center justify-center flex-shrink-0">
                <XCircle className="w-6 h-6 text-red-500" />
              </div>
              <div>
                <p className="text-sm text-muted-foreground">
                  Niepoprawne odpowiedzi
                </p>
                <p className="text-2xl font-bold">
                  {statistics.incorrectAnswers}
                </p>
              </div>
            </div>
          </CardContent>
        </Card>

        <Card className="border-blue-500/20 bg-blue-500/5">
          <CardContent className="p-6">
            <div className="flex items-center gap-4">
              <div className="w-12 h-12 rounded-full bg-blue-500/10 flex items-center justify-center flex-shrink-0">
                <Clock className="w-6 h-6 text-blue-500" />
              </div>
              <div>
                <p className="text-sm text-muted-foreground">
                  Średni czas odpowiedzi
                </p>
                <p className="text-2xl font-bold">
                  {formatDurationMs(statistics.averageResponseTime)}
                </p>

              </div>
            </div>
          </CardContent>
        </Card>

        <Card className="border-purple-500/20 bg-purple-500/5">
          <CardContent className="p-6">
            <div className="flex items-center gap-4">
              <div className="w-12 h-12 rounded-full bg-purple-500/10 flex items-center justify-center flex-shrink-0">
                <TrendingUp className="w-6 h-6 text-purple-500" />
              </div>
              <div>
                <p className="text-sm text-muted-foreground">Odpowiedzi poniżej 30 sekund</p>
                <p className="text-2xl font-bold">
                  {statistics.until30SecAnswers}
                </p>
              </div>
            </div>
          </CardContent>
        </Card>
      </div>

      <Card>
        <CardHeader>
          <CardTitle className="flex items-center gap-2">
            <Timer className="w-5 h-5" />
            Czasy odpowiedzi
          </CardTitle>
        </CardHeader>
        <CardContent>
          <div className="grid grid-cols-3 gap-4">
            <div className="text-center p-3 rounded-lg bg-muted">
              <div className="flex items-center justify-center w-10 h-10 mx-auto mb-2 rounded-full bg-green-500/10">
                <Zap className="w-5 h-5 text-green-500" />
              </div>
              <p className="text-xs text-muted-foreground mb-1">Najszybsza</p>
              <p className="text-lg font-bold">
                {formatDurationMs(statistics.fastestResponse)}
              </p>

            </div>

            <div className="text-center p-3 rounded-lg bg-muted">
              <div className="flex items-center justify-center w-10 h-10 mx-auto mb-2 rounded-full bg-blue-500/10">
                <Clock className="w-5 h-5 text-blue-500" />
              </div>
              <p className="text-xs text-muted-foreground mb-1">Średnia</p>
              <p className="text-lg font-bold">
                {formatDurationMs(statistics.averageResponseTime)}

              </p>
            </div>

            <div className="text-center p-3 rounded-lg bg-muted">
              <div className="flex items-center justify-center w-10 h-10 mx-auto mb-2 rounded-full bg-orange-500/10">
                <Timer className="w-5 h-5 text-orange-500" />
              </div>
              <p className="text-xs text-muted-foreground mb-1">
                Najwolniejsza
              </p>
              <p className="text-lg font-bold">
                {formatDurationMs(statistics.slowestResponse)}
              </p>

            </div>
          </div>
        </CardContent>
      </Card>

      <Card>
        <CardContent className="p-6">
          <div className="grid grid-cols-2 gap-4 text-center">
            <div>
              <p className="text-sm text-muted-foreground mb-1">
                Całkowity czas nauki
              </p>
              <p className="text-xl font-bold">
                {formatStudyTimeMs(statistics.totalStudyTime)}
              </p>

            </div>
            <div>
              <p className="text-sm text-muted-foreground mb-1">
                Ostatnia sesja
              </p>
              <p className="text-xl font-bold">
                {formatUnixSecondsToPlDate(statistics.lastSessionDate)}
              </p>
            </div>
          </div>
        </CardContent>
      </Card>
    </div>
  );
};
