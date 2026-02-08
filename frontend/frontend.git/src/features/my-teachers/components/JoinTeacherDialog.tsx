"use client";

import { useState, useEffect } from "react";
import {
  Loader2,
  UserPlus,
  CheckCircle,
  AlertCircle,
  Clock,
} from "lucide-react";
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
import { Label } from "@/components/ui/label";
import { Badge } from "@/components/ui/badge";
import { useInvitationInfo, useJoinTeacher } from "../hooks/useMyTeachersData";
import { useDebounce } from "@/lib/hooks/useDebounce";
import type { InvitationStatus } from "../types";
import {timee} from "@/lib/time";

interface JoinTeacherDialogProps {
  open: boolean;
  onOpenChange: (open: boolean) => void;
  onSuccess?: () => void;
  initialCode?: string;
}

const statusConfig: Record<
  InvitationStatus,
  {
    label: string;
    variant: "default" | "secondary" | "destructive" | "outline";
    icon: React.ReactNode;
  }
> = {
  ACTIVE: {
    label: "Aktywne",
    variant: "default",
    icon: <CheckCircle className="size-3" />,
  },
  USED: {
    label: "Wykorzystane",
    variant: "secondary",
    icon: <AlertCircle className="size-3" />,
  },
  EXPIRED: {
    label: "Wygasłe",
    variant: "destructive",
    icon: <Clock className="size-3" />,
  },
  REVOKED: {
    label: "Anulowane",
    variant: "destructive",
    icon: <AlertCircle className="size-3" />,
  },
};

/**
 * Dialog do dołączania do nauczyciela za pomocą kodu zaproszenia
 */
export function JoinTeacherDialog({
  open,
  onOpenChange,
  onSuccess,
  initialCode = "",
}: JoinTeacherDialogProps) {
  const [code, setCode] = useState(initialCode);
  const debouncedCode = useDebounce(code.trim(), 500);

  const {
    data: invitationInfo,
    isLoading: isLoadingInfo,
    error: infoError,
  } = useInvitationInfo(debouncedCode);

  const joinTeacher = useJoinTeacher();

  useEffect(() => {
    if (!open) {
      setCode(initialCode);
    }
  }, [open, initialCode]);

  const handleJoin = async () => {
    if (!invitationInfo || invitationInfo.status !== "ACTIVE") return;

    try {
      await joinTeacher.mutateAsync(code.trim());
      onOpenChange(false);
      onSuccess?.();
    } catch (error) {
      console.error("Błąd podczas dołączania:", error);
    }
  };

  const canJoin =
    invitationInfo &&
    invitationInfo.status === "ACTIVE" &&
    !joinTeacher.isPending;

  return (
    <Dialog open={open} onOpenChange={onOpenChange}>
      <DialogContent className="sm:max-w-md">
        <DialogHeader>
          <DialogTitle className="flex items-center gap-2">
            <UserPlus className="size-5" />
            Dołącz do nauczyciela
          </DialogTitle>
          <DialogDescription>
            Wpisz kod zaproszenia otrzymany od nauczyciela
          </DialogDescription>
        </DialogHeader>

        <div className="space-y-4">
          {/* Pole kodu */}
          <div className="space-y-2">
            <Label htmlFor="invitation-code">Kod zaproszenia</Label>
            <Input
              id="invitation-code"
              placeholder="np. ABC123XY"
              value={code}
              onChange={(e) => setCode(e.target.value.toUpperCase())}
              disabled={joinTeacher.isPending}
              className="font-mono text-center text-lg tracking-wider"
            />
          </div>

          {/* Loading */}
          {isLoadingInfo && debouncedCode.length >= 6 && (
            <div className="flex items-center justify-center p-4 text-muted-foreground">
              <Loader2 className="size-4 mr-2 animate-spin" />
              Sprawdzanie kodu...
            </div>
          )}

          {/* Błąd */}
          {infoError && debouncedCode.length >= 6 && (
            <div className="p-4 rounded-lg border border-destructive/20 bg-destructive/10 text-destructive text-sm">
              <AlertCircle className="size-4 inline mr-2" />
              Nie znaleziono zaproszenia o podanym kodzie
            </div>
          )}

          {/* Podgląd zaproszenia */}
          {invitationInfo && (
            <div className="p-4 rounded-lg border bg-muted/50 space-y-3">
              <div className="flex items-center justify-between">
                <span className="font-medium">{invitationInfo.name}</span>
                <Badge
                  variant={statusConfig[invitationInfo.status].variant}
                  className="gap-1"
                >
                  {statusConfig[invitationInfo.status].icon}
                  {statusConfig[invitationInfo.status].label}
                </Badge>
              </div>

              <div className="grid grid-cols-2 gap-2 text-sm text-muted-foreground">
                <div>
                  <span className="block text-xs uppercase tracking-wide">
                    Użycia
                  </span>
                  <span className="font-medium text-foreground">
                    {invitationInfo.currentUses}
                    {invitationInfo.maxUses && ` / ${invitationInfo.maxUses}`}
                  </span>
                </div>
                <div>
                  <span className="block text-xs uppercase tracking-wide">
                    Wygasa
                  </span>
                  <span className="font-medium text-foreground">
                    {invitationInfo.expiresAt
                      ? timee.formatDate(invitationInfo.expiresAt)
                      : "Nigdy"}
                  </span>
                </div>
              </div>

              {invitationInfo.status !== "ACTIVE" && (
                <div className="text-sm text-destructive">
                  {invitationInfo.status === "EXPIRED" &&
                    "To zaproszenie wygasło. Poproś nauczyciela o nowy kod."}
                  {invitationInfo.status === "USED" &&
                    "To zaproszenie zostało już w pełni wykorzystane."}
                  {invitationInfo.status === "REVOKED" &&
                    "To zaproszenie zostało anulowane przez nauczyciela."}
                </div>
              )}
            </div>
          )}

          {/* Błąd dołączania */}
          {joinTeacher.error && (
            <div className="p-3 rounded-lg border border-destructive/20 bg-destructive/10 text-destructive text-sm">
              {(joinTeacher.error as Error).message ||
                "Wystąpił błąd podczas dołączania"}
            </div>
          )}
        </div>

        <DialogFooter className="gap-2 sm:gap-0">
          <Button
            type="button"
            variant="outline"
            onClick={() => onOpenChange(false)}
            disabled={joinTeacher.isPending}
          >
            Anuluj
          </Button>
          <Button type="button" onClick={handleJoin} disabled={!canJoin}>
            {joinTeacher.isPending ? (
              <>
                <Loader2 className="size-4 mr-2 animate-spin" />
                Dołączanie...
              </>
            ) : (
              <>
                <UserPlus className="size-4 mr-2" />
                Dołącz
              </>
            )}
          </Button>
        </DialogFooter>
      </DialogContent>
    </Dialog>
  );
}
