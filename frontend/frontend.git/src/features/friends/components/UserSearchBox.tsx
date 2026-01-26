"use client";

import { useState, useEffect } from "react";
import { Card, CardHeader, CardTitle, CardContent } from "@/components/ui/card";
import { Button } from "@/components/ui/button";
import { Input } from "@/components/ui/input";
import { Badge } from "@/components/ui/badge";
import { Textarea } from "@/components/ui/textarea";
import {
  Dialog,
  DialogContent,
  DialogHeader,
  DialogTitle,
  DialogDescription,
  DialogFooter,
} from "@/components/ui/dialog";
import {
  Search,
  UserPlus,
  UserCheck,
  Clock,
  Trophy,
  Medal,
  RefreshCw,
  X,
} from "lucide-react";
import { useSearchUsers, useSendFriendRequest } from "../hooks/useFriends";
import type { UserSearchResult } from "../types/friend.types";

const useDebounce = <T,>(value: T, delay: number): T => {
  const [debouncedValue, setDebouncedValue] = useState<T>(value);

  useEffect(() => {
    const handler = setTimeout(() => {
      setDebouncedValue(value);
    }, delay);

    return () => {
      clearTimeout(handler);
    };
  }, [value, delay]);

  return debouncedValue;
};

/**
 * Komponent pojedynczego wyniku wyszukiwania
 */
const SearchResultItem = ({
  user,
  onSendRequest,
  isSending,
}: {
  user: UserSearchResult;
  onSendRequest: (userId: string, username: string) => void;
  isSending: boolean;
}) => {
  return (
    <div className="flex items-center gap-4 p-3 border rounded-lg hover:bg-muted/50 transition-colors">
      <div className="w-10 h-10 rounded-full bg-primary/10 flex items-center justify-center flex-shrink-0">
        <span className="text-sm font-semibold text-primary">
          {user.username.charAt(0).toUpperCase()}
        </span>
      </div>

      <div className="flex-1 min-w-0">
        <div className="flex items-center gap-2">
          <span className="font-medium truncate">{user.username}</span>
          {user.isFriend && (
            <Badge variant="secondary" className="flex items-center gap-1">
              <UserCheck className="w-3 h-3" />
              Znajomy
            </Badge>
          )}
          {user.hasPendingRequest && (
            <Badge variant="outline" className="flex items-center gap-1">
              <Clock className="w-3 h-3" />
              {user.requestDirection === "sent" ? "Wysłano" : "Oczekuje"}
            </Badge>
          )}
        </div>
        {user.rankPosition && (
          <div className="flex items-center gap-3 text-xs text-muted-foreground mt-1">
            <span className="flex items-center gap-1">
              <Medal className="w-3 h-3" />#{user.rankPosition}
            </span>
            <span className="flex items-center gap-1">
              <Trophy className="w-3 h-3" />
              {user.totalPoints?.toLocaleString()} pkt
            </span>
          </div>
        )}
      </div>

      {!user.isFriend && !user.hasPendingRequest && (
        <Button
          size="sm"
          onClick={() => onSendRequest(user.userId, user.username)}
          disabled={isSending}
        >
          {isSending ? (
            <RefreshCw className="w-4 h-4 animate-spin" />
          ) : (
            <>
              <UserPlus className="w-4 h-4 mr-1" />
              Dodaj
            </>
          )}
        </Button>
      )}
    </div>
  );
};

/**
 * Komponent wyszukiwarki użytkowników
 */
export const UserSearchBox = () => {
  const [query, setQuery] = useState("");
  const [showSendDialog, setShowSendDialog] = useState(false);
  const [selectedUser, setSelectedUser] = useState<{
    userId: string;
    username: string;
  } | null>(null);
  const [message, setMessage] = useState("");

  const debouncedQuery = useDebounce(query, 300);
  const {
    data: results,
    isLoading,
    isFetching,
  } = useSearchUsers(debouncedQuery);
  const sendRequestMutation = useSendFriendRequest();

  const handleSendRequest = (userId: string, username: string) => {
    setSelectedUser({ userId, username });
    setShowSendDialog(true);
  };

  const handleConfirmSend = () => {
    if (selectedUser) {
      sendRequestMutation.mutate(selectedUser.userId, {
        onSuccess: () => {
          setShowSendDialog(false);
          setSelectedUser(null);
          setMessage("");
          setQuery("");
        },
      });
    }
  };

  const handleClearSearch = () => {
    setQuery("");
  };

  return (
    <>
      <Card>
        <CardHeader className="border-b">
          <CardTitle className="flex items-center gap-2">
            <Search className="w-5 h-5" />
            Znajdź użytkowników
          </CardTitle>
        </CardHeader>
        <CardContent className="pt-4">
          {/* Pole wyszukiwania */}
          <div className="relative mb-4">
            <Search className="absolute left-3 top-1/2 -translate-y-1/2 w-4 h-4 text-muted-foreground" />
            <Input
              placeholder="Wpisz nazwę użytkownika..."
              value={query}
              onChange={(e) => setQuery(e.target.value)}
              className="pl-9 pr-9"
            />
            {query && (
              <button
                onClick={handleClearSearch}
                className="absolute right-3 top-1/2 -translate-y-1/2 text-muted-foreground hover:text-foreground"
              >
                <X className="w-4 h-4" />
              </button>
            )}
          </div>

          {/* Wyniki */}
          {query.length < 2 ? (
            <p className="text-sm text-muted-foreground text-center py-4">
              Wpisz minimum 2 znaki, aby wyszukać
            </p>
          ) : isLoading || isFetching ? (
            <div className="flex items-center justify-center py-8">
              <RefreshCw className="w-6 h-6 animate-spin text-muted-foreground" />
            </div>
          ) : !results || results.length === 0 ? (
            <div className="text-center py-8">
              <Search className="w-12 h-12 mx-auto mb-4 text-muted-foreground opacity-50" />
              <p className="text-muted-foreground">
                Nie znaleziono użytkowników o nazwie &quot;{query}&quot;
              </p>
            </div>
          ) : (
            <div className="space-y-2">
              <p className="text-xs text-muted-foreground mb-3">
                Znaleziono {results.length} użytkowników
              </p>
              {results.map((user) => (
                <SearchResultItem
                  key={user.userId}
                  user={user}
                  onSendRequest={handleSendRequest}
                  isSending={
                    sendRequestMutation.isPending &&
                    sendRequestMutation.variables === user.userId
                  }
                />
              ))}
            </div>
          )}
        </CardContent>
      </Card>

      {/* Dialog wysyłania zaproszenia */}
      <Dialog open={showSendDialog} onOpenChange={setShowSendDialog}>
        <DialogContent>
          <DialogHeader>
            <DialogTitle>Wyślij zaproszenie do znajomych</DialogTitle>
            <DialogDescription>
              Wyślij zaproszenie do użytkownika{" "}
              <strong>{selectedUser?.username}</strong>
            </DialogDescription>
          </DialogHeader>

          <div className="py-4">
            <label className="text-sm font-medium mb-2 block">
              Wiadomość (opcjonalnie)
            </label>
            <Textarea
              placeholder="Hej! Dodajmy się do znajomych..."
              value={message}
              onChange={(e) => setMessage(e.target.value)}
              maxLength={200}
            />
            <p className="text-xs text-muted-foreground mt-1">
              {message.length}/200 znaków
            </p>
          </div>

          <DialogFooter>
            <Button variant="outline" onClick={() => setShowSendDialog(false)}>
              Anuluj
            </Button>
            <Button
              onClick={handleConfirmSend}
              disabled={sendRequestMutation.isPending}
            >
              {sendRequestMutation.isPending ? (
                <>
                  <RefreshCw className="w-4 h-4 mr-2 animate-spin" />
                  Wysyłanie...
                </>
              ) : (
                <>
                  <UserPlus className="w-4 h-4 mr-2" />
                  Wyślij zaproszenie
                </>
              )}
            </Button>
          </DialogFooter>
        </DialogContent>
      </Dialog>
    </>
  );
};
