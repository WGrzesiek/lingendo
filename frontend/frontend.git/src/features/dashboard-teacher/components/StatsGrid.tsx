"use client";

import { Users, BookOpen, UsersRound, TrendingUp } from "lucide-react";
import { StatsCard } from "./StatsCard";
import { useTeacherStats } from "../hooks";
import { Skeleton } from "@/components/ui/skeleton";

/**
 * Siatka statystyk dla dashboardu nauczyciela
 */
export const StatsGrid = () => {
  const { data: stats, isLoading, error } = useTeacherStats();

  if (isLoading) {
    return (
      <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-6">
        {[1, 2, 3, 4].map((i) => (
          <Skeleton key={i} className="h-32 w-full" />
        ))}
      </div>
    );
  }

  if (error || !stats) {
    return (
      <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-6">
        {[1, 2, 3, 4].map((i) => (
          <StatsCard
            key={i}
            title="--"
            value={0}
            description="Błąd ładowania"
            icon={Users}
          />
        ))}
      </div>
    );
  }

  const totalStudents =
    stats.activeStudents + stats.invitedStudents + stats.blockedStudents;

  const statsData = [
    {
      title: "Wszyscy uczniowie",
      value: totalStudents,
      description: "Łącznie przypisanych",
      icon: Users,
      trend:
        stats.activeStudents > 0
          ? { value: `${stats.activeStudents} aktywnych`, isPositive: true }
          : undefined,
    },
    {
      title: "Aktywne zaproszenia",
      value: stats.activeInvitations,
      description: "Oczekujące na użycie",
      icon: BookOpen,
      trend:
        stats.totalInvitations > 0
          ? { value: `${stats.totalInvitations} łącznie`, isPositive: true }
          : undefined,
    },
    {
      title: "Zaproszeni",
      value: stats.invitedStudents,
      description: "Oczekujący uczniowie",
      icon: UsersRound,
      trend: undefined,
    },
    {
      title: "Zablokowanych",
      value: stats.blockedStudents,
      description: "Uczniów zablokowanych",
      icon: TrendingUp,
      trend:
        stats.blockedStudents === 0
          ? { value: "Brak zablokowanych", isPositive: true }
          : {
              value: `${stats.blockedStudents} zablokowanych`,
              isPositive: false,
            },
    },
  ];

  return (
    <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-6">
      {statsData.map((stat, index) => (
        <StatsCard key={index} {...stat} />
      ))}
    </div>
  );
};
