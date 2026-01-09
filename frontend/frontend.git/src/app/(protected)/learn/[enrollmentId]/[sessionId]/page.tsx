"use client";

import { useEffect, useRef } from "react";
import { StepRenderer } from "@/features/learning/components/steps";
import { SessionProgress } from "@/features/learning/components/progress";
import { Button } from "@/components/ui/button";
import { ArrowLeft } from "lucide-react";
import { useRouter } from "next/navigation";

import type {
  NextFlashcardRecommendation,
  SubmitAnswerRequest,
} from "@/features/learning";

import {
  useNextFlashcardRecommendation,
  useSubmitAnswerMutation,
  useCompleteSession,
} from "@/features/learning/hooks";
import { isNoMoreFlashcardsError } from "@/lib/api/error";
import { SessionCompletedView } from "@/features/learning/components/progress";
import { useCourseHeader } from "@/features/course/hooks/useCourseHeader";
import { useCourseSettings } from "@/features/course/hooks/useCourseSettings";
import { useCourseProgress } from "@/features/course/hooks/useCourseProgress";
import { algorithms } from "@/types/learning";

const LearningSessionPage = ({
  params,
}: {
  params: { enrollmentId: string; sessionId: string };
}) => {
  const router = useRouter();
  const sessionId = params.sessionId;
  const enrollmentId = params.enrollmentId;
  const {
    data: sessionHeader,
    isLoading: isSessionHeaderLoading,
    isError: isSessionHeaderError,
  } = useCourseHeader(enrollmentId);
  const { data: sessionSettings } = useCourseSettings(enrollmentId);
  const { data: sessionProgress } = useCourseProgress(enrollmentId);
  const { data, isLoading, isError, error, refetch } =
    useNextFlashcardRecommendation(sessionId);
  const submit = useSubmitAnswerMutation();
  const completeSession = useCompleteSession();

  const currentFlashcard: NextFlashcardRecommendation | null = data ?? null;

  const noMore = isError && isNoMoreFlashcardsError(error);

  const didCompleteRef = useRef(false);

  useEffect(() => {
    if (!noMore) return;
    if (didCompleteRef.current) return;

    didCompleteRef.current = true;

    completeSession.mutate(sessionId);
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [noMore, sessionId]);

  const handleStepComplete = async (answer: SubmitAnswerRequest) => {
    if (!currentFlashcard) return;

    try {
      await submit.mutateAsync({
        sessionId,
        flashcardId: currentFlashcard.flashcardId,
        answer,
      });
    } catch (e) {
      console.error("Submit answer failed:", e);
    }
  };

  if (isLoading || isSessionHeaderLoading) {
    return (
      <div className="min-h-screen flex items-center justify-center text-muted-foreground">
        Ładowanie fiszki...
      </div>
    );
  }

  if (noMore) {
    return <SessionCompletedView />;
  }

  if (isError || isSessionHeaderError) {
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
  if (!sessionHeader || !sessionProgress || !sessionSettings) return null;

  const selectedAlgorithm = algorithms.find(
    (a) => a.id === sessionSettings.algorithm
  );

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
              Zakończ sesję
            </Button>

            <div className="flex flex-col gap-2 sm:items-end">
              {selectedAlgorithm && (
                <div
                  className={`flex items-center gap-2 px-3 py-1 rounded-lg ${selectedAlgorithm.bgColor}`}
                >
                  <selectedAlgorithm.icon
                    className={`w-4 h-4 ${selectedAlgorithm.color}`}
                  />
                  <span className="text-sm font-medium">
                    {selectedAlgorithm.name}
                  </span>
                </div>
              )}

              <div className="text-base font-medium text-muted-foreground">
                {sessionHeader.name} · Sesja{" "}
                {
                  sessionProgress.sessions.find(
                    (s) => s.status === "IN_PROGRESS"
                  )?.sessionNumber
                }
              </div>
            </div>
          </div>

          <SessionProgress sessionId={sessionId} />

          <StepRenderer
            interactionType={currentFlashcard.interactionType}
            flashcardId={currentFlashcard.flashcardId}
            wordContent={currentFlashcard.content}
            quizOptions={currentFlashcard.quizOptions}
            onStepComplete={handleStepComplete}
          />
        </div>
      </div>
    </div>
  );
};

export default LearningSessionPage;
