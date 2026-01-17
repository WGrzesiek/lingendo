import apiClient from '@/lib/api/axios';
import { ENDPOINTS } from '@/constants';
import type { CourseHeader, CourseProgress, CourseSettings } from '../types';


export const courseService = {
  /**
   * Pobiera nagłówek kursu (nazwa, opis, właściciel, języki)
   */
  getCourseHeader: async (enrollmentId: string): Promise<CourseHeader> => {
    const { data } = await apiClient.get<CourseHeader>(ENDPOINTS.COURSES.HEADER(enrollmentId));
    return data;
  },

  /**
   * Pobiera postęp kursu (sesje, słówka do powtórki)
   */
  getCourseProgress: async (enrollmentId: string): Promise<CourseProgress> => {
    const { data } = await apiClient.get<CourseProgress>(ENDPOINTS.COURSES.PROGRESS(enrollmentId));
    return data;
  },

  /**
   * Pobiera ustawienia kursu (algorytm, słówka na sesję, harmonogram)
   */
  getCourseSettings: async (enrollmentId: string): Promise<CourseSettings> => {
    const { data } = await apiClient.get<CourseSettings>(ENDPOINTS.COURSES.SETTINGS(enrollmentId));
    return data;
  },

  /**
   * Inicjalizuje nową sesję nauki
   */
  initializeSession: async (enrollmentId: string): Promise<void> => {
    await apiClient.post(ENDPOINTS.COURSES.INITIALIZE_SESSION(enrollmentId));
    console.log('[courseService] Zainicjalizowano sesję');
  },
};
