"use client";

import { useState } from "react";
import { useForm } from "react-hook-form";
import { zodResolver } from "@hookform/resolvers/zod";
import { z } from "zod";
import { Card, CardContent, CardHeader, CardTitle } from "@/components/ui/card";
import { Button } from "@/components/ui/button";
import { Input } from "@/components/ui/input";
import { Alert, AlertDescription } from "@/components/ui/alert";
import {
  Form,
  FormControl,
  FormField,
  FormItem,
  FormMessage,
} from "@/components/ui/form";
import { UserPlus, Search, CheckCircle2, XCircle, Loader2 } from "lucide-react";

const addFriendSchema = z.object({
  username: z
    .string()
    .min(1, "Wprowadź nazwę użytkownika")
    .min(3, "Nazwa użytkownika musi mieć minimum 3 znaki")
    .max(50, "Nazwa użytkownika może mieć maksymalnie 50 znaków"),
});

type AddFriendFormValues = z.infer<typeof addFriendSchema>;

interface AddFriendFormProps {
  onAddFriend: (username: string) => Promise<void>;
}

/**
 * Formularz dodawania znajomego przez nazwę użytkownika
 */
export const AddFriendForm = ({ onAddFriend }: AddFriendFormProps) => {
  const [error, setError] = useState<string | null>(null);
  const [success, setSuccess] = useState<string | null>(null);

  const form = useForm<AddFriendFormValues>({
    resolver: zodResolver(addFriendSchema),
    defaultValues: {
      username: "",
    },
  });

  const isLoading = form.formState.isSubmitting;

  const onSubmit = async (values: AddFriendFormValues) => {
    setError(null);
    setSuccess(null);

    try {
      await onAddFriend(values.username.trim());
      setSuccess(`Wysłano zaproszenie do: ${values.username}`);
      form.reset();
    } catch (err) {
      setError(
        err instanceof Error ? err.message : "Nie udało się dodać znajomego"
      );
    }
  };

  return (
    <Card>
      <CardHeader>
        <CardTitle className="flex items-center gap-2">
          <UserPlus className="w-5 h-5" />
          Dodaj znajomego
        </CardTitle>
      </CardHeader>
      <CardContent>
        <Form {...form}>
          <form onSubmit={form.handleSubmit(onSubmit)} className="space-y-4">
            <div className="flex gap-2">
              <FormField
                control={form.control}
                name="username"
                render={({ field }) => (
                  <FormItem className="relative flex-1">
                    <FormControl>
                      <div className="relative">
                        <Search className="absolute left-3 top-1/2 -translate-y-1/2 w-4 h-4 text-muted-foreground" />
                        <Input
                          placeholder="Wpisz nazwę użytkownika..."
                          className="pl-9"
                          disabled={isLoading}
                          {...field}
                        />
                      </div>
                    </FormControl>
                    <FormMessage />
                  </FormItem>
                )}
              />
              <Button type="submit" disabled={isLoading}>
                {isLoading ? (
                  <>
                    <Loader2 className="w-4 h-4 mr-2 animate-spin" />
                    Dodawanie...
                  </>
                ) : (
                  "Dodaj"
                )}
              </Button>
            </div>

            {error && (
              <Alert variant="destructive">
                <XCircle className="h-4 w-4" />
                <AlertDescription>{error}</AlertDescription>
              </Alert>
            )}

            {success && (
              <Alert className="bg-green-500/10 text-green-500 border-green-500/20">
                <CheckCircle2 className="h-4 w-4" />
                <AlertDescription>{success}</AlertDescription>
              </Alert>
            )}

            <p className="text-sm text-muted-foreground">
              Wprowadź dokładną nazwę użytkownika. Po dodaniu znajomy pojawi się
              na liście poniżej.
            </p>
          </form>
        </Form>
      </CardContent>
    </Card>
  );
};
