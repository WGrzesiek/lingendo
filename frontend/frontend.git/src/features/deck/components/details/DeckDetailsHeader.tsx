"use client";

import { useState } from "react";
import { Button } from "@/components/ui/button";
import {
  ArrowLeft,
  Edit,
  PlusCircle,
  BookOpen,
  Calendar,
  User,
  Loader2,
  CheckCircle,
} from "lucide-react";
import { useRouter } from "next/navigation";
import { DeckCategoryBadge } from "../deck/DeckCategoryBadge";
import { DeckDifficultyBadge } from "../deck/DeckDifficultyBadge";
import { DeckVisibilityBadge } from "../deck/DeckVisibilityBadge";
import { ShareDeckDialog } from "@/features/deck-share/components/ShareDeckDialog";
import { useEnrollToDeck } from "@/features/deckEnrollment/hooks/hooks";
import type { DeckDetails } from "@/features/deck/types/deck-details.types";
import { time } from "@/lib/time";

interface DeckDetailsHeaderProps {
  deck: DeckDetails;
}

/**
 * Header szczegółów decka
 * Pokazuje tytuł, opis, przyciski akcji (tylko dla właściciela)
 */
export const DeckDetailsHeader = ({ deck }: DeckDetailsHeaderProps) => {
  const router = useRouter();
  const [isEnrolled, setIsEnrolled] = useState(false);
  const enrollMutation = useEnrollToDeck();

  const handleEnroll = () => {
    enrollMutation.mutate(
      { deckId: deck.id },
      {
        onSuccess: () => {
          setIsEnrolled(true);
        },
      }
    );
  };

  return (
    <div className="space-y-6">
      <Button
        variant="ghost"
        size="lg"
        className="gap-2"
        onClick={() => router.back()}
      >
        <ArrowLeft className="w-5 h-5" />
        Powrót
      </Button>

      <div className="flex flex-col lg:flex-row gap-6 lg:items-start lg:justify-between">
        <div className="flex-1 space-y-4">
          <div className="space-y-2">
            <h1 className="text-4xl font-bold">{deck.name}</h1>
            <div className="flex flex-wrap items-center gap-2">
              <DeckCategoryBadge category={deck.category} />
              <DeckDifficultyBadge difficulty={deck.difficulty} />
              <DeckVisibilityBadge visibility={deck.visibility} />
            </div>
          </div>

          <p className="text-lg text-muted-foreground leading-relaxed">
            {deck.description || "Brak opisu kursu."}
          </p>

          {/* Metadata */}
          <div className="flex flex-wrap gap-4 text-sm text-muted-foreground">
            <span className="flex items-center gap-1.5">
              <BookOpen className="w-4 h-4" />
              {deck.wordCount} słówek
            </span>
            <span className="flex items-center gap-1.5">
              <User className="w-4 h-4" />
              Autor: {deck.createdBy.username}
            </span>
            <span className="flex items-center gap-1.5">
              <Calendar className="w-4 h-4" />
              Utworzono {time(deck.createdAt)}
            </span>
            {deck.updatedAt !== deck.createdAt && (
              <span className="flex items-center gap-1.5">
                Zaktualizowano {time(deck.updatedAt)}
              </span>
            )}
          </div>
        </div>

        {deck.isOwner && (
          <div className="flex flex-col gap-3 lg:shrink-0">
            <ShareDeckDialog deckId={deck.id} deckName={deck.name} />
            <Button
              size="lg"
              variant="outline"
              className="gap-2 w-full lg:w-auto"
              onClick={() => router.push(`/decks/${deck.id}/edit`)}
            >
              <Edit className="w-5 h-5" />
              Edytuj kurs
            </Button>
            <Button
              size="lg"
              className="gap-2 w-full lg:w-auto"
              onClick={() => router.push(`/decks/${deck.id}/words/add`)}
            >
              <PlusCircle className="w-5 h-5" />
              Dodaj słówka
            </Button>
          </div>
        )}

        {/* Przycisk zapisu - tylko dla studentów (nie-nauczycieli) */}
        {!deck.isTeacher && (
          <div className="flex flex-col gap-3 lg:shrink-0">
            {isEnrolled ? (
              <>
                <div className="flex items-center gap-2 text-green-600 p-3 bg-green-50 rounded-lg">
                  <CheckCircle className="w-5 h-5" />
                  <span className="font-medium">Zapisano na kurs!</span>
                </div>
                <Button
                  size="lg"
                  className="gap-2 w-full lg:w-auto"
                  onClick={() => router.push(`/course/${deck.id}`)}
                >
                  <BookOpen className="w-5 h-5" />
                  Rozpocznij naukę
                </Button>
              </>
            ) : (
              <Button
                size="lg"
                className="gap-2 w-full lg:w-auto"
                onClick={handleEnroll}
                disabled={enrollMutation.isPending}
              >
                {enrollMutation.isPending ? (
                  <>
                    <Loader2 className="w-5 h-5 animate-spin" />
                    Zapisywanie...
                  </>
                ) : (
                  <>
                    <PlusCircle className="w-5 h-5" />
                    Zapisz się na kurs
                  </>
                )}
              </Button>
            )}
          </div>
        )}
      </div>
    </div>
  );
};
