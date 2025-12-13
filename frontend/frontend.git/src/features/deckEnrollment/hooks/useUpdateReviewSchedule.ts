import {useMutation, useQueryClient} from "@tanstack/react-query";
import {UpdateReviewScheduleRequest} from "@/features/deckEnrollment/type/enrollment";
import {updateReviewSchedule} from "@/features/deckEnrollment/service/enrollment.service";

export const useUpdateReviewSchedule = () => {
    const queryClient = useQueryClient();
    return useMutation({
        mutationFn: ({
                         enrollmentId,
                         data,
                     }: {
            enrollmentId: string;
            data: UpdateReviewScheduleRequest;
        }) => updateReviewSchedule(enrollmentId, data),
        onSuccess: (_, variables) => {
            queryClient.invalidateQueries({ queryKey: ["deck", variables.enrollmentId] });
            queryClient.invalidateQueries({
                queryKey: ["deck-details", variables.enrollmentId],
            });
        },
    });
}