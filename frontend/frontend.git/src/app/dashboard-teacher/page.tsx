"use client";

import { useRequireRole } from "@/features/auth/hooks/useRequireRole";

const DashboardTeacherPage = () => {
  const { user, isLoading } = useRequireRole("TEACHER", "NORMAL");

  if (isLoading) {
    return (
      <div className="min-h-screen flex items-center justify-center">
        <p className="text-muted-foreground">Ładowanie...</p>
      </div>
    );
  }

  if (!user) {
    return null;
  }

  return (
    <div className="min-h-screen p-8">
      <h1 className="text-3xl font-bold mb-6">Panel Nauczyciela</h1>
      <p className="text-muted-foreground mb-4">
        Witaj, {user.username}! Ta strona jest dostępna tylko dla nauczycieli.
      </p>

      <div className="bg-card p-6 rounded-lg border">
        <h2 className="text-xl font-semibold mb-2">Twoje dane:</h2>
        <ul className="space-y-2">
          <li>
            <strong>Username:</strong> {user.username}
          </li>
          <li>
            <strong>Typ konta:</strong> {user.accountType}
          </li>
          <li>
            <strong>Role:</strong> {user.userType}
          </li>
        </ul>
      </div>
    </div>
  );
};

export default DashboardTeacherPage;
