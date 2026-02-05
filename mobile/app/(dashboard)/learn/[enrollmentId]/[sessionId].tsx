import { useEffect, useRef } from 'react';
import {
  View,
  Text,
  TouchableOpacity,
  ActivityIndicator,
  ScrollView,
  KeyboardAvoidingView,
  Platform,
} from 'react-native';
import { SafeAreaView } from 'react-native-safe-area-context';
import { useLocalSearchParams, router } from 'expo-router';
import { ArrowLeft } from 'lucide-react-native';
import {
  useLearning,
  SessionCompletedView,
  StepRenderer,
  SessionProgress,
} from '@/features/learning';
import { useCourse } from '@/features/course';
import { isNoMoreFlashcardsError } from '../../../../lib/api';
import type { SubmitAnswerRequest, NextFlashcardRecommendation } from '@/features/learning';

/**
 * Ekran sesji nauki - wyświetla fiszki jedna po drugiej
 */
export default function LearnSessionScreen() {
  const { enrollmentId, sessionId } = useLocalSearchParams<{
    enrollmentId: string;
    sessionId: string;
  }>();

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
      <SafeAreaView className="flex-1 items-center justify-center bg-background" edges={['top']}>
        <ActivityIndicator size="large" className="text-primary" />
        <Text className="mt-4 text-muted-foreground">Ładowanie fiszki...</Text>
      </SafeAreaView>
    );
  }

  if (noMore) {
    return (
      <SafeAreaView className="flex-1 bg-background" edges={['top']}>
        <SessionCompletedView courseId={enrollmentId} />
      </SafeAreaView>
    );
  }

  if (isError) {
    return (
      <SafeAreaView
        className="flex-1 items-center justify-center bg-background p-6"
        edges={['top']}>
        <Text className="mb-4 text-lg font-semibold text-destructive">
          Nie udało się pobrać fiszki
        </Text>
        <TouchableOpacity className="rounded-xl bg-primary px-6 py-3" onPress={() => refetch()}>
          <Text className="font-semibold text-primary-foreground">Spróbuj ponownie</Text>
        </TouchableOpacity>
      </SafeAreaView>
    );
  }

  // Brak danych
  if (!courseHeader || !courseProgress || !courseSettings) {
    return null;
  }

  if (!currentFlashcard) {
    return (
      <SafeAreaView className="flex-1 items-center justify-center bg-background" edges={['top']}>
        <Text className="text-muted-foreground">Brak danych fiszki.</Text>
      </SafeAreaView>
    );
  }

  const currentSession = courseProgress.sessions.find((s) => s.status === 'IN_PROGRESS');

  return (
    <SafeAreaView className="flex-1 bg-background" edges={['top']}>
      <KeyboardAvoidingView
        className="flex-1"
        behavior={Platform.OS === 'ios' ? 'padding' : 'height'}
        keyboardVerticalOffset={0}>
        <ScrollView
          className="flex-1"
          contentContainerStyle={{ flexGrow: 1 }}
          keyboardShouldPersistTaps="handled">
          {/* Header */}
          <View className="p-4">
            <View className="mb-4 flex-row items-center justify-between">
              <TouchableOpacity
                className="flex-row items-center gap-2 py-2"
                onPress={() => router.back()}
                disabled={submitAnswer.isPending}>
                <ArrowLeft size={20} className="text-foreground" />
                <Text className="font-medium text-foreground">Zakończ sesję</Text>
              </TouchableOpacity>

              <View className="items-end">
                <Text className="text-sm text-muted-foreground">{courseHeader.name}</Text>
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
          <View className="mt-4 flex-1">
            <StepRenderer
              interactionType={currentFlashcard.interactionType}
              flashcardId={currentFlashcard.flashcardId}
              wordContent={currentFlashcard.content}
              quizOptions={currentFlashcard.quizOptions}
              onStepComplete={handleStepComplete}
            />
          </View>
        </ScrollView>
      </KeyboardAvoidingView>
    </SafeAreaView>
  );
}
