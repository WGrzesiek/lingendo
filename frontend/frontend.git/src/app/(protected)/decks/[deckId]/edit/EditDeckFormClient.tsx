"use client";

import { EditDeckForm } from "@/features/deck/components/deck/EditDeckForm";
import { useDeckDetails } from "@/features/deck/hooks/useDeckDetails";
import { Card } from "@/components/ui/card";
import { Loader2, AlertCircle } from "lucide-react";
import { useRouter } from "next/navigation";
import { Button } from "@/components/ui/button";

/**
 * Strona edycji kursu
 * Pobiera szczegóły kursu i wyświetla formularz edycji
 */
export default function EditDeckFormClient({ deckId}: { deckId: string }) {
    const router = useRouter();
    const { data: deckDetails, isLoading, error } = useDeckDetails(deckId);

    // Loading state
    if (isLoading) {
        return (
            <div className="min-h-screen bg-background flex items-center justify-center">
                <div className="flex flex-col items-center gap-4">
                    <Loader2 className="w-8 h-8 animate-spin text-primary" />
                    <p className="text-muted-foreground">Ładowanie kursu...</p>
                </div>
            </div>
        );
    }

    // Error state
    if (error || !deckDetails) {
        return (
            <div className="min-h-screen bg-background flex items-center justify-center p-4">
                <Card className="p-8 max-w-md w-full text-center">
                    <div className="flex flex-col items-center gap-4">
                        <div className="p-3 bg-destructive/10 rounded-full">
                            <AlertCircle className="w-8 h-8 text-destructive" />
                        </div>
                        <div>
                            <h3 className="font-semibold text-lg mb-2">
                                Nie udało się załadować kursu
                            </h3>
                            <p className="text-sm text-muted-foreground mb-4">
                                {error?.message ||
                                    "Kurs nie istnieje lub nie masz do niego dostępu."}
                            </p>
                            <Button onClick={() => router.back()} variant="outline">
                                Powrót
                            </Button>
                        </div>
                    </div>
                </Card>
            </div>
        );
    }

    return (
        <div className="min-h-screen bg-background py-8 px-4">
            <EditDeckForm deckId={deckId} initialData={deckDetails} />
        </div>
    );
}
