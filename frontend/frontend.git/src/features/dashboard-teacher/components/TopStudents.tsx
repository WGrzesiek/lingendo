"use client";

import { Card } from "@/components/ui/card";
import { Trophy, Loader2 } from "lucide-react";
import { useTopStudents } from "../hooks";
import { formatDistanceToNow } from "date-fns";
import { pl } from "date-fns/locale";


const formatLastActive = (lastActive: string | null): string => {
  if (!lastActive) return "Brak aktywności";
  try {
    return formatDistanceToNow(new Date(lastActive), {
      addSuffix: true,
      locale: pl,
    });
  } catch {
    return "Nieznana";
  }
};

/**
 * Lista najbardziej aktywnych uczniów
 * Wyświetla uczniów z najwyższą liczbą punktów
 */
export const TopStudents = () => {
  const { data: students, isLoading, error } = useTopStudents(5);

  if (isLoading) {
    return (
      <Card className="p-6">
        <h2 className="text-2xl font-bold mb-6">Najlepsi uczniowie</h2>
        <div className="flex items-center justify-center py-8">
          <Loader2 className="w-8 h-8 animate-spin text-muted-foreground" />
        </div>
      </Card>
    );
  }

  if (error) {
    return (
      <Card className="p-6">
        <h2 className="text-2xl font-bold mb-6">Najlepsi uczniowie</h2>
        <p className="text-muted-foreground text-center py-8">
          Nie udało się załadować danych
        </p>
      </Card>
    );
  }

  if (!students || students.length === 0) {
    return (
      <Card className="p-6">
        <h2 className="text-2xl font-bold mb-6">Najlepsi uczniowie</h2>
        <p className="text-muted-foreground text-center py-8">
          Brak aktywnych uczniów
        </p>
      </Card>
    );
  }

  return (
    <Card className="p-6">
      <h2 className="text-2xl font-bold mb-6">Najlepsi uczniowie</h2>

      <div className="space-y-4">
        {students.map((student, index) => (
          <div
            key={student.studentId}
            className="p-3 rounded-lg hover:bg-accent/50 transition-colors cursor-pointer"
          >
            <div className="flex items-start gap-3">
              {/* Numer ranking */}
              <div className="flex items-center justify-center w-8 h-8 rounded-full bg-primary/10 font-semibold text-primary flex-shrink-0">
                {index + 1}
              </div>

              {/* Avatar */}
              <div className="w-10 h-10 bg-gradient-to-br from-primary to-primary/50 flex items-center justify-center text-primary-foreground font-semibold rounded-full flex-shrink-0">
                {student.studentName
                  .split(" ")
                  .map((n) => n[0])
                  .join("")
                  .slice(0, 2)
                  .toUpperCase()}
              </div>

              {/* Info studenta */}
              <div className="flex-1 min-w-0">
                <div className="flex items-start justify-between gap-2 mb-1">
                  <div className="flex-1 min-w-0">
                    <h3 className="font-semibold truncate">
                      {student.studentName}
                    </h3>
                  </div>

                  {/* Trophy dla top 3 */}
                  {index < 3 && (
                    <div className="hidden sm:block flex-shrink-0">
                      <Trophy
                        className={`w-5 h-5 ${
                          index === 0
                            ? "text-yellow-500"
                            : index === 1
                            ? "text-gray-400"
                            : "text-amber-600"
                        }`}
                      />
                    </div>
                  )}
                </div>

                {/* Statystyki */}
                <div className="flex items-center justify-between gap-2 mt-2">
                  <div className="flex items-center gap-3 text-sm">
                    <span className="font-semibold">
                      {student.totalPoints} pkt
                    </span>
                    <span className="text-muted-foreground text-xs hidden md:inline">
                      · {formatLastActive(student.lastActive)}
                    </span>
                  </div>

                  {/* Trophy - mobile */}
                  {index < 3 && (
                    <div className="sm:hidden flex-shrink-0">
                      <Trophy
                        className={`w-5 h-5 ${
                          index === 0
                            ? "text-yellow-500"
                            : index === 1
                            ? "text-gray-400"
                            : "text-amber-600"
                        }`}
                      />
                    </div>
                  )}
                </div>
              </div>
            </div>
          </div>
        ))}
      </div>
    </Card>
  );
};
