import React, { useState, useMemo, useCallback } from 'react';
import {
  View,
  Text,
  ScrollView,
  ActivityIndicator,
  TouchableOpacity,
  TextInput,
} from 'react-native';
import { SafeAreaView } from 'react-native-safe-area-context';
import { useLocalSearchParams, useRouter } from 'expo-router';
import {
  ArrowLeft,
  BookOpen,
  Calendar,
  User,
  Search,
  AlertCircle,
  CheckCircle,
  Play,
  Sparkles,
  Globe,
  Lock,
} from 'lucide-react-native';
import { useEnrollment } from '@/features/enroll/hooks/enrollment.hook';
import { useAuth } from '@/features/auth/hooks/auth.hook';
import { deckCategoryConfig, deckDifficultyConfig } from '@/features/deck/types/deck.types';
import type { WordSentence } from '@/features/deck/types';
import { useDeck } from '@/features/deck';

/**
 * Strona szczegółów talii - uniwersalna dla community i moich kursów
 */
export default function DeckDetailsPage() {
  const { id: deck } = useLocalSearchParams<{ id: string }>();
  const routerNav = useRouter();
  const { id: deckId } = useLocalSearchParams<{ id: string }>();
  const [searchQuery, setSearchQuery] = useState('');
  const [isEnrolled, setIsEnrolled] = useState(false);

  const { user: currentUser } = useAuth();
  const { useDeckDetail, useInfiniteDeckFlashcards } = useDeck();
  const { useEnrollToDeck } = useEnrollment();

  const {
    data: deckData,
    isLoading: isDeckLoading,
    isError: isDeckError,
  } = useDeckDetail(deckId);
  const {
    data: flashcardsData,
    isLoading: isFlashcardsLoading,
    fetchNextPage,
    hasNextPage,
    isFetchingNextPage,
  } = useInfiniteDeckFlashcards(deckId);
  const enrollMutation = useEnrollToDeck();

  const isTeacher = currentUser?.accountType === 'TEACHER';

  const flashcards = flashcardsData?.pages.flatMap((page) => page.content) ?? [];
  const totalWords = flashcardsData?.pages[0]?.totalElements ?? 0;


  const handleLoadMoreWords = useCallback(() => {
    if (hasNextPage && !isFetchingNextPage) {
      fetchNextPage();
    }
  }, [hasNextPage, isFetchingNextPage, fetchNextPage]);



  const filteredFlashcards = useMemo(() => {
    if (!searchQuery.trim()) return flashcards;
    const query = searchQuery.toLowerCase();
    return flashcards.filter(
      (f) =>
        f.word.toLowerCase().includes(query) ||
        f.translations?.some((t: string) => t.toLowerCase().includes(query))
    );
  }, [flashcards, searchQuery]);

  const handleEnroll = () => {
    enrollMutation.mutate(
      { deckId: deck },
      {
        onSuccess: () => {
          setIsEnrolled(true);
        },
      }
    );
  };

  const formatDate = (dateString: string) => {
    return new Date(dateString).toLocaleDateString('pl-PL', {
      day: 'numeric',
      month: 'short',
      year: 'numeric',
    });
  };

  if (isDeckLoading) {
    return (
      <SafeAreaView className="flex-1 items-center justify-center bg-background">
        <ActivityIndicator size="large" />
        <Text className="mt-2 text-muted-foreground">Ładowanie kursu...</Text>
      </SafeAreaView>
    );
  }

  if (isDeckError || !deckData) {
    return (
      <SafeAreaView className="flex-1 items-center justify-center bg-background p-4">
        <View className="mb-4 h-16 w-16 items-center justify-center rounded-full bg-destructive/10">
          <AlertCircle size={32} className="text-destructive" />
        </View>
        <Text className="mb-2 text-xl font-semibold text-foreground">Błąd ładowania</Text>
        <Text className="max-w-xs text-center text-muted-foreground">
          Nie udało się załadować szczegółów kursu.
        </Text>
        <TouchableOpacity
          onPress={() => routerNav.back()}
          className="mt-4 rounded-xl bg-primary px-6 py-3">
          <Text className="font-semibold text-primary-foreground">Powrót</Text>
        </TouchableOpacity>
      </SafeAreaView>
    );
  }
  const isPublic = deckData.visibility === 'PUBLIC';
  const showEnrollButton = !isTeacher

  return (
    <SafeAreaView className="flex-1 bg-background" edges={['top']}>
      <ScrollView className="flex-1" showsVerticalScrollIndicator={false}>
        <View className="p-4 pb-8">
          {/* Back button */}
          <TouchableOpacity
            onPress={() => routerNav.back()}
            className="mb-4 flex-row items-center gap-2">
            <ArrowLeft size={20} className="text-foreground" />
            <Text className="font-medium text-foreground">Powrót</Text>
          </TouchableOpacity>
          {/* Header */}
          <View className="mb-6">
            <View className="mb-3 flex-row items-start justify-between">
              <Text className="flex-1 text-3xl font-bold text-foreground">{deckData.name}</Text>
              <View
                className={`ml-2 rounded-full px-2 py-1 ${isPublic ? 'bg-green-500/20' : 'bg-orange-500/20'}`}>
                {isPublic ? (
                  <Globe size={16} className="text-green-500" />
                ) : (
                  <Lock size={16} className="text-orange-500" />
                )}
              </View>
            </View>

            {/* Badge'y */}
            <View className="mb-3 flex-row flex-wrap gap-2">
              {deckData.deckCategory && (
                <View className="rounded-md bg-primary/10 px-2 py-1">
                  <Text className="text-xs font-medium text-primary">
                    {deckCategoryConfig[deckData.deckCategory]?.label ?? deckData.deckCategory}
                  </Text>
                </View>
              )}
              {deckData.deckDifficulty && (
                <View className="rounded-md bg-secondary px-2 py-1">
                  <Text className="text-xs font-medium text-secondary-foreground">
                    {deckDifficultyConfig[deckData.deckDifficulty]?.label ??
                      deckData.deckDifficulty}
                  </Text>
                </View>
              )}
              {deckData.languageFrom && deckData.languageTo && (
                <View className="rounded-md bg-muted px-2 py-1">
                  <Text className="text-xs text-muted-foreground">
                    {deckData.languageFrom} → {deckData.languageTo}
                  </Text>
                </View>
              )}
            </View>

            {/* Opis */}
            <Text className="mb-4 text-base leading-relaxed text-muted-foreground">
              {deckData.deckDescription || 'Brak opisu kursu.'}
            </Text>

            {/* Metadata */}
            <View className="flex-row flex-wrap gap-4">
              <View className="flex-row items-center gap-1.5">
                <BookOpen size={14} className="text-muted-foreground" />
                <Text className="text-sm text-muted-foreground">{deckData.wordCount} słówek</Text>
              </View>
              {deckData.username && (
                <View className="flex-row items-center gap-1.5">
                  <User size={14} className="text-muted-foreground" />
                  <Text className="text-sm text-muted-foreground">Autor: {deckData.username}</Text>
                </View>
              )}
              {deckData.createdAt && (
                <View className="flex-row items-center gap-1.5">
                  <Calendar size={14} className="text-muted-foreground" />
                  <Text className="text-sm text-muted-foreground">
                    {formatDate(deckData.createdAt)}
                  </Text>
                </View>
              )}
            </View>
          </View>
          <View className="mb-6 flex-row items-center gap-2 rounded-xl bg-green-500/10 p-4">
            <CheckCircle size={20} className="text-green-600" />
            <Text className="flex-1 font-medium text-green-600">
              {isEnrolled
                ? 'Zapisano na kurs! Przekierowanie...'
                : 'Jesteś już zapisany na ten kurs'}
            </Text>
          </View>
          ){/* Przycisk zapisu - tylko dla studentów */}
          {showEnrollButton && (
            <View className="mb-6">
              <TouchableOpacity
                onPress={handleEnroll}
                disabled={enrollMutation.isPending}
                className={`flex-row items-center justify-center gap-2 rounded-xl py-4 ${
                  enrollMutation.isPending ? 'bg-muted' : 'bg-primary'
                }`}>
                {enrollMutation.isPending ? (
                  <>
                    <ActivityIndicator size="small" color="white" />
                    <Text className="font-semibold text-white">Zapisywanie...</Text>
                  </>
                ) : (
                  <>
                    <Play size={20} className="text-primary-foreground" />
                    <Text className="font-semibold text-primary-foreground">
                      Zapisz się na kurs
                    </Text>
                  </>
                )}
              </TouchableOpacity>
              {enrollMutation.isError && (
                <View className="mt-2 flex-row items-center gap-2">
                  <AlertCircle size={14} className="text-destructive" />
                  <Text className="text-sm text-destructive">
                    Błąd podczas zapisywania. Spróbuj ponownie.
                  </Text>
                </View>
              )}
            </View>
          )}
          {/* Lista słówek */}
          <View className="rounded-xl border border-border bg-card p-4">
            {/* Header listy */}
            <View className="mb-4 flex-row items-center justify-between">
              <View className="flex-row items-center gap-2">
                <BookOpen size={20} className="text-foreground" />
                <Text className="text-xl font-bold text-foreground">Lista słówek</Text>
              </View>
              <View className="rounded-lg bg-secondary px-3 py-1">
                <Text className="text-sm font-semibold text-secondary-foreground">
                  {/*{ flashcardsData?.pages.length?? 0} słówek*/}
                  {totalWords ?? 0} słówek
                </Text>
              </View>
            </View>

            {/* Wyszukiwarka */}
            <View className="mb-4 flex-row items-center rounded-xl bg-muted px-3 py-2">
              <Search size={18} className="mr-2 text-muted-foreground" />
              <TextInput
                placeholder="Szukaj słówka..."
                placeholderTextColor="#9ca3af"
                value={searchQuery}
                onChangeText={setSearchQuery}
                className="flex-1 text-base text-foreground"
              />
            </View>

            {/* Słówka */}
            {isFlashcardsLoading ? (
              <View className="items-center justify-center py-8">
                <ActivityIndicator size="large" />
                <Text className="mt-2 text-muted-foreground">Ładowanie słówek...</Text>
              </View>
            ) : filteredFlashcards.length > 0 ? (
              <View className="gap-3">
                {filteredFlashcards.map((flashcard) => (
                  <View
                    key={flashcard.id}
                    className="rounded-lg border border-border/50 bg-background p-3">
                    <View className="mb-2 flex-row items-center gap-2">
                      <Text className="text-lg font-semibold text-foreground">
                        {flashcard.word}
                      </Text>
                      <Text className="text-muted-foreground">→</Text>
                      <Text className="text-lg text-foreground">
                        {(flashcard.translations ?? []).join(', ')}
                      </Text>
                    </View>
                    {((flashcard.sentences ?? []).length > 0 ||
                      (flashcard.sentencesAI ?? []).length > 0) && (
                      <View className="space-y-1">
                        {[...(flashcard.sentences ?? []), ...(flashcard.sentencesAI ?? [])]
                          .slice(0, 1)
                          .map((sentence: WordSentence, index: number) => (
                            <View key={sentence.id ?? index}>
                              <Text className="text-sm italic text-muted-foreground">
                                &ldquo;{sentence.sentence}&rdquo;
                              </Text>
                              <Text className="text-sm italic text-muted-foreground">
                                &ldquo;{sentence.translation}&rdquo;
                              </Text>
                            </View>
                          ))}
                      </View>
                    )}
                    {(flashcard.sentences ?? []).length === 0 &&
                      (flashcard.sentencesAI ?? []).length === 0 && (
                        <View className="flex-row items-center gap-2">
                          <Sparkles size={14} className="text-muted-foreground" />
                          <Text className="text-sm text-muted-foreground">
                            Brak przykładowego zdania
                          </Text>
                        </View>
                      )}
                  </View>
                ))}
                {/* Przycisk "Załaduj więcej" */}
                {hasNextPage && (
                  <TouchableOpacity
                    onPress={handleLoadMoreWords}
                    disabled={isFetchingNextPage}
                    className="mt-3 items-center rounded-lg border border-border py-3">
                    {isFetchingNextPage ? (
                      <ActivityIndicator size="small" color="#22c55e" />
                    ) : (
                      <Text className="font-medium text-primary">Załaduj więcej słówek</Text>
                    )}
                  </TouchableOpacity>
                )}{' '}
              </View>
            ) : (
              <View className="items-center justify-center rounded-xl border border-dashed border-border bg-muted/20 py-8">
                <View className="mb-3 h-12 w-12 items-center justify-center rounded-full bg-muted">
                  <Search size={24} className="text-muted-foreground" />
                </View>
                <Text className="mb-1 text-lg font-semibold text-foreground">
                  Nie znaleziono słówek
                </Text>
                <Text className="max-w-xs text-center text-sm text-muted-foreground">
                  {searchQuery
                    ? 'Spróbuj zmienić zapytanie lub wyczyść wyszukiwanie'
                    : 'Ten kurs nie zawiera jeszcze żadnych słówek'}
                </Text>
                {searchQuery && (
                  <TouchableOpacity
                    onPress={() => setSearchQuery('')}
                    className="mt-3 rounded-lg border border-border px-4 py-2">
                    <Text className="text-foreground">Wyczyść wyszukiwanie</Text>
                  </TouchableOpacity>
                )}
              </View>
            )}
          </View>
        </View>
      </ScrollView>
    </SafeAreaView>
  );
}
