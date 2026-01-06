import { Card, CardContent, CardHeader, CardTitle } from "@/components/ui/card";
import { Progress } from "@/components/ui/progress";
import { Target, CheckCircle2, XCircle, TrendingUp } from "lucide-react";
import { ISessionStatistics } from "@/features/statistics/types/statistics.types";

interface SessionStatisticsProps {
  statistics: ISessionStatistics;
}

/**
 * Statystyki sesji nauki - odpowiedzi, celność
 */
export const SessionStatistics = ({ statistics }: SessionStatisticsProps) => {
  const totalAnswers = statistics.totalCorrectAnswers + statistics.totalIncorrectAnswers;

  return (
    <Card>
      <CardHeader>
        <CardTitle className="flex items-center gap-2">
          <Target className="w-5 h-5" />
          Statystyki nauki
        </CardTitle>
        <p className="text-sm text-muted-foreground mt-1">
          Twoje wyniki w sesjach nauki
        </p>
      </CardHeader>
      <CardContent className="space-y-6">
        {/* Celność */}
        <div className="space-y-2">
          <div className="flex items-center justify-between">
            <span className="text-sm font-medium">Celność odpowiedzi</span>
            <span className="text-2xl font-bold text-primary">
              {statistics.accuracy}%
            </span>
          </div>
          <Progress value={statistics.accuracy} className="h-3" />
          <p className="text-xs text-muted-foreground">
            {statistics.totalCorrectAnswers} poprawnych z {totalAnswers}{" "}
            odpowiedzi
          </p>
        </div>

        {/* Grid ze statystykami */}
        <div className="grid grid-cols-2 gap-4">
          {/* Poprawne odpowiedzi */}
          <div className="p-4 rounded-lg border bg-green-500/5 border-green-500/20">
            <div className="flex items-center gap-2 mb-2">
              <div className="w-8 h-8 rounded-full bg-green-500/10 flex items-center justify-center">
                <CheckCircle2 className="w-4 h-4 text-green-500" />
              </div>
              <span className="text-sm font-medium">Poprawne</span>
            </div>
            <p className="text-2xl font-bold">
              {statistics.totalCorrectAnswers.toLocaleString("pl-PL")}
            </p>
            <p className="text-xs text-muted-foreground mt-1">odpowiedzi</p>
          </div>

          {/* Niepoprawne odpowiedzi */}
          <div className="p-4 rounded-lg border bg-red-500/5 border-red-500/20">
            <div className="flex items-center gap-2 mb-2">
              <div className="w-8 h-8 rounded-full bg-red-500/10 flex items-center justify-center">
                <XCircle className="w-4 h-4 text-red-500" />
              </div>
              <span className="text-sm font-medium">Niepoprawne</span>
            </div>
            <p className="text-2xl font-bold">
              {statistics.totalIncorrectAnswers.toLocaleString("pl-PL")}
            </p>
            <p className="text-xs text-muted-foreground mt-1">odpowiedzi</p>
          </div>
        </div>

        {/* Dodatkowe info */}
        <div className="grid grid-cols-2 gap-4 pt-4 border-t">
          <div className="text-center">
            <div className="flex items-center justify-center w-10 h-10 mx-auto mb-2 rounded-full bg-primary/10">
              <TrendingUp className="w-5 h-5 text-primary" />
            </div>
            <p className="text-xs text-muted-foreground mb-1">
              Ukończone sesje
            </p>
            <p className="text-lg font-bold">
              {statistics.totalSessionsFinished}
            </p>
          </div>

          <div className="text-center">
            <div className="flex items-center justify-center w-10 h-10 mx-auto mb-2 rounded-full bg-primary/10">
              <Target className="w-5 h-5 text-primary" />
            </div>
            <p className="text-xs text-muted-foreground mb-1">
              Średnia na sesję
            </p>
            <p className="text-lg font-bold">
              {Math.round(statistics.avgCorrectPerSession)}
            </p>
          </div>
        </div>
      </CardContent>
    </Card>
  );
};
