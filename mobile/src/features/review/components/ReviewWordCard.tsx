import { View, Text } from 'react-native';
import { Sparkles } from 'lucide-react-native';
import type { CourseWord } from '@/features/course/types';

interface ReviewWordCardProps {
  word: CourseWord;
}

/**
 * Karta słówka do powtórki - wersja mobilna
 */
export const ReviewWordCard = ({ word }: ReviewWordCardProps) => {
  const allSentences = [...(word.sentences ?? []), ...(word.sentencesAI ?? [])];
  const isOverdue = word.nextReviewAt ? new Date(word.nextReviewAt) < new Date() : false;

  return (
    <View
      className={`rounded-xl border p-4 ${
        isOverdue ? 'border-orange-500/30 bg-orange-500/5' : 'border-border bg-card'
      }`}>
      {/* Badge  */}
      <View className="mb-3 flex-row flex-wrap gap-2">
        <View className="rounded-md border border-border bg-background px-2 py-1">
          <Text className="text-xs text-foreground">Powtórzeń: {word.repetitionCount ?? 0}</Text>
        </View>
        {isOverdue && (
          <View className="rounded-md bg-orange-500/10 px-2 py-1">
            <Text className="text-xs text-orange-600">Zaległe</Text>
          </View>
        )}
        {word.nextReviewAt && (
          <View className="rounded-md border border-border bg-background px-2 py-1">
            <Text className="text-xs text-foreground">
              {new Date(word.nextReviewAt).toLocaleDateString('pl-PL')}
            </Text>
          </View>
        )}
      </View>

      {/* Word */}
      <View className="mb-3 flex-row flex-wrap items-center gap-2">
        <Text className="text-lg font-semibold text-foreground">{word.word}</Text>
        <Text className="text-muted-foreground">→</Text>
        <Text className="text-lg text-foreground">{word.translations?.join(', ')}</Text>
      </View>

      {/* Sentences */}
      {allSentences.length > 0 ? (
        <View className="gap-2">
          {allSentences.slice(0, 2).map((sentence) => (
            <View key={sentence.id} className="gap-1">
              <Text className="text-sm italic text-muted-foreground">
                &ldquo;{sentence.sentence}&rdquo;
              </Text>
              <Text className="text-sm italic text-muted-foreground">
                &ldquo;{sentence.translation}&rdquo;
              </Text>
            </View>
          ))}
        </View>
      ) : (
        <View className="flex-row items-center gap-2">
          <Sparkles size={16} className="text-muted-foreground" />
          <Text className="text-sm text-muted-foreground">Brak przykładowego zdania</Text>
        </View>
      )}
    </View>
  );
};
