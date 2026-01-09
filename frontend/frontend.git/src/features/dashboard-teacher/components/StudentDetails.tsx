"use client";

import { Card, CardHeader, CardTitle, CardContent } from "@/components/ui/card";
import { Button } from "@/components/ui/button";
import { Badge } from "@/components/ui/badge";
import {
  ArrowLeft,
  TrendingUp,
  Mail,
  Ban,
  UserX,
  CheckCircle,
} from "lucide-react";
import {
  useTeacherStudents,
  useBlockStudent,
  useUnblockStudent,
  useRemoveStudent,
} from "../hooks";
import { timee } from "@/lib/time";

interface StudentDetailsProps {
  studentId: string;
  onBack: () => void;
}

/**
 * Komponent szczegółów studenta
 */
export const StudentDetails = ({ studentId, onBack }: StudentDetailsProps) => {
  const { data: studentsData, isLoading } = useTeacherStudents();
  const blockMutation = useBlockStudent();
  const unblockMutation = useUnblockStudent();
  const removeMutation = useRemoveStudent();

  const student = studentsData?.content.find(
    (s) => s.studentId === studentId || s.relationId === studentId
  );

  const handleBlock = () => {
    if (student) {
      blockMutation.mutate(student.studentId);
    }
  };

  const handleUnblock = () => {
    if (student) {
      unblockMutation.mutate(student.studentId);
    }
  };

  const handleRemove = () => {
    if (
      student &&
      window.confirm("Czy na pewno chcesz usunąć tego studenta?")
    ) {
      removeMutation.mutate(student.studentId, {
        onSuccess: () => onBack(),
      });
    }
  };

  if (isLoading) {
    return (
      <div className="flex items-center justify-center h-64">
        <div className="w-8 h-8 border-4 border-primary border-t-transparent rounded-full animate-spin" />
      </div>
    );
  }

  if (!student) {
    return (
      <Card className="p-6">
        <p className="text-destructive">Nie znaleziono studenta</p>
        <Button variant="outline" onClick={onBack} className="mt-4">
          <ArrowLeft className="w-4 h-4 mr-2" />
          Wróć
        </Button>
      </Card>
    );
  }

  const isBlocked = student.status === "BLOCKED";
  const displayName =
    student.firstName && student.lastName
      ? `${student.firstName} ${student.lastName}`
      : student.username;

  return (
    <div className="space-y-6">
      {/* Nagłówek */}
      <div className="flex items-start justify-between flex-wrap gap-4">
        <div className="flex items-center gap-4">
          <Button variant="ghost" size="icon" onClick={onBack}>
            <ArrowLeft className="w-5 h-5" />
          </Button>
          <div className="w-16 h-16 rounded-full bg-primary/10 flex items-center justify-center">
            <span className="text-2xl font-bold text-primary">
              {displayName.charAt(0).toUpperCase()}
            </span>
          </div>
          <div>
            <div className="flex items-center gap-2">
              <h1 className="text-2xl font-bold">{displayName}</h1>
              {isBlocked && <Badge variant="destructive">Zablokowany</Badge>}
            </div>
            {student.email && (
              <p className="text-muted-foreground">{student.email}</p>
            )}
            <p className="text-sm text-muted-foreground">
              Dołączył {timee.formatDate(student.joinedAt)}
            </p>
          </div>
        </div>

        <div className="flex items-center gap-2 flex-wrap">
          <Button variant="outline">
            <Mail className="w-4 h-4 mr-2" />
            Wyślij wiadomość
          </Button>
          {isBlocked ? (
            <Button
              variant="outline"
              onClick={handleUnblock}
              disabled={unblockMutation.isPending}
            >
              <CheckCircle className="w-4 h-4 mr-2" />
              Odblokuj
            </Button>
          ) : (
            <Button
              variant="outline"
              onClick={handleBlock}
              disabled={blockMutation.isPending}
            >
              <Ban className="w-4 h-4 mr-2" />
              Zablokuj
            </Button>
          )}
          <Button
            variant="destructive"
            onClick={handleRemove}
            disabled={removeMutation.isPending}
          >
            <UserX className="w-4 h-4 mr-2" />
            Usuń
          </Button>
        </div>
      </div>

      {/* Informacje o studencie */}
      <Card>
        <CardHeader>
          <CardTitle>Informacje</CardTitle>
        </CardHeader>
        <CardContent>
          <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
            <div>
              <p className="text-sm text-muted-foreground">Nazwa użytkownika</p>
              <p className="font-medium">{student.username}</p>
            </div>
            {student.firstName && (
              <div>
                <p className="text-sm text-muted-foreground">Imię</p>
                <p className="font-medium">{student.firstName}</p>
              </div>
            )}
            {student.lastName && (
              <div>
                <p className="text-sm text-muted-foreground">Nazwisko</p>
                <p className="font-medium">{student.lastName}</p>
              </div>
            )}
            {student.email && (
              <div>
                <p className="text-sm text-muted-foreground">Email</p>
                <p className="font-medium">{student.email}</p>
              </div>
            )}
            <div>
              <p className="text-sm text-muted-foreground">Status</p>
              <Badge variant={isBlocked ? "destructive" : "default"}>
                {student.status === "ACTIVE"
                  ? "Aktywny"
                  : student.status === "BLOCKED"
                  ? "Zablokowany"
                  : "Usunięty"}
              </Badge>
            </div>
            <div>
              <p className="text-sm text-muted-foreground">Data dołączenia</p>
              <p className="font-medium">
                {timee.formatDate(student.joinedAt)}
              </p>
            </div>
          </div>
        </CardContent>
      </Card>

      {/* Placeholder dla statystyk - do rozbudowy gdy backend będzie gotowy */}
      <Card>
        <CardHeader>
          <CardTitle className="flex items-center gap-2">
            <TrendingUp className="w-5 h-5" />
            Statystyki nauki
          </CardTitle>
        </CardHeader>
        <CardContent>
          <p className="text-muted-foreground text-center py-8">
            Statystyki nauki studenta będą dostępne wkrótce
          </p>
        </CardContent>
      </Card>
    </div>
  );
};
