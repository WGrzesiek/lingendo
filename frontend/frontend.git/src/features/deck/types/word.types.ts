/**
 * Zdanie przykładowe ze słówkiem
 */
export interface WordSentence {
  sentence: string;
  translation: string;
}

/**
 * Pojedyncze słówko do dodania
 */
export interface WordToAdd {
  word: string;
  translations: string[];
  sentences?: WordSentence[];
}

/**
 * Request do batch dodawania słówek
 */
export interface BatchAddWordsRequest {
  deckId: string;
  words: WordToAdd[];
}


/**
 * Szablon pustego słówka do formularza
 */
export const emptyWord: WordToAdd = {
  word: "",
  translations: [""],
  sentences: [],
};


/**
 * Szablon pustego zdania
 */
export const emptySentence: WordSentence = {
  sentence: "",
  translation: "",
};
