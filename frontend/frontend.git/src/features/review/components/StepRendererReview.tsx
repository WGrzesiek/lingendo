"use client";


import type {InteractionType, TypingAnswer} from "@/features/learning/types/learning.types";
import type { WordDto } from "@/types/word";
import {TypingStepBase} from "@/features/review/components/WriteLanguageStepReview";

interface StepRendererProps {
  interactionType: InteractionType;
  flashcardId: string;
  wordContent: WordDto;
  quizOptions?: string[];
  onStepComplete: (answer: TypingAnswer) => void;
}

/**
 * Uniwersalny renderer kroków nauki
 */
export const StepRenderer = ({interactionType, wordContent, onStepComplete
}: StepRendererProps) => {
  switch (interactionType) {
      case "TYPING_INPUT_TO":
          return (
              <TypingStepBase
                  data={wordContent}
                  onComplete={onStepComplete}
                  interactionType="TYPING_INPUT_TO"
              />
          );
    default:
      return null;
  }
};
