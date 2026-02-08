/**
 * Zdanie przykładowe ze słówkiem
 */
export interface WordSentence {
  id?: string;
  sentence: string;
  translation: string;
}

/**
 * Fiszka/słówko w talii
 */
export interface DeckFlashcard {
  id: string;
  word: string;
  translations: string[];
  sentences: WordSentence[];
  sentencesAI: WordSentence[];
  createdAt?: string;
}

/**
 * Słówko do dodania (formularz)
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
 * Response po utworzeniu słówek
 */
export interface CreateBatchResponse {
  created: number;
  words: Array<{
    id: string;
    word: string;
  }>;
}

/**
 * Słówko ze społeczności
 */
export interface CommunityWord {
  id: string;
  word: string;
  translations: string[];
  sentences: WordSentence[];
  usageCount: number;
  author: string;
  createdAt: string;
}

/**
 * Szablon pustego słówka do formularza
 */
export const emptyWord: WordToAdd = {
  word: '',
  translations: [''],
  sentences: [],
};

/**
 * Szablon pustego zdania
 */
export const emptySentence: WordSentence = {
  sentence: '',
  translation: '',
};
