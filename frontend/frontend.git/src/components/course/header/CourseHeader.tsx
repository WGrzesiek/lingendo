import { Card } from "@/components/ui/card";
import { Button } from "@/components/ui/button";
import { Badge } from "@/components/ui/badge";
import { ArrowLeft, Settings, Share2, Lock, Globe } from "lucide-react";

interface CourseHeaderProps {
  course: {
    id: string;
    title: string;
    description: string;
    isPublic: boolean;
    isOwner: boolean;
    createdBy: string;
  };
}

/**
 * Nagłówek strony kursu z tytułem, opisem i akcjami
 */
export const CourseHeader = ({ course }: CourseHeaderProps) => {
  return (
    <div className="space-y-4">
      <Button variant="ghost" className="gap-2">
        <ArrowLeft className="w-4 h-4" />
        Powrót do dashboardu
      </Button>

      <Card className="p-6">
        <div className="flex flex-col gap-4 md:flex-row md:items-start md:justify-between">
          <div className="flex-1">
            <div className="flex flex-wrap items-center gap-3 mb-2">
              <h1 className="text-3xl font-bold">{course.title}</h1>
              {course.isPublic ? (
                <Badge className="gap-1">
                  <Globe className="w-3 h-3" />
                  Publiczny
                </Badge>
              ) : (
                <Badge variant="secondary" className="gap-1">
                  <Lock className="w-3 h-3" />
                  Prywatny
                </Badge>
              )}
              {course.isOwner && <Badge variant="outline">Twój kurs</Badge>}
            </div>
            <p className="text-muted-foreground mb-4">{course.description}</p>
            <p className="text-sm text-muted-foreground">
              Utworzony przez: <strong>{course.createdBy}</strong>
            </p>
          </div>

          <div className="flex flex-wrap gap-2 md:flex-nowrap md:flex-shrink-0">
            {course.isOwner && (
              <>
                <Button
                  variant="outline"
                  size="sm"
                  className="gap-2 flex-1 sm:flex-initial"
                >
                  <Settings className="w-4 h-4" />
                  <span className="hidden sm:inline">Ustawienia</span>
                  <span className="sm:hidden">Ustaw.</span>
                </Button>
                <Button
                  variant="outline"
                  size="sm"
                  className="gap-2 flex-1 sm:flex-initial"
                >
                  <Share2 className="w-4 h-4" />
                  <span className="hidden sm:inline">Udostępnij</span>
                  <span className="sm:hidden">Udost.</span>
                </Button>
              </>
            )}
            <Button size="sm" className="flex-1 sm:flex-initial">
              Rozpocznij naukę
            </Button>
          </div>
        </div>
      </Card>
    </div>
  );
};
