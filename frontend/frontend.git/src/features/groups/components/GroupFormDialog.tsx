"use client";

import { useState, useEffect } from "react";
import { useForm } from "react-hook-form";
import { zodResolver } from "@hookform/resolvers/zod";
import { z } from "zod";
import {
  Dialog,
  DialogContent,
  DialogDescription,
  DialogFooter,
  DialogHeader,
  DialogTitle,
} from "@/components/ui/dialog";
import { Button } from "@/components/ui/button";
import { Input } from "@/components/ui/input";
import { Textarea } from "@/components/ui/textarea";
import {
  Form,
  FormControl,
  FormDescription,
  FormField,
  FormItem,
  FormLabel,
  FormMessage,
} from "@/components/ui/form";
import { useCreateGroup, useUpdateGroup } from "../hooks/useGroupsData";
import type { Group } from "../types/group.types";

const groupFormSchema = z.object({
  name: z
    .string()
    .min(3, "Nazwa musi mieć co najmniej 3 znaki")
    .max(100, "Nazwa może mieć maksymalnie 100 znaków"),
  description: z
    .string()
    .max(500, "Opis może mieć maksymalnie 500 znaków")
    .optional(),
});

type GroupFormValues = z.infer<typeof groupFormSchema>;

interface GroupFormDialogProps {
  open: boolean;
  onOpenChange: (open: boolean) => void;
  group?: Group | null;
  onSuccess?: () => void;
}

/**
 * Dialog do tworzenia i edycji grupy
 */
export function GroupFormDialog({
  open,
  onOpenChange,
  group,
  onSuccess,
}: GroupFormDialogProps) {
  const isEditing = !!group;

  const createGroup = useCreateGroup();
  const updateGroup = useUpdateGroup();

  const form = useForm<GroupFormValues>({
    resolver: zodResolver(groupFormSchema),
    defaultValues: {
      name: "",
      description: "",
    },
  });

  useEffect(() => {
    if (open) {
      form.reset({
        name: group?.name || "",
        description: group?.description || "",
      });
    }
  }, [open, group, form]);

  const onSubmit = async (values: GroupFormValues) => {
    try {
      if (isEditing && group) {
        await updateGroup.mutateAsync({
          groupId: group.id,
          data: values,
        });
      } else {
        await createGroup.mutateAsync(values);
      }
      onOpenChange(false);
      form.reset();
      onSuccess?.();
    } catch (error) {
      console.error("Błąd podczas zapisywania grupy:", error);
    }
  };

  const isPending = createGroup.isPending || updateGroup.isPending;

  return (
    <Dialog open={open} onOpenChange={onOpenChange}>
      <DialogContent className="sm:max-w-md">
        <DialogHeader>
          <DialogTitle>
            {isEditing ? "Edytuj grupę" : "Utwórz nową grupę"}
          </DialogTitle>
          <DialogDescription>
            {isEditing
              ? "Zaktualizuj informacje o grupie"
              : "Wprowadź nazwę i opcjonalny opis grupy"}
          </DialogDescription>
        </DialogHeader>

        <Form {...form}>
          <form onSubmit={form.handleSubmit(onSubmit)} className="space-y-4">
            <FormField
              control={form.control}
              name="name"
              render={({ field }) => (
                <FormItem>
                  <FormLabel>Nazwa grupy</FormLabel>
                  <FormControl>
                    <Input
                      placeholder="np. Klasa 3A, Kurs angielskiego"
                      {...field}
                    />
                  </FormControl>
                  <FormMessage />
                </FormItem>
              )}
            />

            <FormField
              control={form.control}
              name="description"
              render={({ field }) => (
                <FormItem>
                  <FormLabel>Opis (opcjonalny)</FormLabel>
                  <FormControl>
                    <Textarea
                      placeholder="Krótki opis grupy..."
                      className="resize-none"
                      rows={3}
                      {...field}
                    />
                  </FormControl>
                  <FormDescription>
                    Możesz dodać opis, aby łatwiej identyfikować grupę
                  </FormDescription>
                  <FormMessage />
                </FormItem>
              )}
            />

            <DialogFooter>
              <Button
                type="button"
                variant="outline"
                onClick={() => onOpenChange(false)}
                disabled={isPending}
              >
                Anuluj
              </Button>
              <Button type="submit" disabled={isPending}>
                {isPending
                  ? "Zapisywanie..."
                  : isEditing
                  ? "Zapisz zmiany"
                  : "Utwórz grupę"}
              </Button>
            </DialogFooter>
          </form>
        </Form>
      </DialogContent>
    </Dialog>
  );
}
