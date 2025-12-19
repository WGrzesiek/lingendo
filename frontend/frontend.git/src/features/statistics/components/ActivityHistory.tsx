import { Card, CardContent, CardHeader, CardTitle } from "@/components/ui/card";
import { Badge } from "@/components/ui/badge";
import { History, Trophy, BookCheck, BookOpen, Flame } from "lucide-react";
import { IUserActivityItem } from "@/features/statistics/types/statistics.types";
import { timeAgo } from "@/lib/timeAgo";

interface ActivityHistoryProps {
  activities: IUserActivityItem[];
}

/**
 * Historia aktywności użytkownika z tabeli user_activity
 */
export const ActivityHistory = ({ activities }: ActivityHistoryProps) => {
  const getActivityIcon = (type: IUserActivityItem["type"]) => {
    switch (type) {
      case "LESSON_COMPLETED":
        return <BookCheck className="w-4 h-4 text-green-500" />;
      case "SESSION_STARTED":
        return <BookOpen className="w-4 h-4 text-blue-500" />;
      case "SESSION_COMPLETED":
        return <Trophy className="w-4 h-4 text-yellow-500" />;
      case "LOGIN":
        return <Flame className="w-4 h-4 text-orange-500" />;
    }
  };

  const getActivityColor = (type: IUserActivityItem["type"]) => {
    switch (type) {
      case "LESSON_COMPLETED":
        return "bg-green-500/10 border-green-500/20";
      case "SESSION_STARTED":
        return "bg-blue-500/10 border-blue-500/20";
      case "SESSION_COMPLETED":
        return "bg-yellow-500/10 border-yellow-500/20";
      case "LOGIN":
        return "bg-orange-500/10 border-orange-500/20";
    }
  };

  if (activities.length === 0) {
    return (
      <Card>
        <CardHeader>
          <CardTitle className="flex items-center gap-2">
            <History className="w-5 h-5" />
            Historia aktywności
          </CardTitle>
        </CardHeader>
        <CardContent>
          <div className="text-center py-8">
            <p className="text-muted-foreground">
              Brak aktywności do wyświetlenia
            </p>
          </div>
        </CardContent>
      </Card>
    );
  }

  return (
    <Card>
      <CardHeader>
        <CardTitle className="flex items-center gap-2">
          <History className="w-5 h-5" />
          Historia aktywności
        </CardTitle>
        <p className="text-sm text-muted-foreground mt-1">
          Ostatnie {activities.length} aktywności
        </p>
      </CardHeader>
      <CardContent>
        <div className="space-y-3">
          {activities.map((activity, index) => (
            <div
              key={index}
              className={`flex items-start gap-3 p-3 rounded-lg border ${getActivityColor(
                activity.type
              )}`}
            >
              {/* Ikona */}
              <div className="flex-shrink-0 mt-1">
                {getActivityIcon(activity.type)}
              </div>

              {/* Treść */}
              <div className="flex-1 min-w-0">
                <div className="flex items-start justify-between gap-2 mb-1">
                  <h4 className="font-semibold text-sm">{activity.title}</h4>
                  {activity.points > 0 && (
                    <Badge variant="secondary" className="text-xs">
                      +{activity.points} pkt
                    </Badge>
                  )}
                </div>
                <p className="text-sm text-muted-foreground line-clamp-2">
                  {activity.subtitle}
                </p>
                <p className="text-xs text-muted-foreground mt-1">
                  {timeAgo(activity.eventTime)}
                </p>
              </div>
            </div>
          ))}
        </div>
      </CardContent>
    </Card>
  );
};
