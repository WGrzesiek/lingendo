import React, { useEffect } from 'react';
import { View, Text, ScrollView, TouchableOpacity, Alert, ActivityIndicator } from 'react-native';
import { SafeAreaView } from 'react-native-safe-area-context';
import { router } from 'expo-router';
import { useAuth } from '@/features/auth';
import { useDashboard } from '@/features/dashboard';
import { useDeck, type DeckListItem } from '@/features/deck';
import { StudentStatsGrid } from '@/components/dashboard/StudentStatsGrid';
import { MyCourses } from '@/components/dashboard/MyCourses';
import { QuickActions } from '@/components/dashboard/QuickActions';
import { Leaderboard } from '@/components/dashboard/Leaderboard';
import { RecentActivity } from '@/components/dashboard/RecentActivity';
import { MOCK_LEADERBOARD } from '@/mocks/dashboard';

/**
 * Dashboard dla ucznia
 */
function StudentDashboard() {
  const { user, isUserLoading, logout, isLogoutLoading } = useAuth();

  const { useStudentStatistics, useStudentActivity } = useDashboard();
  const { useMyEnrolledDecks } = useDeck();

  const { data: statistics, isLoading: isStatsLoading, isError: isStatsError, } = useStudentStatistics();
  const {
    data: activityData,
    isLoading: isActivityLoading,
    isError: isActivityError,
  } = useStudentActivity();
  const {
    data: enrolledDecksResponse,
    isLoading: isDecksLoading,
    isError: isDecksError,
  } = useMyEnrolledDecks(0, 5);

  useEffect(() => {
    if (!isUserLoading && !user) {
      router.replace('/(auth)/login');
    }
  }, [user, isUserLoading]);

  const isLoading = isUserLoading || isStatsLoading || isDecksLoading || isActivityLoading;
  const isError = isStatsError || isDecksError || isActivityError;

  if (isLoading) {
    return (
      <SafeAreaView className="flex-1 items-center justify-center bg-background">
        <ActivityIndicator size="large" color="#22c55e" />
        <Text className="mt-4 text-muted-foreground">Ładowanie...</Text>
      </SafeAreaView>
    );
  }

  if (isError) {
    return (
      <SafeAreaView className="flex-1 items-center justify-center bg-background p-4">
        <Text className="mb-4 text-center text-foreground">
          Wystąpił błąd podczas ładowania danych. Spróbuj ponownie później.
        </Text>
      </SafeAreaView>
    );
  }

  if (!user) {
    return (
      <SafeAreaView className="flex-1 items-center justify-center bg-background">
        <ActivityIndicator size="large" color="#22c55e" />
      </SafeAreaView>
    );
  }

  const decks = enrolledDecksResponse?.content ?? [];


  const handleDeckPress = (deck: DeckListItem) => {
    router.push(`/(dashboard)/course/${deck.enrollmentId}`);
  };

  const handleQuickAction = (action: { title: string }) => {
    Alert.alert('Akcja', `Wybrano: ${action.title}`);
  };

  const handleLogout = () => {
    Alert.alert('Wylogowanie', 'Czy na pewno chcesz się wylogować?', [
      { text: 'Anuluj', style: 'cancel' },
      { text: 'Wyloguj', style: 'destructive', onPress: () => logout() },
    ]);
  };

  const getInitials = () => {
    return user.username?.charAt(0).toUpperCase() || 'U';
  };

  return (
    <SafeAreaView className="flex-1 bg-background">
      <ScrollView className="flex-1" showsVerticalScrollIndicator={false}>
        <View className="p-4">
          {/* Header */}
          <View className="mb-6 flex-row items-center justify-between">
            <View className="flex-1">
              <Text className="text-2xl font-bold text-foreground">Witaj, {user.username}! 👋</Text>
              <Text className="mt-1 text-muted-foreground">
                Kontynuuj naukę i rozwijaj umiejętności
              </Text>
            </View>
            <TouchableOpacity
              onPress={handleLogout}
              disabled={isLogoutLoading}
              className="rounded-lg bg-muted p-2">
              {isLogoutLoading ? (
                <ActivityIndicator size="small" color="#22c55e" />
              ) : (
                <Text className="text-xl">🚪</Text>
              )}
            </TouchableOpacity>
          </View>

          {/* Statystyki */}
          <View className="mb-6">
            <StudentStatsGrid
              statistics={statistics}
              isLoading={isStatsLoading}
              isError={isStatsError}
            />
          </View>

          {/* Moje kursy */}
          <View className="mb-6">
            <MyCourses decks={decks} onDeckPress={handleDeckPress} />
          </View>

          {/* Szybkie akcje */}
          <View className="mb-6">
            <QuickActions onActionPress={handleQuickAction} />
          </View>

          {/* Ranking - na razie mock, do podłączenia */}
          <View className="mb-6">
            <Leaderboard entries={MOCK_LEADERBOARD} />
          </View>

          {/* Ostatnia aktywność */}
          <View className="mb-6">
            <RecentActivity activities={activityData} isLoading={isActivityLoading} isError={isActivityError} />
          </View>

          {/* Info o użytkowniku */}
          <View className="mb-6 rounded-xl border border-border bg-card p-4">
            <Text className="mb-2 text-sm font-medium text-muted-foreground">Zalogowany jako</Text>
            <View className="flex-row items-center">
              <View className="mr-3 h-12 w-12 items-center justify-center rounded-full bg-primary">
                <Text className="text-xl font-bold text-white">{getInitials()}</Text>
              </View>
              <View className="flex-1">
                <Text className="font-semibold text-foreground">{user.username}</Text>
                <Text className="text-sm text-muted-foreground">
                  @{user.username} • {user.accountType}
                </Text>
              </View>
            </View>
          </View>
        </View>
      </ScrollView>
    </SafeAreaView>
  );
}

export default StudentDashboard;
