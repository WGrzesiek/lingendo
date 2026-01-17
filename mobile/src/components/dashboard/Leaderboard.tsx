import React from 'react';
import { View, Text } from 'react-native';
import type { LeaderboardEntry } from '../../types/dashboard';

interface LeaderboardProps {
  entries: LeaderboardEntry[];
}

/**
 * Ranking użytkowników
 */
export const Leaderboard = ({ entries }: LeaderboardProps) => {
  const getRankIcon = (rank: number) => {
    switch (rank) {
      case 1:
        return '🥇';
      case 2:
        return '🥈';
      case 3:
        return '🥉';
      default:
        return `${rank}`;
    }
  };

  return (
    <View className="rounded-xl border border-border bg-card p-4">
      <View className="mb-3 flex-row items-center justify-between">
        <Text className="text-lg font-bold text-foreground">Ranking</Text>
        <Text className="text-2xl">🏆</Text>
      </View>

      {entries.map((entry) => (
        <View
          key={entry.userId}
          className={`mb-1 flex-row items-center rounded-lg px-3 py-2 ${
            entry.isCurrentUser ? 'bg-primary-light' : ''
          }`}>
          <Text className="w-8 text-lg">
            {typeof getRankIcon(entry.rank) === 'string' ? getRankIcon(entry.rank) : entry.rank}
          </Text>
          <Text
            className={`flex-1 font-medium ${
              entry.isCurrentUser ? 'text-primary-dark' : 'text-foreground'
            }`}>
            {entry.username}
            {entry.isCurrentUser && ' (Ty)'}
          </Text>
          <Text className="font-medium text-muted-foreground">
            {entry.points.toLocaleString('pl-PL')} pkt
          </Text>
        </View>
      ))}
    </View>
  );
};
