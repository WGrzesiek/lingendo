import { Card } from "@/components/ui/card";
import { Button } from "@/components/ui/button";
import {
  CheckCircle,
  Circle,
  Frown,
  PlayCircle,
  PlusCircle,
  RotateCcw,
} from "lucide-react";
import { Skeleton } from "@/components/ui/skeleton";
import { useCourseProgress } from "@/features/course/hooks/useCourseProgress";
import { useRouter } from "next/navigation";
import { useInitializeSession } from "@/features/course/hooks/useInitializeSession";

interface SessionProgressProps {
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
 * Widok postępu sesji nauki
 * Pokazuje ukończone sesje i sesje do zrobienia
 */
export const SessionProgress = ({ enrollmentId }: SessionProgressProps) => {
  const router = useRouter();
  const { data, isLoading, isError } = useCourseProgress(enrollmentId);
  const initializeSession = useInitializeSession();

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
  const sessionToContinue = data.sessions
    .filter((s) => s.status === "IN_PROGRESS")
    .sort((a, b) => a.sessionNumber - b.sessionNumber)[0];

  const sessionIdToContinue = sessionToContinue?.sessionId;

  const canStartNewSession =
    !!data &&
    (data.sessions?.every((s) => s.status === "COMPLETED") ?? false) &&
    data.totalSessions >= data.completedSessions;

  const sessions = Array.from({ length: data.totalSessions }, (_, i) => ({
    number: i + 1,
    isCompleted: i < data.completedSessions,
    wordsCount: data.wordsPerSession,
  }));

  const completionPercentage = Math.round(
    (data.completedSessions / data.totalSessions) * 100
  );

  return (
    <Card className="p-6">
      <div className="sm:flex items-center justify-between mb-6">
        <div>
          <h2 className="text-2xl font-bold mb-1">Postęp nauki</h2>
          <p className="text-muted-foreground">
            Ukończono {data.completedSessions} z {data.totalSessions} sesji (
            {completionPercentage}%)
          </p>
        </div>

        <div className="grid grid-rows-1 gap-2 pt-4">
          {data.wordsToReview > 0 && (
            <Button
              variant="outline"
              className="gap-2"
              onClick={() => router.push(`/course/${enrollmentId}/review`)}
            >
              <RotateCcw className="w-4 h-4" />
              Powtórka ({data.wordsToReview})
            </Button>
          )}

          <Button
            className="gap-2"
            disabled={!sessionIdToContinue}
            onClick={() => {
              if (!sessionIdToContinue) return;
              router.push(`/learn/${enrollmentId}/${sessionIdToContinue}`);
            }}
          >
            <PlayCircle className="w-4 h-4" />
            Kontynuuj naukę
          </Button>

          {canStartNewSession && (
            <Button
              variant="outline"
              className="gap-2"
              disabled={initializeSession.isPending}
              onClick={() => initializeSession.mutate(enrollmentId)}
            >
              <PlusCircle className="w-4 h-4" />
              Rozpocznij nową sesję
            </Button>
          )}
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
        {sessions.map((s) => (
          <div
            key={s.number}
            className={`p-4 border rounded-lg flex flex-col items-center justify-center gap-2 transition-all ${
              s.isCompleted
                ? "bg-green-500/10 border-green-500"
                : "hover:bg-accent/50"
            }`}
          >
            {s.isCompleted ? (
              <CheckCircle className="w-6 h-6 text-green-600" />
            ) : (
              <Circle className="w-6 h-6 text-muted-foreground" />
            )}
            <div className="text-center">
              <p className="font-semibold text-sm">Sesja {s.number}</p>
              <p className="text-xs text-muted-foreground">
                {s.wordsCount} słówek
              </p>
            </div>
          </div>
        ))}
      </div>
    </Card>
  );
};
