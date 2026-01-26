"use client";

import { Card } from "@/components/ui/card";
import { Badge } from "@/components/ui/badge";
import { Button } from "@/components/ui/button";
import { Input } from "@/components/ui/input";
import { Search, BookOpen, Loader2 } from "lucide-react";
import { useState, useMemo, useEffect } from "react";
import { DeckDetailsHeader } from "@/features/deck/components/details/DeckDetailsHeader";
import { DeckDetailsStats } from "@/features/deck/components/details/DeckDetailsStats";
import { WordCard } from "@/features/deck/components/details/WordCard";
import { useDeckDetail } from "@/features/deck/hooks/useDeckDetail";
import { useInfiniteDeckFlashcards } from "@/features/deck/hooks/useInfiniteDeckFlashcards";
import { useMyDeckStats } from "@/features/deck/hooks/useMyDeckStats";
import { useCurrentUser } from "@/features/auth/hooks/useCurrentUser";
import type {
  DeckDetails,
  DeckStats,
} from "@/features/deck/types/deck-details.types";
import type {
  DeckCategory,
  DeckDifficulty,
} from "@/features/deck/types/deck.types";
import type { DeckVisibility } from "@/features/deck/types/created-deck.types";

/**
 * Strona szczegółów decka - uniwersalna dla owner i enrolled
 * Pokazuje informacje o kursie, słówka, statystyki (dla właściciela)
 */
export default function DeckDetailsClient({ deckId }: { deckId: string }) {
  const [searchQuery, setSearchQuery] = useState("");
  const [lastScrollElement, setLastScrollElement] =
    useState<HTMLDivElement | null>(null);
  console.log("deckId", deckId);
  // Pobieranie danych użytkownika
  const { data: currentUser, isLoading: isUserLoading } = useCurrentUser();
  console.log(deckId);
  // Pobieranie szczegółów decka
  const {
    data: deckDetail,
    isLoading: isDeckLoading,
    error: deckError,
  } = useDeckDetail(deckId);

  // Sprawdzenie czy user jest właścicielem
  const isOwner =
    currentUser && deckDetail
      ? currentUser.userId === deckDetail.ownerId
      : false;

  // Pobieranie statystyk (tylko dla właściciela)
  const { data: statsData } = useMyDeckStats(isOwner ? [deckId] : []);

  const {
    data: flashcardsData,
    isLoading: isFlashcardsLoading,
    fetchNextPage,
    hasNextPage,
    isFetchingNextPage,
  } = useInfiniteDeckFlashcards(deckId);

  useEffect(() => {
    if (!lastScrollElement || !hasNextPage || isFetchingNextPage) return;

    const observer = new IntersectionObserver(
      (entries) => {
        if (entries[0].isIntersecting) {
          fetchNextPage();
        }
      },
      { threshold: 0.1 }
    );

    observer.observe(lastScrollElement);
    return () => observer.disconnect();
  }, [lastScrollElement, hasNextPage, isFetchingNextPage, fetchNextPage]);

  const allFlashcards = useMemo(() => {
    return flashcardsData?.pages.flatMap((page) => page.content) ?? [];
  }, [flashcardsData]);
  const totalElements = flashcardsData?.pages.flatMap(
    (page) => page.totalElements
  );
  const filteredWords = useMemo(() => {
    if (!searchQuery.trim()) return allFlashcards;

    const query = searchQuery.toLowerCase();
    return allFlashcards.filter(
      (flashcard) =>
        flashcard.word.toLowerCase().includes(query) ||
        flashcard.translations.some((t) => t.toLowerCase().includes(query))
    );
  }, [allFlashcards, searchQuery]);

  const deck: DeckDetails | null = deckDetail
    ? {
        id: deckDetail.id,
        name: deckDetail.name,
        description: deckDetail.deckDescription || "",
        category: deckDetail.deckCategory as DeckCategory,
        difficulty: deckDetail.deckDifficulty as DeckDifficulty,
        visibility: deckDetail.visibility as DeckVisibility,
        languageFrom: deckDetail.languageFrom,
        languageTo: deckDetail.languageTo,
        wordCount: deckDetail.wordCount,
        createdAt: deckDetail.createdAt,
        updatedAt: deckDetail.updatedAt,
        createdBy: {
          id: deckDetail.ownerId,
          username: deckDetail.username,
        },
        isOwner,
        isTeacher: currentUser?.accountType === "TEACHER",
      }
    : null;

  const stats: DeckStats | null =
    statsData && statsData[deckId]
      ? {
          totalStudents: statsData[deckId].totalStudents || 0,
          completedStudents: statsData[deckId].completedStudents || 0,
          activeStudents:
            statsData[deckId].totalStudents -
            (statsData[deckId].completedStudents || 0),
          //NOTE dorobic event w stats - ile wyswietlen ma kurs(tz ile osób go oglądało)
        }
      : null;

  if (isDeckLoading || isUserLoading) {
    return (
      <div className="min-h-screen bg-background flex items-center justify-center">
        <Loader2 className="w-8 h-8 animate-spin" />
      </div>
    );
  }

  if (deckError || !deck) {
    return (
      <div className="min-h-screen bg-background flex items-center justify-center">
        <Card className="p-6">
          <p className="text-destructive">Nie udało się załadować deckaaaaa</p>
        </Card>
      </div>
    );
  }

  return (
    <div className="min-h-screen bg-background">
      <div className="container mx-auto p-6 lg:p-8 space-y-6">
        {/* Header */}
        <DeckDetailsHeader deck={deck} />

        {currentUser?.accountType === "TEACHER" && deck.isOwner && stats && (
          <DeckDetailsStats stats={stats} />
        )}

        {/* Lista słówek */}
        <Card className="p-6">
          <div className="space-y-6">
            {/* Header listy */}
            <div className="flex flex-col sm:flex-row items-start sm:items-center justify-between gap-4">
              <div>
                <h2 className="text-2xl font-bold mb-1 flex items-center gap-2">
                  <BookOpen className="w-6 h-6" />
                  Lista słówek
                </h2>
                <p className="text-muted-foreground">
                  Wszystkie słówka w tym kursie
                </p>
              </div>
              <Badge variant="secondary" className="text-lg px-4 py-2">
                {totalElements} słówek
              </Badge>
            </div>

            {/* Search */}
            <div className="relative">
              <Search className="absolute left-3 top-1/2 -translate-y-1/2 w-5 h-5 text-muted-foreground" />
              <Input
                type="text"
                placeholder="Szukaj słówka..."
                value={searchQuery}
                onChange={(e) => setSearchQuery(e.target.value)}
                className="pl-10 h-12 text-base"
              />
            </div>

            {/* Words list */}
            {isFlashcardsLoading && filteredWords.length === 0 ? (
              <div className="flex items-center justify-center py-12">
                <Loader2 className="w-8 h-8 animate-spin" />
              </div>
            ) : filteredWords.length > 0 ? (
              <>
                <div className="space-y-3">
                  {filteredWords.map((flashcard) => (
                    <WordCard key={flashcard.id} word={flashcard} />
                  ))}
                </div>

                {/* Infinite scroll trigger */}
                {hasNextPage && (
                  <div
                    ref={setLastScrollElement}
                    className="flex items-center justify-center py-4"
                  >
                    {isFetchingNextPage && (
                      <Loader2 className="w-6 h-6 animate-spin" />
                    )}
                  </div>
                )}
              </>
            ) : (
              <div className="flex flex-col items-center justify-center py-12 text-center border rounded-xl border-dashed bg-muted/20">
                <div className="bg-muted p-3 rounded-full mb-3">
                  <Search className="w-6 h-6 text-muted-foreground" />
                </div>
                <h3 className="font-semibold text-lg mb-1">
                  Nie znaleziono słówek
                </h3>
                <p className="text-sm text-muted-foreground max-w-xs">
                  {searchQuery
                    ? "Spróbuj zmienić zapytanie lub wyczyść wyszukiwanie"
                    : "Ten deck nie zawiera jeszcze żadnych słówek"}
                </p>
                {searchQuery && (
                  <Button
                    variant="outline"
                    className="mt-4"
                    onClick={() => setSearchQuery("")}
                  >
                    Wyczyść wyszukiwanie
                  </Button>
                )}
              </div>
            )}
          </div>
        </Card>
      </div>
    </div>
  );
}
