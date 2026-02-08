import { Card, CardContent, CardHeader, CardTitle } from "@/components/ui/card";
import { Badge } from "@/components/ui/badge";
import { History } from "lucide-react";
import { time } from "@/lib/time";
import {IUserActivityItem, userActivity} from "@/common/userActivity";

interface ActivityHistoryProps {
  activities: IUserActivityItem[];
}

/**
 * Historia aktywności użytkownika
 */
export const ActivityHistory = ({ activities }: ActivityHistoryProps) => {

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
              className={`flex items-start gap-3 p-3 rounded-lg border ${userActivity.getActivityColor(
                activity.type
              )}`}
            >
              {/* Ikona */}
              <div className="flex-shrink-0 mt-1">
                {userActivity.getActivityIcon(activity.type)}
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
                  {time(activity.eventTime)}
                </p>
              </div>
            </div>
          ))}
        </div>
      </CardContent>
    </Card>
  );
};
