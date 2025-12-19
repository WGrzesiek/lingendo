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
  IUserActivityItem,
} from "@/features/statistics/types/statistics.types";

// Mock data - statystyki użytkownika
const mockUserStatistics: IUserStatistics = {
  totalPoints: 8420,
  currentStreak: 7,
  finishedDecks: 12,
  createdDecks: 5,
  createdFlashcards: 234,
  enrolledDecks: 8,
  completedSessions: 156,
  accuracy: 87,
};

// Mock data - punkty dzienne (ostatnie 30 dni)
const mockDailyPoints: IUserPointsData[] = Array.from(
  { length: 30 },
  (_, i) => {
    const date = new Date();
    date.setDate(date.getDate() - (29 - i));
    return {
      date: date.toISOString(),
      points: Math.floor(Math.random() * 300) + 50,
    };
  }
);

// Mock data - punkty miesięczne (ostatnie 12 miesięcy)
const mockMonthlyPoints: IUserPointsData[] = Array.from(
  { length: 12 },
  (_, i) => {
    const date = new Date();
    date.setMonth(date.getMonth() - (11 - i));
    return {
      date: date.toISOString(),
      points: Math.floor(Math.random() * 2000) + 500,
    };
  }
);

// Mock data - statystyki sesji
const mockSessionStatistics: ISessionStatistics = {
  totalSessionsStarted: 168,
  totalSessionsFinished: 156,
  totalCorrectAnswers: 2340,
  totalIncorrectAnswers: 350,
  accuracy: 87,
  avgCorrectPerSession: 15,
};

// Mock data - historia aktywności (ostatnie 20)
const mockRecentActivity: IUserActivityItem[] = [
  {
    eventTime: new Date(Date.now() - 1000 * 60 * 30).toISOString(),
    type: "LESSON_COMPLETED",
    title: "Ukończono lekcję",
    subtitle: "Angielski biznesowy w praktyce",
    points: 50,
  },
  {
    eventTime: new Date(Date.now() - 1000 * 60 * 60 * 2).toISOString(),
    type: "LOGIN",
    title: "Seria dni nauki!",
    subtitle: "7 dni nauki z rzędu – nowy rekord!",
    points: 10,
  },
  {
    eventTime: new Date(Date.now() - 1000 * 60 * 60 * 5).toISOString(),
    type: "LESSON_COMPLETED",
    title: "Ukończono lekcję",
    subtitle: "Hiszpański dla podróżników",
    points: 50,
  },
  {
    eventTime: new Date(Date.now() - 1000 * 60 * 60 * 24).toISOString(),
    type: "SESSION_COMPLETED",
    title: "Ukończono kurs",
    subtitle: "Programowanie - terminologia angielska",
    points: 100,
  },
  {
    eventTime: new Date(Date.now() - 1000 * 60 * 60 * 24 * 2).toISOString(),
    type: "LESSON_COMPLETED",
    title: "Ukończono lekcję",
    subtitle: "Niemiecki od podstaw",
    points: 50,
  },
  {
    eventTime: new Date(
      Date.now() - 1000 * 60 * 60 * 24 * 2 - 1000 * 60 * 60 * 3
    ).toISOString(),
    type: "LOGIN",
    title: "Seria dni nauki!",
    subtitle: "6 dni nauki z rzędu – nowy rekord!",
    points: 10,
  },
  {
    eventTime: new Date(Date.now() - 1000 * 60 * 60 * 24 * 3).toISOString(),
    type: "SESSION_STARTED",
    title: "Rozpoczęto nowy kurs",
    subtitle: "Medycyna - słownictwo specjalistyczne",
    points: 0,
  },
  {
    eventTime: new Date(
      Date.now() - 1000 * 60 * 60 * 24 * 3 - 1000 * 60 * 60 * 4
    ).toISOString(),
    type: "LESSON_COMPLETED",
    title: "Ukończono lekcję",
    subtitle: "Francuski - kultura i sztuka",
    points: 50,
  },
  {
    eventTime: new Date(Date.now() - 1000 * 60 * 60 * 24 * 4).toISOString(),
    type: "LESSON_COMPLETED",
    title: "Ukończono lekcję",
    subtitle: "Fizyka i matematyka po angielsku",
    points: 50,
  },
  {
    eventTime: new Date(Date.now() - 1000 * 60 * 60 * 24 * 5).toISOString(),
    type: "LOGIN",
    title: "Seria dni nauki!",
    subtitle: "5 dni nauki z rzędu – nowy rekord!",
    points: 10,
  },
  {
    eventTime: new Date(
      Date.now() - 1000 * 60 * 60 * 24 * 5 - 1000 * 60 * 60 * 2
    ).toISOString(),
    type: "LESSON_COMPLETED",
    title: "Ukończono lekcję",
    subtitle: "Włoski dla początkujących",
    points: 50,
  },
  {
    eventTime: new Date(Date.now() - 1000 * 60 * 60 * 24 * 6).toISOString(),
    type: "SESSION_COMPLETED",
    title: "Ukończono kurs",
    subtitle: "Biznes międzynarodowy - negocjacje",
    points: 100,
  },
  {
    eventTime: new Date(Date.now() - 1000 * 60 * 60 * 24 * 7).toISOString(),
    type: "LESSON_COMPLETED",
    title: "Ukończono lekcję",
    subtitle: "Technologia - AI i Machine Learning",
    points: 50,
  },
  {
    eventTime: new Date(
      Date.now() - 1000 * 60 * 60 * 24 * 7 - 1000 * 60 * 60 * 5
    ).toISOString(),
    type: "LOGIN",
    title: "Seria dni nauki!",
    subtitle: "4 dni nauki z rzędu – nowy rekord!",
    points: 10,
  },
  {
    eventTime: new Date(Date.now() - 1000 * 60 * 60 * 24 * 8).toISOString(),
    type: "LESSON_COMPLETED",
    title: "Ukończono lekcję",
    subtitle: "Angielski biznesowy w praktyce",
    points: 50,
  },
];

/**
 * Strona statystyk użytkownika
 * Dane bazowane na tabelach ClickHouse
 */
const StatisticsPage = () => {
  const router = useRouter();

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
        <StatisticsOverview statistics={mockUserStatistics} />

        {/* Layout - wykresy i aktywność */}
        <div className="grid grid-cols-1 lg:grid-cols-3 gap-6">
          {/* Lewa kolumna - wykres punktów */}
          <div className="lg:col-span-2 space-y-6">
            <PointsChart
              dailyPoints={mockDailyPoints}
              monthlyPoints={mockMonthlyPoints}
            />
          </div>

          {/* Prawa kolumna - statystyki sesji */}
          <div className="lg:col-span-1">
            <SessionStatistics statistics={mockSessionStatistics} />
          </div>
        </div>

        {/* Historia aktywności */}
        <ActivityHistory activities={mockRecentActivity} />
      </div>
    </div>
  );
};

export default StatisticsPage;
