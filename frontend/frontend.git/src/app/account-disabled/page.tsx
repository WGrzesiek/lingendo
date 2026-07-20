"use client";

import { Button } from "@/components/ui/button";
import { useRouter } from "next/navigation";

export default function AccountDisabledPage() {
  const router = useRouter();

  return (
    <div className="min-h-screen flex items-center justify-center p-4">
      <div className="max-w-md w-full bg-card p-8 rounded-lg border text-center space-y-4">
        <div className="text-6xl mb-4">🔒</div>
        <h1 className="text-3xl font-bold">Konto nieaktywne</h1>
        <p className="text-muted-foreground">
          Twoje konto zostało dezaktywowane. W środowisku demonstracyjnym dane
          i konta mogą być okresowo porządkowane.
        </p>
        <div className="pt-4 space-y-2">
          <Button onClick={() => router.push("/")}>
            Wróć do strony głównej
          </Button>
        </div>
      </div>
    </div>
  );
}
