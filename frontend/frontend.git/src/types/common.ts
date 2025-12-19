/**
 * Wspólne typy używane w całej aplikacji
 */

/**
 * Standardowa odpowiedź błędu z API
 */
export interface ApiErrorResponse {
  status: number;
  message: string;
}

/**
 * Dostępne języki w systemie
 */
import { Languages, Flag, Globe, LucideIcon } from "lucide-react"; // użyjemy symbolicznych ikon językowych

export const languageValues = [
  "POLISH",
  "ENGLISH",
  "SPANISH",
  "GERMAN",
  "FRENCH",
  "ITALIAN",
] as const;

export type Language = (typeof languageValues)[number];

export const languageConfig: Record<
  Language,
  {
    label: string;
    icon: LucideIcon;
    className: string; // Badge colors
    iconColor: string; // Select colors
  }
> = {
  POLISH: {
    label: "Polski",
    icon: Flag,
    className: "bg-red-100 text-red-700 border-red-200",
    iconColor: "text-red-700",
  },
  ENGLISH: {
    label: "Angielski",
    icon: Globe,
    className: "bg-blue-100 text-blue-700 border-blue-200",
    iconColor: "text-blue-700",
  },
  SPANISH: {
    label: "Hiszpański",
    icon: Languages,
    className: "bg-yellow-100 text-yellow-700 border-yellow-200",
    iconColor: "text-yellow-700",
  },
  GERMAN: {
    label: "Niemiecki",
    icon: Languages,
    className: "bg-stone-200 text-stone-800 border-stone-300",
    iconColor: "text-stone-800",
  },
  FRENCH: {
    label: "Francuski",
    icon: Languages,
    className: "bg-indigo-100 text-indigo-700 border-indigo-200",
    iconColor: "text-indigo-700",
  },
  ITALIAN: {
    label: "Włoski",
    icon: Languages,
    className: "bg-emerald-100 text-emerald-700 border-emerald-200",
    iconColor: "text-emerald-700",
  },
};

// Select-ready
export const LANGUAGES = languageValues.map((value) => ({
  value,
  label: languageConfig[value].label,
  icon: languageConfig[value].icon,
  iconColor: languageConfig[value].iconColor,
}));

/**
 * Status sesji nauki
 */
export type SessionStatus = "ACTIVE" | "COMPLETED" | "PAUSED" | "ABANDONED" | "IN_PROGRESS";

/**
 * Typ sesji nauki
 */
export type SessionType = "LEARNING" | "REVIEW" | "TEST" | "PRACTICE";

export interface PageResponse<T> {
  content: T[];
  totalElements: number;
  totalPages: number;
  number: number;
  size: number;
  first: boolean;
  last: boolean;
  empty: boolean;
}
