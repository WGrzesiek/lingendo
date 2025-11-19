"use client";

import { useState } from "react";
import { StepRenderer } from "@/features/learning/components/steps/StepRenderer";
import { SessionProgress } from "@/features/learning/components/progress/SessionProgress";
import { SessionSummary } from "@/features/learning/components/summary/SessionSummary";
import { Button } from "@/components/ui/button";
import { Badge } from "@/components/ui/badge";
import { ArrowLeft } from "lucide-react";
import type { StepType } from "@/types/learning";
import { LEARNING_ALGORITHMS } from "@/types/learning";

/**
 * Interfejs słówka w sesji
 */
interface Word {
  id: string;
  word: string;
  translation: string;
  exampleSentence?: string;
  exampleTranslation?: string;
}

/**
 * Interfejs sesji nauki
 */
interface LearningSession {
  id: string;
  courseId: string;
  courseTitle: string;
  sessionNumber: number;
  words: Word[];
  algorithmId: string;
  steps: StepType[];
}

/**
 * Generuje losowe opcje do quizu
 */
const generateQuizOptions = (
  correctTranslation: string,
  allWords: Word[]
): string[] => {
  const incorrectOptions = allWords
    .filter((w) => w.translation !== correctTranslation)
    .map((w) => w.translation)
    .sort(() => Math.random() - 0.5)
    .slice(0, 3);

  const options = [...incorrectOptions, correctTranslation].sort(
    () => Math.random() - 0.5
  );

  return options;
};

/**
 * Mock danych sesji
 */
const mockSession: LearningSession = {
  id: "session-1",
  courseId: "course-123",
  courseTitle: "Angielski dla początkujących",
  sessionNumber: 3,
  algorithmId: "grzesiek",
  steps: ["SHOW_BOTH", "QUIZ", "SHOW_LANGUAGE_FROM", "WRITE_LANGUAGE_TO"],
  words: [
    {
      id: "1",
      word: "apple",
      translation: "jabłko",
      exampleSentence: "I eat an apple every day.",
      exampleTranslation: "Jem jabłko każdego dnia.",
    },
    {
      id: "2",
      word: "book",
      translation: "książka",
      exampleSentence: "She is reading a book.",
      exampleTranslation: "Ona czyta książkę.",
    },
    {
      id: "3",
      word: "water",
      translation: "woda",
      exampleSentence: "I drink water when I'm thirsty.",
      exampleTranslation: "Piję wodę kiedy jestem spragniony.",
    },
    {
      id: "4",
      word: "house",
      translation: "dom",
      exampleSentence: "This is my house.",
      exampleTranslation: "To jest mój dom.",
    },
    {
      id: "5",
      word: "car",
      translation: "samochód",
      exampleSentence: "My car is blue.",
      exampleTranslation: "Mój samochód jest niebieski.",
    },
  ],
};

/**
 * Strona sesji nauki
 * Użytkownik uczy się słówek za pomocą różnych kroków algorytmu
 */
const LearningSessionPage = () => {
  const session = mockSession;
  const algorithm = LEARNING_ALGORITHMS.find(
    (a) => a.id === session.algorithmId
  );

  const [currentWordIndex, setCurrentWordIndex] = useState(0);
  const [currentStepIndex, setCurrentStepIndex] = useState(0);
  const [isSessionCompleted, setIsSessionCompleted] = useState(false);
  const [wordResults, setWordResults] = useState<
    Array<{ wordId: string; difficulty: "easy" | "medium" | "hard" }>
  >([]);

  const currentWord = session.words[currentWordIndex];
  const currentStep = session.steps[currentStepIndex];
  const totalSteps = session.words.length * session.steps.length;
  const completedSteps =
    currentWordIndex * session.steps.length + currentStepIndex;

  const progress = {
    current: completedSteps + 1,
    total: totalSteps,
    percentage: Math.round(((completedSteps + 1) / totalSteps) * 100),
  };

  const quizOptions =
    currentStep === "QUIZ"
      ? generateQuizOptions(currentWord.translation, session.words)
      : undefined;

  const handleStepComplete = (result: {
    type: StepType;
    isCorrect?: boolean;
    difficulty?: "easy" | "medium" | "hard";
  }) => {
    if (result.difficulty) {
      setWordResults([
        ...wordResults,
        { wordId: currentWord.id, difficulty: result.difficulty },
      ]);
    }

    if (currentStepIndex < session.steps.length - 1) {
      setCurrentStepIndex(currentStepIndex + 1);
    } else if (currentWordIndex < session.words.length - 1) {
      setCurrentWordIndex(currentWordIndex + 1);
      setCurrentStepIndex(0);
    } else {
      setIsSessionCompleted(true);
    }
  };

  const handleExit = () => {
    // Przekierowanie do kursu
    console.log("Exit to course");
  };

  if (isSessionCompleted) {
    return (
      <SessionSummary
        session={session}
        results={wordResults}
        onContinue={() => console.log("Continue")}
        onBackToCourse={handleExit}
      />
    );
  }

  return (
    <div className="min-h-screen bg-background">
      <div className="container mx-auto p-4 lg:p-8">
        <div className="max-w-4xl mx-auto space-y-6">
          <div className="flex flex-col gap-3 sm:flex-row sm:items-center sm:justify-between">
            <Button
              variant="ghost"
              className="gap-2 w-fit"
              onClick={handleExit}
            >
              <ArrowLeft className="w-4 h-4" />
              Zakończ sesję
            </Button>
            <div className="flex flex-col gap-2 sm:flex-row sm:items-center sm:gap-3">
              {algorithm && (
                <Badge variant="outline" className="w-fit">
                  {algorithm.name}
                </Badge>
              )}
              <div className="text-sm text-muted-foreground">
                {session.courseTitle} - Sesja {session.sessionNumber}
              </div>
            </div>
          </div>

          <SessionProgress progress={progress} />

          <div className="text-center mb-4">
            <p className="text-sm text-muted-foreground">
              Słówko {currentWordIndex + 1} z {session.words.length} · Krok{" "}
              {currentStepIndex + 1} z {session.steps.length}
            </p>
          </div>

          <StepRenderer
            stepType={currentStep}
            word={currentWord}
            quizOptions={quizOptions}
            onStepComplete={handleStepComplete}
          />
        </div>
      </div>
    </div>
  );
};

export default LearningSessionPage;
