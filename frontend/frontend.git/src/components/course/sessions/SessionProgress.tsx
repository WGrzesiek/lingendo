import { Card } from "@/components/ui/card";
import { Button } from "@/components/ui/button";
import { CheckCircle, Circle, PlayCircle, RotateCcw } from "lucide-react";

interface SessionProgressProps {
  course: {
    completedSessions: number;
    totalSessions: number;
    wordsPerSession: number;
    totalWords: number;
    wordsToReview: number;
  };
}

/**
 * Widok postępu sesji nauki
 * Pokazuje ukończone sesje i sesje do zrobienia
 */
export const SessionProgress = ({ course }: SessionProgressProps) => {
  const sessions = Array.from({ length: course.totalSessions }, (_, i) => ({
    number: i + 1,
    isCompleted: i < course.completedSessions,
    wordsCount: course.wordsPerSession,
  }));

  const completionPercentage = Math.round(
    (course.completedSessions / course.totalSessions) * 100
  );

  return (
    <Card className="p-6">
      <div className="sm:flex items-center justify-between mb-6">
        <div>
          <h2 className="text-2xl font-bold mb-1">Postęp nauki</h2>
          <p className="text-muted-foreground">
            Ukończono {course.completedSessions} z {course.totalSessions} sesji
            ({completionPercentage}%)
          </p>
        </div>
        <div className="grid grid-rows-1 gap-2 pt-4">
          {course.wordsToReview > 0 && (
            <Button variant="outline" className="gap-2">
              <RotateCcw className="w-4 h-4" />
              Powtórka ({course.wordsToReview})
            </Button>
          )}
          <Button className="gap-2">
            <PlayCircle className="w-4 h-4" />
            Kontynuuj naukę
          </Button>
        </div>
      </div>

      <div className="mb-6">
        <div className="w-full bg-secondary rounded-full h-3">
          <div
            className="bg-primary h-3 rounded-full transition-all"
            style={{ width: `${completionPercentage}%` }}
          />
        </div>
      </div>

      <div className="grid grid-cols-2 md:grid-cols-3 lg:grid-cols-7 gap-3">
        {sessions.map((session) => (
          <div
            key={session.number}
            className={`p-4 border rounded-lg flex flex-col items-center justify-center gap-2 transition-all cursor-pointer ${
              session.isCompleted
                ? "bg-green-500/10 border-green-500 hover:bg-green-500/20"
                : "hover:bg-accent/50"
            }`}
          >
            {session.isCompleted ? (
              <CheckCircle className="w-6 h-6 text-green-600" />
            ) : (
              <Circle className="w-6 h-6 text-muted-foreground" />
            )}
            <div className="text-center">
              <p className="font-semibold text-sm">Sesja {session.number}</p>
              <p className="text-xs text-muted-foreground">
                {session.wordsCount} słówek
              </p>
            </div>
          </div>
        ))}
      </div>
    </Card>
  );
};
