"use client";

import { Card } from "@/components/ui/card";
import { Button } from "@/components/ui/button";
import { BookOpen, PlusCircle, Calendar, Loader2, Layers } from "lucide-react";
import { useRouter } from "next/navigation";
import { useInfiniteDecksCreatedByMe } from "@/features/deck/hooks/useInfiniteDecksCreatedByMe";
import { DeckCategoryBadge } from "@/features/deck/components/deck/DeckCategoryBadge";
import { DeckDifficultyBadge } from "@/features/deck/components/deck/DeckDifficultyBadge";
import { DeckVisibilityBadge } from "@/features/deck/components/deck/DeckVisibilityBadge";
import { time } from "@/lib/time";

/**
 * Lista kursów nauczyciela
 * Wyświetla ostatnio utworzone kursy z możliwością przejścia do szczegółów
 */
export const RecentCourses = () => {
  const router = useRouter();
  const { data, isLoading, isError } = useInfiniteDecksCreatedByMe();

  const decks = data?.pages.flatMap((page) => page.content).slice(0, 4) ?? [];

  const handleCreateCourse = () => {
    router.push("/decks/create");
  };

  const handleViewAll = () => {
    router.push("/my-courses");
  };

  const handleCardClick = (deckId: string) => {
    router.push(`/my-courses/${deckId}/details`);
  };

  if (isLoading) {
    return (
      <Card className="p-6">
        <div className="flex items-center justify-between mb-6">
          <div className="flex items-center gap-2">
            <BookOpen className="w-5 h-5" />
            <h2 className="text-2xl font-bold">Twoje kursy</h2>
          </div>
        </div>
        <div className="flex items-center justify-center py-8">
          <Loader2 className="w-8 h-8 animate-spin text-muted-foreground" />
        </div>
      </Card>
    );
  }

  if (isError) {
    return (
      <Card className="p-6">
        <div className="flex items-center justify-between mb-6">
          <div className="flex items-center gap-2">
            <BookOpen className="w-5 h-5" />
            <h2 className="text-2xl font-bold">Twoje kursy</h2>
          </div>
        </div>
        <p className="text-muted-foreground text-center py-8">
          Nie udało się załadować kursów
        </p>
      </Card>
    );
  }

  if (decks.length === 0) {
    return (
      <Card className="p-6">
        <div className="flex items-center justify-between mb-6">
          <div className="flex items-center gap-2">
            <BookOpen className="w-5 h-5" />
            <h2 className="text-2xl font-bold">Twoje kursy</h2>
          </div>
          <Button onClick={handleCreateCourse}>
            <PlusCircle className="w-4 h-4 mr-2" />
            Utwórz kurs
          </Button>
        </div>
        <div className="text-center py-8 border rounded-lg border-dashed bg-muted/20">
          <p className="text-muted-foreground mb-4">
            Nie masz jeszcze żadnych kursów
          </p>
          <Button variant="outline" onClick={handleCreateCourse}>
            <PlusCircle className="w-4 h-4 mr-2" />
            Utwórz pierwszy kurs
          </Button>
        </div>
      </Card>
    );
  }

  return (
    <Card className="p-6">
      <div className="flex items-center justify-between mb-6">
        <div className="flex items-center gap-2">
          <BookOpen className="w-5 h-5" />
          <h2 className="text-2xl font-bold">Twoje kursy</h2>
        </div>
        <div className="flex gap-2">
          <Button variant="outline" size="sm" onClick={handleViewAll}>
            Zobacz wszystkie
          </Button>
          <Button size="sm" onClick={handleCreateCourse}>
            <PlusCircle className="w-4 h-4 mr-2" />
            Nowy kurs
          </Button>
        </div>
      </div>

      <div className="space-y-3">
        {decks.map((deck) => (
          <div
            key={deck.id}
            onClick={() => handleCardClick(deck.id)}
            className="p-4 border rounded-lg hover:bg-accent/50 hover:border-primary/50 transition-all cursor-pointer"
          >
            <div className="flex flex-col sm:flex-row gap-3 justify-between">
              <div className="flex-1 space-y-2">
                <div className="flex flex-wrap items-center gap-2">
                  <h3 className="font-semibold text-base">{deck.name}</h3>
                  <DeckCategoryBadge category={deck.deckCategory} />
                  <DeckDifficultyBadge difficulty={deck.deckDifficulty} />
                  <DeckVisibilityBadge visibility={deck.visibility} />
                </div>

                {deck.deckDescription && (
                  <p className="text-sm text-muted-foreground line-clamp-1">
                    {deck.deckDescription}
                  </p>
                )}

                <div className="flex flex-wrap items-center gap-4 text-xs text-muted-foreground">
                  <span className="flex items-center gap-1">
                    <Layers className="w-3.5 h-3.5" />
                    {deck.wordCount} słówek
                  </span>
                  <span className="flex items-center gap-1">
                    <Calendar className="w-3.5 h-3.5" />
                    {time(deck.createdAt)}
                  </span>
                </div>
              </div>
            </div>
          </div>
        ))}
      </div>
    </Card>
  );
};
