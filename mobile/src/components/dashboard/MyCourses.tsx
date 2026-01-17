import React from 'react';
import { View, Text, TouchableOpacity } from 'react-native';
import type { Deck } from '../../types/dashboard';

interface CourseCardProps {
  deck: Deck;
  onPress: () => void;
}

/**
 * Karta pojedynczego kursu
 */
const CourseCard = ({ deck, onPress }: CourseCardProps) => {
  const progressColor = deck.progress === 100 ? 'bg-success' : 'bg-primary';

  return (
    <TouchableOpacity
      onPress={onPress}
      className="mb-3 rounded-xl border border-border bg-card p-4">
      <View className="mb-2 flex-row items-start justify-between">
        <View className="mr-3 flex-1">
          <Text className="mb-1 text-base font-semibold text-foreground">{deck.name}</Text>
          <Text className="text-sm text-muted-foreground" numberOfLines={1}>
            {deck.description}
          </Text>
        </View>
        <View className="rounded bg-primary-light px-2 py-1">
          <Text className="text-xs font-medium text-primary-dark">{deck.progress}%</Text>
        </View>
      </View>

      {/* Progress bar */}
      <View className="mt-2 h-2 overflow-hidden rounded-full bg-muted">
        <View
          className={`h-full ${progressColor} rounded-full`}
          style={{ width: `${deck.progress}%` }}
        />
      </View>

      <View className="mt-3 flex-row justify-between">
        <Text className="text-xs text-muted-foreground">
          {deck.learnedCards} / {deck.totalCards} kart
        </Text>
        {deck.lastStudied && (
          <Text className="text-xs text-muted-foreground">
            Ostatnio: {new Date(deck.lastStudied).toLocaleDateString('pl-PL')}
          </Text>
        )}
      </View>
    </TouchableOpacity>
  );
};

interface MyCoursesProps {
  decks: Deck[];
  onDeckPress: (deck: Deck) => void;
}

/**
 * Lista kursów użytkownika
 */
export const MyCourses = ({ decks, onDeckPress }: MyCoursesProps) => {
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
        decks.map((deck) => (
          <CourseCard key={deck.id} deck={deck} onPress={() => onDeckPress(deck)} />
        ))
      )}
    </View>
  );
};
