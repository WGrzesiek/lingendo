import { Card } from "@/components/ui/card";
import {useLearnHeaderProgress} from "@/features/learning/hooks/useLearnHeaderProgress";

interface SessionProgressProps {
    sessionId: string;
}

/**
 * Pasek postępu sesji nauki
 * Pokazuje ile słówek zostało i procent ukończenia
 */
export const SessionProgress = ({ sessionId }: SessionProgressProps) => {
  const { data, isLoading, isError } = useLearnHeaderProgress(sessionId);

  if (isError || !data) {
    return (
        <p className="text-destructive text-sm">
          Nie udało się pobrać statystyk.
        </p>
    );
  }
  return (
    <Card className="p-4">
      <div className="space-y-3">
        <div className="flex items-center justify-between text-sm">

          <span className="text-muted-foreground">{data.progressPercent.toPrecision(2)}%</span>
        </div>
        <div className="w-full bg-secondary rounded-full h-3 overflow-hidden">
          <div
            className="bg-primary h-full transition-all duration-300 ease-out"
            style={{ width: `${data.progressPercent}%` }}
          />
        </div>
      </div>
    </Card>
  );
};
