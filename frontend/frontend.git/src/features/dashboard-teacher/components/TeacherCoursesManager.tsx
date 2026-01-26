"use client";

import { Card, CardHeader, CardTitle, CardContent } from "@/components/ui/card";
import { Button } from "@/components/ui/button";
import { Badge } from "@/components/ui/badge";
import { Switch } from "@/components/ui/switch";
import {
  BookOpen,
  Layers,
  Share2,
  ExternalLink,
  Plus,
  RefreshCw,
} from "lucide-react";
import {
  useTeacherCourses,
  useToggleCourseSharing,
} from "../hooks/useTeacherData";
import type { TeacherCourse } from "../types";
import { useRouter } from "next/navigation";
import {timee} from "@/lib/time";

/**
 * Komponent pojedynczego kursu
 */
const CourseCard = ({
  course,
  onToggleSharing,
  isToggling,
}: {
  course: TeacherCourse;
  onToggleSharing: (courseId: string, share: boolean) => void;
  isToggling: boolean;
}) => {
  return (
    <div className="p-4 border rounded-lg hover:shadow-md transition-shadow">
      <div className="flex items-start justify-between gap-4">
        <div className="flex-1 min-w-0">
          <div className="flex items-center gap-2 mb-2">
            <h3 className="font-semibold truncate">{course.name}</h3>
            {course.isShared && (
              <Badge variant="secondary" className="flex items-center gap-1">
                <Share2 className="w-3 h-3" />
                Udostępniony
              </Badge>
            )}
          </div>

          {course.deckDescription && (
            <p className="text-sm text-muted-foreground mb-3 line-clamp-2">
              {course.deckDescription}
            </p>
          )}

          <div className="flex flex-wrap items-center gap-4 text-sm text-muted-foreground">
            <span className="flex items-center gap-1.5">
              <Layers className="w-4 h-4" />
              {course.wordCount} fiszek
            </span>
            <span>Utworzono {timee.formatDate(course.createdAt)}</span>
          </div>
        </div>

        <div className="flex flex-col items-end gap-2">
          <div className="flex items-center gap-2">
            <span className="text-sm text-muted-foreground">
              {course.isShared ? "Udostępniony" : "Prywatny"}
            </span>
            <Switch
              checked={course.isShared}
              onCheckedChange={(checked) => onToggleSharing(course.id, checked)}
              disabled={isToggling}
            />
          </div>
          <Button variant="outline" size="sm">
            <ExternalLink className="w-4 h-4 mr-2" />
            Szczegóły
          </Button>
        </div>
      </div>
    </div>
  );
};

/**
 * Komponent zarządzania kursami nauczyciela
 */
export const TeacherCoursesManager = () => {
  const router = useRouter();
  const { data: courses, isLoading, error } = useTeacherCourses();
  const toggleSharingMutation = useToggleCourseSharing();

  const handleToggleSharing = (courseId: string, share: boolean) => {
    toggleSharingMutation.mutate({ courseId, share });
  };

  const handleCreateCourse = () => {
    router.push("/decks/create");
  };
  const sharedCourses = courses?.filter((c) => c.isShared) || [];
  const privateCourses = courses?.filter((c) => !c.isShared) || [];

  if (error) {
    return (
      <Card className="p-6">
        <p className="text-destructive">Błąd ładowania kursów</p>
      </Card>
    );
  }

  return (
    <Card>
      <CardHeader className="border-b">
        <div className="flex items-center justify-between">
          <div>
            <CardTitle className="flex items-center gap-2">
              <BookOpen className="w-5 h-5" />
              Twoje kursy
            </CardTitle>
            <p className="text-sm text-muted-foreground mt-1">
              Zarządzaj kursami i udostępniaj je studentom
            </p>
          </div>
          <Button>
            <Plus className="w-4 h-4 mr-2" />
            Nowy kurs
          </Button>
        </div>
      </CardHeader>

      <CardContent className="pt-6">
        {isLoading ? (
          <div className="flex items-center justify-center py-8">
            <RefreshCw className="w-6 h-6 animate-spin text-muted-foreground" />
          </div>
        ) : courses?.length === 0 ? (
          <div className="text-center py-8">
            <BookOpen className="w-12 h-12 mx-auto mb-4 text-muted-foreground" />
            <p className="text-muted-foreground mb-4">
              Nie masz jeszcze żadnych kursów
            </p>
            <Button onClick={handleCreateCourse}>
              <Plus className="w-4 h-4 mr-2" />
              Utwórz pierwszy kurs
            </Button>
          </div>
        ) : (
          <div className="space-y-6">
            {/* Udostępnione kursy */}
            {sharedCourses.length > 0 && (
              <div>
                <h3 className="text-sm font-medium mb-3 flex items-center gap-2">
                  <Share2 className="w-4 h-4 text-green-600" />
                  Udostępnione studentom ({sharedCourses.length})
                </h3>
                <div className="space-y-3">
                  {sharedCourses.map((course) => (
                    <CourseCard
                      key={course.id}
                      course={course}
                      onToggleSharing={handleToggleSharing}
                      isToggling={toggleSharingMutation.isPending}
                    />
                  ))}
                </div>
              </div>
            )}

            {/* Prywatne kursy */}
            {privateCourses.length > 0 && (
              <div>
                <h3 className="text-sm font-medium mb-3 text-muted-foreground">
                  Prywatne ({privateCourses.length})
                </h3>
                <div className="space-y-3">
                  {privateCourses.map((course) => (
                    <CourseCard
                      key={course.id}
                      course={course}
                      onToggleSharing={handleToggleSharing}
                      isToggling={toggleSharingMutation.isPending}
                    />
                  ))}
                </div>
              </div>
            )}
          </div>
        )}
      </CardContent>
    </Card>
  );
};
