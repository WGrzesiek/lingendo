import { Card } from "@/components/ui/card";
import { Badge } from "@/components/ui/badge";
import { useStudentActivity } from "../hooks/useStudentActivity";
import { formatDistanceToNow, parseISO } from "date-fns";
import { pl } from "date-fns/locale";
import {userActivity} from "@/common/userActivity";

/**
 * Historia aktywności ucznia
 */
export const RecentActivity = () => {
  const { data, isLoading, error } = useStudentActivity();

  if (isLoading) {
    return (
      <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-6">
        {[1, 2, 3, 4].map((i) => (
          <div key={i} className="h-32 rounded-xl bg-muted animate-pulse" />
        ))}
      </div>
    );
  }

  if (error || !data) {
    return (
        <p className="text-destructive text-sm">
          Nie udało się pobrać statystyk.
        </p>
    );
  }

  return (
    <Card className="p-6">
      <h2 className="text-2xl font-bold mb-6">Ostatnia aktywność</h2>

      <div className="space-y-4">
        {data.map((activity) => (
          <div
            key={activity.type + activity.eventTime}
            className="flex items-start gap-4 p-3 rounded-lg hover:bg-accent/50 transition-colors border"
          >
            <div className="p-2 bg-background rounded-lg">
              {userActivity.getActivityIcon(activity.type)}
            </div>

            <div className="flex-1 min-w-0">
              <div className="flex items-center justify-between gap-2 mb-1">
                <h3 className="font-semibold">{activity.title}</h3>
                {activity.points != null && (
                  <Badge variant="secondary" className="text-xs">
                    +{activity.points} pkt
                  </Badge>
                )}
              </div>
              <p className="text-sm text-muted-foreground">
                {activity.subtitle}
              </p>
            </div>

            <span className="text-xs text-muted-foreground whitespace-nowrap">
              {formatDistanceToNow(parseISO(activity.eventTime), {
                addSuffix: true,
                locale: pl,
              })}
            </span>
          </div>
        ))}
      </div>
    </Card>
  );
};
