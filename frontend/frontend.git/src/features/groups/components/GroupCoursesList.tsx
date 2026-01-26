"use client";

import { BookOpen, Users, Clock } from "lucide-react";
import { Card, CardContent, CardHeader, CardTitle } from "@/components/ui/card";
import { Skeleton } from "@/components/ui/skeleton";
import type { GroupCourseStats } from "../types/group.types";
import {timee} from "@/lib/time";

interface GroupCoursesListProps {
  courses: GroupCourseStats[] | undefined;
  isLoading: boolean;
}

export function GroupCoursesList({
  courses,
  isLoading,
}: GroupCoursesListProps) {
  if (isLoading) {
    return (
      <Card>
        <CardHeader>
          <CardTitle className="text-lg">Udostępnione kursy</CardTitle>
        </CardHeader>
        <CardContent className="space-y-3">
          {[...Array(3)].map((_, i) => (
            <div key={i} className="p-4 border rounded-lg">
              <Skeleton className="h-5 w-48 mb-2" />
              <Skeleton className="h-4 w-32" />
            </div>
          ))}
        </CardContent>
      </Card>
    );
  }

  if (!courses || courses.length === 0) {
    return (
      <Card>
        <CardHeader>
          <CardTitle className="text-lg">Udostępnione kursy</CardTitle>
        </CardHeader>
        <CardContent>
          <div className="text-center py-8 text-muted-foreground">
            <BookOpen className="size-8 mx-auto mb-2 opacity-50" />
            <p>Brak udostępnionych kursów</p>
            <p className="text-sm mt-1">
              Udostępnij kursy grupie, aby śledzić postępy
            </p>
          </div>
        </CardContent>
      </Card>
    );
  }

  return (
    <Card>
      <CardHeader>
        <CardTitle className="text-lg">Udostępnione kursy</CardTitle>
      </CardHeader>
      <CardContent className="space-y-3">
        {courses.map((course) => {
          return (
            <div
              key={course.deckId}
              className="p-4 border rounded-lg hover:bg-muted/30 transition-colors"
            >
              <h4 className="font-medium">{course.deckName}</h4>
              <div className="flex flex-wrap gap-4 text-sm text-muted-foreground mt-2">
                <div className="flex items-center gap-1.5">
                  <Users className="size-4" />
                  <span>{course.studentsCount} uczniów</span>
                </div>
                <div className="flex items-center gap-1.5">
                  <Clock className="size-4" />
                  <span>{timee.formatDateTime(course.lastActivity)}</span>
                </div>
              </div>
            </div>
          );
        })}
      </CardContent>
    </Card>
  );
}
