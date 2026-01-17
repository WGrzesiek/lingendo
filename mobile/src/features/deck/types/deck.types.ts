import { Language, PageResponse } from '@/types/common';

/**
 * Typy właściciela talii
 */
export const deckOwnerTypeValues = ['I', 'TEACHER', 'FRIEND', 'COMMUNITY'] as const;

/**
 * Typy trudności talii
 */
export const deckDifficultyValues = ['EASY', 'MEDIUM', 'HARD'] as const;

/**
 * Kategorie talii
 */
export const deckCategoryValues = [
  'BUSINESS',
  'IT',
  'BASICS',
  'TOURISM',
  'CULTURE',
  'SCIENCE',
  'HOME',
  'WORK',
  'OTHER',
  'HEALTH',
  'SPORTS',
  'EDUCATION',
  'COOKING',
  'FINANCE',
  'ANIMALS',
  'TECHNOLOGY',
  'EMOTIONS',
  'DAILY_LIFE',
  'TRANSPORT',
  'LAW',
  'HOBBY',
  'NATURE',
  'MARKETING',
  'GAMING',
  'GENERAL',
] as const;

/**
 * Algorytmy nauki
 */
export const learnAlgorithmValues = [
  'GRZESIEK_ALGORITHM',
  'LEITNER_ALGORITHM',
  'TEST_ALGORITHM',
] as const;

/**
 * Widoczność talii
 */
export const deckVisibilityValues = ['PRIVATE', 'PUBLIC'] as const;

/**
 * Harmonogramy powtórek
 */
export const reviewScheduleValues = ['AUTO', 'LIGHT', 'NORMAL', 'INTENSE'] as const;

export type DeckOwnerType = (typeof deckOwnerTypeValues)[number];
export type DeckDifficulty = (typeof deckDifficultyValues)[number];
export type DeckCategory = (typeof deckCategoryValues)[number];
export type LearnAlgorithm = (typeof learnAlgorithmValues)[number];
export type DeckVisibility = (typeof deckVisibilityValues)[number];
export type ReviewSchedule = (typeof reviewScheduleValues)[number];

// =====================
// KONFIGURACJA UI
// =====================

/**
 * Konfiguracja właścicieli talii (labele po polsku)
 */
export const deckOwnerConfig: Record<DeckOwnerType, { label: string }> = {
  I: { label: 'Własny' },
  TEACHER: { label: 'Nauczyciela' },
  FRIEND: { label: 'Znajomego' },
  COMMUNITY: { label: 'Społeczności' },
};

/**
 * Konfiguracja trudności talii
 */
export const deckDifficultyConfig: Record<DeckDifficulty, { label: string }> = {
  EASY: { label: 'Łatwy' },
  MEDIUM: { label: 'Średni' },
  HARD: { label: 'Trudny' },
};

/**
 * Konfiguracja kategorii talii
 */
export const deckCategoryConfig: Record<DeckCategory, { label: string }> = {
  BUSINESS: { label: 'Biznes' },
  WORK: { label: 'Praca' },
  FINANCE: { label: 'Finanse' },
  MARKETING: { label: 'Marketing' },
  IT: { label: 'IT & Tech' },
  EDUCATION: { label: 'Edukacja' },
  SCIENCE: { label: 'Nauka' },
  TECHNOLOGY: { label: 'Technologia' },
  BASICS: { label: 'Podstawy' },
  HEALTH: { label: 'Zdrowie' },
  SPORTS: { label: 'Sport' },
  COOKING: { label: 'Gotowanie' },
  DAILY_LIFE: { label: 'Życie codzienne' },
  HOME: { label: 'Dom' },
  EMOTIONS: { label: 'Emocje' },
  GAMING: { label: 'Gaming' },
  HOBBY: { label: 'Hobby' },
  CULTURE: { label: 'Kultura' },
  ANIMALS: { label: 'Zwierzęta' },
  TOURISM: { label: 'Podróże' },
  TRANSPORT: { label: 'Transport' },
  LAW: { label: 'Prawo' },
  NATURE: { label: 'Natura' },
  OTHER: { label: 'Inne' },
  GENERAL: { label: 'Ogólne' },
};

/**
 * Konfiguracja algorytmów nauki
 */
export const learnAlgorithmConfig: Record<LearnAlgorithm, { label: string }> = {
  GRZESIEK_ALGORITHM: { label: 'Algorytm Domyślny (Zalecany)' },
  LEITNER_ALGORITHM: { label: 'System Leitnera (Pudełka)' },
  TEST_ALGORITHM: { label: 'Tryb Testowy' },
};

/**
 * Konfiguracja widoczności talii
 */
export const deckVisibilityConfig: Record<DeckVisibility, { label: string; description: string }> =
  {
    PRIVATE: {
      label: 'Prywatny',
      description: 'Tylko Ty masz dostęp. Możesz udostępnić przez DeckShare.',
    },
    PUBLIC: {
      label: 'Publiczny',
      description: 'Każdy może zobaczyć i zapisać się na tę talię.',
    },
  };

/**
 * Konfiguracja harmonogramów powtórek
 */
export const reviewScheduleConfig: Record<ReviewSchedule, { label: string }> = {
  AUTO: { label: 'Automatyczny — 7, 14, 21 dni' },
  LIGHT: { label: 'Lekki — 3, 7 dni' },
  NORMAL: { label: 'Normalny — 7, 14, 30 dni' },
  INTENSE: { label: 'Intensywny — 1, 3, 7 dni' },
};

// =====================
// DTO - DATA TRANSFER OBJECTS
// =====================

/**
 * Podstawowe DTO talii
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
 * Szczegóły talii
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
  visibility: DeckVisibility;
  category: DeckCategory;
  difficulty: DeckDifficulty;
}

/**
 * Request do utworzenia nowej talii
 */
export interface CreateDeckRequest {
  deckName: string;
  description?: string;
  learnAlgorithm: LearnAlgorithm;
  howManyFlashcardsForOneSession: number;
  languageFrom: Language;
  languageTo: Language;
  owner: DeckOwnerType;
  visibility: DeckVisibility;
  difficulty: DeckDifficulty;
  category: DeckCategory;
  reviewSchedule: ReviewSchedule;
}

/**
 * Response po utworzeniu talii
 */
export interface CreateDeckResponse {
  deckName: string;
  message: string;
}

/**
 * Element listy talii zapisanych (enrolled)
 */
export interface DeckListItem {
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

/**
 * Statystyki utworzonego kursu
 */
export interface CreatedDeckStats {
  enrolledUsers: number;
  averageRating: number | null;
  totalRatings: number;
  completions: number;
}

/**
 * Element listy kursów utworzonych przez użytkownika
 */
export interface CreatedDeckListItem {
  id: string;
  name: string;
  deckDescription: string;
  deckDifficulty: DeckDifficulty;
  deckOwner: DeckOwnerType;
  deckCategory: DeckCategory;
  ownerId: string;
  wordCount: number;
  visibility: DeckVisibility;
  languageFrom: Language;
  languageTo: Language;
  createdAt: string;
  updatedAt: string;
  stats?: CreatedDeckStats;
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
 * Szczegóły pojedynczej talii (pełne)
 */
export interface DeckDetailResponse {
  id: string;
  name: string;
  deckDescription: string;
  deckDifficulty: DeckDifficulty;
  deckOwner: DeckOwnerType;
  deckCategory: DeckCategory;
  ownerId: string;
  wordCount: number;
  visibility: DeckVisibility;
  languageFrom?: Language;
  languageTo?: Language;
  createdAt: string;
  updatedAt: string;
  username: string;
}

/**
 * Statystyka pojedynczego decka
 */
export interface DeckStat {
  totalStudents: number;
  completedStudents: number;
}

/**
 * Statystyki wielu decków (mapa deckId -> stats)
 */
export interface DecksStats {
  [deckId: string]: DeckStat;
}

export interface UpdateDeckVisibilityRequest {
  isPublic: boolean;
}

export interface UpdateDeckOwnerRequest {
  newOwner: DeckOwnerType;
}

export interface UpdateDeckNameRequest {
  deckName: string;
}

export interface UpdateLearnAlgorithmRequest {
  learnAlgorithm: LearnAlgorithm;
}

export interface UpdateFlashcardsPerSessionRequest {
  flashcardsPerSession: number;
}

export type DeckListResponse = PageResponse<DeckListItem>;
export type CreatedDeckListResponse = PageResponse<CreatedDeckListItem>;

/**
 * Request do aktualizacji talii
 */
export interface UpdateDeckRequest {
  name?: string;
  description?: string;
  learnAlgorithm?: LearnAlgorithm;
  howManyFlashcardsForOneSession?: number;
  visibility?: DeckVisibility;
  category?: DeckCategory;
  difficulty?: DeckDifficulty;
  owner?: DeckOwnerType;
}

/**
 * Filtry do wyszukiwania talii
 */
export interface DeckFilters {
  category?: DeckCategory;
  sourceLanguage?: Language;
  targetLanguage?: Language;
  sortBy?: 'newest' | 'oldest' | 'name' | 'popularity';
  searchTerm?: string;
}

/**
 * Szczegóły talii z informacją o zapisie
 */
export interface DeckEnrollmentDetails {
  deck: DeckDetailResponse;
  enrollment: {
    enrollmentId: string;
    learnAlgorithm: LearnAlgorithm;
    flashcardsPerSession: number;
    reviewSchedule: ReviewSchedule;
    enrolledAt: string;
    progressPercentage: number;
    totalSessions: number;
    completedSessions: number;
  } | null;
  isEnrolled: boolean;
}
