"use client";

import { useRouter } from "next/navigation";
import { Card } from "@/components/ui/card";
import { Button } from "@/components/ui/button";
import {
  BookOpen,
  Calendar,
  Users,
  CheckCircle,
  Loader2,
  AlertCircle,
} from "lucide-react";
import { ICommunityCourse } from "@/features/community/types/community-course.types";
import { time } from "@/lib/time";
import { DeckCategoryBadge } from "@/features/deck/components/deck/DeckCategoryBadge";
import { DeckDifficultyBadge } from "@/features/deck/components/deck/DeckDifficultyBadge";
import { LanguageBadge } from "@/features/deck/components/deck/LanguageBadge";
import { useCurrentUser } from "@/features/auth/hooks/useCurrentUser";
import type { DeckStat } from "@/features/deck/types/created-deck.types";
import { useState } from "react";
import { useEnrollToDeck } from "@/features/deckEnrollment";
import { useEffect } from "react";

interface CommunityCourseCardProps {
  course: ICommunityCourse;
  stats?: DeckStat;
}

/**
 * Karta kursu społeczności - taki sam styl jak CreatedDeckCard
 */
export const CommunityCourseCard = ({
  course,
  stats,
}: CommunityCourseCardProps) => {
  const router = useRouter();
  const { data: currentUser } = useCurrentUser();
  const isTeacher = currentUser?.accountType === "TEACHER";

  const handleCardClick = () => {
    router.push(`/my-courses/${course.id}/details`);
  };

  const [isEnrolled, setIsEnrolled] = useState(false);

  const enrollMutation = useEnrollToDeck();

  const handleEnroll = (e: React.MouseEvent) => {
    e.stopPropagation();
    enrollMutation.mutate(
      { deckId: course.id },
      {
        onSuccess: () => {
          setIsEnrolled(true);
        },
      }
    );
  };

  useEffect(() => {
    if (!isEnrolled) return;

    const timeout = setTimeout(() => {
      router.push("/dashboard");
    }, 2000);

    return () => clearTimeout(timeout);
  }, [isEnrolled, router]);

  return (
    <Card
      className="group p-5 hover:shadow-lg hover:border-primary/50 transition-all cursor-pointer"
      onClick={handleCardClick}
    >
      <div className="space-y-4">
        {/* Tytuł i badge'y */}
        <div className="space-y-2">
          <div className="flex flex-wrap items-center gap-2">
            <h3 className="font-semibold text-lg tracking-tight group-hover:text-primary transition-colors line-clamp-1">
              {course.title}
            </h3>
          </div>
          <div className="flex flex-wrap items-center gap-2">
            <DeckCategoryBadge category={course.category} />
            <DeckDifficultyBadge difficulty={course.difficulty} />
            {course.languageFrom && course.languageTo && (
              <LanguageBadge
                languageFrom={course.languageFrom}
                languageTo={course.languageTo}
              />
            )}
          </div>
          <p className="text-sm text-muted-foreground line-clamp-2 leading-relaxed">
            {course.description || "Brak opisu kursu."}
          </p>
        </div>

        {/* Statystyki kursu */}
        <div className="flex flex-wrap gap-4 text-xs text-muted-foreground pt-2 border-t border-border/40">
          <span className="flex items-center gap-1.5">
            <BookOpen className="w-3.5 h-3.5" />
            {course.wordCount} słówek
          </span>
          <span className="flex items-center gap-1.5">
            <Calendar className="w-3.5 h-3.5" />
            {time(course.createdAt)}
          </span>
        </div>

        {/* Statystyki zapisanych użytkowników */}
        {stats && (
          <div className="grid grid-cols-2 gap-3 pt-3 border-t border-border/40">
            <div className="flex items-center gap-2">
              <div className="p-2 bg-blue-500/10 rounded-lg">
                <Users className="w-4 h-4 text-blue-600" />
              </div>
              <div className="flex flex-col">
                <span className="text-sm font-semibold">
                  {stats.totalStudents}
                </span>
                <span className="text-xs text-muted-foreground">Uczniów</span>
              </div>
            </div>

            <div className="flex items-center gap-2">
              <div className="p-2 bg-green-500/10 rounded-lg">
                <CheckCircle className="w-4 h-4 text-green-600" />
              </div>
              <div className="flex flex-col">
                <span className="text-sm font-semibold">
                  {stats.completedStudents}
                </span>
                <span className="text-xs text-muted-foreground">Ukończeń</span>
              </div>
            </div>
          </div>
        )}

        {/* Przycisk zapisu - tylko dla studentów */}
        {!isTeacher &&
          // <Button className="w-full" size="sm" onClick={handleEnroll}>
          //   Dołącz do kursu
          // </Button>
          (isEnrolled ? (
            <div className="flex items-center gap-2 text-sm text-green-600">
              <CheckCircle className="w-4 h-4" />
              <span>Zapisano na kurs! Przekierowanie...</span>
            </div>
          ) : (
            <Button
              className="w-full"
              size="sm"
              onClick={handleEnroll}
              disabled={enrollMutation.isPending}
            >
              {enrollMutation.isPending ? (
                <>
                  <Loader2 className="w-4 h-4 mr-2 animate-spin" />
                  Zapisywanie...
                </>
              ) : (
                "Zapisz się na kurs"
              )}
            </Button>
          ))}
        {enrollMutation.isError && (
          <div className="flex items-center gap-2 text-sm text-destructive">
            <AlertCircle className="w-4 h-4" />
            <span>Błąd podczas zapisywania. Spróbuj ponownie.</span>
          </div>
        )}
      </div>
    </Card>
  );
};
