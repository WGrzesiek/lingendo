"use client";

import { FlashcardView } from "../flashcard/FlashcardView";
import { QuizStep } from "./QuizStep";
import { ShowLanguageStep } from "./ShowLanguageStep";
import { WriteLanguageStep } from "./WriteLanguageStep";
import type { StepType } from "@/types/learning";

interface Word {
  id: string;
  word: string;
  translation: string;
  exampleSentence?: string;
  exampleTranslation?: string;
}

interface StepRendererProps {
  stepType: StepType;
  word: Word;
  quizOptions?: string[];
  onStepComplete: (result: {
    type: StepType;
    isCorrect?: boolean;
    difficulty?: "easy" | "medium" | "hard";
  }) => void;
}

/**
 * Uniwersalny renderer kroków nauki
 * Wybiera odpowiedni komponent na podstawie typu kroku
 */
export const StepRenderer = ({
  stepType,
  word,
  quizOptions,
  onStepComplete,
}: StepRendererProps) => {
  switch (stepType) {
    case "SHOW_BOTH":
      return (
        <FlashcardView
          word={word}
          onAnswer={(difficulty) =>
            onStepComplete({ type: stepType, difficulty })
          }
        />
      );

    case "QUIZ":
      return (
        <QuizStep
          word={word.word}
          correctTranslation={word.translation}
          options={quizOptions || [word.translation]}
          showFrom={true}
          onAnswer={(isCorrect) =>
            onStepComplete({ type: stepType, isCorrect })
          }
        />
      );

    case "SHOW_LANGUAGE_FROM":
      return (
        <ShowLanguageStep
          word={word.word}
          translation={word.translation}
          exampleSentence={word.exampleSentence}
          exampleTranslation={word.exampleTranslation}
          showFrom={true}
          onReveal={() => onStepComplete({ type: stepType })}
        />
      );

    case "SHOW_LANGUAGE_TO":
      return (
        <ShowLanguageStep
          word={word.word}
          translation={word.translation}
          exampleSentence={word.exampleSentence}
          exampleTranslation={word.exampleTranslation}
          showFrom={false}
          onReveal={() => onStepComplete({ type: stepType })}
        />
      );

    case "WRITE_LANGUAGE_FROM":
      return (
        <WriteLanguageStep
          word={word.word}
          translation={word.translation}
          exampleSentence={word.exampleSentence}
          exampleTranslation={word.exampleTranslation}
          writeFrom={true}
          onAnswer={(isCorrect) =>
            onStepComplete({ type: stepType, isCorrect })
          }
        />
      );

    case "WRITE_LANGUAGE_TO":
      return (
        <WriteLanguageStep
          word={word.word}
          translation={word.translation}
          exampleSentence={word.exampleSentence}
          exampleTranslation={word.exampleTranslation}
          writeFrom={false}
          onAnswer={(isCorrect) =>
            onStepComplete({ type: stepType, isCorrect })
          }
        />
      );

    default:
      return null;
  }
};
