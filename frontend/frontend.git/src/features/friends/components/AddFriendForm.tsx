"use client";

import { useState } from "react";
import { Card, CardContent, CardHeader, CardTitle } from "@/components/ui/card";
import { Button } from "@/components/ui/button";
import { Input } from "@/components/ui/input";
import { Alert, AlertDescription } from "@/components/ui/alert";
import { UserPlus, Search, CheckCircle2, XCircle } from "lucide-react";

interface AddFriendFormProps {
  onAddFriend: (username: string) => Promise<void>;
}

/**
 * Formularz dodawania znajomego przez nazwę użytkownika
 */
export const AddFriendForm = ({ onAddFriend }: AddFriendFormProps) => {
  const [username, setUsername] = useState("");
  const [isLoading, setIsLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);
  const [success, setSuccess] = useState<string | null>(null);

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();

    // Walidacja
    if (!username.trim()) {
      setError("Wprowadź nazwę użytkownika");
      return;
    }

    if (username.trim().length < 3) {
      setError("Nazwa użytkownika musi mieć minimum 3 znaki");
      return;
    }

    setError(null);
    setSuccess(null);
    setIsLoading(true);

    try {
      await onAddFriend(username.trim());
      setSuccess(`Dodano znajomego: ${username}`);
      setUsername("");
    } catch (err) {
      setError(
        err instanceof Error ? err.message : "Nie udało się dodać znajomego"
      );
    } finally {
      setIsLoading(false);
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
        <form onSubmit={handleSubmit} className="space-y-4">
          <div className="flex gap-2">
            <div className="relative flex-1">
              <Search className="absolute left-3 top-1/2 -translate-y-1/2 w-4 h-4 text-muted-foreground" />
              <Input
                type="text"
                placeholder="Wpisz nazwę użytkownika..."
                value={username}
                onChange={(e) => setUsername(e.target.value)}
                className="pl-9"
                disabled={isLoading}
              />
            </div>
            <Button type="submit" disabled={isLoading || !username.trim()}>
              {isLoading ? "Dodawanie..." : "Dodaj"}
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
      </CardContent>
    </Card>
  );
};
