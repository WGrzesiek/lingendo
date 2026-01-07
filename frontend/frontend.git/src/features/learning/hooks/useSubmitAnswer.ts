// import {
//     FlashcardInteractionResult,
//     QuizAnswer,
//     RememberAnswer,
//     TypingAnswer
// } from "@/features/learning/types/learning.types";
// import {useMutation, useQueryClient} from "@tanstack/react-query";
// import {submitAnswer} from "@/features/learning/service/learning.service";
// import {qk} from "@/lib/queryKeys";

// export const useSubmitAnswerMutation = () => {
//     const queryClient = useQueryClient();
//     return useMutation(
//         {
//             mutationFn: ({
//                 sessionId,
//                 flashcardId,
//                 answer,
//             }: {
//                 sessionId: string;
//                 flashcardId: string;
//                 answer: RememberAnswer | QuizAnswer | TypingAnswer;
//             }) => submitAnswer(sessionId, flashcardId, answer),
//             onSuccess: (_, variables) => {
//                 queryClient.invalidateQueries({ queryKey: qk.learning.nextFlashcard(variables.sessionId) })
//                 queryClient.invalidateQueries({ queryKey: qk.learning.headerProgress(variables.sessionId) })
//             }
//         }
//     )
// }
