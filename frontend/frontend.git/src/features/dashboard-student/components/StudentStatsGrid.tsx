import { BookOpen, Target, TrendingUp, Award } from "lucide-react";
import { StudentStatsCard } from "./StudentStatsCard";

/**
 * Siatka statystyk dla dashboardu ucznia
 * Wyświetla zmockowane dane o postępach ucznia
 */
export const StudentStatsGrid = () => {
  const stats = [
    {
      title: "Aktywne kursy",
      value: 5,
      description: "W trakcie nauki",
      icon: BookOpen,
      trend: {
        value: "+2 nowe kursy",
        isPositive: true,
      },
    },
    {
      title: "Ukończone lekcje",
      value: 127,
      description: "W tym miesiącu",
      icon: Target,
      trend: {
        value: "+23% od ostatniego miesiąca",
        isPositive: true,
      },
    },
    {
      title: "Seria dni nauki",
      value: 14,
      description: "Dni z rzędu",
      icon: TrendingUp,
      trend: {
        value: "Rekord osobisty!",
        isPositive: true,
      },
    },
    {
      title: "Zdobyte punkty",
      value: "3,420",
      description: "Łącznie",
      icon: Award,
      trend: {
        value: "+240 w tym tygodniu",
        isPositive: true,
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
