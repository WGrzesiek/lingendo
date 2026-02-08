import { useState } from 'react';
import { View, Text, TextInput, TouchableOpacity, ScrollView } from 'react-native';
import { Search, SlidersHorizontal, X } from 'lucide-react-native';
import {
  deckCategoryConfig,
  type DeckCategory,
  type DeckDifficulty,
} from '@/features/deck/types/deck.types';
import type { CommunityCoursesFilters } from '../types';

interface CommunityFiltersProps {
  filters: CommunityCoursesFilters;
  onFiltersChange: (filters: CommunityCoursesFilters) => void;
  resultsCount: number;
}

const difficultyOptions: { value: DeckDifficulty | undefined; label: string }[] = [
  { value: undefined, label: 'Wszystkie' },
  { value: 'EASY', label: 'Łatwy' },
  { value: 'MEDIUM', label: 'Średni' },
  { value: 'HARD', label: 'Trudny' },
];

const sortOptions: { value: 'newest' | 'oldest'; label: string }[] = [
  { value: 'newest', label: 'Najnowsze' },
  { value: 'oldest', label: 'Najstarsze' },
];

/**
 * Filtry dla kursów społeczności - wersja mobilna
 */
export const CommunityFilters = ({
  filters,
  onFiltersChange,
  resultsCount,
}: CommunityFiltersProps) => {
  const [showFilters, setShowFilters] = useState(false);

  const hasActiveFilters = filters.search || filters.category || filters.difficulty;

  const handleClearFilters = () => {
    onFiltersChange({
      search: '',
      category: undefined,
      difficulty: undefined,
      sortBy: 'newest',
    });
  };

  const getResultsText = () => {
    if (resultsCount === 1) return '1 kurs';
    if (resultsCount < 5) return `${resultsCount} kursy`;
    return `${resultsCount} kursów`;
  };

  return (
    <View className="gap-3">
      {/* Wyszukiwarka */}
      <View className="flex-row items-center gap-2">
        <View className="flex-1 flex-row items-center rounded-xl bg-muted px-3">
          <Search size={18} className="text-muted-foreground" />
          <TextInput
            className="flex-1 px-2 py-3 text-foreground"
            placeholder="Szukaj kursu..."
            placeholderTextColor="#9ca3af"
            value={filters.search ?? ''}
            onChangeText={(text) => onFiltersChange({ ...filters, search: text })}
          />
        </View>
        <TouchableOpacity
          className={`rounded-xl p-3 ${showFilters ? 'bg-primary' : 'bg-muted'}`}
          onPress={() => setShowFilters(!showFilters)}>
          <SlidersHorizontal
            size={20}
            className={showFilters ? 'text-primary-foreground' : 'text-foreground'}
          />
        </TouchableOpacity>
      </View>

      {/* Wyniki i clear */}
      <View className="flex-row items-center justify-between">
        <Text className="text-sm text-muted-foreground">
          Znaleziono: <Text className="font-semibold text-foreground">{getResultsText()}</Text>
        </Text>
        {hasActiveFilters && (
          <TouchableOpacity className="flex-row items-center gap-1" onPress={handleClearFilters}>
            <X size={14} className="text-muted-foreground" />
            <Text className="text-sm text-muted-foreground">Wyczyść</Text>
          </TouchableOpacity>
        )}
      </View>

      {/* Rozwijane filtry */}
      {showFilters && (
        <View className="gap-4 rounded-xl border border-border bg-card p-4">
          {/* Trudność */}
          <View className="gap-2">
            <Text className="text-sm font-medium text-foreground">Poziom trudności</Text>
            <ScrollView horizontal showsHorizontalScrollIndicator={false}>
              <View className="flex-row gap-2">
                {difficultyOptions.map((option) => (
                  <TouchableOpacity
                    key={option.label}
                    className={`rounded-lg border px-3 py-2 ${
                      filters.difficulty === option.value
                        ? 'border-primary bg-primary'
                        : 'border-border bg-muted'
                    }`}
                    onPress={() => onFiltersChange({ ...filters, difficulty: option.value })}>
                    <Text
                      className={`text-sm ${
                        filters.difficulty === option.value
                          ? 'font-medium text-primary-foreground'
                          : 'text-foreground'
                      }`}>
                      {option.label}
                    </Text>
                  </TouchableOpacity>
                ))}
              </View>
            </ScrollView>
          </View>

          {/* Sortowanie */}
          <View className="gap-2">
            <Text className="text-sm font-medium text-foreground">Sortuj według</Text>
            <View className="flex-row gap-2">
              {sortOptions.map((option) => (
                <TouchableOpacity
                  key={option.value}
                  className={`rounded-lg border px-3 py-2 ${
                    filters.sortBy === option.value
                      ? 'border-primary bg-primary'
                      : 'border-border bg-muted'
                  }`}
                  onPress={() => onFiltersChange({ ...filters, sortBy: option.value })}>
                  <Text
                    className={`text-sm ${
                      filters.sortBy === option.value
                        ? 'font-medium text-primary-foreground'
                        : 'text-foreground'
                    }`}>
                    {option.label}
                  </Text>
                </TouchableOpacity>
              ))}
            </View>
          </View>

          {/* Kategorie (popular ones) */}
          <View className="gap-2">
            <Text className="text-sm font-medium text-foreground">Kategoria</Text>
            <ScrollView horizontal showsHorizontalScrollIndicator={false}>
              <View className="flex-row gap-2">
                <TouchableOpacity
                  className={`rounded-lg border px-3 py-2 ${
                    !filters.category ? 'border-primary bg-primary' : 'border-border bg-muted'
                  }`}
                  onPress={() => onFiltersChange({ ...filters, category: undefined })}>
                  <Text
                    className={`text-sm ${
                      !filters.category ? 'font-medium text-primary-foreground' : 'text-foreground'
                    }`}>
                    Wszystkie
                  </Text>
                </TouchableOpacity>
                {Object.entries(deckCategoryConfig)
                  .slice(0, 8)
                  .map(([key, config]) => (
                    <TouchableOpacity
                      key={key}
                      className={`rounded-lg border px-3 py-2 ${
                        filters.category === key
                          ? 'border-primary bg-primary'
                          : 'border-border bg-muted'
                      }`}
                      onPress={() =>
                        onFiltersChange({ ...filters, category: key as DeckCategory })
                      }>
                      <Text
                        className={`text-sm ${
                          filters.category === key
                            ? 'font-medium text-primary-foreground'
                            : 'text-foreground'
                        }`}>
                        {config.label}
                      </Text>
                    </TouchableOpacity>
                  ))}
              </View>
            </ScrollView>
          </View>
        </View>
      )}
    </View>
  );
};
