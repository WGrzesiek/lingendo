"use client";

import { CourseHeader } from "@/features/course/components/CourseHeader";
import { CourseStats } from "@/features/course/components/stats/CourseStats";
import { SessionProgress } from "@/features/course/components/SessionProgress";
import { CourseSettings } from "@/features/course/components/CourseSettings";
import { WordsList } from "@/features/course/components/WordsListNew";
import React from "react";

/**
 * Strona szczegółów kursu
 * Pokazuje słówka, statystyki, sesje i ustawienia kursu
 */
export default function CoursePageClient({enrollmentId,}: {enrollmentId: string; }) {
    if (!enrollmentId) {
        return (
            <div className="min-h-screen flex items-center justify-center">
                <p className="text-destructive">Brak ID zapisu na kurs.</p>
            </div>
        );
    }
    const isLoading = false;


    if (isLoading) {
        return (
            <div className="min-h-screen flex items-center justify-center">
                <div className="text-center">
                    <div
                        className="w-16 h-16 border-4 border-primary border-t-transparent rounded-full animate-spin mx-auto mb-4"/>
                    <p className="text-muted-foreground">Ładowanie kursu...</p>
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
