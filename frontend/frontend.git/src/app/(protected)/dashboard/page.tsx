"use client";

import { useProtectedRoute } from "@/features/auth/hooks/useProtectedRoute";

const DashboardPage = () => {
  const { user, isLoading } = useProtectedRoute();

  if (isLoading) {
    return (
      <div className="min-h-screen flex items-center justify-center">
        <p className="text-muted-foreground">Ładowanie...</p>
      </div>
    );
  }

  return (
    <div className="min-h-screen p-8">
      <h1 className="text-3xl font-bold mb-6">Dashboard</h1>
      <p className="text-muted-foreground mb-4">
        Witaj w panelu użytkownika, <strong>{user?.username}</strong>!
      </p>
      <div className="bg-card p-4 rounded-lg border">
        <h2 className="text-xl font-semibold mb-2">Informacje o koncie</h2>
        <ul className="space-y-2">
          <li>
            <span className="text-muted-foreground">Typ konta:</span>{" "}
            <strong>{user?.accountType}</strong>
          </li>
          <li>
            <span className="text-muted-foreground">Typ użytkownika:</span>{" "}
            <strong>{user?.userType}</strong>
          </li>
          <li>
            <span className="text-muted-foreground">Status:</span>{" "}
            <strong>{user?.isEnabled ? "Aktywne" : "Nieaktywne"}</strong>
          </li>
        </ul>
      </div>
    </div>
  );
};

export default DashboardPage;
