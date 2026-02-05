import React from 'react';
import { View, Text, TouchableOpacity, Button, ActivityIndicator } from 'react-native';
import { router } from 'expo-router';
import type { DeckListItem } from '@/features/deck';

interface CourseCardProps {
  deck: DeckListItem;
  onPress: () => void;
}

/**
 * Karta pojedynczego kursu
 */
const CourseCard = ({ deck, onPress }: CourseCardProps) => {
  const progress = deck.progressPercentage ?? 0;
  const progressColor = progress === 100 ? 'bg-success' : 'bg-primary';

  return (
    <TouchableOpacity
      onPress={onPress}
      className="mb-3 rounded-xl border border-border bg-card p-4">
      <View className="mb-2 flex-row items-start justify-between">
        <View className="mr-3 flex-1">
          <Text className="mb-1 text-base font-semibold text-foreground">{deck.deckName}</Text>
          <Text className="text-sm text-muted-foreground" numberOfLines={1}>
            {deck.deckDescription}
          </Text>
        </View>
        <View className="rounded bg-primary-light px-2 py-1">
          <Text className="text-xs font-medium text-primary-dark">{progress}%</Text>
        </View>
      </View>

      {/* Progress bar */}
      <View className="mt-2 h-2 overflow-hidden rounded-full bg-muted">
        <View
          className={`h-full ${progressColor} rounded-full`}
          style={{ width: `${progress}%` }}
        />
      </View>

      <View className="mt-3 flex-row justify-between">
        <Text className="text-xs text-muted-foreground">
          {deck.learnedSession} / {deck.totalSession} sesji nauki
        </Text>
        {deck.lastAccessed && (
          <Text className="text-xs text-muted-foreground">
            Ostatnio: {new Date(deck.lastAccessed).toLocaleDateString('pl-PL')}
          </Text>
        )}
      </View>
    </TouchableOpacity>
  );
};

interface MyCoursesProps {
  decks?: DeckListItem[];
  onDeckPress: (deck: DeckListItem) => void;
}

/**
 * Lista kursów użytkownika
 */
export const MyCourses = ({ decks = [], onDeckPress }: MyCoursesProps) => {
  return (
    <View className="rounded-xl border border-border bg-card p-4">
      <View className="mb-4">
        <Text className="text-lg font-bold text-foreground">Kursy w trakcie nauki</Text>
        <Text className="mt-1 text-sm text-muted-foreground">
          Kontynuuj naukę tam, gdzie skończyłeś
        </Text>
      </View>

      {decks.length === 0 ? (
        <View className="items-center py-8">
          <Text className="mb-2 text-4xl">📖</Text>
          <Text className="text-center text-muted-foreground">
            Nie masz jeszcze żadnych kursów.{'\n'}Zacznij naukę od dodania pierwszego!
          </Text>
        </View>
      ) : (
        <View>
          {decks.map((deck) => (
            <CourseCard key={deck.enrollmentId} deck={deck} onPress={() => onDeckPress(deck)} />
          ))}
          <TouchableOpacity
            onPress={() => router.push('/(dashboard)/courses')}
            className="mt-2 items-center rounded-lg bg-muted py-3">
            <Text className="font-medium text-foreground">Zobacz wszystkie kursy</Text>
          </TouchableOpacity>
        </View>
      )}
    </View>
  );
};
