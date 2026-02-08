"use client";

import { useState } from "react";
import { Card, CardContent, CardHeader, CardTitle } from "@/components/ui/card";
import { Button } from "@/components/ui/button";
import { Badge } from "@/components/ui/badge";
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
import { Skeleton } from "@/components/ui/skeleton";
import { useBlockedUsers, useUnblockUser } from "../hooks/useFriends";
import { BlockedUser } from "../types/friend.types";
import { Ban, UserCheck, AlertTriangle, ShieldOff } from "lucide-react";

interface BlockedUserCardProps {
  user: BlockedUser;
  onUnblock: (userId: string) => void;
  isUnblocking: boolean;
}

/**
 * Karta zablokowanego użytkownika
 */
const BlockedUserCard = ({
  user,
  onUnblock,
  isUnblocking,
}: BlockedUserCardProps) => (
  <Card>
    <CardContent className="p-4">
      <div className="flex items-center justify-between gap-4">
        <div className="flex items-center gap-3 min-w-0 flex-1">
          <div className="w-12 h-12 rounded-full bg-muted flex items-center justify-center flex-shrink-0">
            <span className="text-lg font-semibold text-muted-foreground">
              {user.username.charAt(0).toUpperCase()}
            </span>
          </div>
          <div className="min-w-0">
            <div className="flex items-center gap-2">
              <h3 className="font-semibold truncate">{user.username}</h3>
              <Badge variant="destructive" className="text-xs">
                <Ban className="w-3 h-3 mr-1" />
                Zablokowany
              </Badge>
            </div>
            <p className="text-sm text-muted-foreground">
              Zablokowano:{" "}
              {new Date(user.blockedAt).toLocaleDateString("pl-PL", {
                day: "numeric",
                month: "long",
                year: "numeric",
              })}
            </p>
            {user.reason && (
              <p className="text-sm text-muted-foreground mt-1">
                Powód: {user.reason}
              </p>
            )}
          </div>
        </div>
        <Button
          variant="outline"
          size="sm"
          onClick={() => onUnblock(user.userId)}
          disabled={isUnblocking}
        >
          <UserCheck className="w-4 h-4 mr-2" />
          {isUnblocking ? "Odblokowywanie..." : "Odblokuj"}
        </Button>
      </div>
    </CardContent>
  </Card>
);

/**
 * Skeleton podczas ładowania
 */
const BlockedUsersListSkeleton = () => (
  <div className="space-y-3">
    {[1, 2].map((i) => (
      <Card key={i}>
        <CardContent className="p-4">
          <div className="flex items-center gap-4">
            <Skeleton className="w-12 h-12 rounded-full" />
            <div className="flex-1 space-y-2">
              <Skeleton className="h-4 w-32" />
              <Skeleton className="h-3 w-48" />
            </div>
            <Skeleton className="h-9 w-28" />
          </div>
        </CardContent>
      </Card>
    ))}
  </div>
);

/**
 * Stan pustej listy
 */
const EmptyState = () => (
  <Card>
    <CardContent className="p-12 text-center">
      <ShieldOff className="w-12 h-12 mx-auto text-muted-foreground mb-4" />
      <h3 className="text-lg font-semibold mb-2">
        Brak zablokowanych użytkowników
      </h3>
      <p className="text-muted-foreground max-w-md mx-auto">
        Nie masz żadnych zablokowanych użytkowników. Zablokowane osoby nie mogą
        wysyłać Ci zaproszeń ani wyświetlać Twojego profilu.
      </p>
    </CardContent>
  </Card>
);

/**
 * Komponent zarządzania zablokowanymi użytkownikami
 */
export const BlockedUsersManager = () => {
  const [userToUnblock, setUserToUnblock] = useState<string | null>(null);

  const { data: blockedUsers, isLoading, error } = useBlockedUsers();
  const unblockUser = useUnblockUser();

  const handleUnblock = async () => {
    if (userToUnblock) {
      await unblockUser.mutateAsync(userToUnblock);
      setUserToUnblock(null);
    }
  };

  return (
    <div className="space-y-6">
      {/* Nagłówek */}
      <Card>
        <CardHeader>
          <CardTitle className="flex items-center gap-2">
            <Ban className="w-5 h-5" />
            Zablokowani użytkownicy
            {blockedUsers && blockedUsers.length > 0 && (
              <Badge variant="secondary">{blockedUsers.length}</Badge>
            )}
          </CardTitle>
        </CardHeader>
        <CardContent>
          <div className="flex items-start gap-3 p-4 bg-muted/50 rounded-lg">
            <AlertTriangle className="w-5 h-5 text-amber-500 mt-0.5" />
            <div className="text-sm">
              <p className="font-medium mb-1">Informacja o blokadzie</p>
              <p className="text-muted-foreground">
                Zablokowane osoby nie mogą wysyłać Ci zaproszeń do znajomych,
                wyświetlać Twojego profilu ani widzieć Twoich statystyk. Po
                odblokowaniu użytkownik będzie mógł ponownie wysłać Ci
                zaproszenie.
              </p>
            </div>
          </div>
        </CardContent>
      </Card>

      {/* Lista zablokowanych */}
      {isLoading ? (
        <BlockedUsersListSkeleton />
      ) : error ? (
        <Card>
          <CardContent className="p-6 text-center text-destructive">
            Wystąpił błąd podczas ładowania listy zablokowanych użytkowników
          </CardContent>
        </Card>
      ) : blockedUsers?.length === 0 ? (
        <EmptyState />
      ) : (
        <div className="space-y-3">
          {blockedUsers?.map((user) => (
            <BlockedUserCard
              key={user.userId}
              user={user}
              onUnblock={setUserToUnblock}
              isUnblocking={unblockUser.isPending}
            />
          ))}
        </div>
      )}

      {/* Dialog potwierdzenia odblokowania */}
      <AlertDialog
        open={!!userToUnblock}
        onOpenChange={() => setUserToUnblock(null)}
      >
        <AlertDialogContent>
          <AlertDialogHeader>
            <AlertDialogTitle>Odblokuj użytkownika</AlertDialogTitle>
            <AlertDialogDescription>
              Czy na pewno chcesz odblokować tego użytkownika? Po odblokowaniu
              będzie mógł wysyłać Ci zaproszenia do znajomych i wyświetlać Twój
              profil.
            </AlertDialogDescription>
          </AlertDialogHeader>
          <AlertDialogFooter>
            <AlertDialogCancel>Anuluj</AlertDialogCancel>
            <AlertDialogAction
              onClick={handleUnblock}
              disabled={unblockUser.isPending}
            >
              {unblockUser.isPending ? "Odblokowywanie..." : "Odblokuj"}
            </AlertDialogAction>
          </AlertDialogFooter>
        </AlertDialogContent>
      </AlertDialog>
    </div>
  );
};
