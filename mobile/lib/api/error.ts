import type { AxiosError } from 'axios';

export interface ApiErrorResponse {
  message: string;
  code?: string;
  status?: number;
}

/**
 * Sprawdza czy błąd oznacza brak fiszek do nauki w sesji
 */
export function isNoMoreFlashcardsError(error: unknown): boolean {
  const e = error as AxiosError<ApiErrorResponse>;

  return (
    e?.response?.status === 400 &&
    e?.response?.data?.message === 'Brak dostępnych fiszek do nauki w tej sesji'
  );
}

/**
 * Sprawdza czy błąd to "brak więcej fiszek"
 */
export function isNoMoreFlashcardsToReviewError(error: unknown): boolean {
  const e = error as AxiosError<ApiErrorResponse>;

    return e.response?.status === 404;

}
