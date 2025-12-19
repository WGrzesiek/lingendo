import { Badge } from "@/components/ui/badge";
import { Card } from "@/components/ui/card";
import { Sparkles } from "lucide-react";
import type {CourseWord} from "@/features/course/types/words.types";

interface ReviewWordCardProps {
  word: CourseWord;
}

/**
 * Karta słówka do powtórki - wygląda jak na stronie course
 */
export const ReviewWordCard = ({ word }: ReviewWordCardProps) => {
  const allSentences = [...word.sentences, ...word.sentencesAI];
  const isOverdue = new Date(word.nextReviewAt) < new Date();

  return (
    <div
      className={`p-4 border rounded-lg hover:bg-accent/50 transition-colors ${
        isOverdue ? "border-orange-500/30 bg-orange-500/5" : ""
      }`}
    >
      {/* MOBILE LAYOUT */}
      <div className="flex flex-wrap items-center gap-2 mb-3 sm:hidden">
        <Badge variant="outline" className="text-xs">
          Powtórzeń: {word.repetitionCount}
        </Badge>
        {isOverdue && (
          <Badge
            variant="secondary"
            className="text-xs bg-orange-500/10 text-orange-600"
          >
            Zaległe
          </Badge>
        )}
        <Badge variant="outline" className="text-xs">
          {new Date(word.nextReviewAt).toLocaleDateString("pl-PL")}
        </Badge>
      </div>

      {/* MOBILE - słówko */}
      <div className="sm:hidden mb-3">
        <div className="flex items-center gap-2 mb-2">
          <h3 className="font-semibold text-lg">{word.word}</h3>
          <span className="text-muted-foreground">→</span>
          <span className="text-lg">
            {word.translations.join(", ")}
          </span>
        </div>
        {word.sentences.length > 0 || word.sentencesAI.length > 0 ? (
            <div className="space-y-2 mt-3">
              {[...word.sentences, ...word.sentencesAI].map((sentence) => (
                  <div key={sentence.id} className="space-y-1">
                    <p className="text-sm italic text-muted-foreground">
                      &ldquo;{sentence.sentence}&rdquo;
                    </p>
                    <p className="text-sm italic text-muted-foreground">
                      &ldquo;{sentence.translation}&rdquo;
                    </p>
                  </div>
              ))}
            </div>
        ) : (
            <div className="flex items-center gap-2 text-sm text-muted-foreground mt-3">
              <Sparkles className="w-4 h-4" />
              Brak przykładowego zdania
            </div>
        )}
      </div>

      {/* DESKTOP LAYOUT */}
      <div className="hidden sm:flex items-start justify-between gap-4">
        <div className="flex-1">
          <div className="flex items-center gap-2 mb-2 flex-wrap">
            <h3 className="font-semibold text-lg">{word.word}</h3>
            <span className="text-muted-foreground">→</span>
            <span className="text-lg">
              {word.translations.join(", ")}
            </span>
            <Badge variant="outline" className="text-xs">
              Powtórzeń: {word.repetitionCount}
            </Badge>
            {isOverdue && (
              <Badge
                variant="secondary"
                className="text-xs bg-orange-500/10 text-orange-600"
              >
                Zaległe
              </Badge>
            )}
            <Badge variant="outline" className="text-xs">
              Powtórka:{" "}
              {new Date(word.nextReviewAt).toLocaleDateString("pl-PL")}
            </Badge>
          </div>

          {allSentences.length > 0 ? (
            <div className="space-y-2">
              {allSentences.slice(0, 2).map((sentence) => (
                <div key={sentence.id} className="space-y-1">
                  <p className="text-sm italic text-muted-foreground">
                    &ldquo;{sentence.sentence}&rdquo;
                  </p>
                  <p className="text-sm italic text-muted-foreground">
                    &ldquo;{sentence.translation}&rdquo;
                  </p>
                </div>
              ))}
            </div>
          ) : (
            <div className="flex items-center gap-2 text-sm text-muted-foreground">
              <Sparkles className="w-4 h-4" />
              Brak przykładowego zdania
            </div>
          )}
        </div>
      </div>
    </div>
  );
};
