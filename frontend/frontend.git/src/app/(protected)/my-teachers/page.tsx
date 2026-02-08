"use client";

import { useState } from "react";
import { useCurrentUser } from "@/features/auth/hooks/useCurrentUser";
import { MyTeachersList, JoinTeacherDialog } from "@/features/my-teachers";

/**
 * Strona "Moi nauczyciele" dla uczniów
 * Pozwala na przeglądanie listy nauczycieli i dołączanie do nowych
 */
const MyTeachersPage = () => {
  const { data: user, isLoading, isError } = useCurrentUser();
  const [isJoinDialogOpen, setIsJoinDialogOpen] = useState(false);

  if (isLoading) {
    return (
      <div className="min-h-screen flex items-center justify-center bg-background">
        <div className="text-center">
          <div className="w-16 h-16 border-4 border-primary border-t-transparent rounded-full animate-spin mx-auto mb-4" />
          <p className="text-muted-foreground">Ładowanie...</p>
        </div>
      </div>
    );
  }

  if (isError || !user) {
    return (
      <div className="min-h-screen flex items-center justify-center bg-background">
        <div className="text-center">
          <p className="text-red-500">
            Wystąpił błąd podczas ładowania danych. Spróbuj ponownie później.
          </p>
        </div>
      </div>
    );
  }

  return (
    <div className="min-h-screen bg-background">
      <div className="container mx-auto p-6 lg:p-8 space-y-6">
        {/* Nagłówek */}
        <div>
          <h1 className="text-3xl font-bold mb-2">Moi nauczyciele</h1>
          <p className="text-muted-foreground">
            Zarządzaj swoimi nauczycielami i dołączaj do nowych za pomocą kodu
            zaproszenia
          </p>
        </div>

        {/* Lista nauczycieli */}
        <MyTeachersList onAddTeacher={() => setIsJoinDialogOpen(true)} />

        {/* Dialog dołączania */}
        <JoinTeacherDialog
          open={isJoinDialogOpen}
          onOpenChange={setIsJoinDialogOpen}
        />
      </div>
    </div>
  );
};

export default MyTeachersPage;
