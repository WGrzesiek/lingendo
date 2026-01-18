import { View, Text } from 'react-native';
import { useLearning } from '../../hooks';

interface SessionProgressProps {
  sessionId: string;
}

/**
 * Pasek postępu sesji nauki
 * Pokazuje procent ukończenia
 */
export const SessionProgress = ({ sessionId }: SessionProgressProps) => {
  const { useLearnHeaderProgress } = useLearning();
  const { data, isError } = useLearnHeaderProgress(sessionId);

  if (isError || !data) {
    return (
      <View className="px-4 py-2">
        <Text className="text-sm text-destructive">Nie udało się pobrać statystyk.</Text>
      </View>
    );
  }

  return (
    <View className="mx-4 rounded-xl border border-border bg-card p-4">
      <View className="mb-2 flex-row items-center justify-between">
        <Text className="text-sm text-muted-foreground">Postęp sesji</Text>
        <Text className="text-sm font-medium text-muted-foreground">
          {data.progressPercent.toFixed(0)}%
        </Text>
      </View>
      <View className="h-3 w-full overflow-hidden rounded-full bg-secondary">
        <View
          className="h-full rounded-full bg-primary"
          style={{ width: `${data.progressPercent}%` }}
        />
      </View>
    </View>
  );
};
