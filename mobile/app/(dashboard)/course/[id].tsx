import React from 'react';
import { View, Text, ScrollView, TouchableOpacity, ActivityIndicator } from 'react-native';
import { SafeAreaView } from 'react-native-safe-area-context';
import { router, useLocalSearchParams } from 'expo-router';
import { useCourse } from '@/features/course';
import { deckOwnerConfig } from '@/features/deck/types/deck.types';

/**
 * Ekran szczegółów kursu
 */
function CourseScreen() {
  const { id: enrollmentId } = useLocalSearchParams<{ id: string }>();

  const { useCourseHeader, useCourseSettings, useCourseProgress, useInfiniteCourseWords } =
    useCourse();
  const { data: headerData, isLoading: isHeaderLoading } = useCourseHeader(enrollmentId);
  const { data: courseProgress, isLoading: isProgressLoading } = useCourseProgress(enrollmentId);
  const { data: settingsData, isLoading: isSettingsLoading } = useCourseSettings(enrollmentId);
  const { data: wordsData, isLoading: isWordsLoading } = useInfiniteCourseWords(enrollmentId, 10);

  const allWords = wordsData?.pages.flatMap((page) => page.content) ?? [];
  const totalWords = wordsData?.pages[0]?.totalElements ?? 0;

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

  const handleStartLesson = () => {
    if (enrollmentId) {
      router.push(`/(dashboard)/learn/${enrollmentId}`);
    }
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
            <TouchableOpacity
              onPress={handleStartLesson}
              className="items-center rounded-xl bg-primary py-4">
              <Text className="text-lg font-bold text-white">
                {progress > 0 ? 'Kontynuuj naukę' : 'Rozpocznij naukę'}
              </Text>
            </TouchableOpacity>
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
                {allWords.slice(0, 5).map((word) => (
                  <View
                    key={word.flashcardId}
                    className="mb-2 rounded-lg border border-border bg-background p-3">
                    <Text className="font-semibold text-foreground">{word.word}</Text>
                    <Text className="text-sm text-muted-foreground">
                      {word.translations.join(', ')}
                    </Text>
                  </View>
                ))}
                {totalWords > 5 && (
                  <Text className="mt-2 text-center text-sm text-muted-foreground">
                    ...i {totalWords - 5} więcej
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
          <View className="mb-6 rounded-xl border border-border bg-card p-4">
            <Text className="mb-4 text-lg font-bold text-foreground">Ustawienia nauki</Text>
            <View className="flex-row justify-between">
              <Text className="text-muted-foreground">Algorytm</Text>
              <Text className="font-medium text-foreground">{settingsData.algorithm}</Text>
            </View>
            <View className="mt-2 flex-row justify-between">
              <Text className="text-muted-foreground">Słówek na sesję</Text>
              <Text className="font-medium text-foreground">{settingsData.wordsPerSession}</Text>
            </View>
            <View className="mt-2 flex-row justify-between">
              <Text className="text-muted-foreground">Harmonogram powtórek</Text>
              <Text className="font-medium text-foreground">{settingsData.reviewSchedule}</Text>
            </View>
          </View>
        </View>
      </ScrollView>
    </SafeAreaView>
  );
}

export default CourseScreen;
