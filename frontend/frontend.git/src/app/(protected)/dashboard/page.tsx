"use client";

// import { useProtectedRoute } from "@/features/auth/hooks/useProtectedRoute";
import { StudentStatsGrid } from "@/features/dashboard-student/components/StudentStatsGrid";
import { MyCourses } from "@/features/dashboard-student/components/MyCourses";
import { CommunityCourses } from "@/features/dashboard-student/components/CommunityCourses";
import { Leaderboard } from "@/features/dashboard-student/components/Leaderboard";
import { RecentActivity } from "@/features/dashboard-student/components/RecentActivity";
import { StudentQuickActions } from "@/features/dashboard-student/components/StudentQuickActions";
import type { User } from "@/features/auth/types";
import {useCurrentUser} from "@/features/auth/hooks/useCurrentUser";

/**
 * Mock użytkownika dla celów deweloperskich
 */
const mockUser: User = {
  userId: "student-456",
  username: "Piotr Wiśniewski",
  accountType: "STUDENT",
  userType: "NORMAL",
  isEnabled: true,
};

/**
 * Strona dashboardu dla uczniów
 * Dostępna dla wszystkich zalogowanych użytkowników (domyślny dashboard)
 */
const DashboardPage = () => {
const {data: user, isLoading, isError} = useCurrentUser();




  if (isLoading) {
    return (
      <div className="min-h-screen flex items-center justify-center bg-background">
        <div className="text-center">
          <div className="w-16 h-16 border-4 border-primary border-t-transparent rounded-full animate-spin mx-auto mb-4" />
          <p className="text-muted-foreground">Ładowanie dashboardu...</p>
        </div>
      </div>
    );
  }

    if (isError || !user) {
    return (
        <div className="min-h-screen flex items-center justify-center bg-background">
            <div className="text-center">
                <p className="text-red-500">Wystąpił błąd podczas ładowania danych użytkownika. Proszę spróbować ponownie później.</p>
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
              Kontynuuj naukę i rozwijaj swoje umiejętności
            </p>
          </div>
        </div>

        <StudentStatsGrid />

        <div className="grid grid-cols-1 lg:grid-cols-3 gap-6">
          <div className="lg:col-span-2 space-y-6">
            <MyCourses />
            <CommunityCourses />
          </div>

          <div className="space-y-6">
            <Leaderboard />
            <RecentActivity />
            <StudentQuickActions />
          </div>
        </div>
      </div>
    </div>
  );
};

export default DashboardPage;
