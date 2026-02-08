import { User, GraduationCap, Users, Globe, LucideIcon } from "lucide-react";
import { Feather, TrendingUp, Flame } from "lucide-react";
import { Sparkles, Boxes, FlaskConical } from "lucide-react";

import {
  Code,
  Beaker,
  Palette,
  MoreHorizontal,
  Briefcase,
  BookOpen,
  Plane,
  Home,
  Heart,
  Trophy,
  Utensils,
  Banknote,
  PawPrint,
  Cpu,
  Smile,
  Coffee,
  Car,
  Scale,
  Gamepad2,
  Leaf,
  Megaphone,
  LayoutGrid,
} from "lucide-react";
import type { Language } from "@/types/common";
export type { Language } from "@/types/common";
export { languageConfig, LANGUAGES } from "@/types/common";
import { reviewSchedules, Visibility } from "@/types/learning";

/**
 * DTO talii - podstawowe informacje
 */
export interface DeckDto {
  id: string;
  name: string;
  isPublic: boolean;
  userId: string;
  ownerType: string;
  wordCount: number;
}

/**
 * DTO szczegółów talii
 */
export interface DeckDetailsDto {
  id: string;
  userId: string;
  wordCount: number;
  createdAt: string;
  updatedAt: string;
  name: string;
  description: string;
  owner: DeckOwnerType;
  learnAlgorithm: LearnAlgorithm;
  howManyFlashcardsForOneSession: number;
  languageFrom: Language;
  languageTo: Language;
  visibility: Visibility;
  category: DeckCategory;
  difficulty: DeckDifficulty;
}

/**
 * Request do utworzenia nowej talii
 */
export interface CreateDeckDto {
  deckName: string;
  description?: string;
  learnAlgorithm: LearnAlgorithm;
  howManyFlashcardsForOneSession: number;
  languageFrom: Language;
  languageTo: Language;
  owner: DeckOwnerType;
  visibility: Visibility;
  difficulty: DeckDifficulty;
  category: DeckCategory;
  reviewSchedule: reviewSchedules;
}

/**
 * Response po utworzeniu talii
 */
export interface ResponseDeckDto {
  deckName: string;
  message: string;
}

/**
 * Request do aktualizacji widoczności talii
 */
export interface UpdateDeckVisibilityRequest {
  isPublic: boolean;
}

/**
 * Request do aktualizacji właściciela talii
 */
export interface UpdateDeckOwnerRequest {
  newOwner: DeckOwnerType;
}

/**
 * Request do aktualizacji nazwy talii
 */
export interface UpdateDeckNameRequest {
  deckName: string;
}

/**
 * Request do aktualizacji algorytmu nauki
 */
export interface UpdateLearnAlgorithmRequest {
  learnAlgorithm: LearnAlgorithm;
}

/**
 * Request do aktualizacji liczby fiszek na sesję
 */
export interface UpdateFlashcardsPerSessionRequest {
  flashcardsPerSession: number;
}

/**
 * Statystyki talii
 */
export interface DeckStatisticsDto {
  deckId: string;
  deckName: string;
  totalFlashcards: number;
  learnedFlashcards: number;
  unlearnedFlashcards: number;
  progressPercentage: number;
  totalSessions: number;
  completedSessions: number;
}

/**
 * Liczba talii użytkownika
 */
export interface UserDeckCountDto {
  userId: string;
  totalDecks: number;
  publicDecks: number;
  privateDecks: number;
}

/**
 * Lista talii użytkownika wyświetlana na dashboardzie
 *
 */
export interface IDeckListItem {
  enrollmentId: string;
  deckId: string;
  deckName: string;
  deckDescription: string;
  totalSession: number;
  learnedSession: number;
  progressPercentage: number | null;
  lastAccessed: string | null;
  deckDifficulty: DeckDifficulty;
  deckOwner: DeckOwnerType;
  deckCategory: DeckCategory;
  languageFrom?: Language;
  languageTo?: Language;
}

//============================================================================================
//                                        Typy + wyglad
//============================================================================================

/**
 * Typy właściciela talii
 */
export const deckOwnerTypeValues = [
  "I",
  "TEACHER",
  "FRIEND",
  "COMMUNITY",
] as const;

export type DeckOwnerType = (typeof deckOwnerTypeValues)[number];

export const deckOwnerConfig: Record<
  DeckOwnerType,
  {
    label: string;
    icon: LucideIcon;
    className: string;
    iconColor: string;
  }
> = {
  I: {
    label: "Własny",
    icon: User,
    className: "bg-blue-500/15 text-blue-700 border-blue-200",
    iconColor: "text-blue-700",
  },
  TEACHER: {
    label: "Nauczyciela",
    icon: GraduationCap,
    className: "bg-violet-500/15 text-violet-700 border-violet-200",
    iconColor: "text-violet-700",
  },
  FRIEND: {
    label: "Znajomego",
    icon: Users,
    className: "bg-pink-500/15 text-pink-700 border-pink-200",
    iconColor: "text-pink-700",
  },
  COMMUNITY: {
    label: "Społeczności",
    icon: Globe,
    className: "bg-teal-500/15 text-teal-700 border-teal-200",
    iconColor: "text-teal-700",
  },
};

export const DECK_OWNERS = deckOwnerTypeValues.map((value) => ({
  value,
  label: deckOwnerConfig[value].label,
  icon: deckOwnerConfig[value].icon,
  iconColor: deckOwnerConfig[value].iconColor,
}));

/**
 * Przeznaczenie talii (uproszczona wersja dla formularza)
 * Tylko dwie opcje: Własny (I) i Community (COMMUNITY)
 */
export const DECK_PURPOSES = [
  {
    value: "I" as DeckOwnerType,
    label: "Własny",
    description: "Talia tylko dla Ciebie",
    icon: User,
    iconColor: "text-blue-700",
  },
  {
    value: "COMMUNITY" as DeckOwnerType,
    label: "Community",
    description: "Udostępnij społeczności",
    icon: Globe,
    iconColor: "text-teal-700",
  },
] as const;

/**
 * Typy trudności talii
 *
 */
export const deckDifficultyValues = ["EASY", "MEDIUM", "HARD"] as const;

export type DeckDifficulty = (typeof deckDifficultyValues)[number];

export const deckDifficultyConfig: Record<
  DeckDifficulty,
  {
    label: string;
    icon: LucideIcon;
    className: string;
    iconColor: string;
  }
> = {
  EASY: {
    label: "Łatwy",
    icon: Feather,
    className: "bg-emerald-100 text-emerald-700 border-emerald-200",
    iconColor: "text-emerald-700",
  },
  MEDIUM: {
    label: "Średni",
    icon: TrendingUp,
    className: "bg-orange-100 text-orange-700 border-orange-200",
    iconColor: "text-orange-700",
  },
  HARD: {
    label: "Trudny",
    icon: Flame,
    className: "bg-red-100 text-red-700 border-red-200",
    iconColor: "text-red-700",
  },
};

export const DIFFICULTIES = deckDifficultyValues.map((value) => ({
  value,
  label: deckDifficultyConfig[value].label,
  icon: deckDifficultyConfig[value].icon,
  iconColor: deckDifficultyConfig[value].iconColor,
}));

/**
 * Typy kategorii talii
 */

export const deckCategoryValues = [
  "BUSINESS",
  "IT",
  "BASICS",
  "TOURISM",
  "CULTURE",
  "SCIENCE",
  "HOME",
  "WORK",
  "OTHER",
  "HEALTH",
  "SPORTS",
  "EDUCATION",
  "COOKING",
  "FINANCE",
  "ANIMALS",
  "TECHNOLOGY",
  "EMOTIONS",
  "DAILY_LIFE",
  "TRANSPORT",
  "LAW",
  "HOBBY",
  "NATURE",
  "MARKETING",
  "GAMING",
  "GENERAL",
] as const;

export type DeckCategory = (typeof deckCategoryValues)[number];

export const deckCategoryConfig: Record<
  DeckCategory,
  {
    label: string;
    icon: LucideIcon;
    className: string;
    iconColor: string;
  }
> = {
  BUSINESS: {
    label: "Biznes",
    className: "bg-blue-50 text-blue-700 border-blue-200",
    icon: Briefcase,
    iconColor: "text-blue-700",
  },
  WORK: {
    label: "Praca",
    className: "bg-gray-100 text-gray-700 border-gray-200",
    icon: Briefcase,
    iconColor: "text-gray-700",
  },
  FINANCE: {
    label: "Finanse",
    className: "bg-emerald-50 text-emerald-700 border-emerald-200",
    icon: Banknote,
    iconColor: "text-emerald-700",
  },
  MARKETING: {
    label: "Marketing",
    className: "bg-orange-50 text-orange-700 border-orange-200",
    icon: Megaphone,
    iconColor: "text-orange-700",
  },
  IT: {
    label: "IT & Tech",
    className: "bg-slate-100 text-slate-700 border-slate-200",
    icon: Code,
    iconColor: "text-slate-700",
  },
  EDUCATION: {
    label: "Edukacja",
    className: "bg-indigo-50 text-indigo-700 border-indigo-200",
    icon: GraduationCap,
    iconColor: "text-indigo-700",
  },
  SCIENCE: {
    label: "Nauka",
    className: "bg-violet-50 text-violet-700 border-violet-200",
    icon: Beaker,
    iconColor: "text-violet-700",
  },
  TECHNOLOGY: {
    label: "Technologia",
    className: "bg-cyan-50 text-cyan-700 border-cyan-200",
    icon: Cpu,
    iconColor: "text-cyan-700",
  },
  BASICS: {
    label: "Podstawy",
    className: "bg-zinc-100 text-zinc-700 border-zinc-200",
    icon: LayoutGrid,
    iconColor: "text-zinc-700",
  },
  HEALTH: {
    label: "Zdrowie",
    className: "bg-rose-50 text-rose-700 border-rose-200",
    icon: Heart,
    iconColor: "text-rose-700",
  },
  SPORTS: {
    label: "Sport",
    className: "bg-green-50 text-green-700 border-green-200",
    icon: Trophy,
    iconColor: "text-green-700",
  },
  COOKING: {
    label: "Gotowanie",
    className: "bg-amber-50 text-amber-700 border-amber-200",
    icon: Utensils,
    iconColor: "text-amber-700",
  },
  DAILY_LIFE: {
    label: "Życie codzienne",
    className: "bg-yellow-50 text-yellow-700 border-yellow-200",
    icon: Coffee,
    iconColor: "text-yellow-700",
  },
  HOME: {
    label: "Dom",
    className: "bg-stone-100 text-stone-700 border-stone-200",
    icon: Home,
    iconColor: "text-stone-700",
  },
  EMOTIONS: {
    label: "Emocje",
    className: "bg-pink-50 text-pink-700 border-pink-200",
    icon: Smile,
    iconColor: "text-pink-700",
  },
  GAMING: {
    label: "Gaming",
    className: "bg-purple-50 text-purple-700 border-purple-200",
    icon: Gamepad2,
    iconColor: "text-purple-700",
  },
  HOBBY: {
    label: "Hobby",
    className: "bg-fuchsia-50 text-fuchsia-700 border-fuchsia-200",
    icon: Palette,
    iconColor: "text-fuchsia-700",
  },
  CULTURE: {
    label: "Kultura",
    className: "bg-red-50 text-red-700 border-red-200",
    icon: BookOpen,
    iconColor: "text-red-700",
  },
  ANIMALS: {
    label: "Zwierzęta",
    className: "bg-lime-50 text-lime-700 border-lime-200",
    icon: PawPrint,
    iconColor: "text-lime-700",
  },
  TOURISM: {
    label: "Podróże",
    className: "bg-sky-50 text-sky-700 border-sky-200",
    icon: Plane,
    iconColor: "text-sky-700",
  },
  TRANSPORT: {
    label: "Transport",
    className: "bg-blue-50 text-blue-700 border-blue-200",
    icon: Car,
    iconColor: "text-blue-700",
  },
  LAW: {
    label: "Prawo",
    className: "bg-neutral-100 text-neutral-700 border-neutral-200",
    icon: Scale,
    iconColor: "text-neutral-700",
  },
  NATURE: {
    label: "Natura",
    className: "bg-emerald-50 text-emerald-700 border-emerald-200",
    icon: Leaf,
    iconColor: "text-emerald-700",
  },
  OTHER: {
    label: "Inne",
    className: "bg-slate-50 text-slate-600 border-slate-200",
    icon: MoreHorizontal,
    iconColor: "text-slate-600",
  },
  GENERAL: {
    label: "Ogólne",
    className: "bg-slate-100 text-slate-700 border-slate-200",
    icon: LayoutGrid,
    iconColor: "text-slate-700",
  },
};

export const CATEGORIES = deckCategoryValues.map((value) => ({
  value,
  label: deckCategoryConfig[value].label,
  icon: deckCategoryConfig[value].icon,
  iconColor: deckCategoryConfig[value].iconColor,
}));

/**
 * Algorytmy nauki dostępne w systemie
 */
export const learnAlgorithmValues = [
  "GRZESIEK_ALGORITHM",
  "LEITNER_ALGORITHM",
  // "TEST_ALGORITHM",
] as const;

export type LearnAlgorithm = (typeof learnAlgorithmValues)[number];

export const learnAlgorithmConfig: Record<
  LearnAlgorithm,
  {
    label: string;
    icon: LucideIcon;
    className: string;
    iconColor: string;
  }
> = {
  GRZESIEK_ALGORITHM: {
    label: "Algorytm Domyślny (Zalecany)",
    icon: Sparkles,
    className: "bg-blue-50 text-blue-700 border-blue-200",
    iconColor: "text-blue-700",
  },
  LEITNER_ALGORITHM: {
    label: "System Leitnera (Pudełka)",
    icon: Boxes,
    className: "bg-amber-50 text-amber-700 border-amber-200",
    iconColor: "text-amber-700",
  },
  // TEST_ALGORITHM: {
  //   label: "Tryb Testowy",
  //   icon: FlaskConical,
  //   className: "bg-purple-50 text-purple-700 border-purple-200",
  //   iconColor: "text-purple-700",
  // },
};

export const LEARN_ALGORITHMS = learnAlgorithmValues.map((value) => ({
  value,
  label: learnAlgorithmConfig[value].label,
  icon: learnAlgorithmConfig[value].icon,
  iconColor: learnAlgorithmConfig[value].iconColor,
}));

/**
 * Widoczność talii
 * PRIVATE - tylko właściciel (udostępnianie przez DeckShare)
 * PUBLIC - każdy może zobaczyć
 */
export const deckVisibilityTypeValues = ["PRIVATE", "PUBLIC"] as const;

export type DeckVisibilityType = (typeof deckVisibilityTypeValues)[number];

export const deckVisibilityConfig: Record<
  DeckVisibilityType,
  {
    label: string;
    description: string;
    icon: LucideIcon;
    className: string;
    iconColor: string;
  }
> = {
  PRIVATE: {
    label: "Prywatny",
    description: "Tylko Ty masz dostęp. Możesz udostępnić przez DeckShare.",
    icon: User,
    className: "bg-blue-500/15 text-blue-700 border-blue-200",
    iconColor: "text-blue-700",
  },
  PUBLIC: {
    label: "Publiczny",
    description: "Każdy może zobaczyć i zapisać się na tę talię.",
    icon: Globe,
    className: "bg-teal-500/15 text-teal-700 border-teal-200",
    iconColor: "text-teal-700",
  },
};

export const DECK_VISIBILITY = deckVisibilityTypeValues.map((value) => ({
  value,
  label: deckVisibilityConfig[value].label,
  description: deckVisibilityConfig[value].description,
  icon: deckVisibilityConfig[value].icon,
  iconColor: deckVisibilityConfig[value].iconColor,
}));
