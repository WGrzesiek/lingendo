"use client";

import { useState } from "react";
import {
  UserPlus,
  Search,
  MoreVertical,
  Trash2,
  Mail,
  Crown,
  CheckCircle2,
  Clock,
  XCircle,
  Info,
} from "lucide-react";
import { Button } from "@/components/ui/button";
import { Input } from "@/components/ui/input";
import { Badge } from "@/components/ui/badge";
import { Skeleton } from "@/components/ui/skeleton";
import { Checkbox } from "@/components/ui/checkbox";
import {
  DropdownMenu,
  DropdownMenuContent,
  DropdownMenuItem,
  DropdownMenuTrigger,
} from "@/components/ui/dropdown-menu";
import {
  Table,
  TableBody,
  TableCell,
  TableHead,
  TableHeader,
  TableRow,
} from "@/components/ui/table";
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
import { cn } from "@/lib/utils";
import {
  useGroupMembers,
  useRemoveGroupMembersBatch,
} from "../hooks/useGroupsData";
import type { GroupMember, GroupMemberStatus } from "../types/group.types";

interface GroupMembersListProps {
  groupId: string;
  onAddMembers: () => void;
}

const statusConfig: Record<
  GroupMemberStatus,
  { label: string; icon: React.ElementType; color: string }
> = {
  ACTIVE: { label: "Aktywny", icon: CheckCircle2, color: "text-green-600" },
  PENDING: { label: "Oczekujący", icon: Clock, color: "text-yellow-600" },
  INACTIVE: {
    label: "Nieaktywny",
    icon: XCircle,
    color: "text-muted-foreground",
  },
};

/**
 * Lista członków grupy z możliwością zarządzania
 */
export function GroupMembersList({
  groupId,
  onAddMembers,
}: GroupMembersListProps) {
  const [searchValue, setSearchValue] = useState("");
  const [selectedIds, setSelectedIds] = useState<Set<string>>(new Set());
  const [memberToDelete, setMemberToDelete] = useState<GroupMember | null>(
    null
  );
  const [showBatchDeleteDialog, setShowBatchDeleteDialog] = useState(false);

  const { data: members, isLoading } = useGroupMembers(groupId);
  const removeMembersBatch = useRemoveGroupMembersBatch();

  const handleSearchChange = (value: string) => {
    setSearchValue(value);
  };

  const handleSelectAll = (checked: boolean) => {
    if (checked && members) {
      setSelectedIds(new Set(members.map((m) => m.studentId)));
    } else {
      setSelectedIds(new Set());
    }
  };

  const handleSelectMember = (studentId: string, checked: boolean) => {
    const newSet = new Set(selectedIds);
    if (checked) {
      newSet.add(studentId);
    } else {
      newSet.delete(studentId);
    }
    setSelectedIds(newSet);
  };

  const handleDeleteMember = async () => {
    if (!memberToDelete) return;
    try {
      await removeMembersBatch.mutateAsync({
        groupId,
        studentIds: [memberToDelete.studentId],
      });
      setMemberToDelete(null);
    } catch (error) {
      console.error("Błąd podczas usuwania członka:", error);
    }
  };

  const handleBatchDelete = async () => {
    try {
      await removeMembersBatch.mutateAsync({
        groupId,
        studentIds: Array.from(selectedIds),
      });
      setSelectedIds(new Set());
      setShowBatchDeleteDialog(false);
    } catch (error) {
      console.error("Błąd podczas usuwania członków:", error);
    }
  };

  const formattedDate = (dateStr: string) =>
    new Date(dateStr).toLocaleDateString("pl-PL", {
      day: "numeric",
      month: "short",
      year: "numeric",
    });

  return (
    <div className="space-y-4">
      {/* Nagłówek */}
      <div className="flex items-center justify-between">
        <h2 className="text-lg font-semibold">Członkowie grupy</h2>
        <div className="flex items-center gap-2">
          {selectedIds.size > 0 && (
            <Button
              variant="destructive"
              size="sm"
              onClick={() => setShowBatchDeleteDialog(true)}
            >
              <Trash2 className="size-4 mr-2" />
              Usuń ({selectedIds.size})
            </Button>
          )}
          <Button onClick={onAddMembers}>
            <UserPlus className="size-4 mr-2" />
            Dodaj uczniów
          </Button>
        </div>
      </div>

      {/* Wyszukiwarka */}
      <div className="relative max-w-sm">
        <Search className="absolute left-3 top-1/2 -translate-y-1/2 size-4 text-muted-foreground" />
        <Input
          placeholder="Szukaj ucznia..."
          value={searchValue}
          onChange={(e) => handleSearchChange(e.target.value)}
          className="pl-9"
        />
      </div>

      {/* Tabela członków */}
      {isLoading ? (
        <div className="space-y-3">
          {[...Array(5)].map((_, i) => (
            <Skeleton key={i} className="h-16 w-full" />
          ))}
        </div>
      ) : members && members.length > 0 ? (
        <div className="rounded-md border">
          <Table>
            <TableHeader>
              <TableRow>
                <TableHead className="w-12">
                  <Checkbox
                    checked={
                      members.length > 0 && selectedIds.size === members.length
                    }
                    onCheckedChange={handleSelectAll}
                  />
                </TableHead>
                <TableHead>Uczeń</TableHead>
                <TableHead>Status</TableHead>
                <TableHead>Data dołączenia</TableHead>
                <TableHead className="w-12" />
              </TableRow>
            </TableHeader>
            <TableBody>
              {members.map((member) => {
                const status = statusConfig[member.status];
                const StatusIcon = status.icon;

                return (
                  <TableRow key={member.studentId}>
                    <TableCell>
                      <Checkbox
                        checked={selectedIds.has(member.studentId)}
                        onCheckedChange={(checked) =>
                          handleSelectMember(
                            member.studentId,
                            checked as boolean
                          )
                        }
                      />
                    </TableCell>
                    <TableCell>
                      <div className="flex items-center gap-3">
                        <div className="flex size-10 items-center justify-center rounded-full bg-primary/10 text-primary font-medium">
                          {member.studentName.charAt(0).toUpperCase()}
                        </div>
                        <div>
                          <div className="font-medium flex items-center gap-2">
                            {member.studentName}
                            {member.isOwner && (
                              <Crown className="size-4 text-yellow-500" />
                            )}
                          </div>
                          {member.studentEmail && (
                            <div className="text-sm text-muted-foreground flex items-center gap-1">
                              <Mail className="size-3" />
                              {member.studentEmail}
                            </div>
                          )}
                        </div>
                      </div>
                    </TableCell>
                    <TableCell>
                      <Badge
                        variant="outline"
                        className={cn("gap-1", status.color)}
                      >
                        <StatusIcon className="size-3" />
                        {status.label}
                      </Badge>
                    </TableCell>
                    <TableCell className="text-muted-foreground">
                      {formattedDate(member.joinedAt)}
                    </TableCell>
                    <TableCell>
                      <DropdownMenu>
                        <DropdownMenuTrigger asChild>
                          <Button variant="ghost" size="icon-sm">
                            <MoreVertical className="size-4" />
                          </Button>
                        </DropdownMenuTrigger>
                        <DropdownMenuContent align="end">
                          <DropdownMenuItem
                            onClick={() => setMemberToDelete(member)}
                            className="text-destructive focus:text-destructive"
                            disabled={member.isOwner}
                          >
                            <Trash2 className="size-4 mr-2" />
                            Usuń z grupy
                          </DropdownMenuItem>
                        </DropdownMenuContent>
                      </DropdownMenu>
                    </TableCell>
                  </TableRow>
                );
              })}
            </TableBody>
          </Table>
        </div>
      ) : (
        <div className="flex flex-col items-center justify-center py-12 text-center border rounded-lg">
          <UserPlus className="size-12 text-muted-foreground mb-4" />
          <h3 className="text-lg font-semibold mb-1">Brak członków</h3>
          <p className="text-muted-foreground mb-4">
            {searchValue
              ? "Nie znaleziono uczniów pasujących do wyszukiwania"
              : "Ta grupa nie ma jeszcze żadnych członków"}
          </p>
          {!searchValue && (
            <>
              <Button onClick={onAddMembers} className="mb-4">
                <UserPlus className="size-4 mr-2" />
                Dodaj pierwszego ucznia
              </Button>
              <div className="flex items-start gap-2 max-w-md text-sm text-muted-foreground bg-muted/50 rounded-lg p-3">
                <Info className="size-4 mt-0.5 shrink-0" />
                <p className="text-left">
                  Aby dodać ucznia do grupy, musisz go najpierw dodać jako
                  swojego studenta w zakładce <strong>„Moi studenci”</strong>.
                </p>
              </div>
            </>
          )}
        </div>
      )}

      {/* Dialog usunięcia pojedynczego członka */}
      <AlertDialog
        open={!!memberToDelete}
        onOpenChange={(open) => !open && setMemberToDelete(null)}
      >
        <AlertDialogContent>
          <AlertDialogHeader>
            <AlertDialogTitle>Usuń ucznia z grupy</AlertDialogTitle>
            <AlertDialogDescription>
              Czy na pewno chcesz usunąć{" "}
              <strong>{memberToDelete?.studentName}</strong> z tej grupy?
              <br />
              Uczeń straci dostęp do kursów udostępnionych grupie.
            </AlertDialogDescription>
          </AlertDialogHeader>
          <AlertDialogFooter>
            <AlertDialogCancel disabled={removeMembersBatch.isPending}>
              Anuluj
            </AlertDialogCancel>
            <AlertDialogAction
              onClick={handleDeleteMember}
              disabled={removeMembersBatch.isPending}
              className="bg-destructive text-white hover:bg-destructive/90"
            >
              {removeMembersBatch.isPending ? "Usuwanie..." : "Usuń z grupy"}
            </AlertDialogAction>
          </AlertDialogFooter>
        </AlertDialogContent>
      </AlertDialog>

      {/* Dialog usunięcia wielu członków */}
      <AlertDialog
        open={showBatchDeleteDialog}
        onOpenChange={setShowBatchDeleteDialog}
      >
        <AlertDialogContent>
          <AlertDialogHeader>
            <AlertDialogTitle>Usuń wybranych uczniów</AlertDialogTitle>
            <AlertDialogDescription>
              Czy na pewno chcesz usunąć <strong>{selectedIds.size}</strong>{" "}
              uczniów z tej grupy?
              <br />
              Uczniowie stracą dostęp do kursów udostępnionych grupie.
            </AlertDialogDescription>
          </AlertDialogHeader>
          <AlertDialogFooter>
            <AlertDialogCancel disabled={removeMembersBatch.isPending}>
              Anuluj
            </AlertDialogCancel>
            <AlertDialogAction
              onClick={handleBatchDelete}
              disabled={removeMembersBatch.isPending}
              className="bg-destructive text-white hover:bg-destructive/90"
            >
              {removeMembersBatch.isPending
                ? "Usuwanie..."
                : `Usuń ${selectedIds.size} uczniów`}
            </AlertDialogAction>
          </AlertDialogFooter>
        </AlertDialogContent>
      </AlertDialog>
    </div>
  );
}
