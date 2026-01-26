"use client";
import { useRouter } from "next/navigation";

import type {
  NextFlashcardRecommendation,
  TypingAnswer,
} from "@/features/learning";
import { isNoMoreFlashcardsError } from "@/lib/api/error";

import { Button } from "@/components/ui/button";
import { ArrowLeft } from "lucide-react";
import { StepRenderer } from "@/features/review/components/StepRendererReview";
import {
  useNextFlashcardRecommendationReview,
  useSubmitAnswerMutationReview,
} from "@/features/review/hooks";
import { ReviewCompletedView } from "@/features/review/components";

const ReviewSessionPage = ({
  params,
}: {
  params: { enrollmentId: string };
}) => {
  const router = useRouter();
  const enrollmentId = params.enrollmentId;
  const { data, isLoading, isError, error, refetch } =
    useNextFlashcardRecommendationReview(enrollmentId);
  const submit = useSubmitAnswerMutationReview();

  const currentFlashcard: NextFlashcardRecommendation | null = data ?? null;
  const noMore = isError && isNoMoreFlashcardsError(error);

  const handleStepComplete = async (answer: TypingAnswer) => {
    if (!currentFlashcard) return;

    try {
      await submit.mutateAsync({
        flashcardId: currentFlashcard.flashcardId,
        enrollmentId: enrollmentId,
        answer,
      });
    } catch (e) {
      console.error("Submit answer failed:", e);
    }
  };

  if (isLoading) {
    return (
      <div className="min-h-screen flex items-center justify-center text-muted-foreground">
        Ładowanie fiszki...
      </div>
    );
  }

  if (noMore) {
    return <ReviewCompletedView />;
  }

  if (isError) {
    return (
      <div className="min-h-screen flex items-center justify-center">
        <div className="space-y-3 text-center">
          <div className="text-red-600 font-semibold">
            Nie udało się pobrać fiszki
          </div>
          <Button onClick={() => refetch()}>Spróbuj ponownie</Button>
        </div>
      </div>
    );
  }

  if (!currentFlashcard) {
    return (
      <div className="min-h-screen flex items-center justify-center text-muted-foreground">
        Brak danych fiszki.
      </div>
    );
  }

  return (
    <div className="min-h-screen bg-gradient-to-br from-background via-accent/5 to-background">
      <div className="container mx-auto p-4 lg:p-8">
        <div className="max-w-5xl mx-auto space-y-6">
          {/* Header */}
          <div className="flex flex-col gap-4 sm:flex-row sm:items-center sm:justify-between">
            <Button
              variant="ghost"
              size="lg"
              className="gap-2 w-fit hover:bg-accent"
              onClick={() => router.back()}
              disabled={submit.isPending}
            >
              <ArrowLeft className="w-5 h-5" />
              Zakończ powtórkę
            </Button>
          </div>

          <StepRenderer
            interactionType={currentFlashcard.interactionType}
            flashcardId={currentFlashcard.flashcardId}
            wordContent={currentFlashcard.content}
            onStepComplete={handleStepComplete}
          />
        </div>
      </div>
    </div>
  );
};

export default ReviewSessionPage;
