import { Card } from "@/components/ui/card";
import { Button } from "@/components/ui/button";
import { Badge } from "@/components/ui/badge";
import { BookOpen, Clock, PlayCircle } from "lucide-react";
import { DeckDto } from "@/features/deck/types";
import { useUserDecks } from "@/features/deck/hooks/useUserDecks";
import { UseQueryResult } from "@tanstack/react-query";
import { DecksForDashboard } from "@/features/deck/components/deck/DeckForDashboard";

interface MyCourse {
  id: string;
  title: string;
  description: string;
  progress: number;
  lessonsCompleted: number;
  totalLessons: number;
  lastAccessed: string;
  difficulty: "beginner" | "intermediate" | "advanced";
  isOwn: boolean;
}

/**
 * Lista kursów ucznia (własne i zapisane)
 * Wyświetla zmockowane dane kursów w trakcie nauki
 */
export const MyCourses = () => {
  // const courses: MyCourse[] = [
  //   {
  //     id: "1",
  //     title: "Angielski dla początkujących",
  //     description: "Podstawy języka angielskiego od zera",
  //     progress: 65,
  //     lessonsCompleted: 13,
  //     totalLessons: 20,
  //     lastAccessed: "2 godziny temu",
  //     difficulty: "beginner",
  //     isOwn: false,
  //   },
  //   {
  //     id: "2",
  //     title: "Mój kurs hiszpańskiego",
  //     description: "Własny kurs tworzony na potrzeby wyjazdu",
  //     progress: 40,
  //     lessonsCompleted: 8,
  //     totalLessons: 20,
  //     lastAccessed: "1 dzień temu",
  //     difficulty: "intermediate",
  //     isOwn: true,
  //   },
  //   {
  //     id: "3",
  //     title: "Niemiecki w podróży",
  //     description: "Przydatne zwroty podczas podróży",
  //     progress: 90,
  //     lessonsCompleted: 18,
  //     totalLessons: 20,
  //     lastAccessed: "5 godzin temu",
  //     difficulty: "beginner",
  //     isOwn: false,
  //   },
  //   {
  //     id: "4",
  //     title: "Moja lista słówek IT",
  //     description: "Słownictwo techniczne z branży IT",
  //     progress: 25,
  //     lessonsCompleted: 5,
  //     totalLessons: 20,
  //     lastAccessed: "3 dni temu",
  //     difficulty: "advanced",
  //     isOwn: true,
  //   },
  // ];

  // const getDifficultyBadge = (difficulty: MyCourse["difficulty"]) => {
  //   const variants = {
  //     beginner: { label: "Podstawowy", variant: "default" as const },
  //     intermediate: {
  //       label: "Średniozaawansowany",
  //       variant: "secondary" as const,
  //     },
  //     advanced: { label: "Zaawansowany", variant: "outline" as const },
  //   };

  //   const { label, variant } = variants[difficulty];
  //   return <Badge variant={variant}>{label}</Badge>;
  // };
  return (
    <Card className="p-6">
      <div className="sm:flex items-center justify-between mb-6">
        <h2 className="text-2xl font-bold pb-3">Moje kursy</h2>
        <Button>
          <BookOpen className="w-4 h-4 mr-2" />
          Utwórz nowy kurs
        </Button>
      </div>

      <div className="space-y-4">
        <DecksForDashboard />
      </div>
    </Card>
  );
};
