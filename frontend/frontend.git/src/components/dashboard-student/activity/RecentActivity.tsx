import { Card } from "@/components/ui/card";
import { Badge } from "@/components/ui/badge";
import { Calendar, Target, BookOpen, Flame } from "lucide-react";

interface RecentActivity {
  id: string;
  type: "lesson_completed" | "course_started" | "achievement" | "streak";
  title: string;
  description: string;
  time: string;
  points?: number;
}

/**
 * Historia aktywności ucznia
 * Wyświetla ostatnie osiągnięcia i ukończone lekcje
 */
export const RecentActivity = () => {
  const activities: RecentActivity[] = [
    {
      id: "1",
      type: "lesson_completed",
      title: "Ukończono lekcję",
      description: "Past Simple - ćwiczenia praktyczne",
      time: "2 godziny temu",
      points: 50,
    },
    {
      id: "2",
      type: "streak",
      title: "Seria dni nauki!",
      description: "14 dni nauki z rzędu - nowy rekord!",
      time: "Dzisiaj",
      points: 100,
    },
    {
      id: "3",
      type: "achievement",
      title: "Nowe osiągnięcie",
      description: "Ukończono 100 lekcji",
      time: "5 godzin temu",
      points: 200,
    },
    {
      id: "4",
      type: "course_started",
      title: "Rozpoczęto nowy kurs",
      description: "Niemiecki w podróży",
      time: "1 dzień temu",
    },
    {
      id: "5",
      type: "lesson_completed",
      title: "Ukończono lekcję",
      description: "Słownictwo biznesowe - część 3",
      time: "2 dni temu",
      points: 50,
    },
  ];

  const getActivityIcon = (type: RecentActivity["type"]) => {
    switch (type) {
      case "lesson_completed":
        return <BookOpen className="w-5 h-5 text-success" />;
      case "course_started":
        return <Target className="w-5 h-5 text-info" />;
      case "achievement":
        return <Calendar className="w-5 h-5 text-premium" />;
      case "streak":
        return <Flame className="w-5 h-5 text-streak" />;
    }
  };

  return (
    <Card className="p-6">
      <h2 className="text-2xl font-bold mb-6">Ostatnia aktywność</h2>

      <div className="space-y-4">
        {activities.map((activity) => (
          <div
            key={activity.id}
            className="flex items-start gap-4 p-3 rounded-lg hover:bg-accent/50 transition-colors border"
          >
            <div className="p-2 bg-background rounded-lg">
              {getActivityIcon(activity.type)}
            </div>

            <div className="flex-1 min-w-0">
              <div className="flex items-center justify-between gap-2 mb-1">
                <h3 className="font-semibold">{activity.title}</h3>
                {activity.points && (
                  <Badge variant="secondary" className="text-xs">
                    +{activity.points} pkt
                  </Badge>
                )}
              </div>
              <p className="text-sm text-muted-foreground">
                {activity.description}
              </p>
            </div>

            <span className="text-xs text-muted-foreground whitespace-nowrap">
              {activity.time}
            </span>
          </div>
        ))}
      </div>
    </Card>
  );
};
