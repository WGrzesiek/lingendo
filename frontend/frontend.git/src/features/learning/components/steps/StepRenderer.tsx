"use client";

import { FlashcardView } from "./FlashcardView";
import {QuizFrom} from "./QuizStep";
import {TypingStepBase} from "./WriteLanguageStep";
import type {InteractionType, SubmitAnswerRequest} from "@/features/learning/types/learning.types";
import type { WordDto } from "@/types/word";
import {
    RememberCheckBase
} from "@/features/learning/components/steps/RememberCheckBaseProps";

interface StepRendererProps {
  interactionType: InteractionType;
  flashcardId: string;
  wordContent: WordDto;
  quizOptions?: string[];
  onStepComplete: (answer: SubmitAnswerRequest) => void;
}

/**
 * Uniwersalny renderer kroków nauki
 * Wybiera odpowiedni komponent na podstawie typu interakcji z backendu
 */
export const StepRenderer = ({interactionType, wordContent, onStepComplete, quizOptions
}: StepRendererProps) => {
  switch (interactionType) {
    case "PRESENTATION":
      return (
          <FlashcardView
              data={wordContent}
              onComplete={(answer) => onStepComplete(answer)}
          />
      );
      case "REMEMBER_CHECK_FROM":
          return (
              <RememberCheckBase
                  data={wordContent}
                  onComplete={onStepComplete}
                  interactionType="REMEMBER_CHECK_FROM"
              />
          );

      case "REMEMBER_CHECK_TO":
          return (
              <RememberCheckBase
                  data={wordContent}
                  onComplete={onStepComplete}
                  interactionType="REMEMBER_CHECK_TO"
              />
          );

      case "TYPING_INPUT_FROM":
          return (
              <TypingStepBase
                  data={wordContent}
                  onComplete={onStepComplete}
                  interactionType="TYPING_INPUT_FROM"
              />
          );

      case "TYPING_INPUT_TO":
          return (
              <TypingStepBase
                  data={wordContent}
                  onComplete={onStepComplete}
                  interactionType="TYPING_INPUT_TO"
              />
          );

    case "QUIZ_CHOICE":
        return <QuizFrom data={wordContent} options={quizOptions ?? []} onComplete={onStepComplete} />;

    default:
      return null;
  }
};
