"use client";

import { useEffect, useState } from "react";
import { Card } from "@/components/ui/card";
import { Button } from "@/components/ui/button";
import { Badge } from "@/components/ui/badge";
import { Eye, Sparkles } from "lucide-react";
import type { WordDto } from "@/types/word";
import type { InteractionType, RememberAnswer } from "@/features/learning";

interface RememberCheckBaseProps {
  data: WordDto;
  interactionType: InteractionType;
  onComplete: (answer: RememberAnswer) => void;
}

/**
 * Krok: użytkownik próbuje sobie przypomnieć, potem klika "Pokaż", a na końcu ocenia.
 */
export const RememberCheckBase = ({
  data,
  interactionType,
  onComplete,
}: RememberCheckBaseProps) => {
  const direction: "FROM" | "TO" =
    interactionType === "REMEMBER_CHECK_TO" ? "TO" : "FROM";
  console.log(direction);
  console.log(interactionType);
  const [isRevealed, setIsRevealed] = useState(false);

  const allSentences = [...data.sentences, ...data.sentencesAI];

  useEffect(() => {
    setIsRevealed(false);
  }, [data.word]);

  const promptText =
    direction === "FROM"
      ? "Przypomnij sobie tłumaczenie"
      : "Przypomnij sobie słowo";

  const question =
    direction === "FROM" ? data.word : data.translations[0] || "";

  const showWordAsAnswer = direction === "TO";

  const submit = (remembered: boolean) => {
    onComplete({ type: "remembered", remembered });
  };

  return (
    <Card className="p-8 md:p-12">
      <div className="space-y-8">
        <div className="text-center space-y-4">
          <Badge variant="outline" className="text-xs uppercase tracking-wider">
            {promptText}
          </Badge>

          <h2 className="text-5xl md:text-7xl font-bold bg-gradient-to-r from-primary to-primary/60 bg-clip-text text-transparent">
            {question}
          </h2>
        </div>

        {!isRevealed ? (
          <div className="text-center space-y-4">
            <div className="p-10 border-2 border-dashed rounded-xl bg-gradient-to-br from-accent/40 to-accent/10">
              <p className="text-muted-foreground mb-6 text-lg">
                Pomyśl o odpowiedzi, a następnie kliknij aby sprawdzić
              </p>
              <Button
                size="lg"
                onClick={() => setIsRevealed(true)}
                className="gap-2 text-lg px-8"
              >
                <Eye className="w-5 h-5" />
                Pokaż {direction === "FROM" ? "tłumaczenie" : "słowo"}
              </Button>
            </div>
          </div>
        ) : (
          <div className="space-y-6">
            <div className="text-center p-8 bg-gradient-to-br from-primary/20 to-primary/5 rounded-xl border-2 border-primary">
              <p className="text-sm text-muted-foreground mb-3 uppercase tracking-wider">
                {direction === "FROM" ? "Tłumaczenie" : "Słówko"}
              </p>

              {showWordAsAnswer ? (
                <h3 className="text-4xl md:text-5xl font-bold text-primary">
                  {data.word}
                </h3>
              ) : (
                <div className="flex flex-wrap gap-3 justify-center">
                  {data.translations.map((trans, index) => (
                    <Badge
                      key={index}
                      variant={index === 0 ? "default" : "secondary"}
                      className="text-2xl md:text-3xl px-6 py-3 font-bold"
                    >
                      {trans}
                    </Badge>
                  ))}
                </div>
              )}
            </div>

            {allSentences.length > 0 && (
              <div className="p-6 border rounded-xl bg-accent/30">
                <p className="text-sm text-muted-foreground mb-4 uppercase tracking-wider flex items-center justify-center gap-2">
                  <Sparkles className="w-4 h-4" />
                  Przykłady użycia
                </p>
                <div className="space-y-3">
                  {allSentences.slice(0, 2).map((sentence) => (
                    <div
                      key={sentence.id}
                      className="p-4 bg-background/50 rounded-lg"
                    >
                      <p className="text-base md:text-lg mb-2 font-medium">
                        {sentence.sentence}
                      </p>
                      <p className="text-sm md:text-base text-muted-foreground italic">
                        {sentence.translation}
                      </p>
                    </div>
                  ))}
                </div>
              </div>
            )}

            <Card className="p-6 bg-gradient-to-br from-accent/50 to-background border-2">
              <div className="space-y-4">
                <p className="text-center text-base font-semibold">
                  Pamiętałeś to?
                </p>
                <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
                  <Button size="lg" onClick={() => submit(true)}>
                    😊 Pamiętałem
                  </Button>
                  <Button
                    size="lg"
                    variant="outline"
                    onClick={() => submit(false)}
                  >
                    🔁 Nie pamiętałem
                  </Button>
                </div>
              </div>
            </Card>
          </div>
        )}
      </div>
    </Card>
  );
};
