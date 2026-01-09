"use client";

import { useState } from "react";
import { Card } from "@/components/ui/card";
import { Button } from "@/components/ui/button";
import { Badge } from "@/components/ui/badge";
import {
  ArrowLeft,
  Settings,
  Share2,
  Lock,
  Globe,
  Frown,
  BarChart3,
} from "lucide-react";
import { useCourseHeader } from "@/features/course/hooks/useCourseHeader";
import { Skeleton } from "@/components/ui/skeleton";
import { CourseStatsModal } from "./CourseStatsModal";
import { useRouter } from "next/navigation";

interface CourseHeaderProps {
  enrollmentId: string;
}

/**
 * Nagłówek strony kursu z tytułem, opisem i akcjami
 */
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

export const CourseHeader = ({ enrollmentId }: CourseHeaderProps) => {
  const router = useRouter();
  const [statsModalOpen, setStatsModalOpen] = useState(false);
  const { data, isLoading, isError } = useCourseHeader(enrollmentId);
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
  // if (data === null) {
  //   return <EmptyState />;
  // }
  return (
    <div className="space-y-4">
      <Button variant="ghost" className="gap-2" onClick={() => router.back()}>
        <ArrowLeft className="w-4 h-4" />
        Powrót do dashboardu
      </Button>

      <Card className="p-6">
        <div className="flex flex-col gap-4 md:flex-row md:items-start md:justify-between">
          <div className="flex-1">
            <div className="flex flex-wrap items-center gap-3 mb-2">
              <h1 className="text-3xl font-bold">{data.name}</h1>
              {data.visibility ? (
                <Badge className="gap-1">
                  <Globe className="w-3 h-3" />
                  Publiczny
                </Badge>
              ) : (
                <Badge variant="secondary" className="gap-1">
                  <Lock className="w-3 h-3" />
                  Prywatny
                </Badge>
              )}
              {data.ownerType === "I" && (
                <Badge variant="outline">Twój kurs</Badge>
              )}
            </div>
            <p className="text-muted-foreground mb-4">{data.description}</p>
            <p className="text-sm text-muted-foreground">
              Utworzony przez: <strong>{data.username}</strong>
            </p>
          </div>

          <div className="flex flex-wrap gap-2 md:flex-nowrap md:flex-shrink-0">
            <Button
              variant="outline"
              size="sm"
              className="gap-2 flex-1 sm:flex-initial"
              onClick={() => setStatsModalOpen(true)}
            >
              <BarChart3 className="w-4 h-4" />
              <span className="hidden sm:inline">Statystyki</span>
              <span className="sm:hidden">Stats</span>
            </Button>
            {data.ownerType === "I" && (
              <>
                <Button
                  variant="outline"
                  size="sm"
                  className="gap-2 flex-1 sm:flex-initial"
                >
                  <Settings className="w-4 h-4" />
                  <span className="hidden sm:inline">Ustawienia</span>
                  <span className="sm:hidden">Ustaw.</span>
                </Button>
                <Button
                  variant="outline"
                  size="sm"
                  className="gap-2 flex-1 sm:flex-initial"
                >
                  <Share2 className="w-4 h-4" />
                  <span className="hidden sm:inline">Udostępnij</span>
                  <span className="sm:hidden">Udost.</span>
                </Button>
              </>
            )}
            <Button
              size="sm"
              className="flex-1 sm:flex-initial"
              onClick={() => router.push(`/course/${data.deckId}`)}
            >
              Rozpocznij naukę
            </Button>
          </div>
        </div>
      </Card>

      {/* Modal ze statystykami */}
      <CourseStatsModal
        enrollmentId={enrollmentId}
        open={statsModalOpen}
        onOpenChange={setStatsModalOpen}
      />
    </div>
  );
};
