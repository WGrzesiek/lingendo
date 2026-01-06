"use client";

import { Card, CardContent } from "@/components/ui/card";
import {
  Trophy,
  Users,
  BookOpen,
  Layers,
  Target,
  Zap,
  CheckCircle,
  UserCheck,
} from "lucide-react";
import type { TeacherStatsDetails } from "@/features/dashboard-teacher/types";

interface TeacherStatisticsOverviewProps {
  statistics: TeacherStatsDetails;
}

/**
 * Karty z głównymi statystykami nauczyciela
 */
export const TeacherStatisticsOverview = ({
  statistics,
}: TeacherStatisticsOverviewProps) => {
  const stats = [
    {
      title: "Punkty uczniów",
      value: statistics.totalStudentPoints.toLocaleString("pl-PL"),
      icon: Trophy,
      iconColor: "text-yellow-500",
      bgColor: "bg-yellow-500/10",
      description: "Suma zdobytych punktów",
    },
    {
      title: "Aktywni uczniowie",
      value: statistics.activeStudents,
      icon: UserCheck,
      iconColor: "text-green-500",
      bgColor: "bg-green-500/10",
      description: `z ${statistics.totalStudents} wszystkich`,
    },
    {
      title: "Ukończone sesje",
      value: statistics.totalStudentSessions.toLocaleString("pl-PL"),
      icon: Zap,
      iconColor: "text-cyan-500",
      bgColor: "bg-cyan-500/10",
      description: "Wszystkich uczniów",
    },
    {
      title: "Średnia dokładność",
      value: `${statistics.averageAccuracy}%`,
      icon: Target,
      iconColor: "text-pink-500",
      bgColor: "bg-pink-500/10",
      description: "Poprawne odpowiedzi",
    },
    {
      title: "Utworzone kursy",
      value: statistics.createdDecks,
      icon: BookOpen,
      iconColor: "text-blue-500",
      bgColor: "bg-blue-500/10",
      description: "Twoje materiały",
    },
    {
      title: "Utworzone fiszki",
      value: statistics.createdFlashcards.toLocaleString("pl-PL"),
      icon: Layers,
      iconColor: "text-purple-500",
      bgColor: "bg-purple-500/10",
      description: "Dodane słówka",
    },
    {
      title: "Poprawne odpowiedzi",
      value: statistics.totalCorrectAnswers.toLocaleString("pl-PL"),
      icon: CheckCircle,
      iconColor: "text-emerald-500",
      bgColor: "bg-emerald-500/10",
      description: "Wszystkich uczniów",
    },
    {
      title: "Wszystkich uczniów",
      value: statistics.totalStudents,
      icon: Users,
      iconColor: "text-indigo-500",
      bgColor: "bg-indigo-500/10",
      description: "Przypisanych do Ciebie",
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
