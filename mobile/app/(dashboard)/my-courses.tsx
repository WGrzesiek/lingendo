import { useState, useMemo } from 'react';
import { View, Text, ScrollView, ActivityIndicator, TouchableOpacity } from 'react-native';
import { SafeAreaView } from 'react-native-safe-area-context';
import { BookOpen, AlertCircle, Sparkles, Globe, Lock, Info } from 'lucide-react-native';
import { useDeck } from '@/features/deck/hooks/deck.hook';
import { MyCourseDeckCard } from '@/features/deck/components';

type TabFilter = 'all' | 'public' | 'private';

/**
 * Strona kursów utworzonych przez użytkownika
 */
export default function MyCoursesPage() {
  const [activeTab, setActiveTab] = useState<TabFilter>('all');

  const { useDecksCreatedByMe } = useDeck();
  const { data: decksData, isLoading, isError } = useDecksCreatedByMe();

  const decks = useMemo(() => decksData?.content ?? [], [decksData?.content]);

  const stats = useMemo(() => {
    return {
      total: decks.length,
      public: decks.filter((d) => d.visibility === 'PUBLIC').length,
      private: decks.filter((d) => d.visibility === 'PRIVATE').length,
    };
  }, [decks]);

  const filteredDecks = useMemo(() => {
    switch (activeTab) {
      case 'public':
        return decks.filter((d) => d.visibility === 'PUBLIC');
      case 'private':
        return decks.filter((d) => d.visibility === 'PRIVATE');
      default:
        return decks;
    }
  }, [decks, activeTab]);

  const tabs: { key: TabFilter; label: string; count: number }[] = [
    { key: 'all', label: 'Wszystkie', count: stats.total },
    { key: 'public', label: 'Publiczne', count: stats.public },
    { key: 'private', label: 'Prywatne', count: stats.private },
  ];

  return (
    <SafeAreaView className="flex-1 bg-background" edges={['top']}>
      <ScrollView className="flex-1" showsVerticalScrollIndicator={false}>
        <View className="p-4 pb-8">
          {/* Header */}
          <View className="mb-6">
            <View className="mb-2 flex-row items-center gap-3">
              <BookOpen size={32} className="text-primary" />
              <Text className="text-3xl font-bold text-foreground">Moje kursy</Text>
            </View>
            <Text className="text-base text-muted-foreground">Kursy, które utworzyłeś</Text>
          </View>

          {/* Info Box */}
          <View className="mb-6 rounded-xl border border-blue-500/30 bg-blue-500/10 p-4">
            <View className="flex-row items-start gap-3">
              <Info size={20} className="mt-0.5 text-blue-500" />
              <View className="flex-1">
                <Text className="mb-1 text-sm font-medium text-blue-500">Zarządzanie kursami</Text>
                <Text className="text-sm text-blue-400">
                  Tworzenie i edycja kursów jest dostępna w aplikacji webowej na lingendo.app
                </Text>
              </View>
            </View>
          </View>

          {/* Tabs */}
          <View className="mb-6 flex-row rounded-xl bg-muted p-1">
            {tabs.map((tab) => (
              <TouchableOpacity
                key={tab.key}
                onPress={() => setActiveTab(tab.key)}
                className={`flex-1 flex-row items-center justify-center gap-1 rounded-lg px-3 py-2 ${
                  activeTab === tab.key ? 'bg-card' : ''
                }`}>
                {tab.key === 'public' && (
                  <Globe
                    size={14}
                    className={activeTab === tab.key ? 'text-green-500' : 'text-muted-foreground'}
                  />
                )}
                {tab.key === 'private' && (
                  <Lock
                    size={14}
                    className={activeTab === tab.key ? 'text-orange-500' : 'text-muted-foreground'}
                  />
                )}
                <Text
                  className={`text-center text-sm font-medium ${
                    activeTab === tab.key ? 'text-foreground' : 'text-muted-foreground'
                  }`}>
                  {tab.label} ({tab.count})
                </Text>
              </TouchableOpacity>
            ))}
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
          ) : filteredDecks.length === 0 ? (
            <View className="items-center justify-center py-12">
              <View className="mb-4 h-16 w-16 items-center justify-center rounded-full bg-muted">
                <BookOpen size={32} className="text-muted-foreground" />
              </View>
              <Text className="mb-2 text-xl font-semibold text-foreground">Brak kursów</Text>
              <Text className="max-w-xs text-center text-muted-foreground">
                {activeTab === 'all'
                  ? 'Nie masz jeszcze żadnych kursów. Utwórz je w aplikacji webowej na lingendo.app'
                  : activeTab === 'public'
                    ? 'Nie masz żadnych publicznych kursów.'
                    : 'Nie masz żadnych prywatnych kursów.'}
              </Text>
            </View>
          ) : (
            <View className="gap-4">
              <View className="mb-2 flex-row items-center gap-2">
                <Sparkles size={18} className="text-primary" />
                <Text className="text-lg font-semibold text-foreground">Twoje kursy</Text>
              </View>
              {filteredDecks.map((deck) => (
                <MyCourseDeckCard key={deck.id} deck={deck} />
              ))}
            </View>
          )}
        </View>
      </ScrollView>
    </SafeAreaView>
  );
}
