"use client";

import { useProtectedRoute } from "@/features/auth/hooks/useProtectedRoute";
import { StatsGrid } from "@/features/dashboard-teacher/components/StatsGrid";
import { RecentCourses } from "@/features/dashboard-teacher/components/RecentCourses";
import { TopStudents } from "@/features/dashboard-teacher/components/TopStudents";
import { ActivityFeed } from "@/features/dashboard-teacher/components/ActivityFeed";
import { QuickActions } from "@/features/dashboard-teacher/components/QuickActions";
import type { User } from "@/features/auth/types";

/**
 * Mock użytkownika dla celów deweloperskich
 */
const mockUser: User = {
  userId: "teacher-123",
  username: "Jan Kowalski",
  accountType: "TEACHER",
  userType: "NORMAL",
  isEnabled: true,
};

/**
 * Strona dashboardu dla nauczycieli
 * Dostępna tylko dla użytkowników z accountType = TEACHER
 */
const DashboardTeacherPage = () => {
  // const { user, isLoading } = useProtectedRoute({
  //   requiredAccountType: "TEACHER",
  //   redirectTo: "/dashboard-teacher",
  // });

  const user = mockUser;
  const isLoading = false;

  if (isLoading) {
    return (
      <div className="min-h-screen flex items-center justify-center">
        <div className="text-center">
          <div className="w-16 h-16 border-4 border-primary border-t-transparent rounded-full animate-spin mx-auto mb-4" />
          <p className="text-muted-foreground">Ładowanie dashboardu...</p>
        </div>
      </div>
    );
  }

  return (
    <div className="min-h-screen bg-background">
      <div className="container mx-auto p-6 lg:p-8 space-y-8">
        <div className="flex items-center justify-between">
          <div>
            <h1 className="text-4xl font-bold mb-2">
              Witaj, {user?.username}! 👋
            </h1>
            <p className="text-muted-foreground text-lg">
              Oto podsumowanie Twojej aktywności jako nauczyciela
            </p>
          </div>
        </div>

        <StatsGrid />

        <div className="grid grid-cols-1 lg:grid-cols-3 gap-6">
          <div className="lg:col-span-2 space-y-6">
            <RecentCourses />
            <ActivityFeed />
          </div>

          <div className="space-y-6">
            <TopStudents />
            <QuickActions />
          </div>
        </div>
      </div>
    </div>
  );
};

export default DashboardTeacherPage;
