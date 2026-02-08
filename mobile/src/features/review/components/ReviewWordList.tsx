import { View, Text, TouchableOpacity, ActivityIndicator } from 'react-native';
import { ChevronDown, Frown, PlusCircle } from 'lucide-react-native';
import { ReviewWordCard } from './ReviewWordCard';
import { useReview } from '../hooks';

const WordListSkeleton = () => (
  <View className="gap-4">
    {[1, 2, 3].map((i) => (
      <View key={i} className="rounded-xl border border-border bg-card p-4">
        <View className="mb-2 h-6 w-1/3 rounded bg-muted" />
        <View className="h-4 w-full rounded bg-muted" />
      </View>
    ))}
  </View>
);

const EmptyState = () => (
  <View className="items-center justify-center rounded-xl border border-dashed border-border bg-muted/20 py-12">
    <View className="mb-3 rounded-full bg-muted p-3">
      <PlusCircle size={24} className="text-muted-foreground" />
    </View>
    <Text className="text-lg font-semibold text-foreground">Brak słówek</Text>
    <Text className="mb-4 mt-1 max-w-xs text-center text-sm text-muted-foreground">
      Wygląda na to, że ten kurs nie zawiera jeszcze żadnych słówek do powtórki.
    </Text>
  </View>
);

interface ReviewWordListProps {
  enrollmentId: string;
}

export const ReviewWordList = ({ enrollmentId }: ReviewWordListProps) => {
  const { useReviewWordsInfinite } = useReview();
  const { data, isLoading, isError, fetchNextPage, hasNextPage, isFetchingNextPage } =
    useReviewWordsInfinite(enrollmentId);

  if (isLoading) return <WordListSkeleton />;

  if (isError || !data) {
    return (
      <View className="flex-row items-center gap-3 rounded-lg border border-destructive/50 bg-destructive/10 p-6">
        <Frown size={20} className="text-destructive" />
        <Text className="flex-1 text-destructive">
          Nie udało się załadować listy słówek. Spróbuj odświeżyć stronę.
        </Text>
      </View>
    );
  }

  const allWords = data.pages.flatMap((page) => page.content);
  const totalElements = data.pages[0]?.totalElements ?? 0;

  if (allWords.length === 0) {
    return <EmptyState />;
  }

  // sortowanie po dacie nextReviewAt rosnąco
  const sortedWords = [...allWords].sort((a, b) => {
    const dateA = a.nextReviewAt ? new Date(a.nextReviewAt).getTime() : Infinity;
    const dateB = b.nextReviewAt ? new Date(b.nextReviewAt).getTime() : Infinity;
    return dateA - dateB;
  });

  return (
    <View className="rounded-xl border border-border bg-card p-4">
      {/* Header */}
      <View className="mb-4 flex-row items-center justify-between">
        <View>
          <Text className="mb-1 text-xl font-bold text-foreground">Lista słówek</Text>
          <Text className="text-sm text-muted-foreground">
            Wszystkie słówka czekające na powtórkę
          </Text>
        </View>
        <View className="rounded-md bg-secondary px-3 py-1">
          <Text className="font-medium text-secondary-foreground">{totalElements} słówek</Text>
        </View>
      </View>

      {/* Word list */}
      <View className="gap-3">
        {sortedWords.map((word) => (
          <ReviewWordCard key={word.id} word={word} />
        ))}
      </View>

      {/* Zaladuj wiecej */}
      {hasNextPage && (
        <TouchableOpacity
          className="mt-4 flex-row items-center justify-center gap-2 rounded-xl bg-secondary py-3"
          onPress={() => fetchNextPage()}
          disabled={isFetchingNextPage}>
          {isFetchingNextPage ? (
            <ActivityIndicator size="small" />
          ) : (
            <ChevronDown size={16} className="text-secondary-foreground" />
          )}
          <Text className="font-medium text-secondary-foreground">
            {isFetchingNextPage ? 'Ładowanie...' : 'Załaduj więcej'}
          </Text>
        </TouchableOpacity>
      )}

      {!hasNextPage && allWords.length > 0 && (
        <Text className="mt-4 text-center text-xs text-muted-foreground">
          Wyświetlono wszystkie słówka do powtórki ({allWords.length}).
        </Text>
      )}
    </View>
  );
};
