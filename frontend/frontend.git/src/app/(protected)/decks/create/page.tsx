"use client";

import { CreateDeckForm } from "@/features/deck/components/deck/CreateDeckForm";

/**
 * Strona tworzenia nowej talii fiszek
 * Dostępna dla zalogowanych użytkowników
 */
export default function CreateDeckPage() {
  return (
    <div className="min-h-screen bg-background">
      <div className="container mx-auto p-6 lg:p-8">
        <div className="mb-8">
          <h1 className="text-3xl font-bold mb-2">Nowa talia</h1>
          <p className="text-muted-foreground">
            Utwórz talię fiszek do nauki nowych słówek
          </p>
        </div>

        <CreateDeckForm />
      </div>
    </div>
  );
}
