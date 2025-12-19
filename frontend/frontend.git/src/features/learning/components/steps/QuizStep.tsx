"use client";

import { useEffect, useMemo, useState } from "react";
import { Card } from "@/components/ui/card";
import { Button } from "@/components/ui/button";
import { Badge } from "@/components/ui/badge";
import { CheckCircle2, XCircle } from "lucide-react";
import type { WordDto, SentenceDto } from "@/types/word";
import type { QuizAnswer } from "@/features/learning/types/learning.types";

type Direction = "FROM" | "TO";

interface QuizBaseProps {
  data: WordDto;
  options: string[]; // opcje zawsze jako stringi (to co klikamy)
  direction: Direction; // FROM: pytanie=word, odpowiedź=translation; TO: pytanie=translation, odpowiedź=word
  onComplete: (answer: QuizAnswer) => void;
}

/**
 * QUIZ step (multi-choice):
 * - pokazuje prompt (zależnie od direction)
 * - user wybiera opcję
 * - pokazuje feedback lokalnie (opcjonalnie)
 * - po chwili wysyła onComplete({type:'choice', selectedOption})
 */
const QuizStepBase = ({ data, options, direction, onComplete }: QuizBaseProps) => {
  const [selected, setSelected] = useState<string | null>(null);
  const [showResult, setShowResult] = useState(false);

  useEffect(() => {
    setSelected(null);
    setShowResult(false);
  }, [data.word, options.join("|"), direction]);

  const correct = useMemo(() => {

    if (direction === "FROM") return data.translations[0] || "";
    return data.word;
  }, [data, direction]);

  const prompt = direction === "FROM" ? data.word : (data.translations[0] || "");

  const exampleSentence: SentenceDto | undefined =
      (data.sentences?.[0] ?? data.sentencesAI?.[0]) || undefined;

  const handleSelect = (option: string) => {
    setSelected(option);
    setShowResult(true);

    setTimeout(() => {
      onComplete({ type: "choice", selectedOption: option });
    }, 900);
  };

  const getVariant = (option: string) => {
    if (!showResult) return "outline";
    if (option === correct) return "default";
    if (option === selected && option !== correct) return "destructive";
    return "outline";
  };

  const isCorrect = selected != null && selected === correct;

  return (
      <Card className="p-8 md:p-12">
        <div className="space-y-8">
          <div className="text-center space-y-4">
            <Badge variant="outline" className="text-xs uppercase tracking-wider">
              {direction === "FROM"
                  ? "Quiz: wybierz tłumaczenie"
                  : "Quiz: wybierz słówko"}
            </Badge>

            <h2 className="text-4xl md:text-6xl font-bold bg-gradient-to-r from-primary to-primary/60 bg-clip-text text-transparent">
              {prompt}
            </h2>

            {exampleSentence && !showResult && (
                <p className="text-muted-foreground italic text-sm md:text-base mt-4">
                  Kontekst: {exampleSentence.sentence}
                </p>
            )}
          </div>

          <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
            {options.map((option, index) => (
                <Button
                    key={`${option}-${index}`}
                    variant={getVariant(option) as never}
                    size="lg"
                    className="h-24 text-xl font-semibold hover:scale-105 transition-transform"
                    onClick={() => handleSelect(option)}
                    disabled={showResult}
                >
              <span className="mr-3 text-muted-foreground font-bold text-lg">
                {String.fromCharCode(65 + index)}.
              </span>
                  {option}
                </Button>
            ))}
          </div>

          {showResult && selected && (
              <Card
                  className={`p-6 border-2 ${
                      isCorrect ? "bg-green-500/10 border-green-500" : "bg-red-500/10 border-red-500"
                  }`}
              >
                <div className="flex items-center gap-3 justify-center">
                  {isCorrect ? (
                      <>
                        <CheckCircle2 className="w-6 h-6 text-green-600" />
                        <p className="font-bold text-lg text-green-600">
                          Świetnie! Poprawna odpowiedź!
                        </p>
                      </>
                  ) : (
                      <>
                        <XCircle className="w-6 h-6 text-red-600" />
                        <div className="text-center">
                          <p className="font-bold text-lg text-red-600">Niepoprawnie</p>
                          <p className="text-sm text-muted-foreground mt-1">
                            Poprawna odpowiedź: <strong>{correct}</strong>
                          </p>
                        </div>
                      </>
                  )}
                </div>
              </Card>
          )}
        </div>
      </Card>
  );
};

export const QuizFrom = (props: { data: WordDto; options: string[]; onComplete: (a: QuizAnswer) => void }) => (
    <QuizStepBase {...props} direction="FROM" />
);

export const QuizTo = (props: { data: WordDto; options: string[]; onComplete: (a: QuizAnswer) => void }) => (
    <QuizStepBase {...props} direction="TO" />
);
