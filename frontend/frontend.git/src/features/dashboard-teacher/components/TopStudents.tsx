import { Card } from "@/components/ui/card";
import { TrendingUp, TrendingDown } from "lucide-react";
// import { TrendingUp, TrendingDown } from "lucide-react";

interface Student {
  id: string;
  name: string;
  email: string;
  progress: number;
  coursesCompleted: number;
  lastActive: string;
  trend: "up" | "down";
}

/**
 * Lista najbardziej aktywnych uczniów
 * Wyświetla zmockowane dane o postępach uczniów
 */
export const TopStudents = () => {
  const students: Student[] = [
    {
      id: "1",
      name: "Anna Kowalska",
      email: "anna.kowalska@example.com",
      progress: 95,
      coursesCompleted: 3,
      lastActive: "5 min temu",
      trend: "up",
    },
    {
      id: "2",
      name: "Jan Nowak",
      email: "jan.nowak@example.com",
      progress: 87,
      coursesCompleted: 2,
      lastActive: "1 godzinę temu",
      trend: "up",
    },
    {
      id: "3",
      name: "Maria Wiśniewska",
      email: "maria.wisniewska@example.com",
      progress: 82,
      coursesCompleted: 4,
      lastActive: "2 godziny temu",
      trend: "down",
    },
    {
      id: "4",
      name: "Piotr Zieliński",
      email: "piotr.zielinski@example.com",
      progress: 78,
      coursesCompleted: 2,
      lastActive: "3 godziny temu",
      trend: "up",
    },
    {
      id: "5",
      name: "Katarzyna Dąbrowska",
      email: "katarzyna.dabrowska@example.com",
      progress: 75,
      coursesCompleted: 3,
      lastActive: "5 godzin temu",
      trend: "up",
    },
  ];

  return (
    <Card className="p-6">
      <h2 className="text-2xl font-bold mb-6">Najlepsi uczniowie</h2>

      <div className="space-y-4">
        {students.map((student, index) => (
          <div
            key={student.id}
            className="p-3 rounded-lg hover:bg-accent/50 transition-colors cursor-pointer"
          >
            <div className="flex items-start gap-3">
              {/* Numer ranking */}
              <div className="flex items-center justify-center w-8 h-8 rounded-full bg-primary/10 font-semibold text-primary flex-shrink-0">
                {index + 1}
              </div>

              {/* Avatar */}
              <div className="w-10 h-10 bg-gradient-to-br from-primary to-primary/50 flex items-center justify-center text-primary-foreground font-semibold rounded-full flex-shrink-0">
                {student.name
                  .split(" ")
                  .map((n) => n[0])
                  .join("")}
              </div>

              {/* Info studenta */}
              <div className="flex-1 min-w-0">
                <div className="flex items-start justify-between gap-2 mb-1">
                  <div className="flex-1 min-w-0">
                    <h3 className="font-semibold truncate">{student.name}</h3>
                    <p className="text-sm text-muted-foreground truncate hidden sm:block">
                      {student.email}
                    </p>
                  </div>

                  {/* Trend icon - desktop */}
                  <div className="hidden sm:block flex-shrink-0">
                    {student.trend === "up" ? (
                      <TrendingUp className="w-5 h-5 text-success" />
                    ) : (
                      <TrendingDown className="w-5 h-5 text-error" />
                    )}
                  </div>
                </div>

                {/* Statystyki */}
                <div className="flex items-center justify-between gap-2 mt-2">
                  <div className="flex items-center gap-3 text-sm">
                    <span className="font-semibold">{student.progress}%</span>
                    <span className="text-muted-foreground">
                      {student.coursesCompleted} ukończone
                    </span>
                    <span className="text-muted-foreground text-xs hidden md:inline">
                      · {student.lastActive}
                    </span>
                  </div>

                  {/* Trend icon - mobile */}
                  <div className="sm:hidden flex-shrink-0">
                    {student.trend === "up" ? (
                      <TrendingUp className="w-5 h-5 text-success" />
                    ) : (
                      <TrendingDown className="w-5 h-5 text-error" />
                    )}
                  </div>
                </div>
              </div>
            </div>
          </div>
        ))}
      </div>
    </Card>
  );
};
