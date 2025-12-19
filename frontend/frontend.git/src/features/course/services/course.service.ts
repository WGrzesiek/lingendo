import {CourseHeader, CourseProgress, CourseSettings} from "@/features/course/types/course.type";
import apiClient from "@/lib/api/axios";

const BASE_URL = "/v1/decks";

export const getCourseHeader = async (enrollmentId: string): Promise<CourseHeader> => {
    const response = await apiClient.get<CourseHeader>(
        `${BASE_URL}/${enrollmentId}/course-header`
    );
    return response.data;
}

export const getCourseProgress = async (enrollmentId: string): Promise<CourseProgress> => {
    const response = await apiClient.get<CourseProgress>(
        `${BASE_URL}/sessions/${enrollmentId}/session-progres`
    );
    return response.data;
}

export const getCourseSettings = async (enrollmentId: string): Promise<CourseSettings> => {
    const response = await apiClient.get<CourseSettings>(
        `${BASE_URL}/enrollment/${enrollmentId}/settings`
    );
    return response.data;
}

export const initializeSession = async (enrollmentId: string): Promise<void> => {
    await apiClient.post(
        `${BASE_URL}/${enrollmentId}/sessions?flashcardFetchStrategy=ALPHABETICAL&type=LEARNING`
    );
    console.log("[Learning Service] Initialized session");
}