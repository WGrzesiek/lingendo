"use client";

import { useState } from "react";
import { useProtectedRoute } from "@/features/auth/hooks/useProtectedRoute";
import { Skeleton } from "@/components/ui/skeleton";
import {
  GroupFormDialog,
  DeleteGroupDialog,
} from "@/features/groups/components";
import { useTeacherGroups } from "@/features/dashboard-teacher/hooks";
import type { Group } from "@/features/groups/types/group.types";
import type { GroupResponse } from "@/features/dashboard-teacher/types/api";
import { GroupsListWithPagination } from "@/features/groups/components/GroupsListWithPagination";

/**
 * Mapuje GroupResponse z API na Group dla komponentów
 */
const mapGroupResponseToGroup = (response: GroupResponse): Group => ({
  id: response.id,
  name: response.name,
  description: response.description || undefined,
  teacherId: response.teacherId,
  memberCount: response.memberCount,
  sharedDecksCount: 0,
  createdAt: response.createdAt,
  updatedAt: response.updatedAt,
  status: response.isArchived ? "ARCHIVED" : "ACTIVE",
});

/**
 * Strona listy grup
 * Dostępna dla nauczycieli - zarządzanie grupami uczniów
 */
const GroupsPage = () => {
  const { user, isLoading: authLoading } = useProtectedRoute({
    requiredAccountType: "TEACHER",
  });

  const [includeArchived, setIncludeArchived] = useState(false);
  const [page, setPage] = useState(0);
  const [pageSize] = useState(12);
  const [isFormOpen, setIsFormOpen] = useState(false);
  const [editingGroup, setEditingGroup] = useState<Group | null>(null);
  const [deletingGroup, setDeletingGroup] = useState<Group | null>(null);

  const { data: groupsPage, isLoading: groupsLoading } = useTeacherGroups(
    includeArchived,
    page,
    pageSize
  );

  const groups = groupsPage?.content.map(mapGroupResponseToGroup);

  const handleCreateGroup = () => {
    setEditingGroup(null);
    setIsFormOpen(true);
  };

  const handleEditGroup = (group: Group) => {
    setEditingGroup(group);
    setIsFormOpen(true);
  };

  const handleDeleteGroup = (group: Group) => {
    setDeletingGroup(group);
  };

  if (authLoading) {
    return (
      <div className="min-h-screen bg-background">
        <div className="container mx-auto p-6 lg:p-8 space-y-6">
          <div className="flex items-center justify-between">
            <div>
              <Skeleton className="h-8 w-48 mb-2" />
              <Skeleton className="h-5 w-96" />
            </div>
            <Skeleton className="h-10 w-32" />
          </div>
          <div className="flex gap-4">
            <Skeleton className="h-10 w-64" />
            <Skeleton className="h-10 w-48" />
          </div>
          <div className="grid gap-4 md:grid-cols-2 lg:grid-cols-3">
            {[...Array(6)].map((_, i) => (
              <Skeleton key={i} className="h-40 rounded-xl" />
            ))}
          </div>
        </div>
      </div>
    );
  }

  if (!user) {
    return null;
  }

  return (
    <div className="min-h-screen bg-background">
      <div className="container mx-auto p-6 lg:p-8">
        <GroupsListWithPagination
          groups={groups}
          isLoading={groupsLoading}
          onCreateGroup={handleCreateGroup}
          onEditGroup={handleEditGroup}
          onDeleteGroup={handleDeleteGroup}
          includeArchived={includeArchived}
          onIncludeArchivedChange={setIncludeArchived}
          page={page}
          totalPages={groupsPage?.totalPages || 0}
          totalElements={groupsPage?.totalElements || 0}
          onPageChange={setPage}
        />

        {/* Dialog tworzenia/edycji grupy */}
        <GroupFormDialog
          open={isFormOpen}
          onOpenChange={setIsFormOpen}
          group={editingGroup}
        />

        {/* Dialog usuwania grupy */}
        <DeleteGroupDialog
          open={!!deletingGroup}
          onOpenChange={(open) => !open && setDeletingGroup(null)}
          group={deletingGroup}
        />
      </div>
    </div>
  );
};

export default GroupsPage;
