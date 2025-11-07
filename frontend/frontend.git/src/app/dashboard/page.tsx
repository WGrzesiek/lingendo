"use client";

import { useRequireAuth } from "@/features/auth/hooks/useRequireAuth";

const DashboardPage = () => {
  useRequireAuth();

  return (
    <div className="min-h-screen p-8">
      <h1 className="text-3xl font-bold mb-6">Dashboard</h1>
      <p className="text-muted-foreground">
        Witaj w panelu użytkownika! Ta strona jest chroniona.
      </p>
    </div>
  );
};

export default DashboardPage;
