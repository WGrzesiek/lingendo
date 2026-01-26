// import {useQuery} from "@tanstack/react-query";
// import {getLearnHeaderProgress} from "@/features/learning/service/learning.service";
// import {LearnHeaderProgress} from "@/features/learning/types/learning.types";
// import {qk} from "@/lib/queryKeys";

// export const useLearnHeaderProgress = (sessionId: string) => {
//     return useQuery<LearnHeaderProgress>(
//         {
//             queryKey: qk.learning.headerProgress(sessionId),
//             queryFn: () => getLearnHeaderProgress(sessionId),
//             enabled: !!sessionId
//         }
//     )
// }
