import type { AxiosError } from "axios";
import {ApiErrorResponse} from "@/types/common";

export function isNoMoreFlashcardsError(error: unknown): boolean {
    const e = error as AxiosError<ApiErrorResponse>;

    return (
        e?.response?.status === 400 &&
        e?.response?.data?.message ===
        "Brak dostępnych fiszek do nauki w tej sesji"
    );
}