import {
  View,
  Text,
  TouchableOpacity,
  ActivityIndicator,
  KeyboardAvoidingView,
  Platform,
} from 'react-native';
import { SafeAreaView } from 'react-native-safe-area-context';
import { useLocalSearchParams, useRouter } from 'expo-router';
import { ArrowLeft } from 'lucide-react-native';
import { useReview } from '@/features/review/hooks';
import { ReviewCompletedView, ReviewTypingStep } from '@/features/review/components';
import type { TypingAnswer } from '@/features/learning/types';
import { isNoMoreFlashcardsToReviewError } from '../../../../lib/api/error';

/**
 * Ekran sesji powtórki - wpisywanie odpowiedzi
 */
export default function ReviewSessionPage() {
  const router = useRouter();
  const { enrollmentId } = useLocalSearchParams<{ enrollmentId: string }>();
  const { useNextFlashcardReview, useSubmitAnswerReview } = useReview();

  const { data, isLoading, isError, error, refetch } = useNextFlashcardReview(enrollmentId ?? '');
  const submitMutation = useSubmitAnswerReview();

  const currentFlashcard = data ?? null;
  const noMore = isError && isNoMoreFlashcardsToReviewError(error);

  const handleStepComplete = async (answer: TypingAnswer) => {
    if (!currentFlashcard) return;

    try {
      await submitMutation.mutateAsync({
        flashcardId: currentFlashcard.flashcardId,
        enrollmentId: enrollmentId ?? '',
        answer,
      });
    } catch (e) {
      console.error('Submit answer failed:', e);
    }
  };

  if (isLoading) {
    return (
      <SafeAreaView className="flex-1 items-center justify-center bg-background">
        <ActivityIndicator size="large" />
        <Text className="mt-2 text-muted-foreground">Ładowanie fiszki...</Text>
      </SafeAreaView>
    );
  }

  if (noMore) {
    return <ReviewCompletedView />;
  }

  if (isError) {
    return (
      <SafeAreaView className="flex-1 items-center justify-center bg-background p-4">
        <Text className="mb-4 text-center font-semibold text-destructive">
          Nie udało się pobrać fiszki
        </Text>
        <TouchableOpacity className="rounded-xl bg-primary px-6 py-3" onPress={() => refetch()}>
          <Text className="font-medium text-primary-foreground">Spróbuj ponownie</Text>
        </TouchableOpacity>
      </SafeAreaView>
    );
  }

  if (!currentFlashcard) {
    return (
      <SafeAreaView className="flex-1 items-center justify-center bg-background">
        <Text className="text-muted-foreground">Brak danych fiszki.</Text>
      </SafeAreaView>
    );
  }

  return (
    <SafeAreaView className="flex-1 bg-background" edges={['top']}>
      <KeyboardAvoidingView
        className="flex-1"
        behavior={Platform.OS === 'ios' ? 'padding' : 'height'}
        keyboardVerticalOffset={Platform.OS === 'ios' ? 0 : 20}>
        {/* Header */}
        <View className="px-4 py-2">
          <TouchableOpacity
            className="flex-row items-center gap-2 py-2"
            onPress={() => router.back()}
            disabled={submitMutation.isPending}>
            <ArrowLeft size={20} className="text-foreground" />
            <Text className="font-medium text-foreground">Zakończ powtórkę</Text>
          </TouchableOpacity>
        </View>

        {/* Content */}
        <ReviewTypingStep
          data={currentFlashcard.content}
          interactionType={currentFlashcard.interactionType}
          onComplete={handleStepComplete}
        />
      </KeyboardAvoidingView>
    </SafeAreaView>
  );
}
