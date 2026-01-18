import React, { useState, useCallback } from 'react';
import {
  View,
  Text,
  ScrollView,
  TouchableOpacity,
  ActivityIndicator,
  RefreshControl,
} from 'react-native';
import { SafeAreaView } from 'react-native-safe-area-context';
import { router } from 'expo-router';
import { useDeck } from '@/features/deck';
import {
  deckDifficultyConfig,
  deckCategoryConfig,
  type DeckListItem,
} from '@/features/deck/types/deck.types';

const PAGE_SIZE = 10;

/**
 * Ekran listy wszystkich kursów użytkownika na które jest zapisany
 */
function CoursesScreen() {
  const [page, setPage] = useState(0);
  const [allCourses, setAllCourses] = useState<DeckListItem[]>([]);
  const [hasMoreToLoad, setHasMoreToLoad] = useState(true);

  const { useMyEnrolledDecks } = useDeck();
  const {
    data: enrolledDecks,
    isLoading,
    isError,
    refetch,
    isRefetching,
    isFetching,
  } = useMyEnrolledDecks(page, PAGE_SIZE);

  React.useEffect(() => {
    if (enrolledDecks?.content) {
      if (page === 0) {
        setAllCourses(enrolledDecks.content);
      } else {
        setAllCourses((prev) => {
          const existingIds = new Set(prev.map((c) => c.enrollmentId));
          const newCourses = enrolledDecks.content.filter((c) => !existingIds.has(c.enrollmentId));
          return [...prev, ...newCourses];
        });
      }
      setHasMoreToLoad(!enrolledDecks.last);
    }
  }, [enrolledDecks, page]);

  const handleCoursePress = (enrollmentId: string) => {
    router.push(`/(dashboard)/course/${enrollmentId}`);
  };

  const handleBack = () => {
    router.back();
  };

  const handleLoadMore = useCallback(() => {
    if (!isFetching && hasMoreToLoad) {
      setPage((prev) => prev + 1);
    }
  }, [isFetching, hasMoreToLoad]);

  const handleRefresh = useCallback(() => {
    setPage(0);
    setAllCourses([]);
    setHasMoreToLoad(true);
    refetch();
  }, [refetch]);

  if (isLoading && page === 0) {
    return (
      <SafeAreaView className="flex-1 items-center justify-center bg-background">
        <ActivityIndicator size="large" color="#22c55e" />
        <Text className="mt-4 text-muted-foreground">Ładowanie kursów...</Text>
      </SafeAreaView>
    );
  }

  if (isError && allCourses.length === 0) {
    return (
      <SafeAreaView className="flex-1 items-center justify-center bg-background p-4">
        <Text className="mb-4 text-center text-lg text-foreground">
          Nie udało się załadować kursów
        </Text>
        <TouchableOpacity onPress={handleRefresh} className="rounded-lg bg-primary px-6 py-3">
          <Text className="font-semibold text-white">Spróbuj ponownie</Text>
        </TouchableOpacity>
      </SafeAreaView>
    );
  }

  const totalElements = enrolledDecks?.totalElements ?? allCourses.length;

  return (
    <SafeAreaView className="flex-1 bg-background">
      {/* Header */}
      <View className="flex-row items-center border-b border-border px-4 py-4">
        <TouchableOpacity onPress={handleBack} className="mr-3 rounded-lg bg-muted p-2">
          <Text className="text-xl">←</Text>
        </TouchableOpacity>
        <Text className="flex-1 text-xl font-bold text-foreground">Moje kursy</Text>
        <Text className="text-sm text-muted-foreground">{totalElements} kursów</Text>
      </View>

      <ScrollView
        className="flex-1 p-4"
        showsVerticalScrollIndicator={false}
        refreshControl={
          <RefreshControl
            refreshing={isRefetching && page === 0}
            onRefresh={handleRefresh}
            tintColor="#22c55e"
          />
        }>
        {allCourses.length === 0 ? (
          <View className="flex-1 items-center justify-center py-20">
            <Text className="mb-2 text-center text-lg text-foreground">
              Nie masz jeszcze żadnych kursów
            </Text>
            <Text className="text-center text-muted-foreground">
              Zapisz się na kurs, aby rozpocząć naukę
            </Text>
          </View>
        ) : (
          <View className="gap-4">
            {allCourses.map((course) => (
              <CourseCard
                key={course.enrollmentId}
                course={course}
                onPress={() => handleCoursePress(course.enrollmentId)}
              />
            ))}

            {/* Przycisk "Załaduj więcej" */}
            {hasMoreToLoad && (
              <TouchableOpacity
                onPress={handleLoadMore}
                disabled={isFetching}
                className="mt-2 items-center rounded-xl border border-border bg-card py-4">
                {isFetching ? (
                  <ActivityIndicator size="small" color="#22c55e" />
                ) : (
                  <Text className="font-medium text-primary">Załaduj więcej</Text>
                )}
              </TouchableOpacity>
            )}

            {/* Informacja o załadowaniu wszystkich */}
            {!hasMoreToLoad && allCourses.length > 0 && (
              <Text className="mt-2 text-center text-sm text-muted-foreground">
                Załadowano wszystkie kursy ({allCourses.length})
              </Text>
            )}
          </View>
        )}
      </ScrollView>
    </SafeAreaView>
  );
}

interface CourseCardProps {
  course: DeckListItem;
  onPress: () => void;
}

/**
 * Karta pojedynczego kursu
 */
function CourseCard({ course, onPress }: CourseCardProps) {
  const progress = course.progressPercentage ?? 0;
  const difficulty = deckDifficultyConfig[course.deckDifficulty]?.label ?? 'Nieznany';
  const category = deckCategoryConfig[course.deckCategory]?.label ?? 'Inna';

  return (
    <TouchableOpacity
      onPress={onPress}
      className="rounded-xl border border-border bg-card p-4"
      activeOpacity={0.7}>
      {/* Nagłówek */}
      <View className="mb-2 flex-row items-start justify-between">
        <Text className="flex-1 text-lg font-bold text-foreground" numberOfLines={2}>
          {course.deckName}
        </Text>
        <View className="ml-2 rounded-full bg-primary-light px-2 py-1">
          <Text className="text-xs font-medium text-primary-dark">{progress}%</Text>
        </View>
      </View>

      {/* Opis */}
      {course.deckDescription && (
        <Text className="mb-3 text-sm text-muted-foreground" numberOfLines={2}>
          {course.deckDescription}
        </Text>
      )}

      {/* Tagi */}
      <View className="mb-3 flex-row flex-wrap gap-2">
        <View className="rounded-full bg-muted px-2 py-1">
          <Text className="text-xs text-muted-foreground">{difficulty}</Text>
        </View>
        <View className="rounded-full bg-muted px-2 py-1">
          <Text className="text-xs text-muted-foreground">{category}</Text>
        </View>
        {course.languageFrom && course.languageTo && (
          <View className="rounded-full bg-muted px-2 py-1">
            <Text className="text-xs text-muted-foreground">
              {course.languageFrom} → {course.languageTo}
            </Text>
          </View>
        )}
      </View>

      {/* Pasek postępu */}
      <View className="mb-2">
        <View className="h-2 overflow-hidden rounded-full bg-muted">
          <View className="h-full rounded-full bg-primary" style={{ width: `${progress}%` }} />
        </View>
      </View>

      {/* Statystyki */}
      <View className="flex-row justify-between">
        <Text className="text-xs text-muted-foreground">
          Sesje: {course.learnedSession}/{course.totalSession}
        </Text>
        {course.lastAccessed && (
          <Text className="text-xs text-muted-foreground">
            Ostatnio: {new Date(course.lastAccessed).toLocaleDateString('pl-PL')}
          </Text>
        )}
      </View>
    </TouchableOpacity>
  );
}

export default CoursesScreen;
