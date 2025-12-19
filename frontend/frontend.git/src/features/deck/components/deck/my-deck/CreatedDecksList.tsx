import { Button } from "@/components/ui/button";
import { Skeleton } from "@/components/ui/skeleton";
import { PlusCircle, Frown, BookPlus, ArrowRight } from "lucide-react";
import { CreatedDeckCard } from "./CreatedDeckCard";
import {DecksStats, ICreatedDeckListItem} from "../../../types/created-deck.types";
import Link from "next/link";
import {useMyDeckStats} from "@/features/deck/hooks/useMyDeckStats";

const DeckListSkeleton = () => (
  <div className="space-y-4">
    {[1, 2, 3].map((i) => (
      <div key={i} className="p-5 border rounded-xl space-y-4">
        <div className="flex justify-between">
          <div className="space-y-2 w-2/3">
            <Skeleton className="h-6 w-1/3" />
            <Skeleton className="h-4 w-full" />
          </div>
          <div className="flex gap-2">
            <Skeleton className="h-9 w-20 rounded-md" />
            <Skeleton className="h-9 w-32 rounded-md" />
          </div>
        </div>
        <div className="flex gap-4">
          <Skeleton className="h-4 w-24" />
          <Skeleton className="h-4 w-32" />
        </div>
      </div>
    ))}
  </div>
);

const EmptyState = () => (
  <div className="flex flex-col items-center justify-center py-16 text-center border rounded-xl border-dashed bg-muted/20">
    <div className="bg-muted p-4 rounded-full mb-4">
      <BookPlus className="w-8 h-8 text-muted-foreground" />
    </div>
    <h3 className="font-semibold text-lg mb-2">Brak utworzonych kursów</h3>
    <p className="text-sm text-muted-foreground max-w-sm mb-6">
      Stwórz swój pierwszy kurs i udostępnij go innym lub wykorzystaj do własnej
      nauki.
    </p>
    <Button asChild>
      <Link href="/decks/create">
        <PlusCircle className="w-4 h-4 mr-2" />
        Utwórz pierwszy kurs
      </Link>
    </Button>
  </div>
);

interface CreatedDecksListProps {
  decks?: ICreatedDeckListItem[];
  isLoading?: boolean;
  isError?: boolean;
  onLoadMore?: () => void;
  hasNextPage?: boolean;
  isFetchingNextPage?: boolean;
}

/**
 * Komponent listy kursów utworzonych przez użytkownika
 * Wyświetla karty kursów z możliwością edycji i dodawania słówek
 */
export const CreatedDecksList = ({
  decks = [],
  isLoading = false,
  isError = false,
  onLoadMore,
  hasNextPage = false,
  isFetchingNextPage = false,
}: CreatedDecksListProps) => {
    const decksIds = decks?.map(d => d.id);
    console.log(decksIds);
    const {data: decksStats, isLoading: decksStatsIsLoading, isError: decksStatsIsError} = useMyDeckStats(decksIds);
  if (isLoading || decksStatsIsLoading) return <DeckListSkeleton />;
  if (isError || decksStatsIsError) {
    return (
      <div className="p-6 border border-destructive/50 rounded-lg bg-destructive/10 text-destructive flex items-center gap-3">
        <Frown className="h-5 w-5" />
        <span>
          Nie udało się załadować listy kursów. Spróbuj odświeżyć stronę.
        </span>
      </div>
    );
  }

  if (decks.length === 0) {
    return <EmptyState />;
  }

  return (
    <div className="space-y-6">
      <div className="space-y-4">
        {decks.map((deck) => (
            <CreatedDeckCard
                key={deck.id}
                deck={deck}
                deckStat={decksStats ? decksStats[deck.id] : undefined}
            />
        ))}
      </div>

      {hasNextPage && onLoadMore && (
        <div className="flex justify-center pt-4 border-t border-border/40">
          <Button
            variant="secondary"
            onClick={onLoadMore}
            disabled={isFetchingNextPage}
          >
            {isFetchingNextPage ? (
              <>
                <div className="w-4 h-4 mr-2 border-2 border-current border-t-transparent rounded-full animate-spin" />
                Ładowanie...
              </>
            ) : (
              <>
                <ArrowRight className="w-4 h-4 mr-2" />
                Załaduj więcej
              </>
            )}
          </Button>
        </div>
      )}

      {!hasNextPage && decks.length > 0 && (
        <p className="text-center text-xs text-muted-foreground pt-4 border-t border-border/40">
          Wyświetlono wszystkie utworzone kursy ({decks.length}).
        </p>
      )}
    </div>
  );
};
