import { useMutation, useQueryClient, useQuery } from "@tanstack/react-query";
import { qk } from "@/lib/queryKeys";
import {
  completeSession,
  getLearnHeaderProgress,
  getNextFlashcard,
  submitAnswer,
} from "@/features/learning/service/learning.service";
import {
  LearnHeaderProgress,
  NextFlashcardRecommendation,
  QuizAnswer,
  RememberAnswer,
  TypingAnswer,
} from "@/features/learning/types/learning.types";

export const useCompleteSession = () => {
  const queryClient = useQueryClient();
  return useMutation({
    mutationFn: (sessionId: string) => completeSession(sessionId),
    onSuccess: (_, sessionId) => {
      queryClient.invalidateQueries({
        queryKey: qk.learning.sessionCompleted(sessionId),
      });
    },
  });
};

export const useLearnHeaderProgress = (sessionId: string) => {
  return useQuery<LearnHeaderProgress>({
    queryKey: qk.learning.headerProgress(sessionId),
    queryFn: () => getLearnHeaderProgress(sessionId),
    enabled: !!sessionId,
  });
};

export const useNextFlashcardRecommendation = (sessionId: string) => {
  return useQuery<NextFlashcardRecommendation>({
    queryKey: qk.learning.nextFlashcard(sessionId),
    queryFn: () => getNextFlashcard(sessionId),
    enabled: !!sessionId,
  });
};

export const useSubmitAnswerMutation = () => {
  const queryClient = useQueryClient();
  return useMutation({
    mutationFn: ({
      sessionId,
      flashcardId,
      answer,
    }: {
      sessionId: string;
      flashcardId: string;
      answer: RememberAnswer | QuizAnswer | TypingAnswer;
    }) => submitAnswer(sessionId, flashcardId, answer),
    onSuccess: (_, variables) => {
      queryClient.invalidateQueries({
        queryKey: qk.learning.nextFlashcard(variables.sessionId),
      });
      queryClient.invalidateQueries({
        queryKey: qk.learning.headerProgress(variables.sessionId),
      });
    },
  });
};
