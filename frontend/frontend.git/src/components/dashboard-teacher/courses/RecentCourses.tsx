import { Card } from "@/components/ui/card";
import { Button } from "@/components/ui/button";
import { Badge } from "@/components/ui/badge";
import { Users, Calendar, BarChart } from "lucide-react";

interface Course {
  id: string;
  title: string;
  studentsCount: number;
  completionRate: number;
  lastActivity: string;
  status: "active" | "draft" | "archived";
}

/**
 * Lista aktywnych kursów nauczyciela
 * Wyświetla zmockowane dane kursów
 */
export const RecentCourses = () => {
  const courses: Course[] = [
    {
      id: "1",
      title: "Angielski dla początkujących",
      studentsCount: 45,
      completionRate: 82,
      lastActivity: "2 godziny temu",
      status: "active",
    },
    {
      id: "2",
      title: "Zaawansowany angielski biznesowy",
      studentsCount: 28,
      completionRate: 65,
      lastActivity: "5 godzin temu",
      status: "active",
    },
    {
      id: "3",
      title: "Konwersacje po angielsku",
      studentsCount: 51,
      completionRate: 91,
      lastActivity: "1 dzień temu",
      status: "active",
    },
    {
      id: "4",
      title: "Gramatyka angielska - poziom B2",
      studentsCount: 12,
      completionRate: 45,
      lastActivity: "3 dni temu",
      status: "draft",
    },
  ];

  const getStatusBadge = (status: Course["status"]) => {
    const variants = {
      active: "default",
      draft: "secondary",
      archived: "outline",
    } as const;

    const labels = {
      active: "Aktywny",
      draft: "Szkic",
      archived: "Zarchiwizowany",
    };

    return <Badge variant={variants[status]}>{labels[status]}</Badge>;
  };

  return (
    <Card className="p-6">
      <div className="flex items-center justify-between mb-6">
        <h2 className="text-2xl font-bold">Twoje kursy</h2>
        <Button>Dodaj nowy kurs</Button>
      </div>

      <div className="space-y-4">
        {courses.map((course) => (
          <div
            key={course.id}
            className="p-4 border rounded-lg hover:bg-accent/50 transition-colors cursor-pointer"
          >
            <div className="flex flex-col gap-3 sm:flex-row sm:items-start sm:justify-between mb-3">
              <div className="flex-1">
                <div className="flex items-start gap-2 mb-2">
                  <h3 className="font-semibold text-lg flex-1">
                    {course.title}
                  </h3>
                  <div className="sm:hidden flex-shrink-0">
                    {getStatusBadge(course.status)}
                  </div>
                </div>
                <div className="flex flex-wrap items-center gap-x-4 gap-y-2 text-sm text-muted-foreground">
                  <span className="flex items-center gap-1">
                    <Users className="w-4 h-4" />
                    {course.studentsCount} uczniów
                  </span>
                  <span className="flex items-center gap-1">
                    <BarChart className="w-4 h-4" />
                    {course.completionRate}% ukończenia
                  </span>
                  <span className="flex items-center gap-1">
                    <Calendar className="w-4 h-4" />
                    {course.lastActivity}
                  </span>
                </div>
              </div>
              <div className="hidden sm:block flex-shrink-0">
                {getStatusBadge(course.status)}
              </div>
            </div>

            <div className="w-full bg-secondary rounded-full h-2">
              <div
                className="bg-primary h-2 rounded-full transition-all"
                style={{ width: `${course.completionRate}%` }}
              />
            </div>
          </div>
        ))}
      </div>
    </Card>
  );
};
