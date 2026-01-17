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
      className="bg-card rounded-xl p-4 border border-border mb-3"
    >
      <View className="flex-row items-start justify-between mb-2">
        <View className="flex-1 mr-3">
          <Text className="text-base font-semibold text-foreground mb-1">
            {deck.name}
          </Text>
          <Text className="text-sm text-muted-foreground" numberOfLines={1}>
            {deck.description}
          </Text>
        </View>
        <View className="bg-primary-light px-2 py-1 rounded">
          <Text className="text-primary-dark text-xs font-medium">
            {deck.progress}%
          </Text>
        </View>
      </View>

      {/* Progress bar */}
      <View className="h-2 bg-muted rounded-full overflow-hidden mt-2">
        <View
          className={`h-full ${progressColor} rounded-full`}
          style={{ width: `${deck.progress}%` }}
        />
      </View>

      <View className="flex-row justify-between mt-3">
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
    <View className="bg-card rounded-xl p-4 border border-border">
      <View className="mb-4">
        <Text className="text-lg font-bold text-foreground">
          Kursy w trakcie nauki
        </Text>
        <Text className="text-sm text-muted-foreground mt-1">
          Kontynuuj naukę tam, gdzie skończyłeś
        </Text>
      </View>

      {decks.length === 0 ? (
        <View className="py-8 items-center">
          <Text className="text-4xl mb-2">📖</Text>
          <Text className="text-muted-foreground text-center">
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
