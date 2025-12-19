"use client";

import { useEffect, useMemo, useState } from "react";
import { Card } from "@/components/ui/card";
import { Button } from "@/components/ui/button";
import { Input } from "@/components/ui/input";
import { Badge } from "@/components/ui/badge";
import { CheckCircle, XCircle, Sparkles } from "lucide-react";
import type { SentenceDto, WordDto } from "@/types/word";
import type {InteractionType, TypingAnswer} from "@/features/learning/types/learning.types";

type Direction = "FROM" | "TO";

interface TypingBaseProps {
  data: WordDto;
  interactionType: InteractionType;
  onComplete: (answer: TypingAnswer) => void;
}

/**
 * TYPING step:
 * - user wpisuje tekst
 * - front może pokazać lokalny feedback (opcjonalnie)
 * - finalnie wysyłamy onComplete({type:'text', text})
 */
export const TypingStepBase = ({ data, interactionType, onComplete }: TypingBaseProps) => {
  const direction: "FROM" | "TO" = interactionType === "TYPING_INPUT_TO" ? "TO" : "FROM";
  console.log(direction);
  console.log(interactionType);
  const [userInput, setUserInput] = useState("");
  const [showResult, setShowResult] = useState(false);

  const allSentences: SentenceDto[] = useMemo(
      () => [...(data.sentences ?? []), ...(data.sentencesAI ?? [])],
      [data.sentences, data.sentencesAI]
  );

  useEffect(() => {
    setUserInput("");
    setShowResult(false);
  }, [data.word, direction]);

  const normalize = (s: string) => s.toLowerCase().trim();


  const displayText =
      direction === "FROM"
          ? data.word                // widzę słowo
          : data.translations[0];    // widzę tłumaczenie

  const localCorrect = useMemo(() => {
    if (!showResult) return null;

    const input = normalize(userInput);

    if (direction === "FROM") {
      return (data.translations ?? []).some(t => normalize(t) === input);
    }
    return normalize(data.word) === input;


  }, [showResult, userInput, direction, data.word, data.translations]);

  const handleSubmit = (e: React.FormEvent) => {
    e.preventDefault();
    if (!userInput.trim()) return;

    setShowResult(true);
  };

  const handleContinue = () => {
    onComplete({
      type: "text",
      text: userInput,
    });
  };

  return (
      <Card className="p-8 md:p-12">
        <div className="space-y-8">
          <div className="text-center space-y-4">
            {/*<Badge variant="outline" className="text-xs uppercase tracking-wider">*/}
            {/*  {direction === "FROM"*/}
            {/*      ? "Wpisz słowo (język docelowy)"*/}
            {/*      : "Wpisz tłumaczenie"}*/}
            {/*</Badge>*/}
            <Badge variant="outline" className="text-xs uppercase tracking-wider">
              {direction === "FROM" ? "Wpisz tłumaczenie" : "Wpisz słowo"}
            </Badge>

            <h2 className="text-4xl md:text-6xl font-bold bg-gradient-to-r from-primary to-primary/60 bg-clip-text text-transparent">
              {displayText}
            </h2>

            {/*{direction === "TO" && (data.translations?.length ?? 0) > 1 && !showResult && (*/}
            {/*    <p className="text-sm text-muted-foreground italic">*/}
            {/*      Wskazówka: to słowo ma {data.translations.length} tłumaczeń*/}
            {/*    </p>*/}
            {/*)}*/}
            {direction === "FROM" && (data.translations?.length ?? 0) > 1 && !showResult && (
                <p className="text-sm text-muted-foreground italic">
                  Wskazówka: to słowo ma {data.translations.length} tłumaczeń
                </p>
            )}

          </div>

          {!showResult ? (
              <form onSubmit={handleSubmit} className="space-y-6">
                <Input
                    type="text"
                    // placeholder={
                    //   direction === "FROM"
                    //       ? "Wpisz słowo..."
                    //       : "Wpisz jedno z możliwych tłumaczeń..."
                    // }
                    placeholder={
                      direction === "FROM"
                          ? "Wpisz jedno z możliwych tłumaczeń..."
                          : "Wpisz słowo..."
                    }

                    value={userInput}
                    onChange={(e) => setUserInput(e.target.value)}
                    className="text-2xl md:text-3xl h-16 md:h-20 text-center font-semibold"
                    autoFocus
                />

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

                {localCorrect !== null && (
                    <Card
                        className={`p-8 border-2 ${
                            localCorrect
                                ? "bg-green-500/10 border-green-500"
                                : "bg-red-500/10 border-red-500"
                        }`}
                    >
                      <div className="flex flex-col items-center gap-4">
                        <div className="flex items-center gap-3">
                          {localCorrect ? (
                              <CheckCircle className="w-10 h-10 text-green-600" />
                          ) : (
                              <XCircle className="w-10 h-10 text-red-600" />
                          )}
                          <p
                              className={`text-3xl font-bold ${
                                  localCorrect ? "text-green-600" : "text-red-600"
                              }`}
                          >
                            {localCorrect ? "Świetnie!" : "Niestety nie"}
                          </p>
                        </div>

                        {!localCorrect && (
                            <div className="text-center p-4 bg-background/50 rounded-lg w-full">
                              <p className="text-sm text-muted-foreground mb-2">
                                Twoja odpowiedź:
                              </p>
                              <p className="text-xl font-semibold line-through opacity-60">
                                {userInput}
                              </p>
                            </div>
                        )}
                      </div>
                    </Card>
                )}

                {allSentences.length > 0 && (
                    <div className="p-6 border rounded-xl bg-accent/30">
                      <p className="text-sm text-muted-foreground mb-4 uppercase tracking-wider flex items-center justify-center gap-2">
                        <Sparkles className="w-4 h-4" />
                        Przykład użycia
                      </p>
                      <div className="p-4 bg-background/50 rounded-lg">
                        <p className="text-base md:text-lg mb-2 font-medium">
                          "{allSentences[0].sentence}"
                        </p>
                        <p className="text-sm md:text-base text-muted-foreground italic">
                          {allSentences[0].translation}
                        </p>
                      </div>
                    </div>
                )}

                <Button
                    size="lg"
                    onClick={handleContinue}
                    className="w-full md:w-auto md:mx-auto md:block md:px-12"
                >
                  Kontynuuj
                </Button>
              </div>
          )}
        </div>
      </Card>
  );
};

