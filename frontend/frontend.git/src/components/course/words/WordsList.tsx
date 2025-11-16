import { Card } from "@/components/ui/card";
import { Button } from "@/components/ui/button";
import { Badge } from "@/components/ui/badge";
import { Sparkles, Plus, Edit, Trash2 } from "lucide-react";
import { useState } from "react";

interface Word {
  id: string;
  word: string;
  translation: string;
  exampleSentence?: string;
  exampleTranslation?: string;
  sessionNumber: number;
  isLearned: boolean;
  nextReviewDate?: string;
}

interface WordsListProps {
  course: {
    id: string;
    isOwner: boolean;
  };
}

/**
 * Lista słówek w kursie z możliwością edycji i generowania zdań przez AI
 */
export const WordsList = ({ course }: WordsListProps) => {
  const [isGenerating, setIsGenerating] = useState(false);

  const mockWords: Word[] = [
    {
      id: "1",
      word: "hello",
      translation: "cześć, witaj",
      exampleSentence: "Hello, how are you today?",
      exampleTranslation: "Cześć, jak się dzisiaj masz?",
      sessionNumber: 1,
      isLearned: true,
      nextReviewDate: "2025-11-10",
    },
    {
      id: "2",
      word: "goodbye",
      translation: "do widzenia",
      exampleSentence: "Goodbye, see you tomorrow!",
      exampleTranslation: "Do widzenia, do zobaczenia jutro!",
      sessionNumber: 1,
      isLearned: true,
      nextReviewDate: "2025-11-09",
    },
    {
      id: "3",
      word: "thank you",
      translation: "dziękuję",
      exampleSentence: "Thank you for your help.",
      exampleTranslation: "Dziękuję za twoją pomoc.",
      sessionNumber: 1,
      isLearned: true,
      nextReviewDate: "2025-11-11",
    },
    {
      id: "4",
      word: "please",
      translation: "proszę",
      exampleSentence: "Can you help me, please?",
      exampleTranslation: "Czy możesz mi pomóc, proszę?",
      sessionNumber: 1,
      isLearned: true,
    },
    {
      id: "5",
      word: "sorry",
      translation: "przepraszam",
      exampleSentence: undefined,
      exampleTranslation: undefined,
      sessionNumber: 2,
      isLearned: false,
    },
    {
      id: "6",
      word: "excuse me",
      translation: "przepraszam (zwracanie uwagi)",
      exampleSentence: undefined,
      exampleTranslation: undefined,
      sessionNumber: 2,
      isLearned: false,
    },
  ];

  const wordsWithoutSentences = mockWords.filter(
    (w) => !w.exampleSentence
  ).length;

  const handleGenerateSentences = () => {
    setIsGenerating(true);
    setTimeout(() => setIsGenerating(false), 2000);
  };

  return (
    <Card className="p-6">
      <div className="sm:flex items-center justify-between mb-6">
        <div>
          <h2 className="text-2xl font-bold mb-1">Słówka w kursie</h2>
          <p className="text-muted-foreground">
            {mockWords.length} słówek · {wordsWithoutSentences} bez zdań
          </p>
        </div>
        <div className="sm:flex gap-2">
          {course.isOwner && wordsWithoutSentences > 0 && (
            <Button
              variant="outline"
              className="gap-2 my-3"
              onClick={handleGenerateSentences}
              disabled={isGenerating}
            >
              <Sparkles className="w-4 h-4" />
              {isGenerating
                ? "Generowanie..."
                : `Generuj zdania przez AI (${wordsWithoutSentences})`}
            </Button>
          )}
          {course.isOwner && (
            <Button className="gap-2">
              <Plus className="w-4 h-4" />
              Dodaj słówko
            </Button>
          )}
        </div>
      </div>

      <div className="space-y-3">
        {mockWords.map((word) => (
          <div
            key={word.id}
            className={`p-4 border rounded-lg hover:bg-accent/50 transition-colors ${
              word.isLearned ? "border-success/30 bg-success/5" : ""
            }`}
          >
            {/* MOBILE LAYOUT - badgesy na górze */}
            <div className="flex flex-wrap items-center gap-2 mb-3 sm:hidden">
              <Badge variant="outline" className="text-xs">
                Sesja {word.sessionNumber}
              </Badge>
              {word.isLearned && (
                <Badge
                  variant="secondary"
                  className="text-xs bg-success/10 text-success"
                >
                  Nauczone
                </Badge>
              )}
              {word.nextReviewDate && (
                <Badge variant="outline" className="text-xs">
                  Powtórka:{" "}
                  {new Date(word.nextReviewDate).toLocaleDateString("pl-PL")}
                </Badge>
              )}
            </div>

            {/* MOBILE - słówko z tłumaczeniem i zdania */}
            <div className="sm:hidden mb-3">
              <div className="flex items-center gap-2 mb-2">
                <h3 className="font-semibold text-lg">{word.word}</h3>
                <span className="text-muted-foreground">→</span>
                <span className="text-lg">{word.translation}</span>
              </div>

              {word.exampleSentence ? (
                <div className="space-y-1 mt-3">
                  <p className="text-sm italic text-muted-foreground">
                    &ldquo;{word.exampleSentence}&rdquo;
                  </p>
                  {word.exampleTranslation && (
                    <p className="text-sm italic text-muted-foreground">
                      &ldquo;{word.exampleTranslation}&rdquo;
                    </p>
                  )}
                </div>
              ) : (
                <div className="flex items-center gap-2 text-sm text-muted-foreground mt-3">
                  <Sparkles className="w-4 h-4" />
                  Brak przykładowego zdania
                </div>
              )}
            </div>

            {/* MOBILE - przyciski na dole */}
            {course.isOwner && (
              <div className="flex gap-1 justify-end sm:hidden">
                <Button variant="ghost" size="sm">
                  <Edit className="w-4 h-4" />
                </Button>
                <Button variant="ghost" size="sm">
                  <Trash2 className="w-4 h-4" />
                </Button>
              </div>
            )}

            {/* DESKTOP LAYOUT - wszystko w jednej linii */}
            <div className="hidden sm:flex items-start justify-between gap-4">
              <div className="flex-1">
                <div className="flex items-center gap-2 mb-2">
                  <h3 className="font-semibold text-lg">{word.word}</h3>
                  <span className="text-muted-foreground">→</span>
                  <span className="text-lg">{word.translation}</span>
                  <Badge variant="outline" className="text-xs">
                    Sesja {word.sessionNumber}
                  </Badge>
                  {word.isLearned && (
                    <Badge
                      variant="secondary"
                      className="text-xs bg-success/10 text-success"
                    >
                      Nauczone
                    </Badge>
                  )}
                  {word.nextReviewDate && (
                    <Badge variant="outline" className="text-xs">
                      Powtórka:{" "}
                      {new Date(word.nextReviewDate).toLocaleDateString(
                        "pl-PL"
                      )}
                    </Badge>
                  )}
                </div>

                {word.exampleSentence ? (
                  <div className="space-y-1">
                    <p className="text-sm italic text-muted-foreground">
                      &ldquo;{word.exampleSentence}&rdquo;
                    </p>
                    {word.exampleTranslation && (
                      <p className="text-sm italic text-muted-foreground">
                        &ldquo;{word.exampleTranslation}&rdquo;
                      </p>
                    )}
                  </div>
                ) : (
                  <div className="flex items-center gap-2 text-sm text-muted-foreground">
                    <Sparkles className="w-4 h-4" />
                    Brak przykładowego zdania
                  </div>
                )}
              </div>

              {course.isOwner && (
                <div className="flex gap-1">
                  <Button variant="ghost" size="sm">
                    <Edit className="w-4 h-4" />
                  </Button>
                  <Button variant="ghost" size="sm">
                    <Trash2 className="w-4 h-4" />
                  </Button>
                </div>
              )}
            </div>
          </div>
        ))}
      </div>
    </Card>
  );
};
