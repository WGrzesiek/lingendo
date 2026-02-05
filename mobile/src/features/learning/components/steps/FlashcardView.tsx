import { useEffect, useState } from 'react';
import { View, Text, TouchableOpacity, ScrollView, Pressable } from 'react-native';
import { RotateCcw, Sparkles } from 'lucide-react-native';
import type { WordDto, SentenceDto } from '@/features/course/types';
import type { RememberAnswer } from '../../types';

interface FlashcardViewProps {
  data: WordDto;
  onComplete: (answer: RememberAnswer) => void;
}

/**
 * Komponent fiszki z animacją przewracania
 */
export const FlashcardView = ({ data, onComplete }: FlashcardViewProps) => {
  const allSentences: SentenceDto[] = [...data.sentences, ...data.sentencesAI];
  const [isFlipped, setIsFlipped] = useState(false);

  useEffect(() => {
    setIsFlipped(false);
  }, [data.word]);

  const handleFlip = () => setIsFlipped(true);

  const submit = (remembered: boolean) => {
    onComplete({
      type: 'remembered',
      remembered,
    });
  };

  return (
    <View className="flex-1 gap-6 p-4">
      {/* Karta fiszki */}
      <Pressable onPress={!isFlipped ? handleFlip : undefined}>
        {!isFlipped ? (
          /* FRONT */
          <View className="min-h-[400px] items-center justify-center rounded-2xl border border-border bg-card p-8">
            <Text className="mb-4 text-sm font-medium uppercase tracking-wider text-muted-foreground">
              Słówko
            </Text>
            <Text className="text-center text-5xl font-bold text-primary">{data.word}</Text>
            <Text className="mt-8 text-center text-sm text-muted-foreground">
              Kliknij aby zobaczyć tłumaczenie
            </Text>
          </View>
        ) : (
          /* BACK */
          <View className="min-h-[400px] justify-center rounded-2xl border border-border bg-card p-6">
            <View className="mb-6 items-center">
              <Text className="mb-4 text-sm font-medium uppercase tracking-wider text-muted-foreground">
                {data.translations.length > 1 ? 'Tłumaczenia' : 'Tłumaczenie'}
              </Text>
              <View className="flex-row flex-wrap justify-center gap-2">
                {data.translations.map((translation, index) => (
                  <View
                    key={index}
                    className={`rounded-lg px-4 py-2 ${index === 0 ? 'bg-primary' : 'bg-secondary'}`}>
                    <Text
                      className={`text-2xl font-bold ${index === 0 ? 'text-primary-foreground' : 'text-secondary-foreground'}`}>
                      {translation}
                    </Text>
                  </View>
                ))}
              </View>
            </View>

            {allSentences.length > 0 && (
              <View className="border-t border-border pt-4">
                <View className="mb-4 flex-row items-center justify-center gap-2">
                  <Sparkles size={16} className="text-muted-foreground" />
                  <Text className="text-sm font-medium uppercase tracking-wider text-muted-foreground">
                    Przykłady użycia
                  </Text>
                </View>
                <ScrollView className="max-h-40">
                  {allSentences.slice(0, 3).map((sentence) => (
                    <View
                      key={sentence.id}
                      className="mb-2 rounded-lg border border-border/50 bg-accent/30 p-4">
                      <Text className="mb-1 text-base font-medium text-foreground">
                        {sentence.sentence}
                      </Text>
                      <Text className="text-sm italic text-muted-foreground">
                        {sentence.translation}
                      </Text>
                    </View>
                  ))}
                </ScrollView>
              </View>
            )}
          </View>
        )}
      </Pressable>

      {/* Przyciski oceny */}
      {isFlipped && (
        <View className="rounded-2xl border border-border bg-card p-6">
          <Text className="mb-4 text-center text-base font-semibold text-foreground">
            Zapamiętałeś to słówko?
          </Text>
          <View className="gap-3">
            <TouchableOpacity
              className="items-center rounded-xl bg-primary py-4"
              onPress={() => submit(true)}>
              <Text className="text-lg font-semibold text-primary-foreground">😊 Pamiętam</Text>
            </TouchableOpacity>
            <TouchableOpacity
              className="items-center rounded-xl border border-border bg-secondary py-4"
              onPress={() => submit(false)}>
              <Text className="text-lg font-semibold text-secondary-foreground">
                🔁 Wyświetl ponownie później
              </Text>
            </TouchableOpacity>
          </View>
        </View>
      )}

      {/* Przycisk obrotu */}
      {!isFlipped && (
        <TouchableOpacity
          className="flex-row items-center justify-center gap-2 py-3"
          onPress={handleFlip}>
          <RotateCcw size={16} className="text-muted-foreground" />
          <Text className="text-sm text-muted-foreground">Obróć fiszkę</Text>
        </TouchableOpacity>
      )}
    </View>
  );
};
