import { useEffect, useState } from 'react';
import { View, Text, TouchableOpacity, ScrollView } from 'react-native';
import { Eye, Sparkles } from 'lucide-react-native';
import type { WordDto } from '@/features/course/types';
import type { InteractionType, RememberAnswer } from '../../types';

interface RememberCheckBaseProps {
  data: WordDto;
  interactionType: InteractionType;
  onComplete: (answer: RememberAnswer) => void;
}

/**
 * Krok: użytkownik próbuje sobie przypomnieć, potem klika "Pokaż", a na końcu ocenia.
 */
export const RememberCheckBase = ({
  data,
  interactionType,
  onComplete,
}: RememberCheckBaseProps) => {
  const direction: 'FROM' | 'TO' = interactionType === 'REMEMBER_CHECK_TO' ? 'TO' : 'FROM';
  const [isRevealed, setIsRevealed] = useState(false);

  const allSentences = [...data.sentences, ...data.sentencesAI];

  useEffect(() => {
    setIsRevealed(false);
  }, [data.word]);

  const promptText =
    direction === 'FROM' ? 'Przypomnij sobie tłumaczenie' : 'Przypomnij sobie słowo';
  const question = direction === 'FROM' ? data.word : data.translations[0] || '';
  const showWordAsAnswer = direction === 'TO';

  const submit = (remembered: boolean) => {
    onComplete({ type: 'remembered', remembered });
  };

  return (
    <ScrollView className="flex-1 p-4">
      <View className="rounded-2xl border border-border bg-card p-6">
        {/* Nagłówek z pytaniem */}
        <View className="mb-6 items-center">
          <View className="mb-4 rounded-full bg-secondary px-3 py-1">
            <Text className="text-xs font-medium uppercase tracking-wider text-secondary-foreground">
              {promptText}
            </Text>
          </View>
          <Text className="text-center text-5xl font-bold text-primary">{question}</Text>
        </View>

        {!isRevealed ? (
          /* Przycisk "Pokaż" */
          <View className="items-center rounded-xl border-2 border-dashed border-border bg-accent/20 py-8">
            <Text className="mb-6 px-4 text-center text-base text-muted-foreground">
              Pomyśl o odpowiedzi, a następnie kliknij aby sprawdzić
            </Text>
            <TouchableOpacity
              className="flex-row items-center gap-2 rounded-xl bg-primary px-6 py-4"
              onPress={() => setIsRevealed(true)}>
              <Eye size={20} color="white" />
              <Text className="text-base font-semibold text-primary-foreground">
                Pokaż {direction === 'FROM' ? 'tłumaczenie' : 'słowo'}
              </Text>
            </TouchableOpacity>
          </View>
        ) : (
          /* Odpowiedź + ocena */
          <View className="gap-4">
            {/* Odpowiedź */}
            <View className="items-center rounded-xl border-2 border-primary bg-primary/10 p-6">
              <Text className="mb-3 text-sm uppercase tracking-wider text-muted-foreground">
                {direction === 'FROM' ? 'Tłumaczenie' : 'Słówko'}
              </Text>

              {showWordAsAnswer ? (
                <Text className="text-4xl font-bold text-primary">{data.word}</Text>
              ) : (
                <View className="flex-row flex-wrap justify-center gap-2">
                  {data.translations.map((trans, index) => (
                    <View
                      key={index}
                      className={`rounded-lg px-4 py-2 ${index === 0 ? 'bg-primary' : 'bg-secondary'}`}>
                      <Text
                        className={`text-2xl font-bold ${index === 0 ? 'text-primary-foreground' : 'text-secondary-foreground'}`}>
                        {trans}
                      </Text>
                    </View>
                  ))}
                </View>
              )}
            </View>

            {/* Przykłady użycia */}
            {allSentences.length > 0 && (
              <View className="rounded-xl border border-border bg-accent/30 p-4">
                <View className="mb-3 flex-row items-center justify-center gap-2">
                  <Sparkles size={16} className="text-muted-foreground" />
                  <Text className="text-sm font-medium uppercase tracking-wider text-muted-foreground">
                    Przykłady użycia
                  </Text>
                </View>
                {allSentences.slice(0, 2).map((sentence) => (
                  <View key={sentence.id} className="mb-2 rounded-lg bg-background/50 p-3">
                    <Text className="mb-1 text-base font-medium text-foreground">
                      {sentence.sentence}
                    </Text>
                    <Text className="text-sm italic text-muted-foreground">
                      {sentence.translation}
                    </Text>
                  </View>
                ))}
              </View>
            )}

            {/* Przyciski oceny */}
            <View className="rounded-xl border border-border bg-accent/50 p-4">
              <Text className="mb-4 text-center text-base font-semibold text-foreground">
                Pamiętałeś to?
              </Text>
              <View className="gap-3">
                <TouchableOpacity
                  className="items-center rounded-xl bg-primary py-4"
                  onPress={() => submit(true)}>
                  <Text className="text-lg font-semibold text-primary-foreground">
                    😊 Pamiętałem
                  </Text>
                </TouchableOpacity>
                <TouchableOpacity
                  className="items-center rounded-xl border border-border bg-secondary py-4"
                  onPress={() => submit(false)}>
                  <Text className="text-lg font-semibold text-secondary-foreground">
                    🔁 Nie pamiętałem
                  </Text>
                </TouchableOpacity>
              </View>
            </View>
          </View>
        )}
      </View>
    </ScrollView>
  );
};
