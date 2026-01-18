import { useEffect, useRef } from 'react';
import { View, Text, TouchableOpacity, ActivityIndicator, ScrollView } from 'react-native';
import { useLocalSearchParams, router } from 'expo-router';
import { ArrowLeft } from 'lucide-react-native';
import { useLearning, SessionCompletedView, StepRenderer, SessionProgress } from '@/features/learning';
import { useCourse } from '@/features/course';
import { isNoMoreFlashcardsError } from '../../../../lib/api';
import type { SubmitAnswerRequest, NextFlashcardRecommendation } from '@/features/learning';

/**
 * Ekran sesji nauki - wyświetla fiszki jedna po drugiej
 */
export default function LearnSessionScreen() {
  const { enrollmentId, sessionId } = useLocalSearchParams<{ enrollmentId: string; sessionId: string }>();

  const { useCourseHeader, useCourseProgress, useCourseSettings } = useCourse();
  const { useNextFlashcard, useSubmitAnswer, useCompleteSession } = useLearning();

  // Dane kursu
  const { data: courseHeader, isLoading: isHeaderLoading } = useCourseHeader(enrollmentId || '');
  const { data: courseProgress } = useCourseProgress(enrollmentId || '');
  const { data: courseSettings } = useCourseSettings(enrollmentId || '');

  // Następna fiszka
  const { data, isLoading, isError, error, refetch } = useNextFlashcard(sessionId || '');

  // Mutacje
  const submitAnswer = useSubmitAnswer();
  const completeSession = useCompleteSession();

  const currentFlashcard: NextFlashcardRecommendation | null = data ?? null;
  const noMore = isError && isNoMoreFlashcardsError(error);

  const didCompleteRef = useRef(false);

  useEffect(() => {
    if (!noMore || !sessionId) return;
    if (didCompleteRef.current) return;

    didCompleteRef.current = true;
    completeSession.mutate(sessionId);
  }, [noMore, sessionId, completeSession]);

  const handleStepComplete = async (answer: SubmitAnswerRequest) => {
    if (!currentFlashcard || !sessionId) return;

    try {
      await submitAnswer.mutateAsync({
        sessionId,
        flashcardId: currentFlashcard.flashcardId,
        answer,
      });
    } catch (e) {
      console.error('Submit answer failed:', e);
    }
  };

  if (isLoading || isHeaderLoading) {
    return (
      <View className="flex-1 bg-background justify-center items-center">
        <ActivityIndicator size="large" className="text-primary" />
        <Text className="text-muted-foreground mt-4">Ładowanie fiszki...</Text>
      </View>
    );
  }

  if (noMore) {
    return <SessionCompletedView courseId={enrollmentId} />;
  }

  if (isError) {
    return (
      <View className="flex-1 bg-background justify-center items-center p-6">
        <Text className="text-destructive font-semibold text-lg mb-4">Nie udało się pobrać fiszki</Text>
        <TouchableOpacity className="bg-primary px-6 py-3 rounded-xl" onPress={() => refetch()}>
          <Text className="text-primary-foreground font-semibold">Spróbuj ponownie</Text>
        </TouchableOpacity>
      </View>
    );
  }

  // Brak danych
  if (!courseHeader || !courseProgress || !courseSettings) {
    return null;
  }

  if (!currentFlashcard) {
    return (
      <View className="flex-1 bg-background justify-center items-center">
        <Text className="text-muted-foreground">Brak danych fiszki.</Text>
      </View>
    );
  }

  const currentSession = courseProgress.sessions.find((s) => s.status === 'IN_PROGRESS');

  return (
    <View className="flex-1 bg-background">
      <ScrollView className="flex-1">
        {/* Header */}
        <View className="p-4">
          <View className="flex-row items-center justify-between mb-4">
            <TouchableOpacity
              className="flex-row items-center gap-2 py-2"
              onPress={() => router.back()}
              disabled={submitAnswer.isPending}
            >
              <ArrowLeft size={20} className="text-foreground" />
              <Text className="text-foreground font-medium">Zakończ sesję</Text>
            </TouchableOpacity>

            <View className="items-end">
              <Text className="text-sm text-muted-foreground">
                {courseHeader.name}
              </Text>
              {currentSession && (
                <Text className="text-xs text-muted-foreground">
                  Sesja {currentSession.sessionNumber}
                </Text>
              )}
            </View>
          </View>
        </View>

        {/* Pasek postępu */}
        {sessionId && <SessionProgress sessionId={sessionId} />}

        {/* Krok nauki */}
        <View className="mt-4">
          <StepRenderer
            interactionType={currentFlashcard.interactionType}
            flashcardId={currentFlashcard.flashcardId}
            wordContent={currentFlashcard.content}
            quizOptions={currentFlashcard.quizOptions}
            onStepComplete={handleStepComplete}
          />
        </View>
      </ScrollView>
    </View>
  );
}
