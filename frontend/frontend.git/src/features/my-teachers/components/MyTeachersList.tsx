"use client";

import { useState } from "react";
import {
  GraduationCap,
  MoreVertical,
  LogOut,
  Mail,
  Calendar,
  UserX,
} from "lucide-react";
import { Card, CardContent, CardHeader, CardTitle } from "@/components/ui/card";
import { Button } from "@/components/ui/button";
import { Badge } from "@/components/ui/badge";
import { Skeleton } from "@/components/ui/skeleton";
import {
  DropdownMenu,
  DropdownMenuContent,
  DropdownMenuItem,
  DropdownMenuTrigger,
} from "@/components/ui/dropdown-menu";
import {
  AlertDialog,
  AlertDialogAction,
  AlertDialogCancel,
  AlertDialogContent,
  AlertDialogDescription,
  AlertDialogFooter,
  AlertDialogHeader,
  AlertDialogTitle,
} from "@/components/ui/alert-dialog";
import { useMyTeachers, useLeaveTeacher } from "../hooks/useMyTeachersData";
import type { Teacher, TeacherStudentStatus } from "../types";
import {timee} from "@/lib/time";

const statusConfig: Record<
  TeacherStudentStatus,
  { label: string; variant: "default" | "secondary" | "destructive" }
> = {
  ACTIVE: { label: "Aktywny", variant: "default" },
  BLOCKED: { label: "Zablokowany", variant: "destructive" },
  REMOVED: { label: "Usunięty", variant: "secondary" },
};

interface TeacherCardProps {
  teacher: Teacher;
  onLeave: (teacherId: string) => void;
  isLeaving: boolean;
}

function TeacherCard({ teacher, onLeave, isLeaving }: TeacherCardProps) {
  const [showLeaveDialog, setShowLeaveDialog] = useState(false);

  const displayName =
    teacher.firstName && teacher.lastName
      ? `${teacher.firstName} ${teacher.lastName}`
      : teacher.username;

  const initials = displayName
    .split(" ")
    .map((n) => n[0])
    .join("")
    .toUpperCase()
    .slice(0, 2);

  const { label, variant } = statusConfig[teacher.status];

  return (
    <>
      <Card className="group hover:shadow-md transition-shadow">
        <CardContent className="p-4">
          <div className="flex items-start gap-4">
            {/* Avatar */}
            <div className="flex size-12 shrink-0 items-center justify-center rounded-full bg-primary/10 text-primary font-semibold text-lg">
              {initials}
            </div>

            {/* Info */}
            <div className="flex-1 min-w-0">
              <div className="flex items-center justify-between gap-2">
                <h3 className="font-semibold truncate">{displayName}</h3>
                <div className="flex items-center gap-2">
                  <Badge variant={variant} className="shrink-0">
                    {label}
                  </Badge>
                  <DropdownMenu>
                    <DropdownMenuTrigger asChild>
                      <Button
                        variant="ghost"
                        size="icon"
                        className="size-8 opacity-0 group-hover:opacity-100 transition-opacity"
                      >
                        <MoreVertical className="size-4" />
                      </Button>
                    </DropdownMenuTrigger>
                    <DropdownMenuContent align="end">
                      <DropdownMenuItem
                        className="text-destructive focus:text-destructive"
                        onClick={() => setShowLeaveDialog(true)}
                      >
                        <LogOut className="size-4 mr-2" />
                        Opuść nauczyciela
                      </DropdownMenuItem>
                    </DropdownMenuContent>
                  </DropdownMenu>
                </div>
              </div>

              <p className="text-sm text-muted-foreground truncate">
                @{teacher.username}
              </p>

              <div className="flex flex-wrap items-center gap-x-4 gap-y-1 mt-2 text-xs text-muted-foreground">
                {teacher.email && (
                  <div className="flex items-center gap-1">
                    <Mail className="size-3" />
                    <span className="truncate max-w-[200px]">
                      {teacher.email}
                    </span>
                  </div>
                )}
                <div className="flex items-center gap-1">
                  <Calendar className="size-3" />
                  <span>Dołączono {timee.formatDate(teacher.joinedAt)}</span>
                </div>
              </div>
            </div>
          </div>
        </CardContent>
      </Card>

      {/* Dialog potwierdzenia opuszczenia */}
      <AlertDialog open={showLeaveDialog} onOpenChange={setShowLeaveDialog}>
        <AlertDialogContent>
          <AlertDialogHeader>
            <AlertDialogTitle>Opuścić nauczyciela?</AlertDialogTitle>
            <AlertDialogDescription>
              Czy na pewno chcesz opuścić nauczyciela{" "}
              <strong>{displayName}</strong>? Stracisz dostęp do jego materiałów
              i grup. Możesz dołączyć ponownie używając nowego kodu zaproszenia.
            </AlertDialogDescription>
          </AlertDialogHeader>
          <AlertDialogFooter>
            <AlertDialogCancel disabled={isLeaving}>Anuluj</AlertDialogCancel>
            <AlertDialogAction
              onClick={() => {
                onLeave(teacher.teacherId);
                setShowLeaveDialog(false);
              }}
              disabled={isLeaving}
              className="bg-destructive text-destructive-foreground hover:bg-destructive/90"
            >
              {isLeaving ? "Opuszczanie..." : "Opuść"}
            </AlertDialogAction>
          </AlertDialogFooter>
        </AlertDialogContent>
      </AlertDialog>
    </>
  );
}

function TeacherCardSkeleton() {
  return (
    <Card>
      <CardContent className="p-4">
        <div className="flex items-start gap-4">
          <Skeleton className="size-12 rounded-full" />
          <div className="flex-1 space-y-2">
            <Skeleton className="h-5 w-32" />
            <Skeleton className="h-4 w-24" />
            <Skeleton className="h-3 w-48" />
          </div>
        </div>
      </CardContent>
    </Card>
  );
}

interface MyTeachersListProps {
  onAddTeacher: () => void;
}

/**
 * Lista nauczycieli ucznia
 */
export function MyTeachersList({ onAddTeacher }: MyTeachersListProps) {
  const { data, isLoading, error } = useMyTeachers();
  const leaveTeacher = useLeaveTeacher();

  const handleLeave = async (teacherId: string) => {
    try {
      await leaveTeacher.mutateAsync(teacherId);
    } catch (error) {
      console.error("Błąd podczas opuszczania nauczyciela:", error);
    }
  };

  if (isLoading) {
    return (
      <Card>
        <CardHeader>
          <CardTitle className="flex items-center gap-2">
            <GraduationCap className="size-5" />
            Moi nauczyciele
          </CardTitle>
        </CardHeader>
        <CardContent className="space-y-3">
          {[...Array(3)].map((_, i) => (
            <TeacherCardSkeleton key={i} />
          ))}
        </CardContent>
      </Card>
    );
  }

  if (error) {
    return (
      <Card>
        <CardHeader>
          <CardTitle className="flex items-center gap-2">
            <GraduationCap className="size-5" />
            Moi nauczyciele
          </CardTitle>
        </CardHeader>
        <CardContent>
          <div className="text-center py-8 text-destructive">
            Wystąpił błąd podczas ładowania listy nauczycieli
          </div>
        </CardContent>
      </Card>
    );
  }

  const teachers = data?.content ?? [];

  return (
    <Card>
      <CardHeader className="flex flex-row items-center justify-between">
        <CardTitle className="flex items-center gap-2">
          <GraduationCap className="size-5" />
          Moi nauczyciele ({teachers.length})
        </CardTitle>
        <Button onClick={onAddTeacher} size="sm">
          Dołącz do nauczyciela
        </Button>
      </CardHeader>
      <CardContent>
        {teachers.length === 0 ? (
          <div className="text-center py-12">
            <UserX className="size-12 mx-auto text-muted-foreground/50 mb-4" />
            <h3 className="text-lg font-medium mb-2">Brak nauczycieli</h3>
            <p className="text-muted-foreground mb-4">
              Nie jesteś jeszcze przypisany do żadnego nauczyciela.
              <br />
              Poproś nauczyciela o kod zaproszenia, aby dołączyć.
            </p>
            <Button onClick={onAddTeacher}>Wprowadź kod zaproszenia</Button>
          </div>
        ) : (
          <div className="space-y-3">
            {teachers.map((teacher) => (
              <TeacherCard
                key={teacher.relationId}
                teacher={teacher}
                onLeave={handleLeave}
                isLeaving={leaveTeacher.isPending}
              />
            ))}
          </div>
        )}
      </CardContent>
    </Card>
  );
}
