/**
 * Typy kroków w algorytmie nauki
 */
export type StepType =
  | "SHOW_BOTH" // Pokaż oba języki (fiszka z oceną)
  | "QUIZ" // Quiz wielokrotnego wyboru
  | "SHOW_LANGUAGE_FROM" // Pokaż język źródłowy, odgadnij docelowy
  | "SHOW_LANGUAGE_TO" // Pokaż język docelowy, odgadnij źródłowy
  | "WRITE_LANGUAGE_FROM" // Wpisz słowo w języku źródłowym
  | "WRITE_LANGUAGE_TO"; // Wpisz słowo w języku docelowym

/**
 * Interfejs kroku nauki
 */
export interface LearningStep {
  type: StepType;
  completed: boolean;
  result?: "correct" | "incorrect" | "easy" | "medium" | "hard";
}

/**
 * Interfejs słówka z postępem nauki
 */
export interface WordWithProgress {
  id: string;
  word: string;
  translation: string;
  exampleSentence?: string;
  exampleTranslation?: string;
  currentStep: number;
  steps: LearningStep[];
}

/**
 * Interfejs algorytmu nauki
 */
export interface LearningAlgorithm {
  id: string;
  name: string;
  description: string;
  steps: StepType[];
}

/**
 * Dostępne algorytmy nauki
 */
export const LEARNING_ALGORITHMS: LearningAlgorithm[] = [
  {
    id: "spaced-repetition",
    name: "Powtarzanie Interwałowe",
    description: "Klasyczna fiszka z oceną trudności",
    steps: ["SHOW_BOTH"],
  },
  {
    id: "grzesiek",
    name: "Algorytm Grzeska",
    description: "Wieloetapowa nauka ze wszystkimi typami ćwiczeń",
    steps: [
      "SHOW_BOTH",
      "QUIZ",
      "SHOW_LANGUAGE_FROM",
      "SHOW_LANGUAGE_TO",
      "WRITE_LANGUAGE_FROM",
      "WRITE_LANGUAGE_TO",
    ],
  },
  {
    id: "leitner",
    name: "System Leitnera",
    description: "Fiszki między pudełkami",
    steps: ["SHOW_BOTH"],
  },
  {
    id: "quiz-only",
    name: "Tylko quizy",
    description: "Nauka przez wielokrotny wybór",
    steps: ["QUIZ", "QUIZ", "QUIZ"],
  },
  {
    id: "writing-practice",
    name: "Praktyka pisania",
    description: "Fokus na pisaniu słówek",
    steps: ["SHOW_LANGUAGE_FROM", "WRITE_LANGUAGE_TO", "WRITE_LANGUAGE_FROM"],
  },
];
