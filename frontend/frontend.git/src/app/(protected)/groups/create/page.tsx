"use client";

import { useRouter } from "next/navigation";
import Link from "next/link";
import { ArrowLeft } from "lucide-react";
import { useForm } from "react-hook-form";
import { zodResolver } from "@hookform/resolvers/zod";
import { z } from "zod";
import { useProtectedRoute } from "@/features/auth/hooks/useProtectedRoute";
import { Button } from "@/components/ui/button";
import { Input } from "@/components/ui/input";
import { Textarea } from "@/components/ui/textarea";
import { Skeleton } from "@/components/ui/skeleton";
import {
  Card,
  CardContent,
  CardDescription,
  CardHeader,
  CardTitle,
} from "@/components/ui/card";
import {
  Form,
  FormControl,
  FormDescription,
  FormField,
  FormItem,
  FormLabel,
  FormMessage,
} from "@/components/ui/form";
import { useCreateGroup } from "@/features/groups/hooks/useGroupsData";

const createGroupSchema = z.object({
  name: z
    .string()
    .min(3, "Nazwa musi mieć co najmniej 3 znaki")
    .max(100, "Nazwa może mieć maksymalnie 100 znaków"),
  description: z
    .string()
    .max(500, "Opis może mieć maksymalnie 500 znaków")
    .optional(),
});

type CreateGroupFormValues = z.infer<typeof createGroupSchema>;

/**
 * Strona tworzenia nowej grupy
 */
const CreateGroupPage = () => {
  const router = useRouter();
  const { user, isLoading: authLoading } = useProtectedRoute({
    requiredAccountType: "TEACHER",
  });

  const createGroup = useCreateGroup();

  const form = useForm<CreateGroupFormValues>({
    resolver: zodResolver(createGroupSchema),
    defaultValues: {
      name: "",
      description: "",
    },
  });

  const onSubmit = async (values: CreateGroupFormValues) => {
    try {
      const newGroup = await createGroup.mutateAsync(values);
      router.push(`/groups/${newGroup.id}`);
    } catch (error) {
      console.error("Błąd podczas tworzenia grupy:", error);
    }
  };

  if (authLoading) {
    return (
      <div className="min-h-screen bg-background">
        <div className="container max-w-2xl mx-auto p-6 lg:p-8 space-y-6">
          <div className="flex items-center gap-4">
            <Skeleton className="h-10 w-10" />
            <Skeleton className="h-8 w-48" />
          </div>
          <Skeleton className="h-64 w-full rounded-xl" />
        </div>
      </div>
    );
  }

  if (!user) {
    return null;
  }

  return (
    <div className="min-h-screen bg-background">
      <div className="container max-w-2xl mx-auto p-6 lg:p-8 space-y-6">
        {/* Nagłówek */}
        <div className="flex items-center gap-4">
          <Button variant="ghost" size="icon" asChild>
            <Link href="/groups">
              <ArrowLeft className="size-5" />
            </Link>
          </Button>
          <h1 className="text-2xl font-bold">Utwórz nową grupę</h1>
        </div>

        {/* Formularz */}
        <Card>
          <CardHeader>
            <CardTitle>Informacje o grupie</CardTitle>
            <CardDescription>
              Wprowadź podstawowe informacje o nowej grupie. Po utworzeniu
              będziesz mógł dodać uczniów i udostępnić kursy.
            </CardDescription>
          </CardHeader>
          <CardContent>
            <Form {...form}>
              <form
                onSubmit={form.handleSubmit(onSubmit)}
                className="space-y-6"
              >
                <FormField
                  control={form.control}
                  name="name"
                  render={({ field }) => (
                    <FormItem>
                      <FormLabel>Nazwa grupy *</FormLabel>
                      <FormControl>
                        <Input
                          placeholder="np. Klasa 3A, Kurs Business English"
                          {...field}
                        />
                      </FormControl>
                      <FormDescription>
                        Wybierz nazwę, która pomoże zidentyfikować grupę
                      </FormDescription>
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
                          placeholder="Dodaj opis grupy, np. cel nauki, poziom zaawansowania..."
                          className="resize-none min-h-[100px]"
                          {...field}
                        />
                      </FormControl>
                      <FormDescription>
                        Opis będzie widoczny dla członków grupy
                      </FormDescription>
                      <FormMessage />
                    </FormItem>
                  )}
                />

                <div className="flex justify-end gap-4 pt-4">
                  <Button
                    type="button"
                    variant="outline"
                    onClick={() => router.push("/groups")}
                    disabled={createGroup.isPending}
                  >
                    Anuluj
                  </Button>
                  <Button type="submit" disabled={createGroup.isPending}>
                    {createGroup.isPending ? "Tworzenie..." : "Utwórz grupę"}
                  </Button>
                </div>
              </form>
            </Form>
          </CardContent>
        </Card>
      </div>
    </div>
  );
};

export default CreateGroupPage;
