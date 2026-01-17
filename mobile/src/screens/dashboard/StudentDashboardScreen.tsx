import React from 'react';
import { View, Text, ScrollView, TouchableOpacity, Alert } from 'react-native';
import { SafeAreaView } from 'react-native-safe-area-context';
import type { User } from '../../types/auth';
import type { Deck } from '../../types/dashboard';
import { StudentStatsGrid } from '../../components/dashboard/StudentStatsGrid';
import { MyCourses } from '../../components/dashboard/MyCourses';
import { QuickActions } from '../../components/dashboard/QuickActions';
import { Leaderboard } from '../../components/dashboard/Leaderboard';
import { RecentActivity } from '../../components/dashboard/RecentActivity';
import {
  MOCK_STATISTICS,
  MOCK_DECKS,
  MOCK_LEADERBOARD,
  MOCK_RECENT_ACTIVITY,
} from '../../mocks/dashboard';

interface StudentDashboardScreenProps {
  user: User;
  onLogout: () => void;
}

/**
 * Dashboard dla ucznia
 */
export const StudentDashboardScreen = ({ user, onLogout }: StudentDashboardScreenProps) => {
  const handleDeckPress = (deck: Deck) => {
    Alert.alert('Kurs', `Otwieranie kursu: ${deck.name}`);
  };

  const handleQuickAction = (action: { title: string }) => {
    Alert.alert('Akcja', `Wybrano: ${action.title}`);
  };

  return (
    <SafeAreaView className="flex-1 bg-background">
      <ScrollView className="flex-1" showsVerticalScrollIndicator={false}>
        <View className="p-4">
          {/* Header */}
          <View className="flex-row items-center justify-between mb-6">
            <View className="flex-1">
              <Text className="text-2xl font-bold text-foreground">
                Witaj, {user.firstName}! 👋
              </Text>
              <Text className="text-muted-foreground mt-1">
                Kontynuuj naukę i rozwijaj umiejętności
              </Text>
            </View>
            <TouchableOpacity
              onPress={onLogout}
              className="p-2 bg-muted rounded-lg"
            >
              <Text className="text-xl">🚪</Text>
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
          <View className="bg-card rounded-xl p-4 border border-border mb-6">
            <Text className="text-sm font-medium text-muted-foreground mb-2">
              Zalogowany jako
            </Text>
            <View className="flex-row items-center">
              <View className="w-12 h-12 bg-primary rounded-full items-center justify-center mr-3">
                <Text className="text-xl text-white font-bold">
                  {user.firstName[0]}{user.lastName[0]}
                </Text>
              </View>
              <View className="flex-1">
                <Text className="font-semibold text-foreground">
                  {user.firstName} {user.lastName}
                </Text>
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
};
