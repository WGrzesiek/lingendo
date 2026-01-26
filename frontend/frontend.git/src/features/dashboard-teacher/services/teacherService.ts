import type { TeacherCourse, DeckVisibility } from "../types";
import apiClient from "@/lib/api/axios";

/**
 * Interfejs odpowiedzi paginowanej z backendu
 */
interface PageResponse<T> {
  content: T[];
  totalElements: number;
  totalPages: number;
  size: number;
  number: number;
}

/**
 * DTO talii z backendu
 */
interface DeckDto {
  id: string;
  name: string;
  deckDescription?: string;
  deckDifficulty?: string;
  deckOwner?: string;
  deckCategory?: string;
  ownerId: string;
  wordCount: number;
  visibility: DeckVisibility;
  createdAt: string;
  updatedAt?: string;
  username?: string;
}

/**
 * Mapuje DeckDto z backendu na TeacherCourse
 */
const mapDeckToTeacherCourse = (deck: DeckDto): TeacherCourse => ({
  id: deck.id,
  name: deck.name,
  deckDescription: deck.deckDescription,
  deckDifficulty: deck.deckDifficulty as TeacherCourse["deckDifficulty"],
  deckOwner: deck.deckOwner as TeacherCourse["deckOwner"],
  deckCategory: deck.deckCategory as TeacherCourse["deckCategory"],
  ownerId: deck.ownerId,
  wordCount: deck.wordCount,
  visibility: deck.visibility,
  createdAt: deck.createdAt,
  updatedAt: deck.updatedAt,
  username: deck.username,
  isShared: deck.visibility === "SHARED",
});

export const teacherCourseService = {
  /**
   * Pobiera kursy (talie) nauczyciela
   */
  async getCourses(): Promise<TeacherCourse[]> {
    const response = await apiClient.get<PageResponse<DeckDto>>("/v1/decks", {
      params: {
        page: 0,
        size: 100,
      },
    });

    return response.data.content.map(mapDeckToTeacherCourse);
  },

  /**
   * Udostępnia kurs wszystkim studentom nauczyciela
   */
  async shareCourse(courseId: string): Promise<TeacherCourse> {
    await apiClient.post(`/v1/decks-share/${courseId}/share/students`, {
      message: "Udostępniono kurs",
    });

    const response = await apiClient.get<DeckDto>(`/v1/decks/${courseId}`);
    const course = mapDeckToTeacherCourse(response.data);
    return { ...course, isShared: true };
  },

  /**
   * Cofa udostępnienie kursu
   */
  async unshareCourse(courseId: string): Promise<TeacherCourse> {
    await apiClient.delete(`/v1/decks-share/${courseId}/shares`);
    const response = await apiClient.get<DeckDto>(`/v1/decks/${courseId}`);
    const course = mapDeckToTeacherCourse(response.data);
    return { ...course, isShared: false };
  },

  /**
   * Sprawdza czy kurs jest udostępniony (ma aktywne udostępnienia)
   */
  async hasShares(courseId: string): Promise<boolean> {
    try {
      const response = await apiClient.get(
        `/v1/decks-share/${courseId}/shares`
      );
      return Array.isArray(response.data) && response.data.length > 0;
    } catch {
      return false;
    }
  },
};
