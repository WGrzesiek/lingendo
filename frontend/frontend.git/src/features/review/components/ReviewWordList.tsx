import { Card } from "@/components/ui/card";
import { Button } from "@/components/ui/button";
import { Skeleton } from "@/components/ui/skeleton";
import {
    ChevronDown,
    Loader2,
    Frown,
    PlusCircle,

} from "lucide-react";
import { ReviewWordCard } from "./ReviewWordCard";
import {Badge} from "@/components/ui/badge";
import {useReviewWordsViewInfinite} from "../hooks/hooks";


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

const EmptyState = () => (
    <div className="flex flex-col items-center justify-center py-12 text-center border rounded-xl border-dashed bg-muted/20">
        <div className="bg-muted p-3 rounded-full mb-3">
            <PlusCircle className="w-6 h-6 text-muted-foreground" />
        </div>
        <h3 className="font-semibold text-lg">Brak słówek</h3>
        <p className="text-sm text-muted-foreground max-w-xs mb-4">
            Wygląda na to, że ten kurs nie zawiera jeszcze żadnych słówek.
        </p>
        <Button variant="outline">Przeglądaj kursy</Button>
    </div>
);

export const ReviewWordList = ({ enrollmentId }: { enrollmentId: string }) => {
    const {
        data,
        isLoading,
        isError,
        fetchNextPage,
        hasNextPage,
        isFetchingNextPage,
    } = useReviewWordsViewInfinite(enrollmentId);

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

    const allWords = data.pages.flatMap((page) => page.content);
    const totalElements = data.pages.flatMap((page) => page.totalElements);

    if (data.pages.length === 0) {
        return <EmptyState />;
    }

    return (
        <Card className="p-6">
            <Card className="p-6">
              <div className="mb-6 flex items-center justify-between">
                <div>
                  <h2 className="text-2xl font-bold mb-1">Lista słówek</h2>
                  <p className="text-muted-foreground">
                    Wszystkie słówka czekające na powtórkę
                  </p>
                </div>
                <Badge variant="secondary" className="text-lg px-4 py-2">
                  {totalElements} słówek
                </Badge>
              </div>

              <div className="space-y-4">
                  {allWords
                      .sort((a, b) => {
                          const dateA = a.nextReviewAt ? new Date(a.nextReviewAt).getTime() : Infinity;
                          const dateB = b.nextReviewAt ? new Date(b.nextReviewAt).getTime() : Infinity;
                          return dateA - dateB;
                      })
                      .map((word) => (
                          <ReviewWordCard key={word.id} word={word} />
                      ))}
              </div>
            </Card>

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
            </div>

            {!hasNextPage && allWords.length > 0 && (
                <p className="text-center text-xs text-muted-foreground">
                    Wyświetlono wszystkie słówka do powtórki ({allWords.length}).
                </p>
            )}
        </Card>
    );
};
