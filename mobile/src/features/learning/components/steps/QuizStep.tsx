import { useEffect, useMemo, useState } from 'react';
import { View, Text, TouchableOpacity } from 'react-native';
import { CheckCircle2, XCircle } from 'lucide-react-native';
import type { WordDto, SentenceDto } from '@/features/course/types';
import type { QuizAnswer } from '../../types';

type Direction = 'FROM' | 'TO';

interface QuizBaseProps {
  data: WordDto;
  options: string[];
  direction: Direction;
  onComplete: (answer: QuizAnswer) => void;
}

/**
 * QUIZ step
 */
const QuizStepBase = ({ data, options, direction, onComplete }: QuizBaseProps) => {
  const [selected, setSelected] = useState<string | null>(null);
  const [showResult, setShowResult] = useState(false);

  const optionsKey = options.join('|');

  useEffect(() => {
    setSelected(null);
    setShowResult(false);
  }, [data.word, optionsKey, direction]);

  const correct = useMemo(() => {
    if (direction === 'FROM') return data.translations[0] || '';
    return data.word;
  }, [data, direction]);

  const prompt = direction === 'FROM' ? data.word : data.translations[0] || '';

  const exampleSentence: SentenceDto | undefined =
    data.sentences?.[0] ?? data.sentencesAI?.[0] ?? undefined;

  const handleSelect = (option: string) => {
    setSelected(option);
    setShowResult(true);

    setTimeout(() => {
      onComplete({ type: 'choice', selectedOption: option });
    }, 900);
  };

  const getButtonStyle = (option: string) => {
    if (!showResult) return 'bg-secondary border-border';
    if (option === correct) return 'bg-green-500/20 border-green-500';
    if (option === selected && option !== correct) return 'bg-red-500/20 border-red-500';
    return 'bg-secondary border-border';
  };

  const getTextStyle = (option: string) => {
    if (!showResult) return 'text-secondary-foreground';
    if (option === correct) return 'text-green-600';
    if (option === selected && option !== correct) return 'text-red-600';
    return 'text-secondary-foreground';
  };

  const isCorrect = selected != null && selected === correct;

  return (
    <View className="flex-1 p-4">
      <View className="rounded-2xl border border-border bg-card p-6">
        {/* Nagłówek */}
        <View className="mb-6 items-center">
          <View className="mb-4 rounded-full bg-secondary px-3 py-1">
            <Text className="text-xs font-medium uppercase tracking-wider text-secondary-foreground">
              {direction === 'FROM' ? 'Quiz: wybierz tłumaczenie' : 'Quiz: wybierz słówko'}
            </Text>
          </View>
          <Text className="text-center text-4xl font-bold text-primary">{prompt}</Text>

          {exampleSentence && !showResult && (
            <Text className="mt-4 px-4 text-center text-sm italic text-muted-foreground">
              Kontekst: {exampleSentence.sentence}
            </Text>
          )}
        </View>

        {/* Opcje */}
        <View className="mb-4 gap-3">
          {options.map((option, index) => (
            <TouchableOpacity
              key={`${option}-${index}`}
              className={`flex-row items-center rounded-xl border-2 p-4 ${getButtonStyle(option)}`}
              onPress={() => handleSelect(option)}
              disabled={showResult}>
              <Text className="mr-3 text-lg font-bold text-muted-foreground">
                {String.fromCharCode(65 + index)}.
              </Text>
              <Text className={`flex-1 text-lg font-semibold ${getTextStyle(option)}`}>
                {option}
              </Text>
            </TouchableOpacity>
          ))}
        </View>

        {/* Wynik */}
        {showResult && selected && (
          <View
            className={`rounded-xl border-2 p-4 ${
              isCorrect ? 'border-green-500 bg-green-500/10' : 'border-red-500 bg-red-500/10'
            }`}>
            <View className="flex-row items-center justify-center gap-2">
              {isCorrect ? (
                <>
                  <CheckCircle2 size={24} color="#16a34a" />
                  <Text className="text-lg font-bold text-green-600">
                    Świetnie! Poprawna odpowiedź!
                  </Text>
                </>
              ) : (
                <View className="items-center">
                  <View className="mb-2 flex-row items-center gap-2">
                    <XCircle size={24} color="#dc2626" />
                    <Text className="text-lg font-bold text-red-600">Niepoprawnie</Text>
                  </View>
                  <Text className="text-sm text-muted-foreground">
                    Poprawna odpowiedź: <Text className="font-bold">{correct}</Text>
                  </Text>
                </View>
              )}
            </View>
          </View>
        )}
      </View>
    </View>
  );
};

/**
 * Quiz FROM - wybierz tłumaczenie
 */
export const QuizFrom = (props: {
  data: WordDto;
  options: string[];
  onComplete: (a: QuizAnswer) => void;
}) => <QuizStepBase {...props} direction="FROM" />;

/**
 * Quiz TO - wybierz słówko
 */
export const QuizTo = (props: {
  data: WordDto;
  options: string[];
  onComplete: (a: QuizAnswer) => void;
}) => <QuizStepBase {...props} direction="TO" />;
