"use client";

import { Card } from "@/components/ui/card";
import { Zap, BookCheck, UserPlus, History, Loader2 } from "lucide-react";
import { useTeacherActivity } from "../hooks";
import type { TeacherActivityItem } from "../types";
import { time } from "@/lib/time";

/**
 * Kanał aktywności wyświetlający ostatnie wydarzenia uczniów
 */
export const ActivityFeed = () => {
  const { data: activities, isLoading, error } = useTeacherActivity(10);

  const getActivityIcon = (type: string) => {
    switch (type) {
      case "SESSION_COMPLETED":
        return <Zap className="w-5 h-5 text-yellow-500" />;
      case "LESSON_COMPLETED":
        return <BookCheck className="w-5 h-5 text-green-500" />;
      case "NEW_STUDENT":
        return <UserPlus className="w-5 h-5 text-blue-500" />;
      default:
        return <Zap className="w-5 h-5 text-gray-500" />;
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

  if (isLoading) {
    return (
      <Card className="p-6">
        <div className="flex items-center gap-2 mb-6">
          <History className="w-5 h-5" />
          <h2 className="text-2xl font-bold">Ostatnia aktywność</h2>
        </div>
        <div className="flex items-center justify-center py-8">
          <Loader2 className="w-8 h-8 animate-spin text-muted-foreground" />
        </div>
      </Card>
    );
  }

  if (error) {
    return (
      <Card className="p-6">
        <div className="flex items-center gap-2 mb-6">
          <History className="w-5 h-5" />
          <h2 className="text-2xl font-bold">Ostatnia aktywność</h2>
        </div>
        <p className="text-muted-foreground text-center py-8">
          Nie udało się załadować aktywności
        </p>
      </Card>
    );
  }

  if (!activities || activities.length === 0) {
    return (
      <Card className="p-6">
        <div className="flex items-center gap-2 mb-6">
          <History className="w-5 h-5" />
          <h2 className="text-2xl font-bold">Ostatnia aktywność</h2>
        </div>
        <p className="text-muted-foreground text-center py-8">
          Brak aktywności do wyświetlenia
        </p>
      </Card>
    );
  }

  return (
    <Card className="p-6">
      <div className="flex items-center gap-2 mb-6">
        <History className="w-5 h-5" />
        <h2 className="text-2xl font-bold">Ostatnia aktywność</h2>
      </div>

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
    </Card>
  );
};
