"use client";

import { Card, CardContent, CardHeader, CardTitle } from "@/components/ui/card";
import { History, BookCheck, UserPlus, Zap } from "lucide-react";
import type { TeacherActivityItem } from "@/features/dashboard-teacher/types";
import { time } from "@/lib/time";

interface TeacherActivityHistoryProps {
  activities: TeacherActivityItem[];
}

/**
 * Historia aktywności uczniów dla nauczyciela
 */
export const TeacherActivityHistory = ({
  activities,
}: TeacherActivityHistoryProps) => {
  const getActivityIcon = (type: string) => {
    switch (type) {
      case "SESSION_COMPLETED":
        return <Zap className="w-4 h-4 text-yellow-500" />;
      case "LESSON_COMPLETED":
        return <BookCheck className="w-4 h-4 text-green-500" />;
      case "NEW_STUDENT":
        return <UserPlus className="w-4 h-4 text-blue-500" />;
      default:
        return <Zap className="w-4 h-4 text-gray-500" />;
    }
  };

  const getActivityColor = (type: string) => {
    switch (type) {
      case "SESSION_COMPLETED":
        return "bg-yellow-500/10 border-yellow-500/20";
      case "LESSON_COMPLETED":
        return "bg-green-500/10 border-green-500/20";
      case "NEW_STUDENT":
        return "bg-blue-500/10 border-blue-500/20";
      default:
        return "bg-gray-500/10 border-gray-500/20";
    }
  };

  const getActivityTitle = (activity: TeacherActivityItem) => {
    switch (activity.activityType) {
      case "SESSION_COMPLETED":
        return "Ukończona sesja";
      case "LESSON_COMPLETED":
        return "Ukończona lekcja";
      case "NEW_STUDENT":
        return "Nowy uczeń";
      default:
        return "Aktywność";
    }
  };

  const getActivitySubtitle = (activity: TeacherActivityItem) => {
    switch (activity.activityType) {
      case "SESSION_COMPLETED":
      case "LESSON_COMPLETED":
        return `${activity.studentName} - ${activity.deckName || "Kurs"}`;
      case "NEW_STUDENT":
        return `${activity.studentName} dołączył do Twojej grupy`;
      default:
        return activity.studentName;
    }
  };

  if (activities.length === 0) {
    return (
      <Card>
        <CardHeader>
          <CardTitle className="flex items-center gap-2">
            <History className="w-5 h-5" />
            Historia aktywności uczniów
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
          Historia aktywności uczniów
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
                activity.activityType
              )}`}
            >
              {/* Ikona */}
              <div className="flex-shrink-0 mt-1">
                {getActivityIcon(activity.activityType)}
              </div>

              {/* Treść */}
              <div className="flex-1 min-w-0">
                <div className="flex items-start justify-between gap-2 mb-1">
                  <h4 className="font-semibold text-sm">
                    {getActivityTitle(activity)}
                  </h4>
                </div>
                <p className="text-sm text-muted-foreground line-clamp-2">
                  {getActivitySubtitle(activity)}
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
