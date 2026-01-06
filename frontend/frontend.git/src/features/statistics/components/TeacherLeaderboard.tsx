"use client";

import { Card, CardContent, CardHeader, CardTitle } from "@/components/ui/card";
import { Trophy } from "lucide-react";
import type { TopStudent } from "@/features/dashboard-teacher/types";
import {time, timee} from "@/lib/time";

interface TeacherLeaderboardProps {
  students: TopStudent[];
}

/**
 * Leaderboard - top 5 uczniów
 */
export const TeacherLeaderboard = ({ students }: TeacherLeaderboardProps) => {
  if (students.length === 0) {
    return (
      <Card>
        <CardHeader>
          <CardTitle className="flex items-center gap-2">
            <Trophy className="w-5 h-5 text-yellow-500" />
            Top uczniowie
          </CardTitle>
        </CardHeader>
        <CardContent>
          <div className="text-center py-8">
            <p className="text-muted-foreground">
              Brak uczniów do wyświetlenia
            </p>
          </div>
        </CardContent>
      </Card>
    );
  }

  return (
    <Card>
      <CardHeader>
        <CardTitle className="flex items-center gap-2">
          <Trophy className="w-5 h-5 text-yellow-500" />
          Top uczniowie
        </CardTitle>
      </CardHeader>
      <CardContent>
        <div className="space-y-3">
          {students.map((student, index) => (
            <div
              key={student.studentId}
              className="flex items-center gap-3 p-2 rounded-lg hover:bg-accent/50 transition-colors"
            >
              {/* Pozycja */}
              <div
                className={`w-8 h-8 rounded-full flex items-center justify-center font-bold text-sm ${
                  index === 0
                    ? "bg-yellow-500/20 text-yellow-600"
                    : index === 1
                    ? "bg-gray-300/30 text-gray-500"
                    : index === 2
                    ? "bg-amber-600/20 text-amber-700"
                    : "bg-muted text-muted-foreground"
                }`}
              >
                {index + 1}
              </div>

              {/* Avatar */}
              <div className="w-8 h-8 bg-gradient-to-br from-primary to-primary/50 flex items-center justify-center text-primary-foreground text-sm font-semibold rounded-full flex-shrink-0">
                {student.studentName
                  .split(" ")
                  .map((n) => n[0])
                  .join("")
                  .slice(0, 2)
                  .toUpperCase()}
              </div>

              {/* Info */}
              <div className="flex-1 min-w-0">
                <p className="font-medium text-sm truncate">
                  {student.studentName}
                </p>
                <p className="text-xs text-muted-foreground">
                  {/*{formatLastActive(student.lastActive)}*/}
                  {time(student.lastActive)}
                </p>
              </div>

              {/* Punkty */}
              <div className="text-right">
                <p className="font-bold text-sm">
                  {student.totalPoints.toLocaleString("pl-PL")}
                </p>
                <p className="text-xs text-muted-foreground">pkt</p>
              </div>
            </div>
          ))}
        </div>
      </CardContent>
    </Card>
  );
};
