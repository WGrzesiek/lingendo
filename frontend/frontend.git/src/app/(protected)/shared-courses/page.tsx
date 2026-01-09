"use client";

import { useState } from "react";
import { Card, CardHeader, CardTitle, CardContent } from "@/components/ui/card";
import { Button } from "@/components/ui/button";
import { Badge } from "@/components/ui/badge";
import {
  BookOpen,
  Users,
  UserCircle,
  Loader2,
  AlertCircle,
  Languages,
  CheckCircle,
} from "lucide-react";
import { useRouter } from "next/navigation";
import { useInfiniteSharedWithMe } from "@/features/deck-share/hooks/useDeckShare";

import { DeckCategoryBadge } from "@/features/deck/components/deck/DeckCategoryBadge";
import { DeckDifficultyBadge } from "@/features/deck/components/deck/DeckDifficultyBadge";
import type { SharedDeckDto } from "@/features/deck-share/types/deckShare.types";
import type {
  DeckCategory,
  DeckDifficulty,
} from "@/features/deck/types/deck.types";
import { useEnrollToDeck } from "@/features/deckEnrollment";

/**
 * Karta udostępnionego kursu
 */
function SharedDeckCard({ deck }: { deck: SharedDeckDto }) {
  const router = useRouter();
  const [isEnrolled, setIsEnrolled] = useState(false);

  const enrollMutation = useEnrollToDeck();

  const handleEnroll = (e: React.MouseEvent) => {
    e.stopPropagation();
    enrollMutation.mutate(
      { deckId: deck.deckId },
      {
        onSuccess: () => {
          setIsEnrolled(true);
        },
      }
    );
  };

  const handleCardClick = () => {
    router.push(`/my-courses/${deck.deckId}/details`);
  };

  return (
    <Card
      className="group p-5 hover:shadow-lg hover:border-primary/50 transition-all cursor-pointer"
      onClick={handleCardClick}
    >
      <div className="space-y-4">
        {/* Tytuł i badge'y */}
        <div className="space-y-2">
          <div className="flex items-center justify-between gap-2">
            <h3 className="font-semibold text-lg tracking-tight group-hover:text-primary transition-colors line-clamp-1">
              {deck.deckName}
            </h3>
            {deck.sharedViaName && (
              <Badge variant="secondary" className="shrink-0 text-xs">
                {deck.sharedViaName}
              </Badge>
            )}
          </div>
          <div className="flex flex-wrap items-center gap-2">
            {deck.category && (
              <DeckCategoryBadge category={deck.category as DeckCategory} />
            )}
            {deck.difficulty && (
              <DeckDifficultyBadge
                difficulty={deck.difficulty as DeckDifficulty}
              />
            )}
          </div>
          <p className="text-sm text-muted-foreground line-clamp-2 leading-relaxed">
            {deck.description || "Brak opisu kursu."}
          </p>
        </div>

        {/* Statystyki kursu */}
        <div className="flex flex-wrap gap-4 text-xs text-muted-foreground pt-2 border-t border-border/40">
          <span className="flex items-center gap-1.5">
            <UserCircle className="w-3.5 h-3.5" />
            {deck.ownerName}
          </span>
          <span className="flex items-center gap-1.5">
            <BookOpen className="w-3.5 h-3.5" />
            {deck.flashcardCount} fiszek
          </span>
          {deck.languageFrom && deck.languageTo && (
            <span className="flex items-center gap-1.5">
              <Languages className="w-3.5 h-3.5" />
              {deck.languageFrom} → {deck.languageTo}
            </span>
          )}
        </div>

        {/* Wiadomość od nauczyciela */}
        {deck.message && (
          <div className="text-sm italic text-muted-foreground border-l-2 border-primary/30 pl-3">
            &quot;{deck.message}&quot;
          </div>
        )}

        {/* Przycisk zapisu */}
        {isEnrolled ? (
          <>
            <div className="flex items-center gap-2 text-sm text-green-600">
              <CheckCircle className="w-4 h-4" />
              <span>Zapisano na kurs!</span>
            </div>
            <Button
              className="w-full"
              size="sm"
              onClick={(e) => {
                e.stopPropagation();
                router.push(`/course/${deck.deckId}`);
              }}
            >
              Rozpocznij naukę
            </Button>
          </>
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
        )}

        {/* Komunikat o błędzie */}
        {enrollMutation.isError && (
          <div className="flex items-center gap-2 text-sm text-destructive">
            <AlertCircle className="w-4 h-4" />
            <span>Błąd podczas zapisywania. Spróbuj ponownie.</span>
          </div>
        )}
      </div>
    </Card>
  );
}

/**
 * Strona udostępnionych kursów
 */
export default function SharedCoursesPage() {
  const {
    data,
    isLoading,
    isError,
    error,
    fetchNextPage,
    hasNextPage,
    isFetchingNextPage,
  } = useInfiniteSharedWithMe();

  const decks = data?.pages.flatMap((page) => page.content) ?? [];

  if (isLoading) {
    return (
      <div className="min-h-screen bg-background flex items-center justify-center">
        <div className="flex flex-col items-center gap-3">
          <Loader2 className="w-8 h-8 animate-spin text-primary" />
          <p className="text-muted-foreground">Ładowanie kursów...</p>
        </div>
      </div>
    );
  }

  if (isError) {
    return (
      <div className="min-h-screen bg-background flex items-center justify-center">
        <div className="flex flex-col items-center gap-3 text-destructive">
          <AlertCircle className="w-8 h-8" />
          <p>Błąd podczas ładowania kursów</p>
          <p className="text-sm text-muted-foreground">
            {error?.message || "Spróbuj ponownie później"}
          </p>
        </div>
      </div>
    );
  }

  return (
    <div className="min-h-screen bg-background">
      <div className="container mx-auto p-6 lg:p-8 space-y-8">
        {/* Header */}
        <div className="space-y-1">
          <h1 className="text-3xl font-bold">Udostępnione kursy</h1>
          <p className="text-muted-foreground">
            Kursy udostępnione Tobie przez nauczycieli i znajomych
          </p>
        </div>

        {/* Statystyka */}
        <Card className="border-l-4 border-l-primary w-fit">
          <CardContent className="pt-6 pb-4 px-6">
            <div className="flex items-center gap-3">
              <div className="p-3 bg-primary/10 rounded-lg">
                <BookOpen className="w-6 h-6 text-primary" />
              </div>
              <div>
                <p className="text-2xl font-bold">{decks.length}</p>
                <p className="text-sm text-muted-foreground">
                  Udostępnionych kursów
                </p>
              </div>
            </div>
          </CardContent>
        </Card>

        {/* Lista kursów */}
        <Card>
          <CardHeader>
            <CardTitle>Twoje udostępnione kursy</CardTitle>
          </CardHeader>

          <CardContent>
            {decks.length === 0 ? (
              <div className="text-center py-12">
                <Users className="w-12 h-12 mx-auto text-muted-foreground/50 mb-4" />
                <h3 className="text-lg font-medium mb-2">
                  Brak udostępnionych kursów
                </h3>
                <p className="text-muted-foreground max-w-md mx-auto">
                  Gdy nauczyciel lub znajomy udostępni Ci kurs, pojawi się
                  tutaj.
                </p>
              </div>
            ) : (
              <>
                <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-4">
                  {decks.map((deck) => (
                    <SharedDeckCard key={deck.deckId} deck={deck} />
                  ))}
                </div>

                {hasNextPage && (
                  <div className="flex justify-center mt-6">
                    <Button
                      variant="outline"
                      onClick={() => fetchNextPage()}
                      disabled={isFetchingNextPage}
                    >
                      {isFetchingNextPage ? (
                        <>
                          <Loader2 className="w-4 h-4 mr-2 animate-spin" />
                          Ładowanie...
                        </>
                      ) : (
                        "Załaduj więcej"
                      )}
                    </Button>
                  </div>
                )}
              </>
            )}
          </CardContent>
        </Card>
      </div>
    </div>
  );
}
