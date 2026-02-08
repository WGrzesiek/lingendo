import { View, Text, TouchableOpacity } from 'react-native';
import { CheckCircle2, ArrowLeft, BookOpen } from 'lucide-react-native';
import { router } from 'expo-router';

interface SessionCompletedViewProps {
  title?: string;
  description?: string;
  courseId?: string;
}

/**
 * Widok zakończonej sesji nauki
 */
export const SessionCompletedView = ({
  title = 'Sesja zakończona 🎉',
  description = 'Świetna robota! W tej sesji nie ma już dostępnych fiszek do nauki.',
  courseId,
}: SessionCompletedViewProps) => {
  return (
    <View className="flex-1 justify-center bg-background p-4">
      <View className="rounded-2xl border border-border bg-card p-8">
        <View className="items-center gap-4">
          {/* Ikona */}
          <View className="relative">
            <CheckCircle2 size={64} className="text-primary" />
          </View>

          {/* Badge */}
          <View className="rounded-full bg-secondary px-3 py-1">
            <Text className="text-xs font-medium uppercase tracking-wider text-secondary-foreground">
              Koniec sesji
            </Text>
          </View>

          {/* Tytuł */}
          <Text className="text-center text-3xl font-bold text-foreground">{title}</Text>

          {/* Opis */}
          <Text className="text-center text-base text-muted-foreground">{description}</Text>

          {/* Przyciski */}
          <View className="mt-4 w-full gap-3">
            <TouchableOpacity
              className="flex-row items-center justify-center gap-2 rounded-xl border border-border bg-secondary py-4"
              onPress={() => router.back()}>
              <ArrowLeft size={20} className="text-secondary-foreground" />
              <Text className="text-lg font-semibold text-secondary-foreground">Powrót</Text>
            </TouchableOpacity>

            {courseId ? (
              <TouchableOpacity
                className="flex-row items-center justify-center gap-2 rounded-xl bg-primary py-4"
                onPress={() => router.push(`/course/${courseId}`)}>
                <BookOpen size={20} color="white" />
                <Text className="text-lg font-semibold text-primary-foreground">Do kursu</Text>
              </TouchableOpacity>
            ) : (
              <TouchableOpacity
                className="flex-row items-center justify-center gap-2 rounded-xl bg-primary py-4"
                onPress={() => router.back()}>
                <BookOpen size={20} color="white" />
                <Text className="text-lg font-semibold text-primary-foreground">Zamknij</Text>
              </TouchableOpacity>
            )}
          </View>
        </View>
      </View>

      <Text className="mt-6 px-4 text-center text-sm text-muted-foreground">
        Możesz wrócić do kursu, zrobić powtórkę albo rozpocząć kolejną sesję.
      </Text>
    </View>
  );
};
