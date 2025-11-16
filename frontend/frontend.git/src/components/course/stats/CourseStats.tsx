import { Card } from "@/components/ui/card";
import { BookOpen, CheckCircle, Clock, Target } from "lucide-react";

interface CourseStatsProps {
  course: {
    totalWords: number;
    completedSessions: number;
    totalSessions: number;
    wordsToReview: number;
    nextReviewDate?: string;
  };
}

/**
 * Statystyki kursu - postęp, słówka do powtórki, następna sesja
 */
export const CourseStats = ({ course }: CourseStatsProps) => {
  const stats = [
    {
      icon: BookOpen,
      label: "Łączna liczba słówek",
      value: course.totalWords,
      color: "text-blue-600",
      bgColor: "bg-blue-500/10",
    },
    {
      icon: CheckCircle,
      label: "Ukończone sesje",
      value: `${course.completedSessions}/${course.totalSessions}`,
      color: "text-green-600",
      bgColor: "bg-green-500/10",
    },
    {
      icon: Target,
      label: "Słówka do powtórki",
      value: course.wordsToReview,
      color: "text-orange-600",
      bgColor: "bg-orange-500/10",
    },
    {
      icon: Clock,
      label: "Następna powtórka",
      value: course.nextReviewDate
        ? new Date(course.nextReviewDate).toLocaleDateString("pl-PL")
        : "Brak",
      color: "text-purple-600",
      bgColor: "bg-purple-500/10",
    },
  ];

  return (
    <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-4">
      {stats.map((stat, index) => {
        const Icon = stat.icon;
        return (
          <Card key={index} className="p-4">
            <div className="flex items-center gap-3">
              <div className={`p-3 ${stat.bgColor} rounded-lg`}>
                <Icon className={`w-5 h-5 ${stat.color}`} />
              </div>
              <div>
                <p className="text-sm text-muted-foreground">{stat.label}</p>
                <p className="text-2xl font-bold">{stat.value}</p>
              </div>
            </div>
          </Card>
        );
      })}
    </div>
  );
};
