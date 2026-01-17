import React from 'react';
import { View, Text } from 'react-native';
import type { StudentStatistics } from '../../types/dashboard';

interface StatsCardProps {
  title: string;
  value: string | number;
  description: string;
  icon: string;
}

/**
 * Pojedyncza karta statystyk
 */
const StatsCard = ({ title, value, description, icon }: StatsCardProps) => (
  <View className="flex-1 bg-card rounded-xl p-4 border border-border min-w-[45%]">
    <View className="flex-row items-start justify-between">
      <View className="flex-1">
        <Text className="text-sm font-medium text-muted-foreground mb-1">
          {title}
        </Text>
        <Text className="text-2xl font-bold text-foreground mb-1">{value}</Text>
        <Text className="text-xs text-muted-foreground">{description}</Text>
      </View>
      <View className="p-2 bg-primary-light rounded-lg">
        <Text className="text-xl">{icon}</Text>
      </View>
    </View>
  </View>
);

interface StudentStatsGridProps {
  statistics: StudentStatistics;
}

/**
 * Siatka statystyk ucznia
 */
export const StudentStatsGrid = ({ statistics }: StudentStatsGridProps) => {
  const stats = [
    {
      title: 'Aktywne kursy',
      value: statistics.activeDecks,
      description: 'W trakcie nauki',
      icon: '📚',
    },
    {
      title: 'Lekcje',
      value: statistics.completedLessonsThisMonth,
      description: 'W tym miesiącu',
      icon: '🎯',
    },
    {
      title: 'Seria dni',
      value: statistics.streakDays,
      description: 'Dni z rzędu',
      icon: '🔥',
    },
    {
      title: 'Punkty',
      value: statistics.totalPoints.toLocaleString('pl-PL'),
      description: `+${statistics.pointsThisWeek} w tym tyg.`,
      icon: '🏆',
    },
  ];

  return (
    <View className="flex-row flex-wrap gap-3">
      {stats.map((stat, index) => (
        <StatsCard key={index} {...stat} />
      ))}
    </View>
  );
};
