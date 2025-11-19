import { Card } from "@/components/ui/card";

interface SessionProgressProps {
  progress: {
    current: number;
    total: number;
    percentage: number;
  };
}

/**
 * Pasek postępu sesji nauki
 * Pokazuje ile słówek zostało i procent ukończenia
 */
export const SessionProgress = ({ progress }: SessionProgressProps) => {
  return (
    <Card className="p-4">
      <div className="space-y-3">
        <div className="flex items-center justify-between text-sm">
          <span className="font-medium">
            Słówko {progress.current} z {progress.total}
          </span>
          <span className="text-muted-foreground">{progress.percentage}%</span>
        </div>
        <div className="w-full bg-secondary rounded-full h-3 overflow-hidden">
          <div
            className="bg-primary h-full transition-all duration-300 ease-out"
            style={{ width: `${progress.percentage}%` }}
          />
        </div>
      </div>
    </Card>
  );
};
