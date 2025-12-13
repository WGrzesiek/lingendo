import { Brain, Layers, Clock, Shuffle, Pencil, Zap } from "lucide-react";



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
// NOTE do usuniecia po wdrożeniu backendu
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

// ==================================
export const algorithms = [
  {
    id: "GRZESIEK_ALGORITHM",
    name: "Algorytm Grześka",
    description: "Wieloetapowa nauka z mieszanymi typami ćwiczeń.",
    icon: Brain,
    color: "text-indigo-600",
    bgColor: "bg-indigo-500/10",
    inDevelopment: false,
  },
  {
    id: "spaced-repetition",
    name: "Powtarzanie interwałowe",
    description: "Harmonogram powtórek oparty na krzywej zapominania.",
    icon: Clock,
    color: "text-purple-600",
    bgColor: "bg-purple-500/10",
    inDevelopment: true,
  },
  {
    id: "LEINER_ALGORITHM",
    name: "System Leitnera",
    description: "Fiszki przechodzą między pudełkami w zależności od odpowiedzi.",
    icon: Layers,
    color: "text-blue-600",
    bgColor: "bg-blue-500/10",
    inDevelopment: false,
  },
  {
    id: "quiz-only",
    name: "Tryb quizowy",
    description: "Nauka wyłącznie przez pytania wielokrotnego wyboru.",
    icon: Shuffle,
    color: "text-yellow-600",
    bgColor: "bg-yellow-500/10",
    inDevelopment: true,
  },
  {
    id: "writing-practice",
    name: "Praktyka pisania",
    description: "Skupienie na ręcznym wpisywaniu odpowiedzi.",
    icon: Pencil,
    color: "text-red-600",
    bgColor: "bg-red-500/10",
    inDevelopment: true,
  },
  {
    id: "fast-review",
    name: "Szybkie powtórki",
    description: "Ekspresowa odświeżająca sesja dla poznanych fiszek.",
    icon: Zap,
    color: "text-green-600",
    bgColor: "bg-green-500/10",
    inDevelopment: true,
  },
];

export type reviewSchedules = "AUTO" | "LIGHT" | "NORMAL" | "INTENSE"


export const REVIEW_SCHEDULE_LABELS: Record<reviewSchedules, string> = {
  AUTO: "Automatyczny — 7, 14, 21 dni",
  LIGHT: "Lekki — 3, 7 dni",
  NORMAL: "Normalny — 7, 14, 30 dni",
  INTENSE: "Intensywny — 1, 3, 7 dni",
};
