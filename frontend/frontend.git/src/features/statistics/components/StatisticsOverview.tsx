import { Card, CardContent} from "@/components/ui/card";
import {
  Trophy,
  Flame,
  BookOpen,
  BookCheck,
  PlusCircle,
  Target,
  TrendingUp,
  Zap,
} from "lucide-react";
import { IUserStatistics } from "@/features/statistics/types/statistics.types";

interface StatisticsOverviewProps {
  statistics: IUserStatistics;
}

/**
 * Karty z głównymi statystykami użytkownika
 */
export const StatisticsOverview = ({ statistics }: StatisticsOverviewProps) => {
  const stats = [
    {
      title: "Całkowite punkty",
      value: statistics.totalPoints.toLocaleString("pl-PL"),
      icon: Trophy,
      iconColor: "text-yellow-500",
      bgColor: "bg-yellow-500/10",
      description: "Zdobyte w sumie",
    },
    {
      title: "Seria dni",
      value: statistics.currentStreak,
      icon: Flame,
      iconColor: "text-orange-500",
      bgColor: "bg-orange-500/10",
      description:
        statistics.currentStreak === 1 ? "dzień z rzędu" : "dni z rzędu",
    },
    {
      title: "Ukończone kursy",
      value: statistics.finishedDecks,
      icon: BookCheck,
      iconColor: "text-green-500",
      bgColor: "bg-green-500/10",
      description: "Kursy zakończone",
    },
    {
      title: "Utworzone kursy",
      value: statistics.createdDecks,
      icon: PlusCircle,
      iconColor: "text-blue-500",
      bgColor: "bg-blue-500/10",
      description: "Twoje kursy",
    },
    {
      title: "Zapisane kursy",
      value: statistics.enrolledDecks,
      icon: BookOpen,
      iconColor: "text-purple-500",
      bgColor: "bg-purple-500/10",
      description: "Aktywne kursy",
    },
    {
      title: "Ukończone sesje",
      value: statistics.completedSessions,
      icon: Zap,
      iconColor: "text-cyan-500",
      bgColor: "bg-cyan-500/10",
      description: "Lekcje zakończone",
    },
    {
      title: "Celność",
      value: `${statistics.accuracy}%`,
      icon: Target,
      iconColor: "text-pink-500",
      bgColor: "bg-pink-500/10",
      description: "Poprawne odpowiedzi",
    },
    {
      title: "Fiszki utworzone",
      value: statistics.createdFlashcards,
      icon: TrendingUp,
      iconColor: "text-indigo-500",
      bgColor: "bg-indigo-500/10",
      description: "Dodane słówka",
    },
  ];

  return (
    <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-4 gap-4">
      {stats.map((stat) => {
        const Icon = stat.icon;
        return (
          <Card key={stat.title} className="hover:shadow-md transition-shadow">
            <CardContent className="p-6">
              <div className="flex items-center gap-4">
                <div
                  className={`w-12 h-12 rounded-lg ${stat.bgColor} flex items-center justify-center flex-shrink-0`}
                >
                  <Icon className={`w-6 h-6 ${stat.iconColor}`} />
                </div>
                <div className="flex-1 min-w-0">
                  <p className="text-sm text-muted-foreground truncate">
                    {stat.title}
                  </p>
                  <p className="text-2xl font-bold">{stat.value}</p>
                  <p className="text-xs text-muted-foreground">
                    {stat.description}
                  </p>
                </div>
              </div>
            </CardContent>
          </Card>
        );
      })}
    </div>
  );
};
