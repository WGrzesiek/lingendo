"use client";

import { useEffect } from "react";
import { usePathname, useRouter } from "next/navigation";
import { LoaderCircle, RefreshCw } from "lucide-react";
import { Button } from "@/components/ui/button";
import { useCurrentUser } from "@/features/auth/hooks/useCurrentUser";

export function AuthBoundary({ children }: { children: React.ReactNode }) {
  const pathname = usePathname();
  const router = useRouter();
  const { data: user, error, isPending, refetch } = useCurrentUser();

  useEffect(() => {
    if (isPending || error) return;

    if (!user) {
      const next = encodeURIComponent(pathname);
      router.replace(`/login?next=${next}`);
      return;
    }

    if (!user.isEnabled) {
      router.replace("/account-disabled");
    }
  }, [error, isPending, pathname, router, user]);

  if (error) {
    return (
      <div className="flex min-h-screen items-center justify-center bg-background p-6">
        <div className="max-w-md rounded-2xl border bg-card p-8 text-center shadow-sm">
          <h1 className="text-xl font-semibold">Nie udało się sprawdzić sesji</h1>
          <p className="mt-2 text-sm text-muted-foreground">
            Sprawdź połączenie i spróbuj ponownie. Nie przekierowujemy do
            logowania przy chwilowym błędzie sieci.
          </p>
          <Button className="mt-6" onClick={() => refetch()}>
            <RefreshCw className="mr-2 size-4" />
            Spróbuj ponownie
          </Button>
        </div>
      </div>
    );
  }

  if (isPending || !user || !user.isEnabled) {
    return (
      <div className="flex min-h-screen items-center justify-center bg-background">
        <div className="flex items-center gap-3 text-sm text-muted-foreground">
          <LoaderCircle className="size-5 animate-spin text-primary" />
          Sprawdzanie sesji…
        </div>
      </div>
    );
  }

  return children;
}
