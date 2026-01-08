"use client";

import { Card } from "@/components/ui/card";
import { Button } from "@/components/ui/button";
import { Users, BookOpen, Loader2 } from "lucide-react";
import { usePublicDecks } from "@/features/community/hooks/usePublicDecks";
import { DeckCategoryBadge } from "@/features/deck/components/deck/DeckCategoryBadge";
import { DeckDifficultyBadge } from "@/features/deck/components/deck/DeckDifficultyBadge";
import { useRouter } from "next/navigation";
import Link from "next/link";
import { useMyDeckStats } from "@/features/deck/hooks/useMyDeckStats";

/**
 * Lista popularnych kursów społeczności
 */
export const CommunityCourses = () => {
  const router = useRouter();
  const { data, isLoading, error } = usePublicDecks({ page: 0, size: 4 });
  const deckIds = data?.content.map((deck) => deck.id) || [];
  const { data: statsData } = useMyDeckStats(deckIds);

  if (isLoading) {
    return (
      <Card className="p-6">
        <div className="flex items-center justify-center h-48">
          <Loader2 className="w-8 h-8 animate-spin text-muted-foreground" />
        </div>
      </Card>
    );
  }

  if (error || !data) {
    return (
      <Card className="p-6">
        <div className="text-center text-muted-foreground py-8">
          Nie udało się załadować kursów społeczności
        </div>
      </Card>
    );
  }

  const courses = data.content;

  if (courses.length === 0) {
    return (
      <Card className="p-6">
        <div className="sm:flex items-center justify-between mb-6">
          <div className="pb-4">
            <h2 className="text-2xl font-bold">Kursy społeczności</h2>
            <p className="text-sm text-muted-foreground mt-1">
              Popularne kursy tworzone przez innych użytkowników
            </p>
          </div>
        </div>
        <div className="text-center text-muted-foreground py-8">
          Brak dostępnych kursów społeczności
        </div>
      </Card>
    );
  }

  return (
    <Card className="p-6">
      <div className="sm:flex items-center justify-between mb-6">
        <div className="pb-4">
          <h2 className="text-2xl font-bold">Kursy społeczności</h2>
          <p className="text-sm text-muted-foreground mt-1">
            Popularne kursy tworzone przez innych użytkowników
          </p>
        </div>
        <Button variant="outline" asChild>
          <Link href="/community">Przeglądaj wszystkie</Link>
        </Button>
      </div>

      <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
        {courses.map((course) => (
          <div
            key={course.id}
            onClick={() => router.push(`/my-courses/${course.id}/details`)}
            className="p-4 border rounded-lg hover:border-primary hover:bg-accent/50 transition-all cursor-pointer"
          >
            <div className="flex items-start justify-between mb-3">
              <div className="flex-1">
                <h3 className="font-semibold text-lg mb-1">{course.name}</h3>
                <p className="text-sm text-muted-foreground mb-3 line-clamp-2">
                  {course.deckDescription || "Brak opisu"}
                </p>

                <div className="flex flex-wrap items-center gap-2 mb-3">
                  {course.deckCategory && (
                    <DeckCategoryBadge category={course.deckCategory} />
                  )}
                  {course.deckDifficulty && (
                    <DeckDifficultyBadge difficulty={course.deckDifficulty} />
                  )}
                </div>

                <div className="flex items-center gap-4 text-sm text-muted-foreground">
                  <span className="flex items-center gap-1">
                    <Users className="w-4 h-4" />
                    {statsData?.[course.id]?.totalStudents ?? 0} uczniów
                  </span>
                  <span className="flex items-center gap-1">
                    <BookOpen className="w-4 h-4" />
                    {course.wordCount ?? 0} fiszek
                  </span>
                </div>
              </div>
            </div>

            <Button className="w-full" size="sm">
              Zobacz kurs
            </Button>
          </div>
        ))}
      </div>
    </Card>
  );
};
