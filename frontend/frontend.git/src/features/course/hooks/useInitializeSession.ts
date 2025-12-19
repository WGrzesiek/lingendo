import {useMutation, useQueryClient} from "@tanstack/react-query";
import {initializeSession} from "@/features/course/services/course.service";

export const useInitializeSession = () => {
const queryClient = useQueryClient()
    return useMutation({
        mutationFn: (enrollmentId: string) => initializeSession(enrollmentId),
        onSuccess: (_, enrollmentId) => {
            queryClient.invalidateQueries({ queryKey: ['course-progress', enrollmentId] })
            queryClient.invalidateQueries({ queryKey: ['course', 'progress', enrollmentId] })
            queryClient.invalidateQueries({ queryKey: ['course', 'settings', enrollmentId] })
        }
    })
};