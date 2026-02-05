import React from 'react';
import { View, Text } from 'react-native';
import { StudentActivityItem } from '@/features/dashboard';

interface RecentActivityProps {
  activities: StudentActivityItem[] | undefined;
  isLoading: boolean;
  isError: boolean;
}

/**
 * Zwraca ikonę dla typu aktywności
 */
const getActivityIcon = (type: StudentActivityItem['type']): string => {
  switch (type) {
    case 'LESSON_COMPLETED':
      return '✅';
    case 'SESSION_STARTED':
      return '📚';
    case 'LOGIN':
      return '🔥';
    case 'SESSION_COMPLETED':
      return '🎉';
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
  export const RecentActivity = ({ activities, isLoading, isError }: RecentActivityProps) => {

    if (isLoading) {
      return (
        <View className="flex-row items-center justify-center rounded-xl border border-border bg-card p-8">
          <Text className="text-muted-foreground">Ładowanie aktywności...</Text>
        </View>
      );
    }

    if (isError || !activities || activities.length === 0) {
      return (
        <View className="rounded-xl border border-border bg-card p-4">
          <Text className="mb-3 text-lg font-bold text-foreground">Ostatnia aktywność</Text>
          <View className="items-center py-4">
            <Text className="text-center text-muted-foreground">
              Brak aktywności do wyświetlenia
            </Text>
          </View>
        </View>
      );
    }

    return (
      <View className="rounded-xl border border-border bg-card p-4">
        <Text className="mb-3 text-lg font-bold text-foreground">Ostatnia aktywność</Text>

        {activities.length === 0 ? (
          <View className="items-center py-4">
            <Text className="text-center text-muted-foreground">
              Brak aktywności do wyświetlenia
            </Text>
          </View>
        ) : (
          activities.map((activity) => (
            <View
              key={activity.type + activity.eventTime}
              className="flex-row items-start border-b border-border py-2 last:border-b-0">
              <Text className="mr-3 text-xl">{getActivityIcon(activity.type)}</Text>
              <View className="flex-1">
                <Text className="font-medium text-foreground">{activity.title}</Text>
                <Text className="text-sm text-muted-foreground">{activity.subtitle}</Text>
                <Text className="mt-1 text-xs text-muted-foreground">
                  {formatDate(activity.eventTime)}
                </Text>
              </View>
            </View>
          ))
        )}
      </View>
    );
  };
