import { Card } from "@/components/ui/card";
import { Button } from "@/components/ui/button";
import { Badge } from "@/components/ui/badge";
import { MessageSquare, CheckCircle, AlertCircle, Clock } from "lucide-react";

interface Activity {
  id: string;
  type: "question" | "completion" | "issue" | "new_student";
  title: string;
  description: string;
  time: string;
  studentName?: string;
  courseName?: string;
  priority?: "low" | "medium" | "high";
}

/**
 * Kanał aktywności wyświetlający ostatnie wydarzenia
 * Pokazuje pytania uczniów, ukończenia kursów i inne wydarzenia
 */
export const ActivityFeed = () => {
  const activities: Activity[] = [
    {
      id: "1",
      type: "question",
      title: "Nowe pytanie od ucznia",
      description: "Anna Kowalska ma pytanie o lekcję 5",
      time: "5 minut temu",
      studentName: "Anna Kowalska",
      courseName: "Angielski dla początkujących",
      priority: "high",
    },
    {
      id: "2",
      type: "completion",
      title: "Ukończono lekcję",
      description: "Jan Nowak ukończył lekcję o czasach przeszłych",
      time: "30 minut temu",
      studentName: "Jan Nowak",
      courseName: "Gramatyka angielska",
      priority: "low",
    },
    {
      id: "3",
      type: "new_student",
      title: "Nowy uczeń",
      description: "Piotr Zieliński dołączył do kursu",
      time: "1 godzinę temu",
      studentName: "Piotr Zieliński",
      courseName: "Konwersacje po angielsku",
      priority: "medium",
    },
    {
      id: "4",
      type: "issue",
      title: "Problem techniczny",
      description: "Maria Wiśniewska zgłosiła problem z testem",
      time: "2 godziny temu",
      studentName: "Maria Wiśniewska",
      courseName: "Angielski biznesowy",
      priority: "high",
    },
    {
      id: "5",
      type: "completion",
      title: "Ukończono kurs",
      description: "Katarzyna Dąbrowska ukończyła cały kurs!",
      time: "3 godziny temu",
      studentName: "Katarzyna Dąbrowska",
      courseName: "Angielski dla początkujących",
      priority: "low",
    },
  ];

  const getActivityIcon = (type: Activity["type"]) => {
    switch (type) {
      case "question":
        return <MessageSquare className="w-5 h-5 text-info" />;
      case "completion":
        return <CheckCircle className="w-5 h-5 text-success" />;
      case "issue":
        return <AlertCircle className="w-5 h-5 text-error" />;
      case "new_student":
        return <Clock className="w-5 h-5 text-premium" />;
    }
  };

  const getPriorityBadge = (priority?: Activity["priority"]) => {
    if (!priority) return null;

    const variants = {
      low: "outline",
      medium: "secondary",
      high: "destructive",
    } as const;

    const labels = {
      low: "Niski",
      medium: "Średni",
      high: "Wysoki",
    };

    return <Badge variant={variants[priority]}>{labels[priority]}</Badge>;
  };

  return (
    <Card className="p-6">
      <div className="flex items-center justify-between mb-6">
        <h2 className="text-2xl font-bold">Ostatnia aktywność</h2>
        <Button variant="outline" size="sm">
          Zobacz wszystkie
        </Button>
      </div>

      <div className="space-y-4">
        {activities.map((activity) => (
          <div
            key={activity.id}
            className="p-4 rounded-lg hover:bg-accent/50 transition-colors cursor-pointer border"
          >
            <div className="flex items-start gap-3">
              {/* Icon */}
              <div className="p-2 bg-background rounded-lg flex-shrink-0">
                {getActivityIcon(activity.type)}
              </div>

              {/* Content */}
              <div className="flex-1 min-w-0">
                {/* Tytuł + badge (mobile w pionie, desktop w poziomie) */}
                <div className="flex flex-col gap-2 sm:flex-row sm:items-center sm:justify-between mb-2">
                  <h3 className="font-semibold">{activity.title}</h3>
                  <div className="flex items-center gap-2">
                    {getPriorityBadge(activity.priority)}
                    <span className="text-xs text-muted-foreground whitespace-nowrap sm:hidden">
                      {activity.time}
                    </span>
                  </div>
                </div>

                {/* Opis */}
                <p className="text-sm text-muted-foreground mb-2">
                  {activity.description}
                </p>

                {/* Kurs + czas (desktop) */}
                <div className="flex items-center justify-between gap-2">
                  {activity.courseName && (
                    <p className="text-xs text-muted-foreground">
                      {activity.courseName}
                    </p>
                  )}
                  <span className="hidden sm:inline text-xs text-muted-foreground whitespace-nowrap ml-auto">
                    {activity.time}
                  </span>
                </div>
              </div>
            </div>
          </div>
        ))}
      </div>
    </Card>
  );
};
