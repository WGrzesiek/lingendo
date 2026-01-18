import React from 'react';
import { View, Text, ScrollView, TouchableOpacity, ActivityIndicator } from 'react-native';
import { SafeAreaView } from 'react-native-safe-area-context';
import { router, useLocalSearchParams } from 'expo-router';
import { useDeck } from '@/features/deck';
import {
  deckDifficultyConfig,
  deckCategoryConfig,
  deckOwnerConfig,
} from '@/features/deck/types/deck.types';

/**
 * Ekran szczegółów kursu
 */
function CourseScreen() {
  const { id } = useLocalSearchParams<{ id: string }>();

  const { useDeckWithEnrollment, useDeckStatistics, useDeckFlashcards } = useDeck();

  const {
    data: deckDetails,
    isLoading: isDetailsLoading,
    isError: isDetailsError,
  } = useDeckWithEnrollment(id ?? '');

  const { data: deckStats, isLoading: isStatsLoading } = useDeckStatistics(id ?? '');

  const { data: flashcardsData, isLoading: isFlashcardsLoading } = useDeckFlashcards(
    id ?? '',
    0,
    10
  );

  const isLoading = isDetailsLoading;

  if (isLoading) {
    return (
      <SafeAreaView className="flex-1 items-center justify-center bg-background">
        <ActivityIndicator size="large" color="#22c55e" />
        <Text className="mt-4 text-muted-foreground">Ładowanie kursu...</Text>
      </SafeAreaView>
    );
  }

  if (isDetailsError || !deckDetails) {
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

  const progress = deckDetails.enrollment?.progressPercentage ?? 0;
  const difficulty = deckDifficultyConfig[deckDetails.deck?.deckDifficulty]?.label ?? 'Nieznany';
  const category = deckCategoryConfig[deckDetails.deck?.deckCategory]?.label ?? 'Inna';
  const owner = deckOwnerConfig[deckDetails.deck?.deckOwner]?.label ?? 'Nieznany';

  const handleStartLesson = () => {
    // TODO: Nawigacja do sesji nauki
    router.push(`/(dashboard)/learn/${id}`);
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
            {deckDetails.deck?.name}
          </Text>
        </View>

        <View className="p-4">
          {/* Główne info o kursie */}
          <View className="mb-6 rounded-xl border border-border bg-card p-4">
            <Text className="mb-2 text-2xl font-bold text-foreground">
              {deckDetails.deck?.name}
            </Text>
            <Text className="mb-4 text-muted-foreground">
              {deckDetails.deck?.deckDescription || 'Brak opisu'}
            </Text>

            {/* Tagi */}
            <View className="mb-4 flex-row flex-wrap gap-2">
              <View className="rounded-full bg-primary-light px-3 py-1">
                <Text className="text-xs font-medium text-primary-dark">{difficulty}</Text>
              </View>
              <View className="rounded-full bg-muted px-3 py-1">
                <Text className="text-xs font-medium text-muted-foreground">{category}</Text>
              </View>
              <View className="rounded-full bg-muted px-3 py-1">
                <Text className="text-xs font-medium text-muted-foreground">{owner}</Text>
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

            {isStatsLoading ? (
              <ActivityIndicator size="small" color="#22c55e" />
            ) : deckStats ? (
              <View className="flex-row flex-wrap">
                <View className="mb-4 w-1/2 pr-2">
                  <Text className="text-2xl font-bold text-foreground">
                    {deckStats.totalFlashcards}
                  </Text>
                  <Text className="text-sm text-muted-foreground">Wszystkich fiszek</Text>
                </View>
                <View className="mb-4 w-1/2 pl-2">
                  <Text className="text-2xl font-bold text-success">
                    {deckStats.learnedFlashcards}
                  </Text>
                  <Text className="text-sm text-muted-foreground">Nauczonych</Text>
                </View>
                <View className="mb-4 w-1/2 pr-2">
                  <Text className="text-2xl font-bold text-warning">
                    {deckStats.unlearnedFlashcards}
                  </Text>
                  <Text className="text-sm text-muted-foreground">Do nauki</Text>
                </View>
                <View className="mb-4 w-1/2 pl-2">
                  <Text className="text-2xl font-bold text-foreground">
                    {deckStats.completedSessions}/{deckStats.totalSessions}
                  </Text>
                  <Text className="text-sm text-muted-foreground">Sesji ukończonych</Text>
                </View>
              </View>
            ) : (
              <Text className="text-muted-foreground">Brak statystyk</Text>
            )}
          </View>

          {/* Podgląd fiszek */}
          <View className="mb-6 rounded-xl border border-border bg-card p-4">
            <View className="mb-4 flex-row items-center justify-between">
              <Text className="text-lg font-bold text-foreground">Fiszki w kursie</Text>
              {flashcardsData?.totalElements && (
                <Text className="text-sm text-muted-foreground">
                  {flashcardsData.totalElements} słówek
                </Text>
              )}
            </View>

            {isFlashcardsLoading ? (
              <ActivityIndicator size="small" color="#22c55e" />
            ) : flashcardsData?.content && flashcardsData.content.length > 0 ? (
              <View>
                {flashcardsData.content.slice(0, 5).map((flashcard) => (
                  <View
                    key={flashcard.id}
                    className="mb-2 rounded-lg border border-border bg-background p-3">
                    <Text className="font-semibold text-foreground">{flashcard.word}</Text>
                    <Text className="text-sm text-muted-foreground">
                      {flashcard.translations.join(', ')}
                    </Text>
                  </View>
                ))}
                {flashcardsData.totalElements > 5 && (
                  <Text className="mt-2 text-center text-sm text-muted-foreground">
                    ...i {flashcardsData.totalElements - 5} więcej
                  </Text>
                )}
              </View>
            ) : (
              <Text className="text-center text-muted-foreground">
                Ten kurs nie ma jeszcze fiszek
              </Text>
            )}
          </View>

          {/* Informacje o zapisie */}
          {deckDetails.enrollment && (
            <View className="mb-6 rounded-xl border border-border bg-card p-4">
              <Text className="mb-4 text-lg font-bold text-foreground">Twój zapis</Text>
              <View className="flex-row justify-between">
                <Text className="text-muted-foreground">Zapisano</Text>
                <Text className="font-medium text-foreground">
                  {deckDetails.enrollment.enrolledAt
                    ? new Date(deckDetails.enrollment.enrolledAt).toLocaleDateString('pl-PL')
                    : 'Nieznane'}
                </Text>
              </View>
              <View className="mt-2 flex-row justify-between">
                <Text className="text-muted-foreground">Ukończone sesje</Text>
                <Text className="font-medium text-foreground">
                  {deckDetails.enrollment.completedSessions}/{deckDetails.enrollment.totalSessions}
                </Text>
              </View>
            </View>
          )}
        </View>
      </ScrollView>
    </SafeAreaView>
  );
}

export default CourseScreen;
