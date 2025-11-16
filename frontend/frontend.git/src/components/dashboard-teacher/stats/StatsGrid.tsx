import { Users, BookOpen, GraduationCap, TrendingUp } from "lucide-react";
import { StatsCard } from "./StatsCard";

/**
 * Siatka statystyk dla dashboardu nauczyciela
 * Wyświetla zmockowane dane o uczniach, kursach i postępach
 */
export const StatsGrid = () => {
  const stats = [
    {
      title: "Aktywni uczniowie",
      value: 124,
      description: "W tym miesiącu",
      icon: Users,
      trend: {
        value: "+12% od ostatniego miesiąca",
        isPositive: true,
      },
    },
    {
      title: "Aktywne kursy",
      value: 8,
      description: "Prowadzone przez Ciebie",
      icon: BookOpen,
      trend: {
        value: "+2 nowe kursy",
        isPositive: true,
      },
    },
    {
      title: "Ukończone lekcje",
      value: "2,547",
      description: "Łącznie przez uczniów",
      icon: GraduationCap,
      trend: {
        value: "+18% od ostatniego miesiąca",
        isPositive: true,
      },
    },
    {
      title: "Średni postęp",
      value: "78%",
      description: "Wszystkich uczniów",
      icon: TrendingUp,
      trend: {
        value: "+5% od ostatniego miesiąca",
        isPositive: true,
      },
    },
  ];

  return (
    <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-6">
      {stats.map((stat, index) => (
        <StatsCard key={index} {...stat} />
      ))}
    </div>
  );
};
