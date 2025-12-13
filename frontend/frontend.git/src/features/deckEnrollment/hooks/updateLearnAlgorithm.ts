import {useMutation, useQueryClient} from "@tanstack/react-query";
import type {UpdateLearnAlgorithmRequest} from "@/features/deckEnrollment/type/enrollment";
import {updateLearnAlgorithm} from "@/features/deckEnrollment/service/enrollment.service";

export const useUpdateLearnAlgorithm = () => {
    const queryClient = useQueryClient();
    return useMutation({
        mutationFn: ({
                         enrollmentId,
                         data,
                     }: {
            enrollmentId: string;
            data: UpdateLearnAlgorithmRequest;
        }) => updateLearnAlgorithm(enrollmentId, data),
        onSuccess: (_, variables) => {
            queryClient.invalidateQueries({ queryKey: ["deck", variables.enrollmentId] });
            queryClient.invalidateQueries({
                queryKey: ["deck-details", variables.enrollmentId],
            });
        },
    });
};