import { useState, useMemo } from 'react';
import { View, Text, ScrollView, ActivityIndicator } from 'react-native';
import { SafeAreaView } from 'react-native-safe-area-context';
import { Globe, AlertCircle, Sparkles } from 'lucide-react-native';
import { usePublicDecks } from '@/features/community/hooks';
import { CommunityCourseCard, CommunityFilters } from '@/features/community/components';
import type { CommunityCoursesFilters } from '@/features/community/types';

/**
 * Strona kursów społeczności
 */
export default function CommunityPage() {
  const [filters, setFilters] = useState<CommunityCoursesFilters>({
    search: '',
    category: undefined,
    difficulty: undefined,
    sortBy: 'newest',
  });

  const { data: coursesData, isLoading, isError } = usePublicDecks({ size: 50 });

  const courses = useMemo(() => coursesData?.content ?? [], [coursesData?.content]);

  const filteredAndSortedCourses = useMemo(() => {
    let result = [...courses];

    if (filters.search) {
      const searchLower = filters.search.toLowerCase();
      result = result.filter(
        (course) =>
          course.name.toLowerCase().includes(searchLower) ||
          course.deckDescription?.toLowerCase().includes(searchLower)
      );
    }

    if (filters.category) {
      result = result.filter((course) => course.deckCategory === filters.category);
    }

    if (filters.difficulty) {
      result = result.filter((course) => course.deckDifficulty === filters.difficulty);
    }

    switch (filters.sortBy) {
      case 'newest':
        result.sort((a, b) => new Date(b.createdAt).getTime() - new Date(a.createdAt).getTime());
        break;
      case 'oldest':
        result.sort((a, b) => new Date(a.createdAt).getTime() - new Date(b.createdAt).getTime());
        break;
    }

    return result;
  }, [courses, filters]);

  return (
    <SafeAreaView className="flex-1 bg-background" edges={['top']}>
      <ScrollView className="flex-1" showsVerticalScrollIndicator={false}>
        <View className="p-4 pb-8">
          {/* Header */}
          <View className="mb-6">
            <View className="mb-2 flex-row items-center gap-3">
              <Globe size={32} className="text-primary" />
              <Text className="text-3xl font-bold text-foreground">Kursy społeczności</Text>
            </View>
            <Text className="text-base text-muted-foreground">
              Przeglądaj i dołączaj do kursów tworzonych przez innych użytkowników
            </Text>
          </View>

          {/* Filtry */}
          <View className="mb-6">
            <CommunityFilters
              filters={filters}
              onFiltersChange={setFilters}
              resultsCount={filteredAndSortedCourses.length}
            />
          </View>

          {/* Lista kursów */}
          {isLoading ? (
            <View className="items-center justify-center py-12">
              <ActivityIndicator size="large" />
              <Text className="mt-2 text-muted-foreground">Ładowanie kursów...</Text>
            </View>
          ) : isError ? (
            <View className="items-center justify-center py-12">
              <View className="mb-4 h-16 w-16 items-center justify-center rounded-full bg-destructive/10">
                <AlertCircle size={32} className="text-destructive" />
              </View>
              <Text className="mb-2 text-xl font-semibold text-foreground">Błąd ładowania</Text>
              <Text className="max-w-xs text-center text-muted-foreground">
                Nie udało się pobrać kursów. Spróbuj odświeżyć stronę.
              </Text>
            </View>
          ) : filteredAndSortedCourses.length === 0 ? (
            <View className="items-center justify-center py-12">
              <View className="mb-4 h-16 w-16 items-center justify-center rounded-full bg-muted">
                <Globe size={32} className="text-muted-foreground" />
              </View>
              <Text className="mb-2 text-xl font-semibold text-foreground">Brak kursów</Text>
              <Text className="max-w-xs text-center text-muted-foreground">
                {courses.length === 0
                  ? 'Nie ma jeszcze żadnych publicznych kursów.'
                  : 'Nie znaleziono kursów spełniających wybrane kryteria. Spróbuj zmienić filtry.'}
              </Text>
            </View>
          ) : (
            <View className="gap-4">
              <View className="mb-2 flex-row items-center gap-2">
                <Sparkles size={18} className="text-primary" />
                <Text className="text-lg font-semibold text-foreground">Dostępne kursy</Text>
              </View>
              {filteredAndSortedCourses.map((course) => (
                <CommunityCourseCard key={course.id} course={course} />
              ))}
            </View>
          )}
        </View>
      </ScrollView>
    </SafeAreaView>
  );
}
