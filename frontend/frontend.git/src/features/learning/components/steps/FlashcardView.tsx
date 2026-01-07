"use client";

import {useEffect, useState} from "react";
import { Card } from "@/components/ui/card";
import { Button } from "@/components/ui/button";
import { Badge } from "@/components/ui/badge";
import {RotateCcw, Volume2, Sparkles, PlusCircle, Frown} from "lucide-react";
import type {SentenceDto, WordDto} from "@/types/word";
import {RememberAnswer} from "@/features/learning";


interface FlashcardViewProps {
  data: WordDto;
  onComplete: (answer: RememberAnswer) => void;}
/**
 * Komponent fiszki z animacją przewracania
 * Użytkownik może przewrócić fiszkę i ocenić jak trudne było słówko
 */
export const FlashcardView = ({data, onComplete
}: FlashcardViewProps) => {
    const allSentences: SentenceDto[] = [
    ...data.sentences,
    ...data.sentencesAI,
    ];
  const [isFlipped, setIsFlipped] = useState(false);
    useEffect(() => {
        setIsFlipped(false);
    }, [data.word]);
    const handleFlip = () => setIsFlipped(true);

    const submit = (remembered: boolean) => {
        onComplete({
            type: "remembered",
            remembered,
        });
    };

    return (
        <div className="space-y-6">
            <div
                className="perspective-1000 cursor-pointer"
                onClick={!isFlipped ? handleFlip : undefined}
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
                    {/* FRONT */}
                    {!isFlipped ? (
                        <Card className="p-8 md:p-12 min-h-[450px] flex flex-col items-center justify-center text-center space-y-6 backface-hidden">
                            <p className="text-sm text-muted-foreground uppercase tracking-wider font-medium">
                                Słówko
                            </p>
                            <h2 className="text-5xl md:text-7xl font-bold bg-gradient-to-r from-primary to-primary/60 bg-clip-text text-transparent">
                                {data.word}
                            </h2>
                            <p className="text-muted-foreground mt-8 text-sm animate-pulse">
                                Kliknij aby zobaczyć tłumaczenie
                            </p>
                        </Card>
                    ) : (
                        /* BACK */
                        <Card
                            className="p-8 md:p-12 min-h-[450px] flex flex-col justify-center space-y-8 backface-hidden"
                            style={{ transform: "rotateY(180deg)" }}
                        >
                            <div className="space-y-6 text-center">
                                <p className="text-sm text-muted-foreground uppercase tracking-wider font-medium">
                                    {data.translations.length > 1
                                        ? "Tłumaczenia"
                                        : "Tłumaczenie"}
                                </p>
                                <div className="flex flex-wrap gap-3 justify-center">
                                    {data.translations.map((translation, index) => (
                                        <Badge
                                            key={index}
                                            variant={index === 0 ? "default" : "secondary"}
                                            className="text-2xl md:text-3xl px-6 py-3 font-bold"
                                        >
                                            {translation}
                                        </Badge>
                                    ))}
                                </div>
                            </div>

                            {allSentences.length > 0 && (
                                <div className="pt-6 border-t space-y-4">
                                    <p className="text-sm text-muted-foreground uppercase tracking-wider font-medium flex items-center justify-center gap-2">
                                        <Sparkles className="w-4 h-4" />
                                        Przykłady użycia
                                    </p>
                                    <div className="space-y-4 max-h-48 overflow-y-auto">
                                        {allSentences.slice(0, 3).map((sentence) => (
                                            <div
                                                key={sentence.id}
                                                className="p-4 rounded-lg bg-accent/30 border border-border/50"
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
                        </Card>
                    )}
                </div>
            </div>

            {isFlipped && (
                <Card className="p-6 bg-gradient-to-br from-accent/50 to-background border-2">
                    <div className="space-y-4">
                        <p className="text-center text-base font-semibold">
                            Zapamiętałeś to słówko?
                        </p>
                        <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
                            <Button size="lg" onClick={() => submit(true)}>
                                😊 Pamiętam
                            </Button>
                            <Button
                                size="lg"
                                variant="outline"
                                onClick={() => submit(false)}
                            >
                                🔁 Wyświetl ponownie później
                            </Button>
                        </div>
                    </div>
                </Card>
            )}

            {!isFlipped && (
                <div className="flex justify-center">
                    <Button variant="ghost" size="sm" onClick={handleFlip} className="gap-2">
                        <RotateCcw className="w-4 h-4" />
                        Obróć fiszkę
                    </Button>
                </div>
            )}
        </div>
    );
};
