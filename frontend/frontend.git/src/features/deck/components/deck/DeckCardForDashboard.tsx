import { Button } from "@/components/ui/button";
import { Progress } from "@/components/ui/progress"; // Używamy komponentu UI
import { BookOpen, Clock, PlayCircle } from "lucide-react";
import { IDeckListItem } from "../../types";
import { DeckOwnerBadge } from "./DeckOwnerBadge";
import { DeckDifficultyBadge } from "./DeckDifficultyBadge";
import { timeAgo } from "@/lib/timeAgo";
import { DeckCategoryBadge } from "./DeckCategoryBadge";
import {useRouter} from "next/navigation";

interface DeckCardProps {
  deck: IDeckListItem;
  onClick?: () => void;
}

export const DeckCardForDashboard = ({ deck }: DeckCardProps) => {
  const router = useRouter();
  const progress = deck.progressPercentage ?? 0;

  const handleCardClick = () => {
    router.push(`/my-courses/${deck.deckId}/details`);
  };

  return (
    <div
      onClick={handleCardClick}
      className="group p-4 border rounded-xl hover:shadow-md hover:border-primary/50 transition-all bg-card text-card-foreground cursor-pointer"
    >
      <div className="flex flex-col sm:flex-row gap-4 justify-between mb-4">
        <div className="flex-1 space-y-2">
          <div className="flex flex-wrap items-center gap-2">
            <h3 className="font-semibold text-lg tracking-tight group-hover:text-primary transition-colors">
              {deck.deckName}
            </h3>
            {deck.deckCategory && (
              <DeckCategoryBadge category={deck.deckCategory} />
            )}
            <DeckOwnerBadge owner={deck.deckOwner} />
            <DeckDifficultyBadge difficulty={deck.deckDifficulty} />
          </div>

          <p className="text-sm text-muted-foreground line-clamp-2 leading-relaxed">
            {deck.deckDescription || "Brak opisu kursu."}
          </p>

          <div className="flex flex-wrap items-center gap-4 text-xs text-muted-foreground pt-1">
            <span className="flex items-center gap-1.5">
              <BookOpen className="w-3.5 h-3.5" />
              {deck.learnedSession}/{deck.totalSession} lekcji
            </span>
            {deck.lastAccessed && (
              <span className="flex items-center gap-1.5">
                <Clock className="w-3.5 h-3.5" />
                {timeAgo(deck.lastAccessed)}
              </span>
            )}
          </div>
        </div>

        <div className="shrink-0 sm:self-center">
          <Button
            size="sm"
            className="w-full sm:w-auto shadow-sm"
            onClick={(e) => {
              e.stopPropagation();
              window.location.href = `/course/${deck.enrollmentId}`;
            }}
          >
            <PlayCircle className="w-4 h-4 mr-2" />
            Kontynuuj
          </Button>
        </div>
      </div>

      <div className="space-y-1.5">
        <div className="flex items-center justify-between text-xs font-medium">
          <span className="text-muted-foreground">Postęp nauki</span>
          <span>{progress}%</span>
        </div>
        <Progress value={progress} className="h-2" />
      </div>
    </div>
  );
};
