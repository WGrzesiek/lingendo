"use client";

import { InvitationGenerator } from "@/features/dashboard-teacher/components/InvitationGenerator";
import type { User } from "@/features/auth/types";


/**
 * Strona zarządzania zaproszeniami
 */
const InvitationsPage = () => {

  const isLoading = false;

  if (isLoading) {
    return (
      <div className="min-h-screen flex items-center justify-center">
        <div className="text-center">
          <div className="w-16 h-16 border-4 border-primary border-t-transparent rounded-full animate-spin mx-auto mb-4" />
          <p className="text-muted-foreground">Ładowanie...</p>
        </div>
      </div>
    );
  }

  return (
    <div className="min-h-screen bg-background">
      <div className="container mx-auto p-6 lg:p-8">
        <div className="mb-6">
          <h1 className="text-3xl font-bold mb-2">Zaproszenia</h1>
          <p className="text-muted-foreground">
            Generuj kody zaproszeń dla nowych studentów
          </p>
        </div>
        <InvitationGenerator />
      </div>
    </div>
  );
};

export default InvitationsPage;
