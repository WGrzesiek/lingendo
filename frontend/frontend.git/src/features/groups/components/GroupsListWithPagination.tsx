"use client";

import { Plus, Users, Archive, ChevronLeft, ChevronRight } from "lucide-react";
import { Button } from "@/components/ui/button";
import { Switch } from "@/components/ui/switch";
import { Label } from "@/components/ui/label";
import { Skeleton } from "@/components/ui/skeleton";
import { GroupCard } from "./GroupCard";
import type { Group } from "../types/group.types";

interface GroupsListWithPaginationProps {
  groups: Group[] | undefined;
  isLoading: boolean;
  onCreateGroup: () => void;
  onEditGroup: (group: Group) => void;
  onDeleteGroup: (group: Group) => void;
  includeArchived: boolean;
  onIncludeArchivedChange: (value: boolean) => void;
  page: number;
  totalPages: number;
  totalElements: number;
  onPageChange: (page: number) => void;
}

/**
 * Lista grup z paginacją i przełącznikiem "Pokaż zarchiwizowane"
 */
export function GroupsListWithPagination({
  groups,
  isLoading,
  onCreateGroup,
  onEditGroup,
  onDeleteGroup,
  includeArchived,
  onIncludeArchivedChange,
  page,
  totalPages,
  totalElements,
  onPageChange,
}: GroupsListWithPaginationProps) {
  return (
    <div className="space-y-6">
      {/* Nagłówek z przyciskiem tworzenia */}
      <div className="flex items-center justify-between">
        <div>
          <h1 className="text-2xl font-bold tracking-tight">Moje grupy</h1>
          <p className="text-muted-foreground">
            Zarządzaj grupami uczniów i udostępniaj im kursy
          </p>
        </div>
        <Button onClick={onCreateGroup}>
          <Plus className="size-4 mr-2" />
          Utwórz grupę
        </Button>
      </div>

      {/* Filtry */}
      <div className="flex items-center justify-between">
        <div className="flex items-center gap-2">
          <Switch
            id="include-archived"
            checked={includeArchived}
            onCheckedChange={onIncludeArchivedChange}
          />
          <Label
            htmlFor="include-archived"
            className="flex items-center gap-2 cursor-pointer"
          >
            <Archive className="size-4 text-muted-foreground" />
            Pokaż zarchiwizowane
          </Label>
        </div>
        {totalElements > 0 && (
          <p className="text-sm text-muted-foreground">
            Łącznie: {totalElements} {totalElements === 1 ? "grupa" : "grup"}
          </p>
        )}
      </div>

      {/* Lista grup */}
      {isLoading ? (
        <div className="grid gap-4 md:grid-cols-2 lg:grid-cols-3">
          {[...Array(6)].map((_, i) => (
            <Skeleton key={i} className="h-40 rounded-xl" />
          ))}
        </div>
      ) : groups && groups.length > 0 ? (
        <>
          <div className="grid gap-4 md:grid-cols-2 lg:grid-cols-3">
            {groups.map((group) => (
              <GroupCard
                key={group.id}
                group={group}
                onEdit={onEditGroup}
                onDelete={onDeleteGroup}
              />
            ))}
          </div>

          {/* Paginacja */}
          {totalPages > 1 && (
            <div className="flex items-center justify-center gap-2 pt-4">
              <Button
                variant="outline"
                size="sm"
                onClick={() => onPageChange(page - 1)}
                disabled={page === 0}
              >
                <ChevronLeft className="size-4 mr-1" />
                Poprzednia
              </Button>
              <span className="text-sm text-muted-foreground px-4">
                Strona {page + 1} z {totalPages}
              </span>
              <Button
                variant="outline"
                size="sm"
                onClick={() => onPageChange(page + 1)}
                disabled={page >= totalPages - 1}
              >
                Następna
                <ChevronRight className="size-4 ml-1" />
              </Button>
            </div>
          )}
        </>
      ) : (
        <div className="flex flex-col items-center justify-center py-12 text-center">
          <div className="rounded-full bg-muted p-4 mb-4">
            <Users className="size-8 text-muted-foreground" />
          </div>
          <h3 className="text-lg font-semibold mb-1">Brak grup</h3>
          <p className="text-muted-foreground mb-4 max-w-sm">
            {includeArchived
              ? "Nie masz żadnych grup (w tym zarchiwizowanych)"
              : "Nie masz jeszcze żadnych aktywnych grup. Utwórz pierwszą grupę, aby rozpocząć."}
          </p>
          <Button onClick={onCreateGroup}>
            <Plus className="size-4 mr-2" />
            Utwórz pierwszą grupę
          </Button>
        </div>
      )}
    </div>
  );
}
