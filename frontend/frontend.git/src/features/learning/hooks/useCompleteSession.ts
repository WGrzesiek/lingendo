// import {QueryClient, useMutation, useQueryClient} from "@tanstack/react-query";
// import {completeSession} from "@/features/learning/service/learning.service";

// export const useCompleteSession = () => {
//  const queryClient = useQueryClient();
//  return useMutation(
//         {
//             mutationFn: (sessionId: string) => completeSession(sessionId),
//             onSuccess: (_, sessionId) => {
//                 queryClient.invalidateQueries({ queryKey: ['learning', 'sessionCompleted', sessionId] })

//             }
//         }
//  )
// }
