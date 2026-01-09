import { Card } from "@/components/ui/card";
import { BookOpen, CheckCircle, Clock, Frown, Target } from "lucide-react";
import { useCourseProgress } from "@/features/course/hooks/useCourseProgress";
import { Skeleton } from "@/components/ui/skeleton";

// interface dataStatsProps {
//   data: {
//     totalWords: number;
//     completedSessions: number;
//     totalSessions: number;
//     wordsToReview: number;
//     nextReviewDate?: string;
//   };
// }
interface dataStatsProps {
  enrollmentId: string;
}
const WordListSkeleton = () => (
  <div className="space-y-4">
    {[1, 2, 3].map((i) => (
      <div key={i} className="p-4 border rounded-xl space-y-3">
        <div className="flex justify-between">
          <div className="space-y-2 w-2/3">
            <Skeleton className="h-6 w-1/3" />
            <Skeleton className="h-4 w-full" />
          </div>
          <Skeleton className="h-9 w-24 rounded-md" />
        </div>
        <Skeleton className="h-2 w-full rounded-full" />
      </div>
    ))}
  </div>
);

/**
 * Statystyki kursu - postęp, słówka do powtórki, następna sesja
 */
export const CourseStats = ({ enrollmentId }: dataStatsProps) => {
  const { data, isLoading, isError } = useCourseProgress(enrollmentId);
  if (isLoading) return <WordListSkeleton />;

  if (isError || !data) {
    return (
      <div className="p-6 border border-destructive/50 rounded-lg bg-destructive/10 text-destructive flex items-center gap-3">
        <Frown className="h-5 w-5" />
        <span>
          Nie udało się załadować listy kursów. Spróbuj odświeżyć stronę.
        </span>
      </div>
    );
  }
  const stats = [
    {
      icon: BookOpen,
      label: "Łączna liczba słówek",
      value: data.totalWords,
      color: "text-blue-600",
      bgColor: "bg-blue-500/10",
    },
    {
      icon: CheckCircle,
      label: "Ukończone sesje",
      value: `${data.completedSessions}/${data.totalSessions}`,
      color: "text-green-600",
      bgColor: "bg-green-500/10",
    },
    {
      icon: Target,
      label: "Słówka do powtórki",
      value: data.wordsToReview,
      color: "text-orange-600",
      bgColor: "bg-orange-500/10",
    },
    {
      icon: Clock,
      label: "Następna powtórka",
      value: data.nextReviewDate
        ? new Date(data.nextReviewDate).toLocaleDateString("pl-PL")
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
