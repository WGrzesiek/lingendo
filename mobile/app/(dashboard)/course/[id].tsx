import React, { useState, useCallback } from 'react';
import {
  View,
  Text,
  ScrollView,
  TouchableOpacity,
  ActivityIndicator,
  Modal,
  Pressable,
} from 'react-native';
import { SafeAreaView } from 'react-native-safe-area-context';
import { router, useLocalSearchParams } from 'expo-router';
import { useCourse, type CourseWord } from '@/features/course';
import { useEnrollment } from '@/features/enroll';
import {
  deckOwnerConfig,
  learnAlgorithmConfig,
  reviewScheduleConfig,
  learnAlgorithmValues,
  reviewScheduleValues,
  type LearnAlgorithm,
  type ReviewSchedule,
} from '@/features/deck/types/deck.types';

/**
 * Ekran szczegółów kursu
 */
function CourseScreen() {
  const { id: enrollmentId } = useLocalSearchParams<{ id: string }>();
  const [settingsModalVisible, setSettingsModalVisible] = useState(false);

  const {
    useCourseHeader,
    useCourseSettings,
    useCourseProgress,
    useInfiniteCourseWords,
    useInitializeSession,
  } = useCourse();
  const { data: headerData, isLoading: isHeaderLoading } = useCourseHeader(enrollmentId);
  const { data: courseProgress, isLoading: isProgressLoading } = useCourseProgress(enrollmentId);
  const { data: settingsData, isLoading: isSettingsLoading } = useCourseSettings(enrollmentId);
  const {
    data: wordsData,
    isLoading: isWordsLoading,
    fetchNextPage,
    hasNextPage,
    isFetchingNextPage,
  } = useInfiniteCourseWords(enrollmentId, 10);
  const initializeSession = useInitializeSession();

  const allWords = wordsData?.pages.flatMap((page) => page.content) ?? [];
  const totalWords = wordsData?.pages[0]?.totalElements ?? 0;

  const handleLoadMoreWords = useCallback(() => {
    if (hasNextPage && !isFetchingNextPage) {
      fetchNextPage();
    }
  }, [hasNextPage, isFetchingNextPage, fetchNextPage]);

  const isLoading = isHeaderLoading || isProgressLoading || isSettingsLoading;

  if (isLoading) {
    return (
      <SafeAreaView className="flex-1 items-center justify-center bg-background">
        <ActivityIndicator size="large" color="#22c55e" />
        <Text className="mt-4 text-muted-foreground">Ładowanie kursu...</Text>
      </SafeAreaView>
    );
  }

  if (!headerData || !settingsData || !courseProgress) {
    return (
      <SafeAreaView className="flex-1 items-center justify-center bg-background p-4">
        <Text className="mb-4 text-center text-lg text-foreground">
          Nie udało się załadować kursu
        </Text>
        <TouchableOpacity onPress={() => router.back()} className="rounded-lg bg-primary px-6 py-3">
          <Text className="font-semibold text-white">Wróć</Text>
        </TouchableOpacity>
      </SafeAreaView>
    );
  }

  const progress =
    courseProgress.totalSessions > 0
      ? Math.round((courseProgress.completedSessions / courseProgress.totalSessions) * 100)
      : 0;
  const sessionToContinue = courseProgress.sessions
    .filter((s) => s.status === 'IN_PROGRESS')
    .sort((a, b) => a.sessionNumber - b.sessionNumber)[0];

  const sessionIdToContinue = sessionToContinue?.sessionId;

  const handleStartLesson = () => {
    if (enrollmentId && sessionIdToContinue) {
      router.push(`/(dashboard)/learn/${enrollmentId}/${sessionIdToContinue}`);
    }
  };

  const handleStartNewSession = () => {
    if (!enrollmentId) return;
    initializeSession.mutate(enrollmentId);
  };

  const handleBack = () => {
    router.back();
  };

  return (
    <SafeAreaView className="flex-1 bg-background">
      <ScrollView className="flex-1" showsVerticalScrollIndicator={false}>
        {/* Header z przyciskiem powrotu */}
        <View className="flex-row items-center px-4 pt-4">
          <TouchableOpacity onPress={handleBack} className="mr-3 rounded-lg bg-muted p-2">
            <Text className="text-xl">←</Text>
          </TouchableOpacity>
          <Text className="flex-1 text-xl font-bold text-foreground" numberOfLines={1}>
            {headerData.name}
          </Text>
        </View>

        <View className="p-4">
          {/* Główne info o kursie */}
          <View className="mb-6 rounded-xl border border-border bg-card p-4">
            <Text className="mb-2 text-2xl font-bold text-foreground">{headerData.name}</Text>
            <Text className="mb-4 text-muted-foreground">
              {headerData.description || 'Brak opisu'}
            </Text>

            {/* Tagi */}
            <View className="mb-4 flex-row flex-wrap gap-2">
              <View className="rounded-full bg-primary-light px-3 py-1">
                <Text className="text-xs font-medium text-primary-dark">
                  {headerData.languageFrom} → {headerData.languageTo}
                </Text>
              </View>
              <View className="rounded-full bg-muted px-3 py-1">
                <Text className="text-xs font-medium text-muted-foreground">
                  {deckOwnerConfig[headerData.ownerType]?.label ?? headerData.ownerType}
                </Text>
              </View>
              <View className="rounded-full bg-muted px-3 py-1">
                <Text className="text-xs font-medium text-muted-foreground">
                  {headerData.visibility === 'PUBLIC' ? 'Publiczny' : 'Prywatny'}
                </Text>
              </View>
            </View>

            {/* Progress */}
            <View className="mb-4">
              <View className="mb-2 flex-row justify-between">
                <Text className="text-sm text-muted-foreground">Postęp</Text>
                <Text className="text-sm font-medium text-foreground">{progress}%</Text>
              </View>
              <View className="h-3 overflow-hidden rounded-full bg-muted">
                <View
                  className="h-full rounded-full bg-primary"
                  style={{ width: `${progress}%` }}
                />
              </View>
            </View>

            {/* Przycisk rozpoczęcia nauki */}
            {sessionIdToContinue ? (
              <TouchableOpacity
                onPress={handleStartLesson}
                className="items-center rounded-xl bg-primary py-4">
                <Text className="text-lg font-bold text-white">Kontynuuj naukę</Text>
              </TouchableOpacity>
            ) : (
              <TouchableOpacity
                onPress={handleStartNewSession}
                disabled={initializeSession.isPending}
                className={`items-center rounded-xl py-4 ${initializeSession.isPending ? 'bg-muted' : 'bg-primary'}`}>
                {initializeSession.isPending ? (
                  <ActivityIndicator color="white" />
                ) : (
                  <Text className="text-lg font-bold text-white">Rozpocznij nową sesję</Text>
                )}
              </TouchableOpacity>
            )}

            {/* Przycisk powtórki */}
            {courseProgress.wordsToReview > 0 && (
              <TouchableOpacity
                onPress={() => router.push(`/(dashboard)/review/${enrollmentId}`)}
                className="mt-3 items-center rounded-xl border-2 border-orange-500 bg-orange-500/10 py-4">
                <Text className="text-lg font-bold text-orange-600">
                  Powtórka ({courseProgress.wordsToReview} słówek)
                </Text>
              </TouchableOpacity>
            )}
          </View>

          {/* Statystyki kursu */}
          <View className="mb-6 rounded-xl border border-border bg-card p-4">
            <Text className="mb-4 text-lg font-bold text-foreground">Statystyki</Text>

            <View className="flex-row flex-wrap">
              <View className="mb-4 w-1/2 pr-2">
                <Text className="text-2xl font-bold text-foreground">
                  {courseProgress.totalWords}
                </Text>
                <Text className="text-sm text-muted-foreground">Wszystkich słówek</Text>
              </View>
              <View className="mb-4 w-1/2 pl-2">
                <Text className="text-2xl font-bold text-success">
                  {courseProgress.totalWords - courseProgress.wordsToReview}
                </Text>
                <Text className="text-sm text-muted-foreground">Nauczonych</Text>
              </View>
              <View className="mb-4 w-1/2 pr-2">
                <Text className="text-2xl font-bold text-warning">
                  {courseProgress.wordsToReview}
                </Text>
                <Text className="text-sm text-muted-foreground">Do powtórki</Text>
              </View>
              <View className="mb-4 w-1/2 pl-2">
                <Text className="text-2xl font-bold text-foreground">
                  {courseProgress.completedSessions}/{courseProgress.totalSessions}
                </Text>
                <Text className="text-sm text-muted-foreground">Sesji ukończonych</Text>
              </View>
            </View>
          </View>

          {/* Podgląd fiszek */}
          <View className="mb-6 rounded-xl border border-border bg-card p-4">
            <View className="mb-4 flex-row items-center justify-between">
              <Text className="text-lg font-bold text-foreground">Fiszki w kursie</Text>
              {totalWords > 0 && (
                <Text className="text-sm text-muted-foreground">{totalWords} słówek</Text>
              )}
            </View>

            {isWordsLoading ? (
              <ActivityIndicator size="small" color="#22c55e" />
            ) : allWords.length > 0 ? (
              <View>
                {allWords.map((word) => (
                  <WordCard key={word.flashcardId} word={word} />
                ))}

                {/* Przycisk "Załaduj więcej" */}
                {hasNextPage && (
                  <TouchableOpacity
                    onPress={handleLoadMoreWords}
                    disabled={isFetchingNextPage}
                    className="mt-3 items-center rounded-lg border border-border py-3">
                    {isFetchingNextPage ? (
                      <ActivityIndicator size="small" color="#22c55e" />
                    ) : (
                      <Text className="font-medium text-primary">Załaduj więcej słówek</Text>
                    )}
                  </TouchableOpacity>
                )}

                {/* Informacja o załadowaniu wszystkich */}
                {!hasNextPage && allWords.length > 0 && (
                  <Text className="mt-3 text-center text-sm text-muted-foreground">
                    Załadowano wszystkie słówka ({allWords.length})
                  </Text>
                )}
              </View>
            ) : (
              <Text className="text-center text-muted-foreground">
                Ten kurs nie ma jeszcze fiszek
              </Text>
            )}
          </View>

          {/* Ustawienia kursu */}
          <TouchableOpacity
            onPress={() => setSettingsModalVisible(true)}
            activeOpacity={0.7}
            className="mb-6 rounded-xl border border-border bg-card p-4">
            <View className="mb-4 flex-row items-center justify-between">
              <Text className="text-lg font-bold text-foreground">Ustawienia nauki</Text>
              <Text className="font-medium text-primary">Zmień ⚙️</Text>
            </View>
            <View className="flex-row justify-between">
              <Text className="text-muted-foreground">Algorytm</Text>
              <Text className="font-medium text-foreground">
                {learnAlgorithmConfig[settingsData.algorithm as LearnAlgorithm]?.label ??
                  settingsData.algorithm}
              </Text>
            </View>
            <View className="mt-2 flex-row justify-between">
              <Text className="text-muted-foreground">Słówek na sesję</Text>
              <Text className="font-medium text-foreground">{settingsData.wordsPerSession}</Text>
            </View>
            <View className="mt-2 flex-row justify-between">
              <Text className="text-muted-foreground">Harmonogram powtórek</Text>
              <Text className="font-medium text-foreground">
                {reviewScheduleConfig[settingsData.reviewSchedule as ReviewSchedule]?.label ??
                  settingsData.reviewSchedule}
              </Text>
            </View>
          </TouchableOpacity>
        </View>
      </ScrollView>

      {/* Modal ustawień */}
      <SettingsModal
        visible={settingsModalVisible}
        onClose={() => setSettingsModalVisible(false)}
        enrollmentId={enrollmentId ?? ''}
        currentAlgorithm={settingsData.algorithm as LearnAlgorithm}
        currentWordsPerSession={settingsData.wordsPerSession}
        currentReviewSchedule={settingsData.reviewSchedule as ReviewSchedule}
      />
    </SafeAreaView>
  );
}

/**
 * Konfiguracja kolorów dla faz fiszek
 */
const phaseConfig = {
  NEW: { label: 'Nowe', bgColor: 'bg-muted', textColor: 'text-muted-foreground' },
  LEARNING: { label: 'W nauce', bgColor: 'bg-warning/20', textColor: 'text-warning' },
  REVIEW: { label: 'Do powtórki', bgColor: 'bg-primary/20', textColor: 'text-primary' },
  RELEARNING: { label: 'Powtórka', bgColor: 'bg-orange-100', textColor: 'text-orange-600' },
} as const;

// =====================
// SETTINGS MODAL
// =====================

interface SettingsModalProps {
  visible: boolean;
  onClose: () => void;
  enrollmentId: string;
  currentAlgorithm: LearnAlgorithm;
  currentWordsPerSession: number;
  currentReviewSchedule: ReviewSchedule;
}

const WORDS_PER_SESSION_OPTIONS = [5, 10, 15, 20, 25, 30, 40, 50];

/**
 * Modal do zmiany ustawień kursu
 */
function SettingsModal({
  visible,
  onClose,
  enrollmentId,
  currentAlgorithm,
  currentWordsPerSession,
  currentReviewSchedule,
}: SettingsModalProps) {
  const [algorithm, setAlgorithm] = useState<LearnAlgorithm>(currentAlgorithm);
  const [wordsPerSession, setWordsPerSession] = useState(currentWordsPerSession);
  const [reviewSchedule, setReviewSchedule] = useState<ReviewSchedule>(currentReviewSchedule);
  const [activeSection, setActiveSection] = useState<'algorithm' | 'words' | 'schedule' | null>(
    null
  );

  const { useUpdateLearnAlgorithm, useUpdateFlashcardsPerSession, useUpdateReviewSchedule } =
    useEnrollment();
  const updateAlgorithmMutation = useUpdateLearnAlgorithm();
  const updateWordsMutation = useUpdateFlashcardsPerSession();
  const updateScheduleMutation = useUpdateReviewSchedule();

  const isSaving =
    updateAlgorithmMutation.isPending ||
    updateWordsMutation.isPending ||
    updateScheduleMutation.isPending;

  React.useEffect(() => {
    if (visible) {
      setAlgorithm(currentAlgorithm);
      setWordsPerSession(currentWordsPerSession);
      setReviewSchedule(currentReviewSchedule);
      setActiveSection(null);
    }
  }, [visible, currentAlgorithm, currentWordsPerSession, currentReviewSchedule]);

  const handleSave = async () => {
    try {
      const promises: Promise<unknown>[] = [];

      if (algorithm !== currentAlgorithm) {
        promises.push(updateAlgorithmMutation.mutateAsync({ enrollmentId, algorithm }));
      }
      if (wordsPerSession !== currentWordsPerSession) {
        promises.push(updateWordsMutation.mutateAsync({ enrollmentId, limit: wordsPerSession }));
      }
      if (reviewSchedule !== currentReviewSchedule) {
        promises.push(updateScheduleMutation.mutateAsync({ enrollmentId, mode: reviewSchedule }));
      }

      await Promise.all(promises);
      onClose();
    } catch (error) {
      console.error('Błąd podczas zapisywania ustawień:', error);
    }
  };

  const hasChanges =
    algorithm !== currentAlgorithm ||
    wordsPerSession !== currentWordsPerSession ||
    reviewSchedule !== currentReviewSchedule;

  return (
    <Modal visible={visible} animationType="slide" transparent={true} onRequestClose={onClose}>
      <View className="flex-1 justify-end bg-black/50">
        <Pressable className="flex-1" onPress={onClose} />
        <View className="max-h-[85%] rounded-t-3xl border-t border-border bg-card">
          {/* Header */}
          <View className="flex-row items-center justify-between border-b border-border p-4">
            <TouchableOpacity onPress={onClose}>
              <Text className="text-base text-muted-foreground">Anuluj</Text>
            </TouchableOpacity>
            <Text className="text-lg font-bold text-foreground">Ustawienia nauki</Text>
            <TouchableOpacity onPress={handleSave} disabled={!hasChanges || isSaving}>
              {isSaving ? (
                <ActivityIndicator size="small" color="#22c55e" />
              ) : (
                <Text
                  className={`text-base font-semibold ${hasChanges ? 'text-primary' : 'text-muted-foreground'}`}>
                  Zapisz
                </Text>
              )}
            </TouchableOpacity>
          </View>

          <ScrollView className="p-4" showsVerticalScrollIndicator={false}>
            {/* Algorytm nauki */}
            <View className="mb-6">
              <TouchableOpacity
                onPress={() => setActiveSection(activeSection === 'algorithm' ? null : 'algorithm')}
                className="mb-2 flex-row items-center justify-between">
                <Text className="text-base font-semibold text-foreground">Algorytm nauki</Text>
                <Text className="text-muted-foreground">
                  {activeSection === 'algorithm' ? '▲' : '▼'}
                </Text>
              </TouchableOpacity>

              <Text className="mb-3 text-sm text-muted-foreground">
                {learnAlgorithmConfig[algorithm]?.label ?? algorithm}
              </Text>

              {activeSection === 'algorithm' && (
                <View className="gap-2">
                  {learnAlgorithmValues.map((alg) => (
                    <TouchableOpacity
                      key={alg}
                      onPress={() => {
                        setAlgorithm(alg);
                        setActiveSection(null);
                      }}
                      className={`rounded-lg border p-3 ${
                        algorithm === alg
                          ? 'border-primary bg-primary/10'
                          : 'border-border bg-background'
                      }`}>
                      <Text
                        className={`font-medium ${algorithm === alg ? 'text-primary' : 'text-foreground'}`}>
                        {learnAlgorithmConfig[alg]?.label ?? alg}
                      </Text>
                    </TouchableOpacity>
                  ))}
                </View>
              )}
            </View>

            {/* Słówka na sesję */}
            <View className="mb-6">
              <TouchableOpacity
                onPress={() => setActiveSection(activeSection === 'words' ? null : 'words')}
                className="mb-2 flex-row items-center justify-between">
                <Text className="text-base font-semibold text-foreground">Słówek na sesję</Text>
                <Text className="text-muted-foreground">
                  {activeSection === 'words' ? '▲' : '▼'}
                </Text>
              </TouchableOpacity>

              <Text className="mb-3 text-sm text-muted-foreground">
                Obecnie: {wordsPerSession} słówek
              </Text>

              {activeSection === 'words' && (
                <View className="flex-row flex-wrap gap-2">
                  {WORDS_PER_SESSION_OPTIONS.map((num) => (
                    <TouchableOpacity
                      key={num}
                      onPress={() => {
                        setWordsPerSession(num);
                        setActiveSection(null);
                      }}
                      className={`rounded-lg border px-4 py-2 ${
                        wordsPerSession === num
                          ? 'border-primary bg-primary/10'
                          : 'border-border bg-background'
                      }`}>
                      <Text
                        className={`font-medium ${wordsPerSession === num ? 'text-primary' : 'text-foreground'}`}>
                        {num}
                      </Text>
                    </TouchableOpacity>
                  ))}
                </View>
              )}
            </View>

            {/* Harmonogram powtórek */}
            <View className="mb-6">
              <TouchableOpacity
                onPress={() => setActiveSection(activeSection === 'schedule' ? null : 'schedule')}
                className="mb-2 flex-row items-center justify-between">
                <Text className="text-base font-semibold text-foreground">
                  Harmonogram powtórek
                </Text>
                <Text className="text-muted-foreground">
                  {activeSection === 'schedule' ? '▲' : '▼'}
                </Text>
              </TouchableOpacity>

              <Text className="mb-3 text-sm text-muted-foreground">
                {reviewScheduleConfig[reviewSchedule]?.label ?? reviewSchedule}
              </Text>

              {activeSection === 'schedule' && (
                <View className="gap-2">
                  {reviewScheduleValues.map((sched) => (
                    <TouchableOpacity
                      key={sched}
                      onPress={() => {
                        setReviewSchedule(sched);
                        setActiveSection(null);
                      }}
                      className={`rounded-lg border p-3 ${
                        reviewSchedule === sched
                          ? 'border-primary bg-primary/10'
                          : 'border-border bg-background'
                      }`}>
                      <Text
                        className={`font-medium ${reviewSchedule === sched ? 'text-primary' : 'text-foreground'}`}>
                        {reviewScheduleConfig[sched]?.label ?? sched}
                      </Text>
                    </TouchableOpacity>
                  ))}
                </View>
              )}
            </View>

            {/* Spacer na dole */}
            <View className="h-8" />
          </ScrollView>
        </View>
      </View>
    </Modal>
  );
}

interface WordCardProps {
  word: CourseWord;
}

/**
 * Karta pojedynczego słówka z pełnymi informacjami
 */
function WordCard({ word }: WordCardProps) {
  const [expanded, setExpanded] = useState(false);
  const phase = phaseConfig[word.phase] ?? phaseConfig.NEW;
  const allSentences = [...(word.sentences ?? []), ...(word.sentencesAI ?? [])];

  return (
    <TouchableOpacity
      onPress={() => setExpanded(!expanded)}
      activeOpacity={0.7}
      className={`mb-3 rounded-lg border p-3 ${
        word.isLearned ? 'border-success/30 bg-success/5' : 'border-border bg-background'
      }`}>
      {/* Nagłówek z słówkiem i tłumaczeniem */}
      <View className="mb-2 flex-row items-center justify-between">
        <View className="flex-1 flex-row flex-wrap items-center gap-1">
          <Text className="text-lg font-semibold text-foreground">{word.word}</Text>
          <Text className="mx-1 text-muted-foreground">→</Text>
          <Text className="text-foreground">{(word.translations ?? []).join(', ')}</Text>
        </View>
        <Text className="ml-2 text-lg text-muted-foreground">{expanded ? '▲' : '▼'}</Text>
      </View>

      {/* Tagi statusu */}
      <View className="mb-2 flex-row flex-wrap gap-2">
        {/* Faza */}
        <View className={`rounded-full px-2 py-0.5 ${phase.bgColor}`}>
          <Text className={`text-xs ${phase.textColor}`}>{phase.label}</Text>
        </View>

        {/* Nauczone */}
        {word.isLearned && (
          <View className="rounded-full bg-success/10 px-2 py-0.5">
            <Text className="text-xs text-success">✓ Nauczone</Text>
          </View>
        )}

        {/* Powtórzenia */}
        {word.repetitionCount > 0 && (
          <View className="rounded-full bg-muted px-2 py-0.5">
            <Text className="text-xs text-muted-foreground">Powtórzeń: {word.repetitionCount}</Text>
          </View>
        )}

        {/* Sesja */}
        {word.sessionNumber > 0 && (
          <View className="rounded-full bg-muted px-2 py-0.5">
            <Text className="text-xs text-muted-foreground">Sesja: {word.sessionNumber}</Text>
          </View>
        )}

        {/* Data powtórki */}
        {word.nextReviewAt && (
          <View className="rounded-full bg-muted px-2 py-0.5">
            <Text className="text-xs text-muted-foreground">
              Powtórka: {new Date(word.nextReviewAt).toLocaleDateString('pl-PL')}
            </Text>
          </View>
        )}
      </View>

      {/* Rozwinięte - zdania przykładowe */}
      {expanded && (
        <View className="mt-2 border-t border-border pt-2">
          {allSentences.length > 0 ? (
            <View className="gap-3">
              {allSentences.map((sentence) => (
                <View key={sentence.id} className="gap-1">
                  <Text className="text-sm italic text-foreground">{sentence.sentence}</Text>
                  <Text className="text-sm italic text-muted-foreground">
                    {sentence.translation}
                  </Text>
                </View>
              ))}
            </View>
          ) : (
            <View className="flex-row items-center gap-2">
              <Text className="text-muted-foreground">✨</Text>
              <Text className="text-sm text-muted-foreground">Brak przykładowych zdań</Text>
            </View>
          )}
        </View>
      )}
    </TouchableOpacity>
  );
}

export default CourseScreen;
