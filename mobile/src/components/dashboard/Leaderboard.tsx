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
    <View className="bg-card rounded-xl p-4 border border-border">
      <View className="flex-row items-center justify-between mb-3">
        <Text className="text-lg font-bold text-foreground">Ranking</Text>
        <Text className="text-2xl">🏆</Text>
      </View>

      {entries.map((entry) => (
        <View
          key={entry.userId}
          className={`flex-row items-center py-2 px-3 rounded-lg mb-1 ${
            entry.isCurrentUser ? 'bg-primary-light' : ''
          }`}
        >
          <Text className="text-lg w-8">
            {typeof getRankIcon(entry.rank) === 'string' ? getRankIcon(entry.rank) : entry.rank}
          </Text>
          <Text
            className={`flex-1 font-medium ${
              entry.isCurrentUser ? 'text-primary-dark' : 'text-foreground'
            }`}
          >
            {entry.username}
            {entry.isCurrentUser && ' (Ty)'}
          </Text>
          <Text className="text-muted-foreground font-medium">
            {entry.points.toLocaleString('pl-PL')} pkt
          </Text>
        </View>
      ))}
    </View>
  );
};
