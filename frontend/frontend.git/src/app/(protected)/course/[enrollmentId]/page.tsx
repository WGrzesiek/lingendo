"use client";

import { CourseHeader } from "@/features/course/components/CourseHeader";
import { CourseStats } from "@/features/course/components/stats/CourseStats";
import { SessionProgress } from "@/features/course/components/SessionProgress";
import { CourseSettings } from "@/features/course/components/CourseSettings";
import { WordsList } from "@/features/course/components/WordsListNew";
/**
 * Interfejs kursu
 */
interface Course {
  id: string;
  title: string;
  description: string;
  isPublic: boolean;
  isOwner: boolean;
  createdBy: string;
  algorithm: "spaced-repetition" | "leitner" | "random";
  wordsPerSession: number;
  totalWords: number;
  completedSessions: number;
  totalSessions: number;
  nextReviewDate?: string;
  wordsToReview: number;
}

/**
 * Mock danych kursu
 */
const mockCourse: Course = {
  id: "course-123",
  title: "Angielski dla początkujących",
  description: "Podstawy języka angielskiego od zera - słownictwo codzienne",
  isPublic: true,
  isOwner: true,
  createdBy: "Piotr Wiśniewski",
  algorithm: "spaced-repetition",
  wordsPerSession: 20,
  totalWords: 140,
  completedSessions: 2,
  totalSessions: 7,
  nextReviewDate: "2025-11-09",
  wordsToReview: 15,
};

/**
 * Strona szczegółów kursu
 * Pokazuje słówka, statystyki, sesje i ustawienia kursu
 */
const CoursePage = ({ params }: { params: { enrollmentId: string } }) => {
  const course = mockCourse;
  const isLoading = false;

  // 👇 TU jest różnica
  const enrollmentId = params.enrollmentId;

  if (isLoading) {
    return (
      <div className="min-h-screen flex items-center justify-center">
        <div className="text-center">
          <div className="w-16 h-16 border-4 border-primary border-t-transparent rounded-full animate-spin mx-auto mb-4" />
          <p className="text-muted-foreground">Ładowanie kursu...</p>
        </div>
      </div>
    );
  }

  if (!enrollmentId) {
    return (
      <div className="min-h-screen flex items-center justify-center">
        <div className="text-center">
          <p className="text-destructive">Brak ID zapisu na kurs.</p>
        </div>
      </div>
    );
  }

  return (
    <div className="min-h-screen bg-background">
      <div className="container mx-auto p-6 lg:p-8 space-y-6">
        <CourseHeader enrollmentId={enrollmentId} />

        <CourseStats enrollmentId={enrollmentId} />

        <SessionProgress enrollmentId={enrollmentId} />

        <div className="grid grid-cols-1 lg:grid-cols-3 gap-6">
          <div className="lg:col-span-2">
            <WordsList enrollmentId={enrollmentId} />
          </div>

          <div>
            <CourseSettings enrollmentId={enrollmentId} />
          </div>
        </div>
      </div>
    </div>
  );
};

export default CoursePage;
