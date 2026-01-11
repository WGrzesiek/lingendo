import { Button } from "@/components/ui/button";
import { Skeleton } from "@/components/ui/skeleton";
import { useInfiniteIDecks } from "@/features/deck/hooks/useInfiniteIDecks";
import {
  ArrowRight,
  ChevronDown,
  Frown,
  Layers,
  Loader2,
  PlusCircle,
} from "lucide-react";
import { DeckCardForDashboard } from "@/features/deck/components/deck/DeckCardForDashboard";
import Link from "next/link";

const DeckListSkeleton = () => (
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

const EmptyState = () => (
  <div className="flex flex-col items-center justify-center py-12 text-center border rounded-xl border-dashed bg-muted/20">
    <div className="bg-muted p-3 rounded-full mb-3">
      <PlusCircle className="w-6 h-6 text-muted-foreground" />
    </div>
    <h3 className="font-semibold text-lg">Brak aktywnych kursów</h3>
    <p className="text-sm text-muted-foreground max-w-xs mb-4">
      Wygląda na to, że nie zapisałeś się jeszcze do żadnego kursu.
    </p>
    <Button variant="outline">Przeglądaj katalog</Button>
  </div>
);

export const DecksForDashboard = () => {
  const {
    data,
    isLoading,
    isError,
    fetchNextPage,
    hasNextPage,
    isFetchingNextPage,
  } = useInfiniteIDecks(4);

  if (isLoading) return <DeckListSkeleton />;

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
  const allDecks = data?.pages.flatMap((page) => page.content) || [];

  if (allDecks.length === 0) {
    return <EmptyState />;
  }

  return (
    <div className="space-y-6">
      <div className="space-y-4">
        {allDecks.map((deck) => (
          <DeckCardForDashboard key={deck.deckId} deck={deck} />
        ))}
      </div>

      <div className="flex flex-col sm:flex-row gap-3 justify-center pt-2 border-t border-border/40 mt-6">
        {hasNextPage && (
          <Button
            variant="secondary"
            onClick={() => fetchNextPage()}
            disabled={isFetchingNextPage}
            className="w-full sm:w-auto"
          >
            {isFetchingNextPage ? (
              <Loader2 className="w-4 h-4 mr-2 animate-spin" />
            ) : (
              <ChevronDown className="w-4 h-4 mr-2" />
            )}
            {isFetchingNextPage ? "Ładowanie..." : "Załaduj więcej"}
          </Button>
        )}
        <Button variant="outline" asChild className="w-full sm:w-auto">
          <Link href="/decks">
            {" "}
            <Layers className="w-4 h-4 mr-2" />
            Wszystkie zapisane kursy
            <ArrowRight className="w-4 h-4 ml-2 opacity-50" />
          </Link>
        </Button>
      </div>

      {!hasNextPage && allDecks.length > 0 && (
        <p className="text-center text-xs text-muted-foreground">
          Wyświetlono wszystkie dostępne kursy ({allDecks.length}).
        </p>
      )}
    </div>
  );
};
