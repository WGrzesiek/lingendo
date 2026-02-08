import { useState, useEffect } from 'react';
import { View, Text, TouchableOpacity, ActivityIndicator } from 'react-native';
import { CheckCircle, AlertCircle, BookOpen, Calendar, Globe, Lock, ChevronRight } from 'lucide-react-native';
import { router } from 'expo-router';
import type { CreatedDeckListItem } from '../types';
import { useEnrollment } from '@/features/enroll';
import { deckCategoryConfig, deckDifficultyConfig } from '@/features/deck/types/deck.types';

interface MyCourseDeckCardProps {
  deck: CreatedDeckListItem;
}

/**
 * Karta kursu utworzonego przez usera
 */
export const MyCourseDeckCard = ({ deck }: MyCourseDeckCardProps) => {
  const handlePress = () => {
    router.push(`/(dashboard)/deck/${deck.id}`);
  };

  const [isEnrolled, setIsEnrolled] = useState(false);
  const { useEnrollToDeck } = useEnrollment();
  const enrollMutation = useEnrollToDeck();

  const isPublic = deck.visibility === 'PUBLIC';

  const handleEnroll = () => {
    enrollMutation.mutate(
      { deckId: deck.id },
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
    <TouchableOpacity
      onPress={handlePress}
      activeOpacity={0.7}
      className="rounded-xl border border-border bg-card p-4">
      {/* Tytuł i status widoczności */}
      <View className="mb-2 flex-row items-center justify-between">
        <Text className="flex-1 text-lg font-semibold text-foreground" numberOfLines={1}>
          {deck.name}
        </Text>
        <View className="flex-row items-center gap-2">
          <View
            className={`rounded-full px-2 py-1 ${isPublic ? 'bg-green-500/20' : 'bg-orange-500/20'}`}>
            {isPublic ? (
              <Globe size={14} className="text-green-500" />
            ) : (
              <Lock size={14} className="text-orange-500" />
            )}
          </View>
          <ChevronRight size={20} className="text-muted-foreground" />
        </View>
      </View>

      {/* Badge'y */}
      <View className="mb-3 flex-row flex-wrap gap-2">
        {deck.deckCategory && (
          <View className="rounded-md bg-primary/10 px-2 py-1">
            <Text className="text-xs font-medium text-primary">
              {deckCategoryConfig[deck.deckCategory]?.label ?? deck.deckCategory}
            </Text>
          </View>
        )}
        {deck.deckDifficulty && (
          <View className="rounded-md bg-secondary px-2 py-1">
            <Text className="text-xs font-medium text-secondary-foreground">
              {deckDifficultyConfig[deck.deckDifficulty]?.label ?? deck.deckDifficulty}
            </Text>
          </View>
        )}
        {deck.languageFrom && deck.languageTo && (
          <View className="rounded-md bg-muted px-2 py-1">
            <Text className="text-xs text-muted-foreground">
              {deck.languageFrom} → {deck.languageTo}
            </Text>
          </View>
        )}
      </View>

      {/* Opis */}
      <Text className="mb-3 text-sm text-muted-foreground" numberOfLines={2}>
        {deck.deckDescription || 'Brak opisu kursu.'}
      </Text>

      {/* Statystyki */}
      <View className="mb-4 flex-row gap-4 border-t border-border/40 pt-2">
        <View className="flex-row items-center gap-1">
          <BookOpen size={14} className="text-muted-foreground" />
          <Text className="text-xs text-muted-foreground">{deck.wordCount} słówek</Text>
        </View>
        <View className="flex-row items-center gap-1">
          <Calendar size={14} className="text-muted-foreground" />
          <Text className="text-xs text-muted-foreground">{formatDate(deck.createdAt)}</Text>
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
            <Text className="font-semibold text-primary-foreground">Rozpocznij naukę</Text>
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
    </TouchableOpacity>
  );
};
