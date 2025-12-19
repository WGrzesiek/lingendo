import { Card, CardContent } from "@/components/ui/card";
import { Button } from "@/components/ui/button";
import { Badge } from "@/components/ui/badge";
import { Users, Star, BookOpen, Calendar, User } from "lucide-react";
import { ICommunityCourse } from "@/features/community/types/community-course.types";
import { timeAgo } from "@/lib/timeAgo";

interface CommunityCourseCardProps {
  course: ICommunityCourse;
  onEnroll?: (courseId: string) => void;
}

/**
 * Karta kursu społeczności - taki sam styl jak na dashboard
 */
export const CommunityCourseCard = ({
  course,
  onEnroll,
}: CommunityCourseCardProps) => {
  const getDifficultyBadge = (difficulty: ICommunityCourse["difficulty"]) => {
    const variants = {
      EASY: { label: "Łatwy", variant: "default" as const },
      MEDIUM: {
        label: "Średni",
        variant: "secondary" as const,
      },
      HARD: { label: "Trudny", variant: "outline" as const },
    };

    const { label, variant } = variants[difficulty];
    return <Badge variant={variant}>{label}</Badge>;
  };

  const handleEnroll = () => {
    if (onEnroll) {
      onEnroll(course.id);
    }
  };

  return (
    <Card className="hover:shadow-md hover:border-primary/50 transition-all">
      <CardContent className="p-4">
        <div className="flex flex-col gap-3">
          {/* Tytuł i opis */}
          <div>
            <h3 className="font-semibold text-lg mb-1 line-clamp-1">
              {course.title}
            </h3>
            <p className="text-sm text-muted-foreground line-clamp-2">
              {course.description}
            </p>
          </div>

          {/* Badge'y */}
          <div className="flex flex-wrap items-center gap-2">
            <Badge variant="secondary" className="text-xs">
              {course.category}
            </Badge>
            {getDifficultyBadge(course.difficulty)}
          </div>

          {/* Statystyki */}
          <div className="flex flex-wrap items-center gap-4 text-sm text-muted-foreground">
            <span className="flex items-center gap-1">
              <Users className="w-4 h-4" />
              {course.studentsCount.toLocaleString()}
            </span>
            <span className="flex items-center gap-1">
              <Star className="w-4 h-4 fill-yellow-500 text-yellow-500" />
              {course.rating} ({course.ratingsCount})
            </span>
            <span className="flex items-center gap-1">
              <BookOpen className="w-4 h-4" />
              {course.lessonsCount} lekcji
            </span>
          </div>

          {/* Metadata */}
          <div className="flex flex-col gap-1 text-xs text-muted-foreground pt-1 border-t">
            <span className="flex items-center gap-1">
              <User className="w-3 h-3" />
              Autor: {course.author}
            </span>
            <span className="flex items-center gap-1">
              <Calendar className="w-3 h-3" />
              Utworzono: {timeAgo(course.createdAt)}
            </span>
          </div>

          {/* Przycisk zapisu */}
          <Button className="w-full mt-2" size="sm" onClick={handleEnroll}>
            Dołącz do kursu
          </Button>
        </div>
      </CardContent>
    </Card>
  );
};
