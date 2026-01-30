import { Badge } from "@/components/ui/badge";
import { Sparkles } from "lucide-react";
import type { DeckWord } from "@/features/deck/types/deck-details.types";

interface SimpleWordCardProps {
  word: DeckWord;
}

/**
 * Karta słówka - tylko słowo, tłumaczenia i zdania
 * Bez statusów nauki (nauczone/powtórzone)
 */
export const WordCard = ({ word }: SimpleWordCardProps) => {
  const sentences = word.sentences ?? [];
  const sentencesAI = word.sentencesAI ?? [];
  const hasSentences = sentences.length > 0 || sentencesAI.length > 0;

  return (
    <div className="p-4 border rounded-lg hover:bg-accent/50 transition-colors">
      {/* MOBILE LAYOUT */}
      <div className="sm:hidden mb-3">
        <div className="flex items-center gap-2 mb-2">
          <h3 className="font-semibold text-lg">{word.word}</h3>
          <span className="text-muted-foreground">→</span>
          <span className="text-lg">
            {(word.translations ?? []).join(", ")}
          </span>
        </div>
        {hasSentences ? (
          <div className="space-y-2 mt-3">
            {sentences.map((sentence) => (
              <div key={sentence.id} className="space-y-1">
                <p className="text-sm italic text-muted-foreground">
                  &ldquo;{sentence.sentence}&rdquo;
                </p>
                <p className="text-sm italic text-muted-foreground">
                  &ldquo;{sentence.translation}&rdquo;
                </p>
              </div>
            ))}
            {sentencesAI.map((sentence) => (
              <div key={sentence.id} className="space-y-1">
                <div className="flex items-start gap-2">
                  <Sparkles className="w-4 h-4 text-amber-400 mt-0.5 flex-shrink-0" />
                  <div>
                    <p className="text-sm italic text-muted-foreground">
                      &ldquo;{sentence.sentence}&rdquo;
                    </p>
                    <p className="text-sm italic text-muted-foreground">
                      &ldquo;{sentence.translation}&rdquo;
                    </p>
                  </div>
                </div>
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
            <div className="flex flex-wrap gap-2">
              {(word.translations ?? []).map((trans, index) => (
                <Badge
                  key={index}
                  variant={index === 0 ? "default" : "secondary"}
                  className="text-base"
                >
                  {trans}
                </Badge>
              ))}
            </div>
          </div>
          {hasSentences ? (
            <div className="space-y-2">
              {sentences.map((sentence) => (
                <div key={sentence.id} className="space-y-1">
                  <p className="text-sm italic text-muted-foreground">
                    &ldquo;{sentence.sentence}&rdquo;
                  </p>
                  <p className="text-sm italic text-muted-foreground">
                    &ldquo;{sentence.translation}&rdquo;
                  </p>
                </div>
              ))}
              {sentencesAI.map((sentence) => (
                <div key={sentence.id} className="space-y-1">
                  <div className="flex items-start gap-2">
                    <Sparkles className="w-4 h-4 text-amber-400 mt-0.5 flex-shrink-0" />
                    <div>
                      <p className="text-sm italic text-muted-foreground">
                        &ldquo;{sentence.sentence}&rdquo;
                      </p>
                      <p className="text-sm italic text-muted-foreground">
                        &ldquo;{sentence.translation}&rdquo;
                      </p>
                    </div>
                  </div>
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

