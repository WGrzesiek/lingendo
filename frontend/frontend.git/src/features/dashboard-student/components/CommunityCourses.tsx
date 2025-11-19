import { Card } from "@/components/ui/card";
import { Button } from "@/components/ui/button";
import { Badge } from "@/components/ui/badge";
import { Users, Star, BookOpen } from "lucide-react";

interface CommunityCourse {
  id: string;
  title: string;
  description: string;
  author: string;
  studentsCount: number;
  rating: number;
  lessonsCount: number;
  difficulty: "beginner" | "intermediate" | "advanced";
  category: string;
}

/**
 * Lista popularnych kursów społeczności
 * Wyświetla zmockowane dane kursów dostępnych dla ucznia
 */
export const CommunityCourses = () => {
  const courses: CommunityCourse[] = [
    {
      id: "1",
      title: "Angielski biznesowy w praktyce",
      description: "Poznaj język biznesu i bądź pewny siebie w rozmowach",
      author: "Maria Nowak",
      studentsCount: 1250,
      rating: 4.8,
      lessonsCount: 35,
      difficulty: "intermediate",
      category: "Biznes",
    },
    {
      id: "2",
      title: "Hiszpański dla podróżników",
      description: "Wszystko czego potrzebujesz podczas podróży",
      author: "Carlos Rodriguez",
      studentsCount: 890,
      rating: 4.9,
      lessonsCount: 28,
      difficulty: "beginner",
      category: "Podróże",
    },
    {
      id: "3",
      title: "Programowanie - terminologia angielska",
      description: "Słownictwo IT niezbędne w pracy programisty",
      author: "Jan Kowalski",
      studentsCount: 2100,
      rating: 4.7,
      lessonsCount: 42,
      difficulty: "advanced",
      category: "IT",
    },
    {
      id: "4",
      title: "Niemiecki od podstaw",
      description: "Kompletny kurs dla początkujących",
      author: "Anna Schmidt",
      studentsCount: 650,
      rating: 4.6,
      lessonsCount: 50,
      difficulty: "beginner",
      category: "Podstawy",
    },
  ];

  const getDifficultyBadge = (difficulty: CommunityCourse["difficulty"]) => {
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
        <div className="pb-4">
          <h2 className="text-2xl font-bold">Kursy społeczności</h2>
          <p className="text-sm text-muted-foreground mt-1">
            Popularne kursy tworzone przez innych użytkowników
          </p>
        </div>
        <Button variant="outline">Przeglądaj wszystkie</Button>
      </div>

      <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
        {courses.map((course) => (
          <div
            key={course.id}
            className="p-4 border rounded-lg hover:border-primary hover:bg-accent/50 transition-all cursor-pointer"
          >
            <div className="flex items-start justify-between mb-3">
              <div className="flex-1">
                <h3 className="font-semibold text-lg mb-1">{course.title}</h3>
                <p className="text-sm text-muted-foreground mb-3">
                  {course.description}
                </p>

                <div className="sm:flex items-center gap-2 mb-3">
                  <Badge variant="secondary" className="text-xs">
                    {course.category}
                  </Badge>
                  {getDifficultyBadge(course.difficulty)}
                </div>

                <div className="flex items-center gap-4 text-sm text-muted-foreground mb-3">
                  <span className="flex items-center gap-1">
                    <Users className="w-4 h-4" />
                    {course.studentsCount.toLocaleString()}
                  </span>
                  <span className="flex items-center gap-1">
                    <Star className="w-4 h-4 fill-yellow-500 text-yellow-500" />
                    {course.rating}
                  </span>
                  <span className="flex items-center gap-1">
                    <BookOpen className="w-4 h-4" />
                    {course.lessonsCount} lekcji
                  </span>
                </div>

                <p className="text-xs text-muted-foreground">
                  Autor: {course.author}
                </p>
              </div>
            </div>

            <Button className="w-full" size="sm">
              Dołącz do kursu
            </Button>
          </div>
        ))}
      </div>
    </Card>
  );
};
