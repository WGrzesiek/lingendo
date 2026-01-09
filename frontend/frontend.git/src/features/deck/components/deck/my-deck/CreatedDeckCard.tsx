import { Button } from "@/components/ui/button";
import { Card } from "@/components/ui/card";
import {
  Edit,
  PlusCircle,
  Star,
  Users,
  CheckCircle,
  Calendar,
} from "lucide-react";
import {
  DeckStat,
  ICreatedDeckListItem,
} from "../../../types/created-deck.types";
import { DeckCategoryBadge } from "../DeckCategoryBadge";
import { DeckDifficultyBadge } from "../DeckDifficultyBadge";
import { time } from "@/lib/time";
import { useRouter } from "next/navigation";
import { DeckVisibilityBadge } from "@/features/deck/components/deck/DeckVisibilityBadge";

interface CreatedDeckCardProps {
  deck: ICreatedDeckListItem;
  deckStat?: DeckStat;
}

/**
 * Komponent karty kursu utworzonego przez użytkownika
 * Wyświetla szczegóły kursu wraz ze statystykami i akcjami
 */
export const CreatedDeckCard = ({ deck, deckStat }: CreatedDeckCardProps) => {
  const router = useRouter();

  const handleEdit = (e: React.MouseEvent) => {
    e.stopPropagation();
    router.push(`/decks/${deck.id}/edit`);
  };

  const handleAddWords = (e: React.MouseEvent) => {
    e.stopPropagation();
    router.push(`/decks/${deck.id}/words/add`);
  };
  console.log("Z poprzedniej deckid", deck.id);
  const handleCardClick = () => {
    router.push(`/my-courses/${deck.id}/details`);
  };
  return (
    <Card
      className="group p-5 hover:shadow-lg hover:border-primary/50 transition-all cursor-pointer"
      onClick={handleCardClick}
    >
      <div className="space-y-4">
        {/* Header z tytułem i akcjami */}
        <div className="flex flex-col sm:flex-row gap-4 justify-between">
          <div className="flex-1 space-y-2">
            <div className="flex flex-wrap items-center gap-2">
              <h3 className="font-semibold text-lg tracking-tight group-hover:text-primary transition-colors">
                {deck.name}
              </h3>
              <DeckCategoryBadge category={deck.deckCategory} />
              <DeckDifficultyBadge difficulty={deck.deckDifficulty} />
              <DeckVisibilityBadge visibility={deck.visibility} />
            </div>

            <p className="text-sm text-muted-foreground line-clamp-2 leading-relaxed">
              {deck.deckDescription || "Brak opisu kursu."}
            </p>
          </div>

          {/* Przyciski akcji */}
          <div className="flex gap-2 shrink-0 sm:self-start">
            <Button
              size="sm"
              variant="outline"
              onClick={handleEdit}
              className="shadow-sm"
            >
              <Edit className="w-4 h-4 sm:mr-2" />
              <span className="hidden sm:inline">Edytuj</span>
            </Button>
            <Button size="sm" onClick={handleAddWords} className="shadow-sm">
              <PlusCircle className="w-4 h-4 sm:mr-2" />
              <span className="hidden sm:inline">Dodaj słówka</span>
            </Button>
          </div>
        </div>

        {/* Statystyki kursu */}
        <div className="flex flex-wrap gap-4 text-xs text-muted-foreground pt-2 border-t border-border/40">
          <span className="flex items-center gap-1.5">
            <PlusCircle className="w-3.5 h-3.5" />
            {deck.wordCount} słówek
          </span>
          <span className="flex items-center gap-1.5">
            <Calendar className="w-3.5 h-3.5" />
            Utworzono {time(deck.createdAt)}
          </span>
          {deck.updatedAt !== deck.createdAt && (
            <span className="flex items-center gap-1.5">
              Zaktualizowano {time(deck.updatedAt)}
            </span>
          )}
        </div>

        {/* Statystyki publicznego kursu */}
        {deckStat && (
          <div className="grid grid-cols-2 sm:grid-cols-4 gap-3 pt-3 border-t border-border/40">
            <div className="flex items-center gap-2">
              <div className="p-2 bg-blue-500/10 rounded-lg">
                <Users className="w-4 h-4 text-blue-600" />
              </div>
              <div className="flex flex-col">
                <span className="text-sm font-semibold">
                  {deckStat.totalStudents}
                </span>
                <span className="text-xs text-muted-foreground">Uczniów</span>
              </div>
            </div>

            {/*<div className="flex items-center gap-2">*/}
            {/*  <div className="p-2 bg-yellow-500/10 rounded-lg">*/}
            {/*    <Star className="w-4 h-4 text-yellow-600" />*/}
            {/*  </div>*/}
            {/*  /!*<div className="flex flex-col">*!/*/}
            {/*  /!*  <span className="text-sm font-semibold">*!/*/}
            {/*  /!*    {avgRating*!/*/}
            {/*  /!*      ? avgRating.toFixed(1)*!/*/}
            {/*  /!*      : "N/A"}*!/*/}
            {/*  /!*  </span>*!/*/}
            {/*  /!*  /!*NOTE Ocena tymczasowo ukryta, moze kiedys dorobic*!/*!/*/}
            {/*  /!*  /!*<span className="text-xs text-muted-foreground">*!/*!/*/}
            {/*  /!*  /!*  Ocena ({deck.stats.totalRatings})*!/*!/*/}
            {/*  /!*  /!*</span>*!/*!/*/}
            {/*  /!*</div>*!/*/}
            {/*</div>*/}

            <div className="flex items-center gap-2">
              <div className="p-2 bg-green-500/10 rounded-lg">
                <CheckCircle className="w-4 h-4 text-green-600" />
              </div>
              <div className="flex flex-col">
                <span className="text-sm font-semibold">
                  {deckStat.completedStudents}
                </span>
                <span className="text-xs text-muted-foreground">Ukończeń</span>
              </div>
            </div>

            <div className="flex items-center gap-2">
              <div className="p-2 bg-purple-500/10 rounded-lg">
                <Star className="w-4 h-4 text-purple-600" />
              </div>
              <div className="flex flex-col">
                <span className="text-sm font-semibold">
                  {deckStat.completedStudents > 0
                    ? Math.round(
                        (deckStat.completedStudents / deckStat.totalStudents) *
                          100
                      )
                    : 0}
                  %
                </span>
                <span className="text-xs text-muted-foreground">
                  Wskaźnik ukończeń
                </span>
              </div>
            </div>
          </div>
        )}
      </div>
    </Card>
  );
};
