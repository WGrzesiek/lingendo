"use client";

import { BookOpen, Target, TrendingUp, Award } from "lucide-react";
import { StudentStatsCard } from "./StudentStatsCard";
import { useStudentStatistics } from "../hooks/useStudentStatistics";

/**
 * Siatka statystyk dla dashboardu ucznia
 */
export const StudentStatsGrid = () => {
  const { data, isLoading, error } = useStudentStatistics();

  if (isLoading) {
    return (
      <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-6">
        {[1, 2, 3, 4].map((i) => (
          <div key={i} className="h-32 rounded-xl bg-muted animate-pulse" />
        ))}
      </div>
    );
  }

  if (error || !data) {
    return (
      <p className="text-destructive text-sm">
        Nie udało się pobrać statystyk.
      </p>
    );
  }

  const stats = [
    {
      title: "Aktywne kursy",
      value: data.activeDecks,
      description: "W trakcie nauki",
      icon: BookOpen,
    },
    {
      title: "Ukończone lekcje",
      value: data.completedLessonsThisMonth,
      description: "W tym miesiącu",
      icon: Target,
    },
    {
      title: "Seria dni nauki",
      value: data.streakDays,
      description: "Dni z rzędu",
      icon: TrendingUp,
    },
    {
      title: "Zdobyte punkty",
      value: data.totalPoints.toLocaleString("pl-PL"),
      description: "Łącznie",
      icon: Award,
      trend: {
        value: `+${data.pointsThisWeek} w tym tygodniu`,
        isPositive: data.pointsThisWeek >= 0,
      },
    },
  ];

  return (
    <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-6">
      {stats.map((stat, index) => (
        <StudentStatsCard key={index} {...stat} />
      ))}
    </div>
  );
};
