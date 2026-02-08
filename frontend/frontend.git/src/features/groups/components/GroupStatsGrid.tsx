"use client";

import {
  Users,
  BookOpen,
  TrendingUp,
  Clock,
  Calendar,
  Award,
} from "lucide-react";
import { Card, CardContent, CardHeader, CardTitle } from "@/components/ui/card";
import { Skeleton } from "@/components/ui/skeleton";
import { cn } from "@/lib/utils";
import type { GroupDashboardStats } from "../types/group.types";

interface GroupStatsGridProps {
  stats: GroupDashboardStats | undefined;
  isLoading: boolean;
}

interface StatCardProps {
  title: string;
  value: string | number;
  subtitle?: string;
  icon: React.ElementType;
  trend?: number;
  className?: string;
}

function StatCard({
  title,
  value,
  subtitle,
  icon: Icon,
  trend,
  className,
}: StatCardProps) {
  return (
    <Card className={cn("overflow-hidden", className)}>
      <CardHeader className="flex flex-row items-center justify-between pb-2">
        <CardTitle className="text-sm font-medium text-muted-foreground">
          {title}
        </CardTitle>
        <Icon className="size-4 text-muted-foreground" />
      </CardHeader>
      <CardContent>
        <div className="text-2xl font-bold">{value}</div>
        {(subtitle || trend !== undefined) && (
          <p className="text-xs text-muted-foreground mt-1">
            {trend !== undefined && (
              <span
                className={cn(
                  "inline-flex items-center mr-1",
                  trend > 0 ? "text-green-600" : trend < 0 ? "text-red-600" : ""
                )}
              >
                {trend > 0 ? "+" : ""}
                {trend}%
              </span>
            )}
            {subtitle}
          </p>
        )}
      </CardContent>
    </Card>
  );
}

function StatCardSkeleton() {
  return (
    <Card>
      <CardHeader className="flex flex-row items-center justify-between pb-2">
        <Skeleton className="h-4 w-24" />
        <Skeleton className="size-4 rounded" />
      </CardHeader>
      <CardContent>
        <Skeleton className="h-8 w-16 mb-1" />
        <Skeleton className="h-3 w-32" />
      </CardContent>
    </Card>
  );
}

/**
 * Siatka statystyk grupy
 */
export function GroupStatsGrid({ stats, isLoading }: GroupStatsGridProps) {
  if (isLoading) {
    return (
      <div className="grid gap-4 md:grid-cols-2 lg:grid-cols-4">
        {[...Array(8)].map((_, i) => (
          <StatCardSkeleton key={i} />
        ))}
      </div>
    );
  }

  if (!stats) {
    return null;
  }
// delete
  // const formatNumber = (num: number) => {
  //   if (num >= 1000000) return `${(num / 1000000).toFixed(1)}M`;
  //   if (num >= 1000) return `${(num / 1000).toFixed(1)}K`;
  //   return num.toString();
  // };

  const formatDuration = (minutes: number) => {
    if (minutes >= 60) {
      const hours = Math.floor(minutes / 60);
      const mins = minutes % 60;
      return `${hours}h ${mins}m`;
    }
    return `${minutes}m`;
  };

  return (
    <div className="space-y-4">
      {/* Główne statystyki */}
      <div className="grid gap-4 md:grid-cols-2 lg:grid-cols-4">
        <StatCard
          title="Członkowie"
          value={stats.totalMembers}
          subtitle={`${stats.activeMembers} aktywnych`}
          icon={Users}
        />
        <StatCard
          title="Udostępnione kursy"
          value={stats.sharedDecks}
          icon={BookOpen}
        />
        <StatCard
          title="Poprawne odpowiedzi"
          value={stats.totalWordsLearned}
          subtitle="łącznie w grupie"
          icon={TrendingUp}
        />
        <StatCard
          title="Czas nauki"
          value={formatDuration(stats.totalStudyTimeMinutes)}
          subtitle="łącznie w grupie"
          icon={Clock}
        />
      </div>

      {/* Dodatkowe statystyki */}
      <div className="grid gap-4 md:grid-cols-2 lg:grid-cols-3">
        <StatCard
          title="Sesje nauki"
          value={stats.totalSessions}
          icon={Calendar}
        />
        <StatCard
          title="Średnia accuracy"
          value={`${stats.averageAccuracy.toFixed(1)}%`}
          icon={Award}
        />
        <StatCard
          title="Średnio odpowiedzi/dzień"
          value={stats.averageWordsPerDay.toFixed(1)}
          icon={BookOpen}
        />
      </div>
    </div>
  );
}
