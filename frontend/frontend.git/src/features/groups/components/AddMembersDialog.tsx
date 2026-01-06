"use client";

import { useState } from "react";
import { useForm } from "react-hook-form";
import { zodResolver } from "@hookform/resolvers/zod";
import { z } from "zod";
import { Search, UserPlus, X, Loader2, Info } from "lucide-react";
import {
  Dialog,
  DialogContent,
  DialogDescription,
  DialogFooter,
  DialogHeader,
  DialogTitle,
} from "@/components/ui/dialog";
import { Alert, AlertDescription } from "@/components/ui/alert";
import { Button } from "@/components/ui/button";
import { Input } from "@/components/ui/input";
import { Badge } from "@/components/ui/badge";
import {
  Form,
  FormControl,
  FormField,
  FormItem,
  FormLabel,
  FormMessage,
} from "@/components/ui/form";
import { useAddGroupMembersBatch } from "../hooks/useGroupsData";
import { useSearchUsers } from "@/features/friends/hooks/useFriends";
import { useDebounce } from "@/lib/hooks/useDebounce";

const addMembersSchema = z.object({
  studentIds: z.array(z.string()).min(1, "Wybierz co najmniej jednego ucznia"),
});

type AddMembersFormValues = z.infer<typeof addMembersSchema>;

interface SelectedStudent {
  id: string;
  name: string;
  email?: string;
}

interface AddMembersDialogProps {
  open: boolean;
  onOpenChange: (open: boolean) => void;
  groupId: string;
  onSuccess?: () => void;
}

/**
 * Dialog do dodawania uczniów do grupy
 */
export function AddMembersDialog({
  open,
  onOpenChange,
  groupId,
  onSuccess,
}: AddMembersDialogProps) {
  const [searchValue, setSearchValue] = useState("");
  const [selectedStudents, setSelectedStudents] = useState<SelectedStudent[]>(
    []
  );

  const debouncedSearch = useDebounce(searchValue, 300);
  const { data: searchResults, isLoading: isSearching } =
    useSearchUsers(debouncedSearch);

  const addMembersBatch = useAddGroupMembersBatch();

  const form = useForm<AddMembersFormValues>({
    resolver: zodResolver(addMembersSchema),
    defaultValues: {
      studentIds: [],
    },
  });

  // Filtracja juz dodanych i znajomych aby nie wyswietlac ich w wynikach wyszukiwania
  const filteredResults =
    searchResults?.filter(
      (user) =>
        !selectedStudents.some((s) => s.id === user.userId) && !user.isFriend
    ) ?? [];

  const handleSelectStudent = (user: {
    userId: string;
    username: string;
    firstName?: string;
    lastName?: string;
  }) => {
    const displayName =
      user.firstName && user.lastName
        ? `${user.firstName} ${user.lastName}`
        : user.username;

    const student: SelectedStudent = {
      id: user.userId,
      name: displayName,
    };

    setSelectedStudents((prev) => [...prev, student]);
    form.setValue("studentIds", [...form.getValues("studentIds"), user.userId]);
    setSearchValue("");
  };

  const handleRemoveStudent = (studentId: string) => {
    setSelectedStudents((prev) => prev.filter((s) => s.id !== studentId));
    form.setValue(
      "studentIds",
      form.getValues("studentIds").filter((id) => id !== studentId)
    );
  };

  const onSubmit = async (values: AddMembersFormValues) => {
    try {
      await addMembersBatch.mutateAsync({
        groupId,
        data: { studentIds: values.studentIds },
      });
      onOpenChange(false);
      setSelectedStudents([]);
      form.reset();
      onSuccess?.();
    } catch (error) {
      console.error("Błąd podczas dodawania uczniów:", error);
    }
  };

  const handleClose = () => {
    onOpenChange(false);
    setSelectedStudents([]);
    setSearchValue("");
    form.reset();
  };

  return (
    <Dialog open={open} onOpenChange={handleClose}>
      <DialogContent className="sm:max-w-lg">
        <DialogHeader>
          <DialogTitle>Dodaj uczniów do grupy</DialogTitle>
          <DialogDescription>
            Wyszukaj i wybierz uczniów, których chcesz dodać do grupy
          </DialogDescription>
        </DialogHeader>

        <Alert className="bg-blue-50 border-blue-200 dark:bg-blue-950/30 dark:border-blue-900">
          <Info className="size-4 text-blue-600 dark:text-blue-400" />
          <AlertDescription className="text-sm text-blue-800 dark:text-blue-300">
            Aby dodać ucznia do grupy, musisz go najpierw dodać jako swojego
            studenta w zakładce <strong>„Moi studenci”</strong>.
          </AlertDescription>
        </Alert>

        <Form {...form}>
          <form onSubmit={form.handleSubmit(onSubmit)} className="space-y-4">
            {/* Wybrani uczniowie */}
            {selectedStudents.length > 0 && (
              <div className="space-y-2">
                <label className="text-sm font-medium">Wybrani uczniowie</label>
                <div className="flex flex-wrap gap-2">
                  {selectedStudents.map((student) => (
                    <Badge
                      key={student.id}
                      variant="secondary"
                      className="gap-1 pr-1"
                    >
                      {student.name}
                      <button
                        type="button"
                        onClick={() => handleRemoveStudent(student.id)}
                        className="ml-1 rounded-full hover:bg-muted-foreground/20 p-0.5"
                      >
                        <X className="size-3" />
                      </button>
                    </Badge>
                  ))}
                </div>
              </div>
            )}

            {/* Wyszukiwarka */}
            <FormField
              control={form.control}
              name="studentIds"
              render={() => (
                <FormItem>
                  <FormLabel>Wyszukaj uczniów</FormLabel>
                  <FormControl>
                    <div className="relative">
                      <Search className="absolute left-3 top-1/2 -translate-y-1/2 size-4 text-muted-foreground" />
                      <Input
                        placeholder="Wpisz imię lub email ucznia..."
                        value={searchValue}
                        onChange={(e) => setSearchValue(e.target.value)}
                        className="pl-9"
                      />
                    </div>
                  </FormControl>
                  <FormMessage />
                </FormItem>
              )}
            />

            {/* Lista wyników wyszukiwania */}
            {debouncedSearch.length >= 2 && (
              <div className="border rounded-md max-h-48 overflow-auto">
                {isSearching ? (
                  <div className="p-4 flex items-center justify-center text-muted-foreground">
                    <Loader2 className="size-4 mr-2 animate-spin" />
                    Wyszukiwanie...
                  </div>
                ) : filteredResults.length > 0 ? (
                  filteredResults.map((user) => {
                    const displayName =
                      user.firstName && user.lastName
                        ? `${user.firstName} ${user.lastName}`
                        : user.username;
                    return (
                      <button
                        key={user.userId}
                        type="button"
                        onClick={() => handleSelectStudent(user)}
                        className="w-full flex items-center gap-3 p-3 hover:bg-muted text-left transition-colors"
                      >
                        <div className="flex size-8 items-center justify-center rounded-full bg-primary/10 text-primary text-sm font-medium">
                          {displayName.charAt(0).toUpperCase()}
                        </div>
                        <div className="flex-1 min-w-0">
                          <div className="font-medium truncate">
                            {displayName}
                          </div>
                          <div className="text-sm text-muted-foreground truncate">
                            @{user.username}
                          </div>
                        </div>
                        <UserPlus className="size-4 text-muted-foreground" />
                      </button>
                    );
                  })
                ) : (
                  <div className="p-4 text-center text-muted-foreground">
                    Nie znaleziono użytkowników
                  </div>
                )}
              </div>
            )}

            <DialogFooter>
              <Button
                type="button"
                variant="outline"
                onClick={handleClose}
                disabled={addMembersBatch.isPending}
              >
                Anuluj
              </Button>
              <Button
                type="submit"
                disabled={
                  selectedStudents.length === 0 || addMembersBatch.isPending
                }
              >
                {addMembersBatch.isPending ? (
                  <>
                    <Loader2 className="size-4 mr-2 animate-spin" />
                    Dodawanie...
                  </>
                ) : (
                  <>
                    <UserPlus className="size-4 mr-2" />
                    Dodaj ({selectedStudents.length})
                  </>
                )}
              </Button>
            </DialogFooter>
          </form>
        </Form>
      </DialogContent>
    </Dialog>
  );
}
