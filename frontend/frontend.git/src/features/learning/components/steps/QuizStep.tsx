"use client";

import { useState } from "react";
import { Card } from "@/components/ui/card";
import { Button } from "@/components/ui/button";

interface QuizStepProps {
  word: string;
  correctTranslation: string;
  options: string[];
  onAnswer: (isCorrect: boolean) => void;
  showFrom: boolean; // true = pokazuje słowo i pytamy o tłumaczenie, false = odwrotnie
}

/**
 * Krok nauki: Quiz wielokrotnego wyboru (A, B, C, D)
 */
export const QuizStep = ({
  word,
  correctTranslation,
  options,
  onAnswer,
  showFrom,
}: QuizStepProps) => {
  const [selectedAnswer, setSelectedAnswer] = useState<string | null>(null);
  const [showResult, setShowResult] = useState(false);

  const handleSelect = (option: string) => {
    setSelectedAnswer(option);
    setShowResult(true);
    const isCorrect = option === correctTranslation;
    setTimeout(() => {
      onAnswer(isCorrect);
    }, 1500);
  };

  const getButtonVariant = (option: string) => {
    if (!showResult) return "outline";
    if (option === correctTranslation) return "default";
    if (option === selectedAnswer && option !== correctTranslation)
      return "destructive";
    return "outline";
  };

  return (
    <Card className="p-8 md:p-12">
      <div className="space-y-8">
        <div className="text-center space-y-4">
          <p className="text-sm text-muted-foreground uppercase tracking-wide">
            {showFrom
              ? "Wybierz poprawne tłumaczenie"
              : "Wybierz słowo po angielsku"}
          </p>
          <h2 className="text-5xl font-bold">
            {showFrom ? word : correctTranslation}
          </h2>
        </div>

        <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
          {options.map((option, index) => (
            <Button
              key={index}
              variant={getButtonVariant(option)}
              size="lg"
              className="h-20 text-xl font-semibold"
              onClick={() => handleSelect(option)}
              disabled={showResult}
            >
              <span className="mr-3 text-muted-foreground">
                {String.fromCharCode(65 + index)}.
              </span>
              {option}
            </Button>
          ))}
        </div>

        {showResult && (
          <div
            className={`text-center p-4 rounded-lg ${
              selectedAnswer === correctTranslation
                ? "bg-green-500/10 text-green-600"
                : "bg-red-500/10 text-red-600"
            }`}
          >
            {selectedAnswer === correctTranslation ? (
              <p className="font-semibold">✓ Poprawna odpowiedź!</p>
            ) : (
              <p className="font-semibold">
                ✗ Niepoprawnie. Poprawna odpowiedź to: {correctTranslation}
              </p>
            )}
          </div>
        )}
      </div>
    </Card>
  );
};
