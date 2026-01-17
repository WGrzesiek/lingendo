import React from 'react';
import { View, Text, TouchableOpacity } from 'react-native';

interface QuickAction {
  title: string;
  description: string;
  icon: string;
  color: string;
}

const QUICK_ACTIONS: QuickAction[] = [
  {
    title: 'Dołącz do nauczyciela',
    description: 'Wprowadź kod zaproszenia',
    icon: '👨‍🏫',
    color: 'bg-premium',
  },
  {
    title: 'Utwórz własny kurs',
    description: 'Stwórz kurs dopasowany do potrzeb',
    icon: '📝',
    color: 'bg-info',
  },
  {
    title: 'Przeglądaj społeczność',
    description: 'Kursy utworzone przez innych',
    icon: '👥',
    color: 'bg-success',
  },
  {
    title: 'Dzienna praktyka',
    description: 'Powtórz słówka z dzisiaj',
    icon: '📅',
    color: 'bg-streak',
  },
];

interface QuickActionItemProps {
  action: QuickAction;
  onPress: () => void;
}

const QuickActionItem = ({ action, onPress }: QuickActionItemProps) => (
  <TouchableOpacity
    onPress={onPress}
    className="flex-row items-center p-3 bg-card rounded-lg border border-border mb-2"
  >
    <View className={`p-2 ${action.color} rounded-lg mr-3`}>
      <Text className="text-lg">{action.icon}</Text>
    </View>
    <View className="flex-1">
      <Text className="font-semibold text-foreground">{action.title}</Text>
      <Text className="text-xs text-muted-foreground">{action.description}</Text>
    </View>
    <Text className="text-muted-foreground">›</Text>
  </TouchableOpacity>
);

interface QuickActionsProps {
  onActionPress: (action: QuickAction) => void;
}

/**
 * Szybkie akcje dla ucznia
 */
export const QuickActions = ({ onActionPress }: QuickActionsProps) => {
  return (
    <View className="bg-card rounded-xl p-4 border border-border">
      <Text className="text-lg font-bold text-foreground mb-3">Szybkie akcje</Text>
      {QUICK_ACTIONS.map((action, index) => (
        <QuickActionItem
          key={index}
          action={action}
          onPress={() => onActionPress(action)}
        />
      ))}
    </View>
  );
};
