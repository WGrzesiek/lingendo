import { useMutation } from "@tanstack/react-query";
import { generateSentences, GenerateSentencesResponse } from "@/features/deck/services/deck.service";
import type { AxiosError } from "axios";
import type { ApiErrorResponse } from "@/types/common";
import { toast } from "sonner";

export const useGenerateSentences = () => {
    return useMutation<
        GenerateSentencesResponse,
        AxiosError<ApiErrorResponse>,
        string
    >({
        mutationFn: generateSentences,
        onSuccess: (data) => {
            toast.success(`Rozpoczęto generowanie zdań (${data.wordsCount} słów). Proces potrwa kilka minut.`);
        },
        onError: (error) => {
            const message = error.response?.data?.message || "Wystąpił błąd podczas zlecania generowania zdań";
            toast.error(message);
        }
    });
};
