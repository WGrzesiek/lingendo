"use client";

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
import { useDeleteGroup } from "../hooks/useGroupsData";
import type { Group } from "../types/group.types";

interface DeleteGroupDialogProps {
  open: boolean;
  onOpenChange: (open: boolean) => void;
  group: Group | null;
  onSuccess?: () => void;
}

/**
 * Dialog potwierdzenia usunięcia grupy
 */
export function DeleteGroupDialog({
  open,
  onOpenChange,
  group,
  onSuccess,
}: DeleteGroupDialogProps) {
  const deleteGroup = useDeleteGroup();

  const handleDelete = async () => {
    if (!group) return;

    try {
      await deleteGroup.mutateAsync(group.id);
      onOpenChange(false);
      onSuccess?.();
    } catch (error) {
      console.error("Błąd podczas usuwania grupy:", error);
    }
  };

  return (
    <AlertDialog open={open} onOpenChange={onOpenChange}>
      <AlertDialogContent>
        <AlertDialogHeader>
          <AlertDialogTitle>Czy na pewno chcesz usunąć grupę?</AlertDialogTitle>
          <AlertDialogDescription>
            Grupa <strong>&quot;{group?.name}&quot;</strong> zostanie trwale
            usunięta wraz ze wszystkimi powiązaniami. Uczniowie zostaną
            odłączeni od grupy, ale ich konta nie zostaną usunięte.
            <br />
            <br />
            <span className="text-destructive">
              Ta operacja jest nieodwracalna.
            </span>
          </AlertDialogDescription>
        </AlertDialogHeader>
        <AlertDialogFooter>
          <AlertDialogCancel disabled={deleteGroup.isPending}>
            Anuluj
          </AlertDialogCancel>
          <AlertDialogAction
            onClick={handleDelete}
            disabled={deleteGroup.isPending}
            className="bg-destructive text-white hover:bg-destructive/90"
          >
            {deleteGroup.isPending ? "Usuwanie..." : "Usuń grupę"}
          </AlertDialogAction>
        </AlertDialogFooter>
      </AlertDialogContent>
    </AlertDialog>
  );
}
