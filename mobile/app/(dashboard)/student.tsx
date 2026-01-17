import React from 'react';
import { View, Text, ScrollView, TouchableOpacity, Alert, ActivityIndicator } from 'react-native';
import { SafeAreaView } from 'react-native-safe-area-context';
import { router } from 'expo-router';
import { useAuth } from '@/features/auth';
import type { Deck } from '@/types/dashboard';
import { StudentStatsGrid } from '@/components/dashboard/StudentStatsGrid';
import { MyCourses } from '@/components/dashboard/MyCourses';
import { QuickActions } from '@/components/dashboard/QuickActions';
import { Leaderboard } from '@/components/dashboard/Leaderboard';
import { RecentActivity } from '@/components/dashboard/RecentActivity';
import {
  MOCK_STATISTICS,
  MOCK_DECKS,
  MOCK_LEADERBOARD,
  MOCK_RECENT_ACTIVITY,
} from '@/mocks/dashboard';

/**
 * Dashboard dla ucznia
 */
function StudentDashboard() {
  const { user, isUserLoading, logout, isLogoutLoading } = useAuth();

  if (isUserLoading) {
    return (
      <SafeAreaView className="flex-1 items-center justify-center bg-background">
        <ActivityIndicator size="large" color="#22c55e" />
        <Text className="mt-4 text-muted-foreground">Ładowanie...</Text>
      </SafeAreaView>
    );
  }

  if (!user) {
    router.replace('/(auth)/login');
    return null;
  }

  const handleDeckPress = (deck: Deck) => {
    Alert.alert('Kurs', `Otwieranie kursu: ${deck.name}`);
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
            <StudentStatsGrid statistics={MOCK_STATISTICS} />
          </View>

          {/* Moje kursy */}
          <View className="mb-6">
            <MyCourses decks={MOCK_DECKS} onDeckPress={handleDeckPress} />
          </View>

          {/* Szybkie akcje */}
          <View className="mb-6">
            <QuickActions onActionPress={handleQuickAction} />
          </View>

          {/* Ranking */}
          <View className="mb-6">
            <Leaderboard entries={MOCK_LEADERBOARD} />
          </View>

          {/* Ostatnia aktywność */}
          <View className="mb-6">
            <RecentActivity activities={MOCK_RECENT_ACTIVITY} />
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
