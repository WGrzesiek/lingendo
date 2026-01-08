"use client";

import { useState, useEffect } from "react";
import { useSearchParams, useRouter } from "next/navigation";
import {
  Card,
  CardHeader,
  CardTitle,
  CardContent,
  CardDescription,
} from "@/components/ui/card";
import { Button } from "@/components/ui/button";
import { Input } from "@/components/ui/input";
import { Label } from "@/components/ui/label";
import { Alert, AlertDescription } from "@/components/ui/alert";
import { Badge } from "@/components/ui/badge";
import {
  UserPlus,
  CheckCircle,
  AlertCircle,
  Loader2,
  GraduationCap,
  Clock,
} from "lucide-react";
import {
  useInvitationInfo,
  useJoinTeacher,
} from "@/features/my-teachers/hooks";
import { useDebounce } from "@/lib/hooks/useDebounce";
import type { InvitationStatus } from "@/features/my-teachers/types";

const statusConfig: Record<
  InvitationStatus,
  {
    label: string;
    variant: "default" | "secondary" | "destructive" | "outline";
  }
> = {
  ACTIVE: { label: "Aktywne", variant: "default" },
  USED: { label: "Wykorzystane", variant: "secondary" },
  EXPIRED: { label: "Wygasłe", variant: "destructive" },
  REVOKED: { label: "Anulowane", variant: "destructive" },
};

/**
 * Strona dołączania do nauczyciela za pomocą kodu zaproszenia
 */
const JoinTeacherPage = () => {
  const searchParams = useSearchParams();
  const router = useRouter();
  const codeFromUrl = searchParams.get("code");

  const [code, setCode] = useState(codeFromUrl || "");
  const [isSuccess, setIsSuccess] = useState(false);

  const debouncedCode = useDebounce(code.trim(), 500);

  const {
    data: invitationInfo,
    isLoading: isLoadingInfo,
    error: infoError,
  } = useInvitationInfo(debouncedCode);

  const joinTeacher = useJoinTeacher();

  useEffect(() => {
    if (codeFromUrl) {
      setCode(codeFromUrl.toUpperCase());
    }
  }, [codeFromUrl]);

  const handleJoin = async () => {
    if (!invitationInfo || invitationInfo.status !== "ACTIVE") return;

    try {
      await joinTeacher.mutateAsync(code.trim());
      setIsSuccess(true);

      setTimeout(() => {
        router.push("/my-teachers");
      }, 2000);
    } catch (error) {
      console.error("Błąd podczas dołączania:", error);
    }
  };

  const handleReset = () => {
    setCode("");
    setIsSuccess(false);
  };

  const formatDate = (dateString: string | null) => {
    if (!dateString) return "Nigdy";
    return new Date(dateString).toLocaleDateString("pl-PL", {
      day: "numeric",
      month: "long",
      year: "numeric",
      hour: "2-digit",
      minute: "2-digit",
    });
  };

  const canJoin =
    invitationInfo &&
    invitationInfo.status === "ACTIVE" &&
    !joinTeacher.isPending;

  return (
    <div className="min-h-screen bg-background flex items-center justify-center p-4">
      <Card className="w-full max-w-md">
        <CardHeader className="text-center">
          <div className="w-16 h-16 bg-primary/10 rounded-full flex items-center justify-center mx-auto mb-4">
            <GraduationCap className="w-8 h-8 text-primary" />
          </div>
          <CardTitle className="text-2xl">Dołącz do nauczyciela</CardTitle>
          <CardDescription>
            Wprowadź kod zaproszenia otrzymany od nauczyciela
          </CardDescription>
        </CardHeader>

        <CardContent className="space-y-6">
          {/* Stan sukcesu */}
          {isSuccess && (
            <div className="text-center py-8">
              <div className="w-16 h-16 bg-green-100 dark:bg-green-900/30 rounded-full flex items-center justify-center mx-auto mb-4">
                <CheckCircle className="w-8 h-8 text-green-600 dark:text-green-400" />
              </div>
              <h3 className="text-xl font-semibold mb-2">Sukces!</h3>
              <p className="text-muted-foreground mb-4">
                Dołączyłeś do nauczyciela!
              </p>
              <p className="text-sm text-muted-foreground">
                Za chwilę zostaniesz przekierowany...
              </p>
            </div>
          )}

          {/* Podgląd zaproszenia */}
          {!isSuccess && invitationInfo && (
            <div className="space-y-4">
              <div className="p-4 bg-muted/50 rounded-lg space-y-3">
                <div className="flex items-center justify-between">
                  <h3 className="font-semibold">{invitationInfo.name}</h3>
                  <Badge variant={statusConfig[invitationInfo.status].variant}>
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
                      {formatDate(invitationInfo.expiresAt)}
                    </span>
                  </div>
                </div>
              </div>

              {invitationInfo.status === "ACTIVE" && (
                <Alert>
                  <AlertCircle className="h-4 w-4" />
                  <AlertDescription>
                    Po dołączeniu, nauczyciel będzie mógł śledzić Twoje postępy
                    w nauce i udostępniać Ci kursy.
                  </AlertDescription>
                </Alert>
              )}

              {invitationInfo.status !== "ACTIVE" && (
                <Alert variant="destructive">
                  <AlertCircle className="h-4 w-4" />
                  <AlertDescription>
                    {invitationInfo.status === "EXPIRED" &&
                      "To zaproszenie wygasło. Poproś nauczyciela o nowy kod."}
                    {invitationInfo.status === "USED" &&
                      "To zaproszenie zostało już w pełni wykorzystane."}
                    {invitationInfo.status === "REVOKED" &&
                      "To zaproszenie zostało anulowane przez nauczyciela."}
                  </AlertDescription>
                </Alert>
              )}

              {joinTeacher.error && (
                <Alert variant="destructive">
                  <AlertCircle className="h-4 w-4" />
                  <AlertDescription>
                    Wystąpił błąd podczas dołączania. Spróbuj ponownie.
                  </AlertDescription>
                </Alert>
              )}

              <div className="flex gap-2">
                <Button
                  variant="outline"
                  className="flex-1"
                  onClick={handleReset}
                  disabled={joinTeacher.isPending}
                >
                  Anuluj
                </Button>
                <Button
                  className="flex-1"
                  onClick={handleJoin}
                  disabled={!canJoin}
                >
                  {joinTeacher.isPending ? (
                    <>
                      <Loader2 className="w-4 h-4 mr-2 animate-spin" />
                      Dołączanie...
                    </>
                  ) : (
                    <>
                      <UserPlus className="w-4 h-4 mr-2" />
                      Dołącz
                    </>
                  )}
                </Button>
              </div>
            </div>
          )}

          {/* Formularz kodu */}
          {!isSuccess && !invitationInfo && (
            <div className="space-y-4">
              <div className="space-y-2">
                <Label htmlFor="code">Kod zaproszenia</Label>
                <Input
                  id="code"
                  placeholder="np. ABC123XY"
                  value={code}
                  onChange={(e) => setCode(e.target.value.toUpperCase())}
                  className="text-center font-mono text-lg tracking-wider"
                />
              </div>

              {/* Loading */}
              {isLoadingInfo && debouncedCode.length >= 6 && (
                <div className="flex items-center justify-center p-4 text-muted-foreground">
                  <Loader2 className="w-4 h-4 mr-2 animate-spin" />
                  Sprawdzanie kodu...
                </div>
              )}

              {/* Błąd */}
              {infoError && debouncedCode.length >= 6 && (
                <Alert variant="destructive">
                  <AlertCircle className="h-4 w-4" />
                  <AlertDescription>
                    Nie znaleziono zaproszenia o podanym kodzie. Sprawdź czy kod
                    został wpisany poprawnie.
                  </AlertDescription>
                </Alert>
              )}

              <p className="text-xs text-center text-muted-foreground">
                Kod zaproszenia otrzymasz od swojego nauczyciela. Upewnij się,
                że wpisujesz go dokładnie tak, jak został podany.
              </p>
            </div>
          )}
        </CardContent>
      </Card>
    </div>
  );
};

export default JoinTeacherPage;
