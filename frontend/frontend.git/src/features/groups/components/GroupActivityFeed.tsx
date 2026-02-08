"use client";

import { formatDistanceToNow, parseISO } from "date-fns";
import { pl } from "date-fns/locale";
import { Clock } from "lucide-react";
import { Card, CardContent, CardHeader, CardTitle } from "@/components/ui/card";
import { Skeleton } from "@/components/ui/skeleton";
import type { GroupActivityItem } from "../types/group.types";

interface GroupActivityFeedProps {
  activities: GroupActivityItem[] | undefined;
  isLoading: boolean;
}

const activityLabels: Record<GroupActivityItem["activityType"], string> = {
  LESSON_COMPLETED: "ukończył(a) lekcję",
  COURSE_STARTED: "rozpoczął(a) kurs",
  COURSE_COMPLETED: "ukończył(a) kurs",
};

/**
 * Komponent wyświetlający kanał aktywności grupy.
 */

export function GroupActivityFeed({
  activities,
  isLoading,
}: GroupActivityFeedProps) {
  if (isLoading) {
    return (
      <Card>
        <CardHeader>
          <CardTitle className="text-lg">Ostatnia aktywność</CardTitle>
        </CardHeader>
        <CardContent className="space-y-4">
          {[...Array(5)].map((_, i) => (
            <div key={i} className="flex gap-3">
              <div className="flex-1">
                <Skeleton className="h-4 w-3/4 mb-1" />
                <Skeleton className="h-3 w-24" />
              </div>
            </div>
          ))}
        </CardContent>
      </Card>
    );
  }

  if (!activities || activities.length === 0) {
    return (
      <Card>
        <CardHeader>
          <CardTitle className="text-lg">Ostatnia aktywność</CardTitle>
        </CardHeader>
        <CardContent>
          <div className="text-center py-8 text-muted-foreground">
            <Clock className="size-8 mx-auto mb-2 opacity-50" />
            <p>Brak aktywności</p>
            <p className="text-sm mt-1">
              Aktywność członków grupy pojawi się tutaj
            </p>
          </div>
        </CardContent>
      </Card>
    );
  }

  return (
    <Card>
      <CardHeader>
        <CardTitle className="text-lg">Ostatnia aktywność</CardTitle>
      </CardHeader>
      <CardContent>
        <div className="space-y-3">
          {activities.map((activity, index) => {
            const timeAgo = formatDistanceToNow(parseISO(activity.eventTime), {
              addSuffix: true,
              locale: pl,
            });

            return (
              <div key={index} className="border-b last:border-0 pb-3 last:pb-0">
                <p className="text-sm">
                  <span className="font-medium">{activity.studentName}</span>{" "}
                  {activityLabels[activity.activityType]}{" "}
                  <span className="font-medium">{activity.deckName}</span>
                </p>
                <p className="text-xs text-muted-foreground mt-0.5">
                  {timeAgo}
                </p>
              </div>
            );
          })}
        </div>
      </CardContent>
    </Card>
  );
}
