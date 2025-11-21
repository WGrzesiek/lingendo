import { Button } from "@/components/ui/button";
import { BookOpen, Clock, PlayCircle } from "lucide-react";
import { useIDecks } from "../../hooks/useIDecks";
import { PageResponse, StudentMyCourseListItem } from "../../types";
import { UseQueryResult } from "@tanstack/react-query";

export const DecksForDashboard = () => {
  const {
    data,
    isLoading,
    error,
  }: UseQueryResult<PageResponse<StudentMyCourseListItem>> = useIDecks();

  if (isLoading) {
    return <div>Ładowanie...</div>;
  }

  if (error || !data) {
    return <div>Błąd podczas ładowania talii.</div>;
  }

  return (
    <div className="space-y-4">
      {data.content.map((course) => (
        <DeckCard key={course.deckId} course={course} />
      ))}
    </div>
  );
};

interface DeckCardProps {
  course: StudentMyCourseListItem;
}

const DeckCard = ({ course }: DeckCardProps) => {
  const progress = course.progressPercentage ?? 0;

  return (
    <div className="p-4 border rounded-lg hover:bg-accent/50 transition-colors cursor-pointer">
      <div className="grid grid-cols-1 gap-4 mb-3 sm:flex sm:items-start sm:justify-between">
        <div className="flex-1">
          <div className="flex items-center gap-2 mb-2">
            <h3 className="font-semibold text-lg">{course.deckName}</h3>
          </div>

          <p className="text-sm text-muted-foreground mb-3">
            {course.deckDescription}
          </p>

          <div className="sm:flex items-center gap-4 text-sm text-muted-foreground">
            <span className="flex items-center gap-1 py-2">
              <BookOpen className="w-4 h-4" />
              {course.learnedSession}/{course.totalSession} lekcji
            </span>

            {course.lastAccessed && (
              <span className="flex items-center gap-1 py-2">
                <Clock className="w-4 h-4" />
                {course.lastAccessed}
              </span>
            )}
          </div>
        </div>

        <div className="sm:flex-1 sm:flex sm:justify-end sm:gap-4">
          <Button size="sm" className="w-full sm:w-auto sm:ml-4">
            <PlayCircle className="w-4 h-4 mr-2" />
            Kontynuuj
          </Button>
        </div>
      </div>

      <div className="space-y-2">
        <div className="flex items-center justify-between text-sm">
          <span className="text-muted-foreground">Postęp</span>
          <span className="font-semibold">{progress}%</span>
        </div>
        <div className="w-full bg-secondary rounded-full h-2">
          <div
            className="bg-primary h-2 rounded-full transition-all"
            style={{ width: `${progress}%` }}
          />
        </div>
      </div>
    </div>
  );
};
