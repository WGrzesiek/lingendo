"use client";

import { useState } from "react";
import { toast } from "sonner";
import { Card, CardHeader, CardTitle, CardContent } from "@/components/ui/card";
import { Button } from "@/components/ui/button";
import { Input } from "@/components/ui/input";
import { Label } from "@/components/ui/label";
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
  Copy,
  Plus,
  Link2,
  Clock,
  Users,
  Trash2,
  AlertCircle,
  RefreshCw,
  XCircle,
} from "lucide-react";
import {
  useTeacherInvitations,
  useCreateInvitation,
  useDeactivateInvitation,
} from "../hooks";
import type { InvitationResponse, CreateInvitationRequest } from "../types/api";
import { cn } from "@/lib/utils";
import {timee} from "@/lib/time";


const isExpired = (dateString: string): boolean => {
  return new Date(dateString) < new Date();
};

const getStatusBadge = (invitation: InvitationResponse) => {
  const expired = invitation.expiresAt
    ? isExpired(invitation.expiresAt)
    : false;

  if (expired || invitation.status === "EXPIRED") {
    return (
      <Badge variant="outline" className="text-muted-foreground">
        Wygasło
      </Badge>
    );
  }

  if (invitation.maxUses && invitation.currentUses >= invitation.maxUses) {
    return <Badge variant="secondary">Wykorzystane</Badge>;
  }

  if (invitation.status === "REVOKED") {
    return <Badge variant="destructive">Anulowane</Badge>;
  }

  if (invitation.status !== "ACTIVE") {
    return <Badge variant="secondary">Nieaktywne</Badge>;
  }

  return (
    <Badge className="bg-green-500/10 text-green-600 hover:bg-green-500/20">
      Aktywne
    </Badge>
  );
};

/**
 * Komponent pojedynczego zaproszenia
 */
const InvitationCard = ({
  invitation,
  onDeactivate,
  onCopy,
}: {
  invitation: InvitationResponse;
  onDeactivate: (id: string) => void;
  onCopy: (code: string) => void;
}) => {
  const expired = invitation.expiresAt
    ? isExpired(invitation.expiresAt)
    : false;
  const isActive =
    invitation.status === "ACTIVE" &&
    !expired &&
    (!invitation.maxUses || invitation.currentUses < invitation.maxUses);

  return (
    <div
      className={cn(
        "p-4 border rounded-lg",
        isActive ? "bg-card" : "bg-muted/50 opacity-70"
      )}
    >
      <div className="flex items-start justify-between gap-4">
        <div className="flex-1 min-w-0">
          <div className="flex items-center gap-2 mb-2">
            <code className="px-2 py-1 bg-primary/10 rounded text-sm font-mono font-bold">
              {invitation.invitationCode}
            </code>
            {getStatusBadge(invitation)}
          </div>

          {invitation.name && (
            <p className="text-sm text-muted-foreground mb-2">
              {invitation.name}
            </p>
          )}

          <div className="flex flex-wrap items-center gap-4 text-xs text-muted-foreground">
            <span className="flex items-center gap-1">
              <Users className="w-3.5 h-3.5" />
              {invitation.currentUses}/{invitation.maxUses ?? "∞"} użyć
            </span>
            {invitation.expiresAt && (
              <span className="flex items-center gap-1">
                <Clock className="w-3.5 h-3.5" />
                Ważne do {timee.formatDate(invitation.expiresAt)}
              </span>
            )}
          </div>
        </div>

        <div className="flex items-center gap-1">
          {isActive && (
            <>
              <Button
                variant="ghost"
                size="icon"
                onClick={() => onCopy(invitation.invitationCode)}
                title="Kopiuj kod"
              >
                <Copy className="w-4 h-4" />
              </Button>
              <Button
                variant="ghost"
                size="icon"
                onClick={() => onDeactivate(invitation.id)}
                title="Dezaktywuj zaproszenie"
                className="text-destructive hover:text-destructive"
              >
                <XCircle className="w-4 h-4" />
              </Button>
            </>
          )}
        </div>
      </div>
    </div>
  );
};

/**
 * Komponent generatora zaproszeń
 */
export const InvitationGenerator = () => {
  const [isDialogOpen, setIsDialogOpen] = useState(false);
  const [formData, setFormData] = useState<CreateInvitationRequest>({
    maxUses: 10,
    name: "",
  });
  const [expiresInDays, setExpiresInDays] = useState(30);

  const { data: invitationsPage, isLoading } = useTeacherInvitations();
  const createMutation = useCreateInvitation();
  const deactivateMutation = useDeactivateInvitation();

  const invitations = invitationsPage?.content || [];

  const handleCopyCode = async (code: string) => {
    try {
      await navigator.clipboard.writeText(code);
      toast.success("Skopiowano kod do schowka!");
    } catch (err) {
      console.error("Nie udało się skopiować kodu:", err);
      toast.error("Nie udało się skopiować kodu");
    }
  };

  const handleCopyLink = async (code: string) => {
    const link = `${window.location.origin}/join?code=${code}`;
    try {
      await navigator.clipboard.writeText(link);
      toast.success("Skopiowano link do schowka!");
    } catch (err) {
      console.error("Nie udało się skopiować linku:", err);
      toast.error("Nie udało się skopiować linku");
    }
  };

  const handleCreateInvitation = () => {
    const expiresAt = new Date();
    expiresAt.setDate(expiresAt.getDate() + expiresInDays);

    createMutation.mutate(
      {
        ...formData,
        expiresAt: expiresAt.toISOString(),
      },
      {
        onSuccess: (newInvitation) => {
          setIsDialogOpen(false);
          setFormData({ maxUses: 10, name: "" });
          setExpiresInDays(30);
          handleCopyCode(newInvitation.invitationCode);
        },
      }
    );
  };

  const handleDeactivate = (invitationId: string) => {
    if (window.confirm("Czy na pewno chcesz dezaktywować to zaproszenie?")) {
      deactivateMutation.mutate(invitationId);
    }
  };

  const activeInvitations = invitations.filter(
    (i) =>
      i.status === "ACTIVE" &&
      (!i.expiresAt || !isExpired(i.expiresAt)) &&
      (!i.maxUses || i.currentUses < i.maxUses)
  );
  const inactiveInvitations = invitations.filter(
    (i) =>
      i.status !== "ACTIVE" ||
      (i.expiresAt && isExpired(i.expiresAt)) ||
      (i.maxUses && i.currentUses >= i.maxUses)
  );

  return (
    <>
      <Card>
        <CardHeader className="border-b">
          <div className="flex items-center justify-between">
            <div>
              <CardTitle className="flex items-center gap-2">
                <Link2 className="w-5 h-5" />
                Zaproszenia
              </CardTitle>
              <p className="text-sm text-muted-foreground mt-1">
                Generuj kody, które studenci mogą użyć, aby dołączyć do Ciebie
              </p>
            </div>
            <Button onClick={() => setIsDialogOpen(true)}>
              <Plus className="w-4 h-4 mr-2" />
              Nowe zaproszenie
            </Button>
          </div>
        </CardHeader>

        <CardContent className="pt-6">
          {isLoading ? (
            <div className="flex items-center justify-center py-8">
              <RefreshCw className="w-6 h-6 animate-spin text-muted-foreground" />
            </div>
          ) : invitations.length === 0 ? (
            <div className="text-center py-8">
              <AlertCircle className="w-12 h-12 mx-auto mb-4 text-muted-foreground" />
              <p className="text-muted-foreground mb-4">
                Nie masz jeszcze żadnych zaproszeń
              </p>
              <Button onClick={() => setIsDialogOpen(true)}>
                <Plus className="w-4 h-4 mr-2" />
                Utwórz pierwsze zaproszenie
              </Button>
            </div>
          ) : (
            <div className="space-y-6">
              {/* Aktywne zaproszenia */}
              {activeInvitations && activeInvitations.length > 0 && (
                <div>
                  <h3 className="text-sm font-medium mb-3">
                    Aktywne ({activeInvitations.length})
                  </h3>
                  <div className="space-y-3">
                    {activeInvitations.map((invitation) => (
                      <InvitationCard
                        key={invitation.id}
                        invitation={invitation}
                        onDeactivate={handleDeactivate}
                        onCopy={handleCopyCode}
                      />
                    ))}
                  </div>
                </div>
              )}

              {/* Nieaktywne zaproszenia */}
              {inactiveInvitations && inactiveInvitations.length > 0 && (
                <div>
                  <h3 className="text-sm font-medium mb-3 text-muted-foreground">
                    Historia ({inactiveInvitations.length})
                  </h3>
                  <div className="space-y-3">
                    {inactiveInvitations.map((invitation) => (
                      <InvitationCard
                        key={invitation.id}
                        invitation={invitation}
                        onDeactivate={handleDeactivate}
                        onCopy={handleCopyCode}
                      />
                    ))}
                  </div>
                </div>
              )}
            </div>
          )}
        </CardContent>
      </Card>

      {/* Dialog tworzenia zaproszenia */}
      <Dialog open={isDialogOpen} onOpenChange={setIsDialogOpen}>
        <DialogContent>
          <DialogHeader>
            <DialogTitle>Utwórz nowe zaproszenie</DialogTitle>
            <DialogDescription>
              Wygeneruj kod, który studenci będą mogli użyć, aby dołączyć do
              Ciebie jako nauczyciela
            </DialogDescription>
          </DialogHeader>

          <div className="space-y-4 py-4">
            <div className="space-y-2">
              <Label htmlFor="name">Nazwa (opcjonalnie)</Label>
              <Textarea
                id="name"
                placeholder="np. Zaproszenie dla klasy 2A"
                value={formData.name || ""}
                onChange={(e) =>
                  setFormData((prev) => ({
                    ...prev,
                    name: e.target.value,
                  }))
                }
              />
            </div>

            <div className="grid grid-cols-2 gap-4">
              <div className="space-y-2">
                <Label htmlFor="maxUses">Maksymalna liczba użyć</Label>
                <Input
                  id="maxUses"
                  type="number"
                  min="1"
                  max="100"
                  value={formData.maxUses}
                  onChange={(e) =>
                    setFormData((prev) => ({
                      ...prev,
                      maxUses: parseInt(e.target.value) || 10,
                    }))
                  }
                />
              </div>

              <div className="space-y-2">
                <Label htmlFor="expiresInDays">Ważność (dni)</Label>
                <Input
                  id="expiresInDays"
                  type="number"
                  min="1"
                  max="365"
                  value={expiresInDays}
                  onChange={(e) =>
                    setExpiresInDays(parseInt(e.target.value) || 30)
                  }
                />
              </div>
            </div>
          </div>

          <DialogFooter>
            <Button variant="outline" onClick={() => setIsDialogOpen(false)}>
              Anuluj
            </Button>
            <Button
              onClick={handleCreateInvitation}
              disabled={createMutation.isPending}
            >
              {createMutation.isPending ? (
                <>
                  <RefreshCw className="w-4 h-4 mr-2 animate-spin" />
                  Generowanie...
                </>
              ) : (
                <>
                  <Plus className="w-4 h-4 mr-2" />
                  Utwórz zaproszenie
                </>
              )}
            </Button>
          </DialogFooter>
        </DialogContent>
      </Dialog>
    </>
  );
};
