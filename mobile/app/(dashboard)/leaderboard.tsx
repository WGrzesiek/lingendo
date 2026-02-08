import React, { useMemo } from 'react';
import { View, Text, ScrollView, ActivityIndicator, RefreshControl } from 'react-native';
import { SafeAreaView } from 'react-native-safe-area-context';
import { useDashboard, type LeaderboardEntryDto } from '@/features/dashboard';

/**
 * Ekran rankingu użytkowników (używa tego samego endpointa co dashboard)
 */
export default function LeaderboardScreen() {
  const { useLeaderboardOverview } = useDashboard();
  const { data, isLoading, isError, refetch, isRefetching } = useLeaderboardOverview();


  const entries = useMemo(() => {
    if (!data) return [];

    const result: LeaderboardEntryDto[] = [...data.top3];

    if (data.aboveYou && !result.some((e) => e.userId === data.aboveYou.userId)) {
      result.push(data.aboveYou);
    }

    if (data.you && !result.some((e) => e.userId === data.you.userId)) {
      result.push(data.you);
    }

    return result.sort((a, b) => a.rank - b.rank);
  }, [data]);

  const currentUser = data?.you;
  const userAbove = data?.aboveYou;

  const getRankIcon = (rank: number) => {
    switch (rank) {
      case 1:
        return '🥇';
      case 2:
        return '🥈';
      case 3:
        return '🥉';
      default:
        return null;
    }
  };

  if (isLoading) {
    return (
      <SafeAreaView className="flex-1 items-center justify-center bg-background">
        <ActivityIndicator size="large" color="#22c55e" />
        <Text className="mt-4 text-muted-foreground">Ładowanie rankingu...</Text>
      </SafeAreaView>
    );
  }

  if (isError) {
    return (
      <SafeAreaView className="flex-1 items-center justify-center bg-background p-4">
        <Text className="mb-4 text-4xl">😕</Text>
        <Text className="mb-2 text-lg font-medium text-foreground">
          Nie udało się załadować rankingu
        </Text>
        <Text className="text-center text-muted-foreground">
          Sprawdź połączenie z internetem i spróbuj ponownie
        </Text>
      </SafeAreaView>
    );
  }

  const pointsToNext = userAbove && currentUser ? userAbove.points - currentUser.points : 0;

  return (
    <SafeAreaView className="flex-1 bg-background">
      <ScrollView
        className="flex-1"
        showsVerticalScrollIndicator={false}
        refreshControl={<RefreshControl refreshing={isRefetching} onRefresh={() => refetch()} />}>
        <View className="p-4">
          {/* Header */}
          <View className="mb-6">
            <View className="mb-2 flex-row items-center gap-3">
              <Text className="text-3xl">📊</Text>
              <Text className="text-2xl font-bold text-foreground">Ranking</Text>
            </View>
            <Text className="text-muted-foreground">
              Zobacz jak wypadasz na tle innych użytkowników
            </Text>
          </View>

          {/* Twoja pozycja - sticky card */}
          {currentUser && (
            <View className="mb-6 rounded-xl border-2 border-primary bg-primary-light p-4">
              <View className="mb-3 flex-row items-center">
                <Text className="text-lg">🎯</Text>
                <Text className="ml-2 font-bold text-foreground">Twoja pozycja</Text>
              </View>

              {/* Główna pozycja */}
              <View className="mb-3 flex-row items-center rounded-lg bg-white/80 p-3">
                <View className="w-12 items-center justify-center">
                  {getRankIcon(currentUser.rank) ? (
                    <Text className="text-2xl">{getRankIcon(currentUser.rank)}</Text>
                  ) : (
                    <View className="h-10 w-10 items-center justify-center rounded-full bg-primary">
                      <Text className="font-bold text-white">{currentUser.rank}</Text>
                    </View>
                  )}
                </View>
                <View className="ml-3 flex-1">
                  <Text className="text-lg font-semibold text-primary">
                    {currentUser.displayName}
                  </Text>
                  <Text className="text-sm text-muted-foreground">
                    {currentUser.completedCourses} ukończonych kursów
                  </Text>
                </View>
                <View className="items-end">
                  <Text className="text-xl font-bold text-primary">
                    {currentUser.points.toLocaleString('pl-PL')}
                  </Text>
                  <Text className="text-xs text-muted-foreground">punktów</Text>
                </View>
              </View>

              {/* Statystyki */}
              <View className="flex-row gap-3">
                <View className="flex-1 items-center rounded-lg bg-white/60 p-3">
                  <Text className="mb-1 text-xs text-muted-foreground">Do następnego</Text>
                  <Text className="font-bold text-foreground">
                    {pointsToNext > 0 ? `${pointsToNext} pkt` : '🏆 Lider!'}
                  </Text>
                </View>
                <View className="flex-1 items-center rounded-lg bg-white/60 p-3">
                  <Text className="mb-1 text-xs text-muted-foreground">Twoja pozycja</Text>
                  <Text className="font-bold text-foreground">#{currentUser.rank}</Text>
                </View>
              </View>

              {/* Użytkownik powyżej */}
              {userAbove && userAbove.userId !== currentUser.userId && (
                <View className="mt-3 border-t border-white/40 pt-3">
                  <Text className="mb-2 text-xs text-muted-foreground">Pokonaj:</Text>
                  <View className="flex-row items-center">
                    <View className="w-8 items-center">
                      {getRankIcon(userAbove.rank) ? (
                        <Text className="text-lg">{getRankIcon(userAbove.rank)}</Text>
                      ) : (
                        <Text className="font-medium text-muted-foreground">{userAbove.rank}</Text>
                      )}
                    </View>
                    <Text className="ml-2 flex-1 text-foreground">{userAbove.displayName}</Text>
                    <Text className="font-medium text-muted-foreground">
                      {userAbove.points.toLocaleString('pl-PL')} pkt
                    </Text>
                  </View>
                </View>
              )}
            </View>
          )}

          {/* Lista rankingu */}
          <View className="rounded-xl border border-border bg-card p-4">
            <View className="mb-4 flex-row items-center">
              <Text className="text-lg">🏆</Text>
              <Text className="ml-2 font-semibold text-foreground">Ranking</Text>
            </View>

            {entries.length === 0 ? (
              <View className="items-center py-8">
                <Text className="text-muted-foreground">Brak wyników do wyświetlenia</Text>
              </View>
            ) : (
              <View className="gap-2">
                {entries.map((entry) => (
                  <LeaderboardEntryItem
                    key={entry.userId}
                    entry={entry}
                    isCurrentUser={entry.userId === currentUser?.userId}
                    getRankIcon={getRankIcon}
                  />
                ))}
              </View>
            )}
          </View>
        </View>
      </ScrollView>
    </SafeAreaView>
  );
}

/**
 * Pojedynczy wpis w rankingu
 */
interface LeaderboardEntryItemProps {
  entry: LeaderboardEntryDto;
  isCurrentUser: boolean;
  getRankIcon: (rank: number) => string | null;
}

function LeaderboardEntryItem({ entry, isCurrentUser, getRankIcon }: LeaderboardEntryItemProps) {
  return (
    <View
      className={`flex-row items-center rounded-lg px-3 py-3 ${
        isCurrentUser ? 'border border-primary bg-primary-light' : 'bg-muted/30'
      }`}>
      {/* Pozycja */}
      <View className="w-10 items-center">
        {getRankIcon(entry.rank) ? (
          <Text className="text-xl">{getRankIcon(entry.rank)}</Text>
        ) : (
          <View
            className={`h-8 w-8 items-center justify-center rounded-full ${
              isCurrentUser ? 'bg-primary' : 'bg-muted'
            }`}>
            <Text className={`font-semibold ${isCurrentUser ? 'text-white' : 'text-foreground'}`}>
              {entry.rank}
            </Text>
          </View>
        )}
      </View>

      {/* Nazwa i kursy */}
      <View className="ml-3 flex-1">
        <Text
          className={`font-medium ${isCurrentUser ? 'text-primary' : 'text-foreground'}`}
          numberOfLines={1}>
          {entry.displayName}
          {isCurrentUser && ' (Ty)'}
        </Text>
        <Text className="text-xs text-muted-foreground">
          {entry.completedCourses}{' '}
          {entry.completedCourses === 1
            ? 'ukończony kurs'
            : entry.completedCourses < 5
              ? 'ukończone kursy'
              : 'ukończonych kursów'}
        </Text>
      </View>

      {/* Punkty */}
      <View className="items-end">
        <Text className={`font-bold ${isCurrentUser ? 'text-primary' : 'text-foreground'}`}>
          {entry.points.toLocaleString('pl-PL')}
        </Text>
        <Text className="text-xs text-muted-foreground">pkt</Text>
      </View>
    </View>
  );
}
