// import {NextFlashcardRecommendation} from "@/features/learning/types/learning.types";
// import {useQuery} from "@tanstack/react-query";
// import {getNextFlashcard} from "@/features/learning/service/learning.service";
// import {qk} from "@/lib/queryKeys";

// export const useNextFlashcardRecommendation = (sessionId: string) => {
//     return useQuery<NextFlashcardRecommendation>({
//         queryKey: qk.learning.nextFlashcard(sessionId),
//         queryFn: () => getNextFlashcard(sessionId),
//         enabled: !!sessionId
// }

//     )
// }
