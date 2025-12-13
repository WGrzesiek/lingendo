import {Language} from "@/types/common";
import {reviewSchedules} from "@/types/learning";

export type OwnerType = 'I' | 'TEACHER' | 'FRIRND' | 'COMMUNITY';
export type Visibility = 'PUBLIC' | 'PRIVATE' | 'FRIENDS_ONLY' | 'STUDENTS_ONLY';

export interface CourseHeader{
    deckId:string;
    name:string;
    description:string;
    ownerId:string;
    username:string;
    ownerType: OwnerType;
    visibility: Visibility;
    languageFrom: Language;
    languageTo: Language;
}
export interface CourseProgress
{
    completedSessions: number;
    totalSessions: number;
    wordsPerSession: number;
    totalWords: number;
    wordsToReview: number;
    nextReviewDate?: string;
}

export interface CourseSettings {
    enrollmentId: string;
    algorithm: string;
    wordsPerSession: number;
    reviewSchedule: reviewSchedules;
}