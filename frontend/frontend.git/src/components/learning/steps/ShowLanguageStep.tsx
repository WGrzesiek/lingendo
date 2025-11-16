"use client";

import { useState } from "react";
import { Card } from "@/components/ui/card";
import { Button } from "@/components/ui/button";
import { Eye } from "lucide-react";

interface ShowLanguageStepProps {
  word: string;
  translation: string;
  exampleSentence?: string;
  exampleTranslation?: string;
  showFrom: boolean; // true = pokazuje word, pytamy o translation
  onReveal: () => void;
}

/**
 * Krok nauki: Pokaż jedno, odkryj drugie
 * Użytkownik widzi słowo i sam próbuje sobie przypomnieć tłumaczenie
 */
export const ShowLanguageStep = ({
  word,
  translation,
  exampleSentence,
  exampleTranslation,
  showFrom,
  onReveal,
}: ShowLanguageStepProps) => {
  const [isRevealed, setIsRevealed] = useState(false);

  const handleReveal = () => {
    setIsRevealed(true);
  };

  const handleContinue = () => {
    onReveal();
  };

  return (
    <Card className="p-8 md:p-12">
      <div className="space-y-8">
        <div className="text-center space-y-4">
          <p className="text-sm text-muted-foreground uppercase tracking-wide">
            {showFrom
              ? "Spróbuj sobie przypomnieć tłumaczenie"
              : "Spróbuj sobie przypomnieć słowo po angielsku"}
          </p>
          <h2 className="text-6xl font-bold">
            {showFrom ? word : translation}
          </h2>
        </div>

        {!isRevealed ? (
          <div className="text-center space-y-4">
            <div className="p-8 border-2 border-dashed rounded-lg bg-accent/30">
              <p className="text-muted-foreground mb-4">
                Pomyśl o odpowiedzi, a następnie kliknij aby sprawdzić
              </p>
              <Button size="lg" onClick={handleReveal} className="gap-2">
                <Eye className="w-5 h-5" />
                Pokaż {showFrom ? "tłumaczenie" : "słowo"}
              </Button>
            </div>
          </div>
        ) : (
          <div className="space-y-6">
            <div className="text-center p-6 bg-primary/10 rounded-lg border-2 border-primary">
              <p className="text-sm text-muted-foreground mb-2">
                {showFrom ? "Tłumaczenie" : "Słowo po angielsku"}
              </p>
              <h3 className="text-4xl font-bold text-primary">
                {showFrom ? translation : word}
              </h3>
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

            <div className="text-center">
              <p className="text-muted-foreground mb-4">
                Pamiętałeś to słówko?
              </p>
              <Button size="lg" onClick={handleContinue} className="w-full">
                Kontynuuj
              </Button>
            </div>
          </div>
        )}
      </div>
    </Card>
  );
};
