"use client";

import { useRouter } from "next/navigation";
import { Button } from "@/components/ui/button";
import { BarChart3, FileDown } from "lucide-react";
import { StatisticsOverview } from "@/features/statistics/components/StatisticsOverview";
import { PointsChart } from "@/features/statistics/components/PointsChart";
import { SessionStatistics } from "@/features/statistics/components/SessionStatistics";
import { ActivityHistory } from "@/features/statistics/components/ActivityHistory";
import {
  IUserStatistics,
  IUserPointsData,
  ISessionStatistics,
} from "@/features/statistics/types/statistics.types";
import { useStatistics } from "@/features/statistics/hooks/useStatistics";
// import { useUserActivity } from "@/features/statistics/hooks/useUserActivity";
import { useMemo } from "react";
import { useStudentActivity } from "@/features/dashboard-student/hooks/useStudentActivity";

/**
 * Strona statystyk użytkownika
 * Dane bazowane na tabelach ClickHouse
 */
const StatisticsPage = () => {
  const router = useRouter();
  const { data: statsData, isLoading: isStatsLoading } = useStatistics();
  const { data: activityData, isLoading: isActivityLoading } =
    useStudentActivity();

  // Konwersja danych z API do formatu komponentów
  const userStatistics: IUserStatistics | undefined = useMemo(() => {
    if (!statsData) return undefined;

    const accuracy =
      statsData.flashcardsAnswered > 0
        ? Math.round(
            (statsData.flashcardsAnsweredCorrectly /
              statsData.flashcardsAnswered) *
              100
          )
        : 0;

    return {
      totalPoints: statsData.totalPoints,
      currentStreak: statsData.streak,
      finishedDecks: statsData.completedDecks,
      createdDecks: statsData.createdDecks,
      createdFlashcards: statsData.flashcardsCreated,
      enrolledDecks: statsData.enrolledDecks,
      completedSessions: statsData.sessionsCompleted,
      accuracy,
    };
  }, [statsData]);

  // Konwersja pointsPerMonth na tablicę do wykresu
  const monthlyPoints: IUserPointsData[] = useMemo(() => {
    if (!statsData?.pointsPerMonth) return [];

    return Object.entries(statsData.pointsPerMonth)
      .map(([yearMonth, points]) => {
        // Format: YYYYMM -> konwersja na datę
        const year = parseInt(yearMonth.substring(0, 4));
        const month = parseInt(yearMonth.substring(4, 6)) - 1;
        const date = new Date(year, month, 1);

        return {
          date: date.toISOString(),
          points,
        };
      })
      .sort((a, b) => new Date(a.date).getTime() - new Date(b.date).getTime());
  }, [statsData]);

  // Statystyki sesji
  const sessionStatistics: ISessionStatistics | undefined = useMemo(() => {
    if (!statsData) return undefined;

    const accuracy =
      statsData.flashcardsAnswered > 0
        ? Math.round(
            (statsData.flashcardsAnsweredCorrectly /
              statsData.flashcardsAnswered) *
              100
          )
        : 0;

    return {
      totalSessionsStarted: statsData.sessionsCompleted, // API nie zwraca rozpoczętych
      totalSessionsFinished: statsData.sessionsCompleted,
      totalCorrectAnswers: statsData.flashcardsAnsweredCorrectly,
      totalIncorrectAnswers:
        statsData.flashcardsAnswered - statsData.flashcardsAnsweredCorrectly,
      accuracy,
      avgCorrectPerSession: statsData.averageAnswersPerSession,
    };
  }, [statsData]);

  const isLoading = isStatsLoading || isActivityLoading;

  if (isLoading) {
    return (
      <div className="min-h-screen flex items-center justify-center bg-background">
        <div className="text-center">
          <div className="w-16 h-16 border-4 border-primary border-t-transparent rounded-full animate-spin mx-auto mb-4" />
          <p className="text-muted-foreground">Ładowanie statystyk...</p>
        </div>
      </div>
    );
  }

  return (
    <div className="min-h-screen bg-background">
      <div className="container mx-auto p-6 lg:p-8 space-y-8">
        {/* Header */}
        <div className="flex items-start justify-between gap-4">
          <div className="space-y-1 flex-1">
            <h1 className="text-4xl font-bold flex items-center gap-3">
              <BarChart3 className="w-10 h-10" />
              Moje statystyki
            </h1>
            <p className="text-muted-foreground text-lg">
              Śledź swoje postępy i osiągnięcia w nauce
            </p>
          </div>
          <Button
            size="lg"
            onClick={() => router.push("/statistics/export")}
            className="gap-2"
          >
            <FileDown className="w-5 h-5" />
            Generuj PDF
          </Button>
        </div>

        {/* Główne statystyki */}
        {userStatistics && <StatisticsOverview statistics={userStatistics} />}

        {/* Layout - wykresy i aktywność */}
        <div className="grid grid-cols-1 lg:grid-cols-3 gap-6">
          {/* Lewa kolumna - wykres punktów */}
          <div className="lg:col-span-2 space-y-6">
            <PointsChart monthlyPoints={monthlyPoints} />
          </div>

          {/* Prawa kolumna - statystyki sesji */}
          <div className="lg:col-span-1">
            {sessionStatistics && (
              <SessionStatistics statistics={sessionStatistics} />
            )}
          </div>
        </div>

        {/* Historia aktywności */}
        {activityData && <ActivityHistory activities={activityData} />}
      </div>
    </div>
  );
};

export default StatisticsPage;
