import { useEffect, useMemo, useState } from 'react';
import { View, Text, TouchableOpacity, TextInput } from 'react-native';
import { CheckCircle, XCircle, Sparkles } from 'lucide-react-native';
import type { WordDto, SentenceDto } from '@/features/course/types';
import type { InteractionType, TypingAnswer } from '@/features/learning';

interface ReviewTypingStepProps {
  data: WordDto;
  interactionType: InteractionType;
  onComplete: (answer: TypingAnswer) => void;
}

/**
 * Krok wpisywania odpowiedzi w powtórce
 */
export const ReviewTypingStep = ({ data, interactionType, onComplete }: ReviewTypingStepProps) => {
  const direction: 'FROM' | 'TO' = interactionType === 'TYPING_INPUT_TO' ? 'TO' : 'FROM';

  const [userInput, setUserInput] = useState('');
  const [showResult, setShowResult] = useState(false);

  const allSentences: SentenceDto[] = useMemo(
    () => [...(data.sentences ?? []), ...(data.sentencesAI ?? [])],
    [data.sentences, data.sentencesAI]
  );

  useEffect(() => {
    setUserInput('');
    setShowResult(false);
  }, [data.word, direction]);

  const normalize = (s: string) => s.toLowerCase().trim();

  const displayText = direction === 'FROM' ? data.word : data.translations[0];

  const localCorrect = useMemo(() => {
    if (!showResult) return null;
    const input = normalize(userInput);
    if (direction === 'FROM') {
      return (data.translations ?? []).some((t) => normalize(t) === input);
    }
    return normalize(data.word) === input;
  }, [showResult, userInput, direction, data.word, data.translations]);

  const handleSubmit = () => {
    if (!userInput.trim()) return;
    setShowResult(true);
  };

  const handleContinue = () => {
    onComplete({
      type: 'text',
      text: userInput,
    });
  };

  return (
    <View className="flex-1 p-4">
      <View className="rounded-2xl border border-border bg-card p-6">
        {/* Nagłówek */}
        <View className="mb-6 items-center">
          <View className="mb-4 rounded-full bg-secondary px-3 py-1">
            <Text className="text-xs font-medium uppercase tracking-wider text-secondary-foreground">
              {direction === 'FROM' ? 'Wpisz tłumaczenie' : 'Wpisz słowo'}
            </Text>
          </View>
          <Text className="text-center text-4xl font-bold text-primary">{displayText}</Text>

          {direction === 'FROM' && (data.translations?.length ?? 0) > 1 && !showResult && (
            <Text className="mt-3 text-center text-sm italic text-muted-foreground">
              Wskazówka: to słowo ma {data.translations.length} tłumaczeń
            </Text>
          )}
        </View>

        {!showResult ? (
          /* Formularz */
          <View className="gap-4">
            <TextInput
              className="rounded-xl border-2 border-border bg-background px-4 py-4 text-center text-xl font-semibold text-foreground"
              placeholder={
                direction === 'FROM' ? 'Wpisz jedno z możliwych tłumaczeń...' : 'Wpisz słowo...'
              }
              placeholderTextColor="#9ca3af"
              value={userInput}
              onChangeText={setUserInput}
              autoFocus
              autoCapitalize="none"
              autoCorrect={false}
            />
            <TouchableOpacity
              className={`items-center rounded-xl py-4 ${userInput.trim() ? 'bg-primary' : 'bg-muted'}`}
              onPress={handleSubmit}
              disabled={!userInput.trim()}>
              <Text
                className={`text-lg font-semibold ${userInput.trim() ? 'text-primary-foreground' : 'text-muted-foreground'}`}>
                Sprawdź odpowiedź
              </Text>
            </TouchableOpacity>
          </View>
        ) : (
          /* Wynik */
          <View className="gap-4">
            {localCorrect !== null && (
              <View
                className={`rounded-xl border-2 p-6 ${
                  localCorrect ? 'border-green-500 bg-green-500/10' : 'border-red-500 bg-red-500/10'
                }`}>
                <View className="items-center gap-3">
                  <View className="flex-row items-center gap-2">
                    {localCorrect ? (
                      <CheckCircle size={32} color="#16a34a" />
                    ) : (
                      <XCircle size={32} color="#dc2626" />
                    )}
                    <Text
                      className={`text-2xl font-bold ${localCorrect ? 'text-green-600' : 'text-red-600'}`}>
                      {localCorrect ? 'Świetnie!' : 'Niestety nie'}
                    </Text>
                  </View>

                  {!localCorrect && (
                    <View className="mt-2 w-full items-center rounded-lg bg-background/50 p-3">
                      <Text className="mb-1 text-sm text-muted-foreground">Twoja odpowiedź:</Text>
                      <Text className="text-lg font-semibold text-muted-foreground line-through">
                        {userInput}
                      </Text>
                    </View>
                  )}
                </View>
              </View>
            )}

            {/* Przykład użycia */}
            {allSentences.length > 0 && (
              <View className="rounded-xl border border-border bg-accent/30 p-4">
                <View className="mb-3 flex-row items-center justify-center gap-2">
                  <Sparkles size={16} className="text-muted-foreground" />
                  <Text className="text-sm font-medium uppercase tracking-wider text-muted-foreground">
                    Przykład użycia
                  </Text>
                </View>
                <View className="rounded-lg bg-background/50 p-3">
                  <Text className="mb-1 text-base font-medium text-foreground">
                    {allSentences[0].sentence}
                  </Text>
                  <Text className="text-sm italic text-muted-foreground">
                    {allSentences[0].translation}
                  </Text>
                </View>
              </View>
            )}

            {/* Przycisk kontynuuj */}
            <TouchableOpacity
              className="items-center rounded-xl bg-primary py-4"
              onPress={handleContinue}>
              <Text className="text-lg font-semibold text-primary-foreground">Kontynuuj</Text>
            </TouchableOpacity>
          </View>
        )}
      </View>
    </View>
  );
};
