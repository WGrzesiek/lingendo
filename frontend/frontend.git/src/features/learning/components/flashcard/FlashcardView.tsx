"use client";

import { useState } from "react";
import { Card } from "@/components/ui/card";
import { Button } from "@/components/ui/button";
import { RotateCcw, Volume2 } from "lucide-react";

interface Word {
  id: string;
  word: string;
  translation: string;
  exampleSentence?: string;
  exampleTranslation?: string;
}

interface FlashcardViewProps {
  word: Word;
  onAnswer: (difficulty: "easy" | "medium" | "hard") => void;
}

/**
 * Komponent fiszki z animacją przewracania
 * Użytkownik może przewrócić fiszkę i ocenić jak trudne było słówko
 */
export const FlashcardView = ({ word, onAnswer }: FlashcardViewProps) => {
  const [isFlipped, setIsFlipped] = useState(false);

  const handleFlip = () => {
    setIsFlipped(!isFlipped);
  };

  const handleAnswer = (difficulty: "easy" | "medium" | "hard") => {
    setIsFlipped(false);
    setTimeout(() => {
      onAnswer(difficulty);
    }, 200);
  };

  const handlePlayAudio = () => {
    console.log("Play audio for:", word.word);
  };

  return (
    <div className="space-y-6">
      <div
        className="perspective-1000 cursor-pointer"
        onClick={handleFlip}
        style={{ perspective: "1000px" }}
      >
        <div
          className={`relative w-full transition-transform duration-500 transform-style-3d ${
            isFlipped ? "rotate-y-180" : ""
          }`}
          style={{
            transformStyle: "preserve-3d",
            transform: isFlipped ? "rotateY(180deg)" : "rotateY(0deg)",
          }}
        >
          {!isFlipped ? (
            <Card className="p-12 min-h-[400px] flex flex-col items-center justify-center text-center space-y-6 backface-hidden hover:shadow-lg transition-shadow">
              <div className="space-y-4">
                <p className="text-sm text-muted-foreground uppercase tracking-wide">
                  Słówko
                </p>
                <h2 className="text-6xl font-bold">{word.word}</h2>
                <Button
                  variant="ghost"
                  size="sm"
                  className="gap-2"
                  onClick={(e) => {
                    e.stopPropagation();
                    handlePlayAudio();
                  }}
                >
                  <Volume2 className="w-4 h-4" />
                  Wymowa
                </Button>
              </div>
              <p className="text-muted-foreground text-sm mt-8">
                Kliknij aby zobaczyć tłumaczenie
              </p>
            </Card>
          ) : (
            <Card
              className="p-12 min-h-[400px] flex flex-col items-center justify-center text-center space-y-6 backface-hidden hover:shadow-lg transition-shadow"
              style={{ transform: "rotateY(180deg)" }}
            >
              <div className="space-y-6 w-full">
                <div className="space-y-2">
                  <p className="text-sm text-muted-foreground uppercase tracking-wide">
                    Tłumaczenie
                  </p>
                  <h2 className="text-5xl font-bold text-primary">
                    {word.translation}
                  </h2>
                </div>

                {word.exampleSentence && (
                  <div className="pt-6 border-t space-y-3">
                    <p className="text-sm text-muted-foreground uppercase tracking-wide">
                      Przykład użycia
                    </p>
                    <p className="text-xl italic">
                      &ldquo;{word.exampleSentence}&rdquo;
                    </p>
                    {word.exampleTranslation && (
                      <p className="text-lg italic text-muted-foreground">
                        &ldquo;{word.exampleTranslation}&rdquo;
                      </p>
                    )}
                  </div>
                )}
              </div>
            </Card>
          )}
        </div>
      </div>

      {isFlipped && (
        <Card className="p-6">
          <div className="space-y-4">
            <p className="text-center text-sm font-medium">
              Jak dobrze znasz to słówko?
            </p>
            <div className="grid grid-cols-3 gap-3">
              <Button
                variant="outline"
                size="lg"
                onClick={() => handleAnswer("hard")}
                className="flex flex-col gap-2 h-auto py-4 hover:bg-red-500/10 hover:border-red-500"
              >
                <span className="text-2xl">😰</span>
                <span className="font-semibold">Trudne</span>
                <span className="text-xs text-muted-foreground">
                  Pokażę ponownie za 1 min
                </span>
              </Button>

              <Button
                variant="outline"
                size="lg"
                onClick={() => handleAnswer("medium")}
                className="flex flex-col gap-2 h-auto py-4 hover:bg-yellow-500/10 hover:border-yellow-500"
              >
                <span className="text-2xl">🤔</span>
                <span className="font-semibold">Średnie</span>
                <span className="text-xs text-muted-foreground">
                  Pokażę ponownie za 10 min
                </span>
              </Button>

              <Button
                variant="outline"
                size="lg"
                onClick={() => handleAnswer("easy")}
                className="flex flex-col gap-2 h-auto py-4 hover:bg-green-500/10 hover:border-green-500"
              >
                <span className="text-2xl">😊</span>
                <span className="font-semibold">Łatwe</span>
                <span className="text-xs text-muted-foreground">
                  Pokażę ponownie za 1 dzień
                </span>
              </Button>
            </div>
          </div>
        </Card>
      )}

      {!isFlipped && (
        <div className="flex justify-center">
          <Button
            variant="ghost"
            size="sm"
            className="gap-2"
            onClick={handleFlip}
          >
            <RotateCcw className="w-4 h-4" />
            Obróć fiszkę
          </Button>
        </div>
      )}
    </div>
  );
};
