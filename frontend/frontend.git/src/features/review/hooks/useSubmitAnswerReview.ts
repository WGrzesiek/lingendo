import {TypingAnswer} from "@/features/learning/types/learning.types";
import {useMutation, useQueryClient} from "@tanstack/react-query";
import {qk} from "@/lib/queryKeys";
import {submitAnswerReview} from "@/features/review/service/review.service";

export const useSubmitAnswerMutationReview = () => {
    const queryClient = useQueryClient();
    return useMutation(
        {
            mutationFn: ({
                             flashcardId,
                             answer,
                         }: {
                enrollmentId: string;
                flashcardId: string;
                answer: TypingAnswer;
            }) => submitAnswerReview(flashcardId, answer),
            onSuccess: (_, variables) => {
                queryClient.invalidateQueries({ queryKey: qk.learning.nextFlashcardReview(variables.enrollmentId) })
                // queryClient.invalidateQueries({ queryKey: qk.learning.headerProgress(variables.sessionId) })
            }
        }
    )
}

