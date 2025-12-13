import { useMutation, useQueryClient } from "@tanstack/react-query";
import { updateFlashcardsPerSession } from "../service/enrollment.service";
import type { UpdateFlashcardsPerSessionRequest } from "../type/enrollment";

/**
 * Hook do zmiany liczby fiszek na sesję (1-100)
 */
export const useUpdateFlashcardsPerSession = () => {
    const queryClient = useQueryClient();
    return useMutation({
        mutationFn: ({
                         enrollmentId,
                         data,
                     }: {
            enrollmentId: string;
            data: UpdateFlashcardsPerSessionRequest;
        }) => updateFlashcardsPerSession(enrollmentId, data),
        onSuccess: (_, variables) => {
            queryClient.invalidateQueries({ queryKey: ["deck", variables.enrollmentId] });
            queryClient.invalidateQueries({
                queryKey: ["deck-details", variables.enrollmentId],
            });
        },
    });
};
