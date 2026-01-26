"use client";

import { Card, CardContent } from "@/components/ui/card";
import { Button } from "@/components/ui/button";
import { Input } from "@/components/ui/input";
import { Skeleton } from "@/components/ui/skeleton";
import {
  ArrowLeft,
  BookOpen,
  ChevronDown,
  Clock,
  Frown,
  Layers,
  Loader2,
  PlayCircle,
  Search,
} from "lucide-react";
import { useInfiniteIDecks } from "@/features/deck/hooks/useInfiniteIDecks";
import { DeckOwnerBadge } from "@/features/deck/components/deck/DeckOwnerBadge";
import { DeckDifficultyBadge } from "@/features/deck/components/deck/DeckDifficultyBadge";
import { DeckCategoryBadge } from "@/features/deck/components/deck/DeckCategoryBadge";
import { LanguageBadge } from "@/features/deck/components/deck/LanguageBadge";
import { Progress } from "@/components/ui/progress";
import { time } from "@/lib/time";
import { useRouter } from "next/navigation";
import { useState, useMemo } from "react";
import Link from "next/link";

const DeckListSkeleton = () => (
  <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-4">
    {[1, 2, 3, 4, 5, 6].map((i) => (
      <div key={i} className="p-4 border rounded-xl space-y-3">
        <div className="space-y-2">
          <Skeleton className="h-6 w-2/3" />
          <Skeleton className="h-4 w-full" />
          <Skeleton className="h-4 w-3/4" />
        </div>
        <div className="flex gap-2">
          <Skeleton className="h-6 w-20 rounded-full" />
          <Skeleton className="h-6 w-16 rounded-full" />
        </div>
        <Skeleton className="h-2 w-full rounded-full" />
        <Skeleton className="h-9 w-full rounded-md" />
      </div>
    ))}
  </div>
);

const EmptyState = () => (
  <div className="flex flex-col items-center justify-center py-16 text-center">
    <div className="bg-muted p-4 rounded-full mb-4">
      <Layers className="w-8 h-8 text-muted-foreground" />
    </div>
    <h3 className="font-semibold text-xl mb-2">Brak zapisanych kursów</h3>
    <p className="text-muted-foreground max-w-md mb-6">
      Nie zapisałeś się jeszcze do żadnego kursu. Przeglądaj kursy społeczności
      lub utwórz własny kurs.
    </p>
    <div className="flex gap-3">
      <Button variant="outline" asChild>
        <Link href="/community">Przeglądaj kursy</Link>
      </Button>
      <Button asChild>
        <Link href="/my-courses">Moje kursy</Link>
      </Button>
    </div>
  </div>
);

/**
 * Strona z listą wszystkich zapisanych kursów użytkownika
 */
export default function EnrolledCoursesPage() {
  const router = useRouter();
  const [searchQuery, setSearchQuery] = useState("");

  const {
    data,
    isLoading,
    isError,
    fetchNextPage,
    hasNextPage,
    isFetchingNextPage,
  } = useInfiniteIDecks(12);

  const allDecks = useMemo(() => {
    return data?.pages.flatMap((page) => page.content) || [];
  }, [data]);

  const filteredDecks = useMemo(() => {
    if (!searchQuery.trim()) return allDecks;

    const query = searchQuery.toLowerCase();
    return allDecks.filter(
      (deck) =>
        deck.deckName.toLowerCase().includes(query) ||
        deck.deckDescription?.toLowerCase().includes(query)
    );
  }, [allDecks, searchQuery]);

  const totalCount = data?.pages[0]?.totalElements ?? 0;

  return (
    <div className="min-h-screen bg-background">
      <div className="container mx-auto p-6 lg:p-8 space-y-6">
        {/* Header */}
        <div className="flex flex-col sm:flex-row sm:items-center sm:justify-between gap-4">
          <div className="flex items-center gap-4">
            <Button variant="ghost" size="icon" asChild>
              <Link href="/dashboard">
                <ArrowLeft className="w-5 h-5" />
              </Link>
            </Button>
            <div>
              <h1 className="text-3xl font-bold">Moje zapisane kursy</h1>
              <p className="text-muted-foreground">
                Wszystkie kursy, na które jesteś zapisany ({totalCount})
              </p>
            </div>
          </div>
        </div>

        {/* Search */}
        <Card>
          <CardContent className="p-4">
            <div className="relative">
              <Search className="absolute left-3 top-1/2 -translate-y-1/2 w-4 h-4 text-muted-foreground" />
              <Input
                placeholder="Szukaj wśród zapisanych kursów..."
                value={searchQuery}
                onChange={(e) => setSearchQuery(e.target.value)}
                className="pl-10"
              />
            </div>
          </CardContent>
        </Card>

        {/* Content */}
        {isLoading ? (
          <DeckListSkeleton />
        ) : isError ? (
          <Card className="p-6 border-destructive/50 bg-destructive/10">
            <div className="flex items-center gap-3 text-destructive">
              <Frown className="h-5 w-5" />
              <span>
                Nie udało się załadować listy kursów. Spróbuj odświeżyć stronę.
              </span>
            </div>
          </Card>
        ) : filteredDecks.length === 0 ? (
          searchQuery ? (
            <Card className="p-8 text-center">
              <p className="text-muted-foreground">
                Nie znaleziono kursów pasujących do &ldquo;{searchQuery}&rdquo;
              </p>
            </Card>
          ) : (
            <EmptyState />
          )
        ) : (
          <>
            <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-4">
              {filteredDecks.map((deck) => (
                <Card
                  key={deck.deckId}
                  className="group cursor-pointer hover:shadow-md hover:border-primary/50 transition-all"
                  onClick={() =>
                    router.push(`/my-courses/${deck.deckId}/details`)
                  }
                >
                  <CardContent className="p-4 space-y-4">
                    <div className="space-y-2">
                      <h3 className="font-semibold text-lg line-clamp-1 group-hover:text-primary transition-colors">
                        {deck.deckName}
                      </h3>
                      <p className="text-sm text-muted-foreground line-clamp-2">
                        {deck.deckDescription || "Brak opisu kursu."}
                      </p>
                    </div>

                    <div className="flex flex-wrap gap-2">
                      {deck.languageFrom && deck.languageTo && (
                        <LanguageBadge
                          languageFrom={deck.languageFrom}
                          languageTo={deck.languageTo}
                        />
                      )}
                      {deck.deckCategory && (
                        <DeckCategoryBadge category={deck.deckCategory} />
                      )}
                      <DeckOwnerBadge owner={deck.deckOwner} />
                      <DeckDifficultyBadge difficulty={deck.deckDifficulty} />
                    </div>

                    <div className="flex flex-wrap items-center gap-4 text-xs text-muted-foreground">
                      <span className="flex items-center gap-1.5">
                        <BookOpen className="w-3.5 h-3.5" />
                        {deck.learnedSession}/{deck.totalSession} lekcji
                      </span>
                      {deck.lastAccessed && (
                        <span className="flex items-center gap-1.5">
                          <Clock className="w-3.5 h-3.5" />
                          {time(deck.lastAccessed)}
                        </span>
                      )}
                    </div>

                    <div className="space-y-1.5">
                      <div className="flex items-center justify-between text-xs font-medium">
                        <span className="text-muted-foreground">Postęp</span>
                        <span>{deck.progressPercentage ?? 0}%</span>
                      </div>
                      <Progress
                        value={deck.progressPercentage ?? 0}
                        className="h-2"
                      />
                    </div>

                    <Button
                      className="w-full"
                      size="sm"
                      onClick={(e) => {
                        e.stopPropagation();
                        router.push(`/course/${deck.enrollmentId}`);
                      }}
                    >
                      <PlayCircle className="w-4 h-4 mr-2" />
                      Kontynuuj naukę
                    </Button>
                  </CardContent>
                </Card>
              ))}
            </div>

            {/* Load more */}
            {hasNextPage && !searchQuery && (
              <div className="flex justify-center pt-4">
                <Button
                  variant="outline"
                  onClick={() => fetchNextPage()}
                  disabled={isFetchingNextPage}
                >
                  {isFetchingNextPage ? (
                    <Loader2 className="w-4 h-4 mr-2 animate-spin" />
                  ) : (
                    <ChevronDown className="w-4 h-4 mr-2" />
                  )}
                  {isFetchingNextPage ? "Ładowanie..." : "Załaduj więcej"}
                </Button>
              </div>
            )}

            {!hasNextPage && filteredDecks.length > 0 && (
              <p className="text-center text-sm text-muted-foreground pt-4">
                Wyświetlono wszystkie zapisane kursy ({filteredDecks.length})
              </p>
            )}
          </>
        )}
      </div>
    </div>
  );
}
