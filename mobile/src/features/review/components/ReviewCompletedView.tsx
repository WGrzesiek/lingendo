import { View, Text, TouchableOpacity } from 'react-native';
import { CheckCircle2, ArrowLeft, BookOpen, Calendar } from 'lucide-react-native';
import { router } from 'expo-router';

interface ReviewCompletedViewProps {
  title?: string;
  description?: string;
  courseId?: string;
}

/**
 * Widok zakończonej powtórki
 */
export const ReviewCompletedView = ({
  title = 'Na dziś wszystko gotowe 🎉',
  description = 'Nie masz już słówek do powtórki na dziś. Świetna robota — wróć jutro po kolejną porcję!',
  courseId,
}: ReviewCompletedViewProps) => {
  return (
    <View className="flex-1 justify-center bg-background p-4">
      <View className="rounded-2xl border border-border bg-card p-8">
        <View className="items-center gap-4">
          {/* Ikona */}
          <View className="relative">
            <CheckCircle2 size={64} className="text-primary" />
          </View>

          {/* Badge */}
          <View className="flex-row items-center gap-2 rounded-full bg-secondary px-3 py-1">
            <Calendar size={12} className="text-secondary-foreground" />
            <Text className="text-xs font-medium uppercase tracking-wider text-secondary-foreground">
              Powtórki na dziś zakończone
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
              <Text className="text-lg font-semibold text-secondary-foreground">Wróć</Text>
            </TouchableOpacity>

            {courseId ? (
              <TouchableOpacity
                className="flex-row items-center justify-center gap-2 rounded-xl bg-primary py-4"
                onPress={() => router.push(`/course/${courseId}`)}>
                <BookOpen size={20} color="white" />
                <Text className="text-lg font-semibold text-primary-foreground">
                  Przejdź do kursu
                </Text>
              </TouchableOpacity>
            ) : (
              <TouchableOpacity
                className="flex-row items-center justify-center gap-2 rounded-xl bg-primary py-4"
                onPress={() => router.push('/courses')}>
                <BookOpen size={20} color="white" />
                <Text className="text-lg font-semibold text-primary-foreground">
                  Przeglądaj kursy
                </Text>
              </TouchableOpacity>
            )}
          </View>
        </View>
      </View>

      <Text className="mt-6 px-4 text-center text-sm text-muted-foreground">
        Nowe słówka do powtórki pojawią się zgodnie z harmonogramem nauki.
      </Text>
    </View>
  );
};
