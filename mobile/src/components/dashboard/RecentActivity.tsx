import React from 'react';
import { View, Text } from 'react-native';
import type { RecentActivityItem } from '../../types/dashboard';

interface RecentActivityProps {
  activities: RecentActivityItem[];
}

/**
 * Zwraca ikonę dla typu aktywności
 */
const getActivityIcon = (type: RecentActivityItem['type']): string => {
  switch (type) {
    case 'lesson_completed':
      return '✅';
    case 'deck_started':
      return '📚';
    case 'achievement_earned':
      return '🏅';
    case 'streak_reached':
      return '🔥';
    default:
      return '📝';
  }
};

/**
 * Formatuje datę w sposób przyjazny
 */
const formatDate = (timestamp: string): string => {
  const date = new Date(timestamp);
  const now = new Date();
  const diffMs = now.getTime() - date.getTime();
  const diffHours = Math.floor(diffMs / (1000 * 60 * 60));
  const diffDays = Math.floor(diffHours / 24);

  if (diffHours < 1) {
    return 'Przed chwilą';
  } else if (diffHours < 24) {
    return `${diffHours} godz. temu`;
  } else if (diffDays === 1) {
    return 'Wczoraj';
  } else if (diffDays < 7) {
    return `${diffDays} dni temu`;
  } else {
    return date.toLocaleDateString('pl-PL');
  }
};

/**
 * Ostatnia aktywność użytkownika
 */
export const RecentActivity = ({ activities }: RecentActivityProps) => {
  return (
    <View className="bg-card rounded-xl p-4 border border-border">
      <Text className="text-lg font-bold text-foreground mb-3">Ostatnia aktywność</Text>

      {activities.length === 0 ? (
        <View className="py-4 items-center">
          <Text className="text-muted-foreground text-center">
            Brak aktywności do wyświetlenia
          </Text>
        </View>
      ) : (
        activities.map((activity) => (
          <View
            key={activity.id}
            className="flex-row items-start py-2 border-b border-border last:border-b-0"
          >
            <Text className="text-xl mr-3">{getActivityIcon(activity.type)}</Text>
            <View className="flex-1">
              <Text className="font-medium text-foreground">{activity.title}</Text>
              <Text className="text-sm text-muted-foreground">{activity.description}</Text>
              <Text className="text-xs text-muted-foreground mt-1">
                {formatDate(activity.timestamp)}
              </Text>
            </View>
          </View>
        ))
      )}
    </View>
  );
};
