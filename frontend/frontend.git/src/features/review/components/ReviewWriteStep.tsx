"use client";

import { useState } from "react";
import { Card } from "@/components/ui/card";
import { Button } from "@/components/ui/button";
import { Input } from "@/components/ui/input";
import { Badge } from "@/components/ui/badge";
import { CheckCircle, XCircle, Sparkles } from "lucide-react";
import type { ReviewWord } from "@/features/review/types/review.types";

interface ReviewWriteStepProps {
  word: ReviewWord;
  onAnswer: (isCorrect: boolean, responseTimeMs: number) => void;
}

/**
 * Komponent kroku powtórki - tylko pisanie słówka
 * User widzi tłumaczenie + zdanie, musi wpisać słówko
 */
export const ReviewWriteStep = ({ word, onAnswer }: ReviewWriteStepProps) => {
  const [userInput, setUserInput] = useState("");
  const [showResult, setShowResult] = useState(false);
  const [isCorrect, setIsCorrect] = useState(false);
  const [startTime] = useState(Date.now());

  const allSentences = [...word.content.sentences, ...word.content.sentencesAI];
  const mainTranslation = word.content.translations[0];
  const exampleSentence = allSentences[0];

  const normalizeString = (str: string) => {
    return str.toLowerCase().trim();
  };

  const handleSubmit = (e: React.FormEvent) => {
    e.preventDefault();
    const normalizedInput = normalizeString(userInput);
    const normalizedWord = normalizeString(word.content.word);
    const correct = normalizedInput === normalizedWord;

    setIsCorrect(correct);
    setShowResult(true);

    const responseTime = Date.now() - startTime;

    // Czekamy 2s aby user zobaczył wynik
    setTimeout(() => {
      onAnswer(correct, responseTime);
    }, 2000);
  };

  return (
    <Card className="p-8 md:p-12">
      <div className="space-y-8">
        {/* Header z trudnością i liczbą powtórzeń */}
        <div className="flex justify-center gap-3">
          <Badge variant="outline">Trudność: {word.difficultyLevel}/5</Badge>
          <Badge variant="outline">Powtórzeń: {word.repetitionCount}</Badge>
        </div>

        {/* Tłumaczenie - główny bodziec */}
        <div className="text-center space-y-4">
          <Badge variant="outline" className="text-xs uppercase tracking-wider">
            Wpisz słówko po angielsku
          </Badge>
          <div>
            <p className="text-sm text-muted-foreground mb-2">Tłumaczenie:</p>
            <h2 className="text-4xl md:text-6xl font-bold bg-gradient-to-r from-primary to-primary/60 bg-clip-text text-transparent">
              {mainTranslation}
            </h2>
            {word.content.translations.length > 1 && (
              <div className="flex flex-wrap gap-2 justify-center mt-3">
                {word.content.translations.slice(1).map((trans, index) => (
                  <Badge key={index} variant="secondary" className="text-base">
                    {trans}
                  </Badge>
                ))}
              </div>
            )}
          </div>
        </div>

        {/* Przykładowe zdanie */}
        {exampleSentence && (
          <div className="p-6 border rounded-xl bg-accent/30">
            <p className="text-sm text-muted-foreground mb-3 uppercase tracking-wider flex items-center justify-center gap-2">
              <Sparkles className="w-4 h-4" />
              Przykład użycia
            </p>
            <div className="space-y-2">
              <p className="text-base md:text-lg text-center font-medium">
                &ldquo;{exampleSentence.sentence}&rdquo;
              </p>
              <p className="text-sm md:text-base text-muted-foreground text-center italic">
                &ldquo;{exampleSentence.translation}&rdquo;
              </p>
            </div>
          </div>
        )}

        {/* Input / Result */}
        {!showResult ? (
          <form onSubmit={handleSubmit} className="space-y-6">
            <div>
              <Input
                type="text"
                placeholder="Zacznij pisać..."
                value={userInput}
                onChange={(e) => setUserInput(e.target.value)}
                className="text-2xl md:text-3xl h-16 md:h-20 text-center font-semibold"
                autoFocus
              />
            </div>
            <Button
              type="submit"
              size="lg"
              className="w-full text-lg"
              disabled={!userInput.trim()}
            >
              Sprawdź odpowiedź
            </Button>
          </form>
        ) : (
          <div className="space-y-6">
            <Card
              className={`p-8 border-2 ${
                isCorrect
                  ? "bg-green-500/10 border-green-500"
                  : "bg-red-500/10 border-red-500"
              }`}
            >
              <div className="flex flex-col items-center gap-4">
                <div className="flex items-center gap-3">
                  {isCorrect ? (
                    <CheckCircle className="w-10 h-10 text-green-600" />
                  ) : (
                    <XCircle className="w-10 h-10 text-red-600" />
                  )}
                  <p
                    className={`text-3xl font-bold ${
                      isCorrect ? "text-green-600" : "text-red-600"
                    }`}
                  >
                    {isCorrect ? "Świetnie!" : "Niestety nie"}
                  </p>
                </div>

                {!isCorrect && (
                  <div className="space-y-4 w-full">
                    <div className="text-center p-4 bg-background/50 rounded-lg">
                      <p className="text-sm text-muted-foreground mb-2">
                        Twoja odpowiedź:
                      </p>
                      <p className="text-xl font-semibold line-through opacity-60">
                        {userInput}
                      </p>
                    </div>
                    <div className="text-center p-4 bg-background/50 rounded-lg">
                      <p className="text-sm text-muted-foreground mb-3">
                        Poprawna odpowiedź:
                      </p>
                      <p className="text-2xl md:text-3xl font-bold text-green-600">
                        {word.content.word}
                      </p>
                    </div>
                  </div>
                )}
              </div>
            </Card>

            {/* Pokazujemy dodatkowe zdania po odpowiedzi */}
            {allSentences.length > 1 && (
              <div className="p-6 border rounded-xl bg-accent/20">
                <p className="text-sm text-muted-foreground mb-4 uppercase tracking-wider text-center">
                  Więcej przykładów
                </p>
                <div className="space-y-3">
                  {allSentences.slice(1, 3).map((sentence) => (
                    <div
                      key={sentence.id}
                      className="p-3 bg-background/50 rounded-lg"
                    >
                      <p className="text-sm md:text-base mb-1">
                        &ldquo;{sentence.sentence}&rdquo;
                      </p>
                      <p className="text-xs md:text-sm text-muted-foreground italic">
                        &ldquo;{sentence.translation}&rdquo;
                      </p>
                    </div>
                  ))}
                </div>
              </div>
            )}
          </div>
        )}
      </div>
    </Card>
  );
};
