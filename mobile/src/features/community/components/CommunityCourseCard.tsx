import { useState, useEffect } from 'react';
import { View, Text, TouchableOpacity, ActivityIndicator } from 'react-native';
import { CheckCircle, AlertCircle, BookOpen, Calendar } from 'lucide-react-native';
import { router } from 'expo-router';
import type { PublicDeckItem } from '../types';
import { useEnrollment } from '@/features/enroll';
import { deckCategoryConfig, deckDifficultyConfig } from '@/features/deck/types/deck.types';

interface CommunityCourseCardProps {
  course: PublicDeckItem;
}

/**
 * Karta kursu społeczności - wersja mobilna
 */
export const CommunityCourseCard = ({ course }: CommunityCourseCardProps) => {
  const [isEnrolled, setIsEnrolled] = useState(false);
  const { useEnrollToDeck } = useEnrollment();
  const enrollMutation = useEnrollToDeck();

  const handleEnroll = () => {
    enrollMutation.mutate(
      { deckId: course.id },
      {
        onSuccess: () => {
          setIsEnrolled(true);
        },
      }
    );
  };

  useEffect(() => {
    if (!isEnrolled) return;

    const timeout = setTimeout(() => {
      router.push('/(dashboard)/student');
    }, 2000);

    return () => clearTimeout(timeout);
  }, [isEnrolled]);

  const formatDate = (dateString: string) => {
    return new Date(dateString).toLocaleDateString('pl-PL', {
      day: 'numeric',
      month: 'short',
      year: 'numeric',
    });
  };

  return (
    <View className="rounded-xl border border-border bg-card p-4">
      {/* Tytuł */}
      <Text className="mb-2 text-lg font-semibold text-foreground" numberOfLines={1}>
        {course.name}
      </Text>

      {/* Badge'y */}
      <View className="mb-3 flex-row flex-wrap gap-2">
        {course.deckCategory && (
          <View className="rounded-md bg-primary/10 px-2 py-1">
            <Text className="text-xs font-medium text-primary">
              {deckCategoryConfig[course.deckCategory]?.label ?? course.deckCategory}
            </Text>
          </View>
        )}
        {course.deckDifficulty && (
          <View className="rounded-md bg-secondary px-2 py-1">
            <Text className="text-xs font-medium text-secondary-foreground">
              {deckDifficultyConfig[course.deckDifficulty]?.label ?? course.deckDifficulty}
            </Text>
          </View>
        )}
        {course.languageFrom && course.languageTo && (
          <View className="rounded-md bg-muted px-2 py-1">
            <Text className="text-xs text-muted-foreground">
              {course.languageFrom} → {course.languageTo}
            </Text>
          </View>
        )}
      </View>

      {/* Opis */}
      <Text className="mb-3 text-sm text-muted-foreground" numberOfLines={2}>
        {course.deckDescription || 'Brak opisu kursu.'}
      </Text>

      {/* Statystyki */}
      <View className="mb-4 flex-row gap-4 border-t border-border/40 pt-2">
        <View className="flex-row items-center gap-1">
          <BookOpen size={14} className="text-muted-foreground" />
          <Text className="text-xs text-muted-foreground">{course.wordCount} słówek</Text>
        </View>
        <View className="flex-row items-center gap-1">
          <Calendar size={14} className="text-muted-foreground" />
          <Text className="text-xs text-muted-foreground">{formatDate(course.createdAt)}</Text>
        </View>
      </View>

      {/* Przycisk zapisu */}
      {isEnrolled ? (
        <View className="flex-row items-center gap-2 py-2">
          <CheckCircle size={16} className="text-green-600" />
          <Text className="text-sm text-green-600">Zapisano na kurs! Przekierowanie...</Text>
        </View>
      ) : (
        <TouchableOpacity
          className={`items-center rounded-xl py-3 ${enrollMutation.isPending ? 'bg-muted' : 'bg-primary'}`}
          onPress={handleEnroll}
          disabled={enrollMutation.isPending}>
          {enrollMutation.isPending ? (
            <View className="flex-row items-center gap-2">
              <ActivityIndicator size="small" color="white" />
              <Text className="font-medium text-white">Zapisywanie...</Text>
            </View>
          ) : (
            <Text className="font-semibold text-primary-foreground">Zapisz się na kurs</Text>
          )}
        </TouchableOpacity>
      )}

      {/* Błąd */}
      {enrollMutation.isError && (
        <View className="mt-2 flex-row items-center gap-2">
          <AlertCircle size={14} className="text-destructive" />
          <Text className="text-sm text-destructive">
            Błąd podczas zapisywania. Spróbuj ponownie.
          </Text>
        </View>
      )}
    </View>
  );
};
