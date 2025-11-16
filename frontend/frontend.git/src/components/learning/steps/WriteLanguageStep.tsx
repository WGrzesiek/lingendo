"use client";

import { useState } from "react";
import { Card } from "@/components/ui/card";
import { Button } from "@/components/ui/button";
import { Input } from "@/components/ui/input";
import { CheckCircle, XCircle } from "lucide-react";

interface WriteLanguageStepProps {
  word: string;
  translation: string;
  exampleSentence?: string;
  exampleTranslation?: string;
  writeFrom: boolean; // true = widzi translation, pisze word
  onAnswer: (isCorrect: boolean) => void;
}

/**
 * Krok nauki: Wpisz słowo/tłumaczenie
 * Użytkownik musi ręcznie wpisać poprawną odpowiedź
 */
export const WriteLanguageStep = ({
  word,
  translation,
  exampleSentence,
  exampleTranslation,
  writeFrom,
  onAnswer,
}: WriteLanguageStepProps) => {
  const [userInput, setUserInput] = useState("");
  const [showResult, setShowResult] = useState(false);
  const [isCorrect, setIsCorrect] = useState(false);

  const correctAnswer = writeFrom ? word : translation;
  const displayText = writeFrom ? translation : word;

  const normalizeString = (str: string) => {
    return str.toLowerCase().trim();
  };

  const handleSubmit = (e: React.FormEvent) => {
    e.preventDefault();
    const correct =
      normalizeString(userInput) === normalizeString(correctAnswer);
    setIsCorrect(correct);
    setShowResult(true);
  };

  const handleContinue = () => {
    onAnswer(isCorrect);
  };

  return (
    <Card className="p-8 md:p-12">
      <div className="space-y-8">
        <div className="text-center space-y-4">
          <p className="text-sm text-muted-foreground uppercase tracking-wide">
            {writeFrom
              ? "Wpisz słowo po angielsku"
              : "Wpisz tłumaczenie po polsku"}
          </p>
          <h2 className="text-5xl font-bold">{displayText}</h2>
        </div>

        {!showResult ? (
          <form onSubmit={handleSubmit} className="space-y-4">
            <div>
              <Input
                type="text"
                placeholder={
                  writeFrom
                    ? "Wpisz słowo po angielsku..."
                    : "Wpisz tłumaczenie..."
                }
                value={userInput}
                onChange={(e) => setUserInput(e.target.value)}
                className="text-2xl h-16 text-center"
                autoFocus
              />
            </div>
            <Button
              type="submit"
              size="lg"
              className="w-full"
              disabled={!userInput.trim()}
            >
              Sprawdź odpowiedź
            </Button>
          </form>
        ) : (
          <div className="space-y-6">
            <div
              className={`p-6 rounded-lg border-2 ${
                isCorrect
                  ? "bg-green-500/10 border-green-500"
                  : "bg-red-500/10 border-red-500"
              }`}
            >
              <div className="flex items-center justify-center gap-3 mb-4">
                {isCorrect ? (
                  <CheckCircle className="w-8 h-8 text-green-600" />
                ) : (
                  <XCircle className="w-8 h-8 text-red-600" />
                )}
                <p
                  className={`text-2xl font-bold ${
                    isCorrect ? "text-green-600" : "text-red-600"
                  }`}
                >
                  {isCorrect ? "Poprawnie!" : "Niepoprawnie"}
                </p>
              </div>

              {!isCorrect && (
                <div className="space-y-2">
                  <div className="text-center">
                    <p className="text-sm text-muted-foreground">
                      Twoja odpowiedź:
                    </p>
                    <p className="text-xl font-semibold line-through">
                      {userInput}
                    </p>
                  </div>
                  <div className="text-center">
                    <p className="text-sm text-muted-foreground">
                      Poprawna odpowiedź:
                    </p>
                    <p className="text-2xl font-bold text-green-600">
                      {correctAnswer}
                    </p>
                  </div>
                </div>
              )}
            </div>

            {exampleSentence && (
              <div className="p-4 border rounded-lg bg-accent/50">
                <p className="text-sm text-muted-foreground mb-2">
                  Przykład użycia:
                </p>
                <p className="text-lg italic mb-2">
                  &ldquo;{exampleSentence}&rdquo;
                </p>
                {exampleTranslation && (
                  <p className="text-base italic text-muted-foreground">
                    &ldquo;{exampleTranslation}&rdquo;
                  </p>
                )}
              </div>
            )}

            <Button size="lg" onClick={handleContinue} className="w-full">
              Kontynuuj
            </Button>
          </div>
        )}
      </div>
    </Card>
  );
};
