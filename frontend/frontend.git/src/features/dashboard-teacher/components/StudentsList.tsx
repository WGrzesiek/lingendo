"use client";

import { useState } from "react";
import { Card, CardHeader, CardTitle, CardContent } from "@/components/ui/card";
import { Button } from "@/components/ui/button";
import { Input } from "@/components/ui/input";
import { Badge } from "@/components/ui/badge";
import {
  Select,
  SelectContent,
  SelectItem,
  SelectTrigger,
  SelectValue,
} from "@/components/ui/select";
import {
  DropdownMenu,
  DropdownMenuContent,
  DropdownMenuItem,
  DropdownMenuSeparator,
  DropdownMenuTrigger,
} from "@/components/ui/dropdown-menu";
import {
  Search,
  MoreVertical,
  UserX,
  Ban,
  CheckCircle,
  Mail,
  Clock,
  ChevronLeft,
  ChevronRight,
} from "lucide-react";
import {
  useTeacherStudents,
  useBlockStudent,
  useUnblockStudent,
  useRemoveStudent,
} from "../hooks";
import type { StudentResponse } from "../types/api";
import { cn } from "@/lib/utils";
import {timee} from "@/lib/time";

type StudentStatus = "ACTIVE" | "BLOCKED";



/**
 * Komponent wiersza studenta
 */
const StudentRow = ({
  student,
  onBlock,
  onUnblock,
  onRemove,
  onViewDetails,
}: {
  student: StudentResponse;
  onBlock: (id: string) => void;
  onUnblock: (id: string) => void;
  onRemove: (id: string) => void;
  onViewDetails: (id: string) => void;
}) => {
  const isBlocked = student.status === "BLOCKED";

  return (
    <div
      className={cn(
        "flex items-center gap-4 p-4 border-b last:border-b-0 hover:bg-muted/50 transition-colors",
        isBlocked && "opacity-60"
      )}
    >
      {/* Avatar i podstawowe info */}
      <div className="flex items-center gap-3 flex-1 min-w-0">
        <div className="w-10 h-10 rounded-full bg-primary/10 flex items-center justify-center flex-shrink-0">
          <span className="text-sm font-semibold text-primary">
            {student.username.charAt(0).toUpperCase()}
          </span>
        </div>
        <div className="min-w-0">
          <div className="flex items-center gap-2">
            <span className="font-medium truncate">{student.username}</span>
            {isBlocked && (
              <Badge variant="destructive" className="text-xs">
                Zablokowany
              </Badge>
            )}
          </div>
          {student.email && (
            <p className="text-sm text-muted-foreground truncate">
              {student.email}
            </p>
          )}
        </div>
      </div>

      {/* Info o dołączeniu */}
      <div className="hidden md:flex items-center gap-6 text-sm">
        <div
          className="flex items-center gap-1.5 text-muted-foreground"
          title="Data dołączenia"
        >
          <Clock className="w-4 h-4" />
          <span>{timee.formatDateTime(student.joinedAt)}</span>
        </div>
      </div>

      {/* Akcje */}
      <DropdownMenu>
        <DropdownMenuTrigger asChild>
          <Button variant="ghost" size="icon">
            <MoreVertical className="w-4 h-4" />
          </Button>
        </DropdownMenuTrigger>
        <DropdownMenuContent align="end">
          <DropdownMenuItem onClick={() => onViewDetails(student.studentId)}>
            Zobacz szczegóły
          </DropdownMenuItem>
          <DropdownMenuItem>
            <Mail className="w-4 h-4 mr-2" />
            Wyślij wiadomość
          </DropdownMenuItem>
          <DropdownMenuSeparator />
          {isBlocked ? (
            <DropdownMenuItem onClick={() => onUnblock(student.studentId)}>
              <CheckCircle className="w-4 h-4 mr-2 text-green-500" />
              Odblokuj
            </DropdownMenuItem>
          ) : (
            <DropdownMenuItem onClick={() => onBlock(student.studentId)}>
              <Ban className="w-4 h-4 mr-2 text-orange-500" />
              Zablokuj
            </DropdownMenuItem>
          )}
          <DropdownMenuItem
            onClick={() => onRemove(student.studentId)}
            className="text-destructive"
          >
            <UserX className="w-4 h-4 mr-2" />
            Usuń ze studentów
          </DropdownMenuItem>
        </DropdownMenuContent>
      </DropdownMenu>
    </div>
  );
};

/**
 * Komponent listy studentów nauczyciela
 */
export const StudentsList = ({
  onViewDetails,
}: {
  onViewDetails?: (studentId: string) => void;
}) => {
  const [page, setPage] = useState(0);
  const [pageSize] = useState(10);
  const [searchQuery, setSearchQuery] = useState("");
  const [statusFilter, setStatusFilter] = useState<StudentStatus | "ALL">(
    "ALL"
  );

  const { data, isLoading, error } = useTeacherStudents(page, pageSize);
  const blockMutation = useBlockStudent();
  const unblockMutation = useUnblockStudent();
  const removeMutation = useRemoveStudent();

  const handleBlock = (studentId: string) => {
    blockMutation.mutate(studentId);
  };

  const handleUnblock = (studentId: string) => {
    unblockMutation.mutate(studentId);
  };

  const handleRemove = (studentId: string) => {
    if (window.confirm("Czy na pewno chcesz usunąć tego studenta?")) {
      removeMutation.mutate(studentId);
    }
  };

  const handleViewDetails = (studentId: string) => {
    onViewDetails?.(studentId);
  };

  const filteredStudents = data?.content?.filter((student) => {
    const matchesSearch =
      !searchQuery ||
      student.username.toLowerCase().includes(searchQuery.toLowerCase()) ||
      student.email?.toLowerCase().includes(searchQuery.toLowerCase());

    const matchesStatus =
      statusFilter === "ALL" ||
      (statusFilter === "BLOCKED" && student.status === "BLOCKED") ||
      (statusFilter === "ACTIVE" && student.status === "ACTIVE");

    return matchesSearch && matchesStatus;
  });

  if (error) {
    return (
      <Card className="p-6">
        <p className="text-destructive">Błąd ładowania studentów</p>
      </Card>
    );
  }

  const totalElements = data?.totalElements || 0;
  const totalPages = data?.totalPages || 0;

  return (
    <Card>
      <CardHeader className="border-b">
        <div className="flex flex-col sm:flex-row sm:items-center justify-between gap-4">
          <CardTitle>Studenci ({totalElements})</CardTitle>
          <div className="flex items-center gap-2">
            {/* Wyszukiwarka */}
            <div className="relative">
              <Search className="absolute left-3 top-1/2 -translate-y-1/2 w-4 h-4 text-muted-foreground" />
              <Input
                placeholder="Szukaj studentów..."
                className="pl-9 w-48"
                value={searchQuery}
                onChange={(e) => {
                  setSearchQuery(e.target.value);
                }}
              />
            </div>

            {/* Filtr statusu */}
            <Select
              value={statusFilter}
              onValueChange={(value) => {
                setStatusFilter(value as StudentStatus | "ALL");
              }}
            >
              <SelectTrigger className="w-36">
                <SelectValue placeholder="Status" />
              </SelectTrigger>
              <SelectContent>
                <SelectItem value="ALL">Wszyscy</SelectItem>
                <SelectItem value="ACTIVE">Aktywni</SelectItem>
                <SelectItem value="BLOCKED">Zablokowani</SelectItem>
              </SelectContent>
            </Select>
          </div>
        </div>
      </CardHeader>

      <CardContent className="p-0">
        {isLoading ? (
          <div className="p-8 text-center">
            <div className="w-8 h-8 border-4 border-primary border-t-transparent rounded-full animate-spin mx-auto mb-2" />
            <p className="text-muted-foreground">Ładowanie...</p>
          </div>
        ) : !filteredStudents || filteredStudents.length === 0 ? (
          <div className="p-8 text-center">
            <p className="text-muted-foreground">
              {searchQuery || statusFilter !== "ALL"
                ? "Nie znaleziono studentów pasujących do filtrów"
                : "Nie masz jeszcze żadnych studentów"}
            </p>
          </div>
        ) : (
          <>
            <div className="divide-y">
              {filteredStudents.map((student) => (
                <StudentRow
                  key={student.studentId}
                  student={student}
                  onBlock={handleBlock}
                  onUnblock={handleUnblock}
                  onRemove={handleRemove}
                  onViewDetails={handleViewDetails}
                />
              ))}
            </div>

            {/* Paginacja */}
            {totalPages > 1 && (
              <div className="flex items-center justify-between p-4 border-t">
                <p className="text-sm text-muted-foreground">
                  Strona {page + 1} z {totalPages}
                </p>
                <div className="flex items-center gap-2">
                  <Button
                    variant="outline"
                    size="sm"
                    disabled={page <= 0}
                    onClick={() => setPage((p) => p - 1)}
                  >
                    <ChevronLeft className="w-4 h-4" />
                  </Button>
                  <Button
                    variant="outline"
                    size="sm"
                    disabled={page >= totalPages - 1}
                    onClick={() => setPage((p) => p + 1)}
                  >
                    <ChevronRight className="w-4 h-4" />
                  </Button>
                </div>
              </div>
            )}
          </>
        )}
      </CardContent>
    </Card>
  );
};
