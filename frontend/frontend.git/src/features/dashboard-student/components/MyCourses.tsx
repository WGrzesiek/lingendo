import { Card } from "@/components/ui/card";
import { Button } from "@/components/ui/button";
import { Badge } from "@/components/ui/badge";
import { BookOpen, Clock, PlayCircle } from "lucide-react";

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
  const courses: MyCourse[] = [
    {
      id: "1",
      title: "Angielski dla początkujących",
      description: "Podstawy języka angielskiego od zera",
      progress: 65,
      lessonsCompleted: 13,
      totalLessons: 20,
      lastAccessed: "2 godziny temu",
      difficulty: "beginner",
      isOwn: false,
    },
    {
      id: "2",
      title: "Mój kurs hiszpańskiego",
      description: "Własny kurs tworzony na potrzeby wyjazdu",
      progress: 40,
      lessonsCompleted: 8,
      totalLessons: 20,
      lastAccessed: "1 dzień temu",
      difficulty: "intermediate",
      isOwn: true,
    },
    {
      id: "3",
      title: "Niemiecki w podróży",
      description: "Przydatne zwroty podczas podróży",
      progress: 90,
      lessonsCompleted: 18,
      totalLessons: 20,
      lastAccessed: "5 godzin temu",
      difficulty: "beginner",
      isOwn: false,
    },
    {
      id: "4",
      title: "Moja lista słówek IT",
      description: "Słownictwo techniczne z branży IT",
      progress: 25,
      lessonsCompleted: 5,
      totalLessons: 20,
      lastAccessed: "3 dni temu",
      difficulty: "advanced",
      isOwn: true,
    },
  ];

  const getDifficultyBadge = (difficulty: MyCourse["difficulty"]) => {
    const variants = {
      beginner: { label: "Podstawowy", variant: "default" as const },
      intermediate: {
        label: "Średniozaawansowany",
        variant: "secondary" as const,
      },
      advanced: { label: "Zaawansowany", variant: "outline" as const },
    };

    const { label, variant } = variants[difficulty];
    return <Badge variant={variant}>{label}</Badge>;
  };

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
        {courses.map((course) => (
          <div
            key={course.id}
            className="p-4 border rounded-lg hover:bg-accent/50 transition-colors cursor-pointer"
          >
            <div className="grid grid-cols-1 gap-4 mb-3 sm:flex sm:items-start sm:justify-between">
              <div className="flex-1">
                <div className="flex items-center gap-2 mb-2">
                  <h3 className="font-semibold text-lg">{course.title}</h3>
                  {course.isOwn && (
                    <Badge variant="outline" className="text-xs">
                      Własny
                    </Badge>
                  )}
                </div>
                <p className="text-sm text-muted-foreground mb-3">
                  {course.description}
                </p>
                <div className="sm:flex items-center gap-4 text-sm text-muted-foreground">
                  <span className="flex items-center gap-1 py-2">
                    <BookOpen className="w-4 h-4" />
                    {course.lessonsCompleted}/{course.totalLessons} lekcji
                  </span>
                  <span className="flex items-center gap-1 py-2">
                    <Clock className="w-4 h-4" />
                    {course.lastAccessed}
                  </span>
                  {getDifficultyBadge(course.difficulty)}
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
                <span className="font-semibold">{course.progress}%</span>
              </div>
              <div className="w-full bg-secondary rounded-full h-2">
                <div
                  className="bg-primary h-2 rounded-full transition-all"
                  style={{ width: `${course.progress}%` }}
                />
              </div>
            </div>
          </div>
        ))}
      </div>
    </Card>
  );
};
