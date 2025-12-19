/**
 * Zdanie przykładowe ze słówkiem
 */
export interface WordSentence {
  /** Zdanie w języku źródłowym */
  sentence: string;
  /** Tłumaczenie zdania */
  translation: string;
}

/**
 * Pojedyncze słówko do dodania
 */
export interface WordToAdd {
  /** Słówko w języku źródłowym */
  word: string;
  /** Lista tłumaczeń (może być kilka) */
  translations: string[];
  /** Lista zdań przykładowych z tłumaczeniami */
  sentences: WordSentence[];
}

/**
 * Request do batch dodawania słówek
 */
export interface BatchAddWordsRequest {
  /** ID talii, do której dodajemy słówka */
  deckId: string;
  /** Lista słówek do dodania */
  words: WordToAdd[];
}

/**
 * Odpowiedź po dodaniu słówek
 */
export interface BatchAddWordsResponse {
  /** Liczba pomyślnie dodanych słówek */
  addedCount: number;
  /** Liczba słówek z błędami */
  errorCount: number;
  /** Szczegóły błędów (jeśli wystąpiły) */
  errors?: {
    word: string;
    reason: string;
  }[];
}

/**
 * Szablon pustego słówka do formularza
 */
export const emptyWord: WordToAdd = {
  word: "",
  translations: [""],
  sentences: [
    {
      sentence: "",
      translation: "",
    },
  ],
};

/**
 * Szablon pustego zdania
 */
export const emptySentence: WordSentence = {
  sentence: "",
  translation: "",
};
