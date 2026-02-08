import { View, Text, TouchableOpacity, ScrollView, ActivityIndicator } from 'react-native';
import { SafeAreaView } from 'react-native-safe-area-context';
import { useLocalSearchParams, useRouter } from 'expo-router';
import { ArrowLeft, Clock, AlertCircle, Target, CheckCircle2 } from 'lucide-react-native';
import { useReview } from '@/features/review/hooks';
import { ReviewWordList } from '@/features/review/components';

/**
 * Główny ekran powtórki - statystyki + lista słówek
 */
export default function ReviewPage() {
  const router = useRouter();
  const { enrollmentId } = useLocalSearchParams<{ enrollmentId: string }>();
  const { useReviewHeader } = useReview();

  const { data: reviewHeader, isLoading, isError } = useReviewHeader(enrollmentId ?? '');

  const handleStartReview = () => {
    router.push(`/review/${enrollmentId}/session`);
  };

  if (isLoading) {
    return (
      <SafeAreaView className="flex-1 items-center justify-center bg-background">
        <ActivityIndicator size="large" />
        <Text className="mt-2 text-muted-foreground">Ładowanie danych powtórek...</Text>
      </SafeAreaView>
    );
  }

  if (isError || !reviewHeader) {
    return (
      <SafeAreaView className="flex-1 bg-background p-4">
        <TouchableOpacity
          className="flex-row items-center gap-2 py-2"
          onPress={() => router.back()}>
          <ArrowLeft size={20} className="text-foreground" />
          <Text className="font-medium text-foreground">Powrót</Text>
        </TouchableOpacity>
        <View className="flex-1 items-center justify-center">
          <Text className="font-semibold text-destructive">
            Wystąpił błąd podczas ładowania danych powtórek.
          </Text>
        </View>
      </SafeAreaView>
    );
  }

  const countToReview = reviewHeader.counters.wordsForToday + reviewHeader.counters.overdueWords;

  return (
    <SafeAreaView className="flex-1 bg-background" edges={['top']}>
      <ScrollView className="flex-1" contentContainerClassName="p-4 pb-8">
        {/* Header */}
        <TouchableOpacity
          className="mb-4 flex-row items-center gap-2 py-2"
          onPress={() => router.back()}>
          <ArrowLeft size={20} className="text-foreground" />
          <Text className="font-medium text-foreground">Powrót do kursu</Text>
        </TouchableOpacity>

        {/* Title */}
        <View className="mb-6">
          <Text className="mb-2 text-3xl font-bold text-foreground">Słówka do powtórki</Text>
          <Text className="text-base text-muted-foreground">
            Odśwież swoją wiedzę i utrwal poznane słówka
          </Text>
        </View>

        {/* Stats Cards */}
        <View className="mb-6 flex-row flex-wrap gap-3">
          {/* Total to review */}
          <View className="min-w-[45%] flex-1 rounded-xl border border-border bg-card p-4">
            <View className="flex-row items-center gap-3">
              <View className="rounded-lg bg-primary/10 p-2">
                <Target size={20} className="text-primary" />
              </View>
              <View>
                <Text className="text-xs text-muted-foreground">Do powtórki</Text>
                <Text className="text-2xl font-bold text-foreground">
                  {reviewHeader.counters.totalWordsToReview}
                </Text>
              </View>
            </View>
          </View>

          {/* Na dzisiaj */}
          <View className="min-w-[45%] flex-1 rounded-xl border border-border bg-card p-4">
            <View className="flex-row items-center gap-3">
              <View className="rounded-lg bg-blue-500/10 p-2">
                <Clock size={20} className="text-blue-600" />
              </View>
              <View>
                <Text className="text-xs text-muted-foreground">Na dziś</Text>
                <Text className="text-2xl font-bold text-foreground">
                  {reviewHeader.counters.wordsForToday}
                </Text>
              </View>
            </View>
          </View>

          {/* Over */}
          <View className="min-w-[45%] flex-1 rounded-xl border border-border bg-card p-4">
            <View className="flex-row items-center gap-3">
              <View className="rounded-lg bg-orange-500/10 p-2">
                <AlertCircle size={20} className="text-orange-600" />
              </View>
              <View>
                <Text className="text-xs text-muted-foreground">Zaległe</Text>
                <Text className="text-2xl font-bold text-foreground">
                  {reviewHeader.counters.overdueWords}
                </Text>
              </View>
            </View>
          </View>
        </View>

        {/* Start Review Card */}
        <View className="mb-6 rounded-xl border border-primary/20 bg-primary/5 p-4">
          {countToReview === 0 ? (
            <View className="flex-row items-center gap-4 rounded-xl border border-green-500/30 bg-green-500/5 p-4">
              <View className="rounded-full bg-green-500/10 p-3">
                <CheckCircle2 size={24} className="text-green-600" />
              </View>
              <View className="flex-1">
                <Text className="mb-1 text-lg font-bold text-foreground">
                  Wszystko powtórzone 🎉
                </Text>
                <Text className="text-muted-foreground">
                  Na dziś nie masz już słówek do powtórki. Świetna robota!
                </Text>
              </View>
            </View>
          ) : (
            <View className="gap-4">
              <View>
                <Text className="mb-1 text-lg font-bold text-foreground">Gotowy do powtórki?</Text>
                <Text className="text-muted-foreground">
                  Powtórzysz {countToReview} słówek w trybie pisania
                </Text>
              </View>
              <TouchableOpacity
                className="flex-row items-center justify-center gap-2 rounded-xl bg-primary py-3"
                onPress={handleStartReview}>
                <Target size={20} className="text-primary-foreground" />
                <Text className="text-base font-semibold text-primary-foreground">
                  Rozpocznij powtórkę
                </Text>
              </TouchableOpacity>
            </View>
          )}
        </View>

        {/* Lista  */}
        <ReviewWordList enrollmentId={enrollmentId ?? ''} />
      </ScrollView>
    </SafeAreaView>
  );
}
