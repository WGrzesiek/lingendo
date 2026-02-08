"use client";

import { useEffect } from "react";
import { Button } from "@/components/ui/button";
import { AlertTriangle, RefreshCw, Home, ArrowLeft } from "lucide-react";

interface ErrorProps {
  error: Error & { digest?: string };
  reset: () => void;
}

/**
 * Strona błędu wyświetlana gdy wystąpi niespodziewany błąd w aplikacji.
 * Wyświetla przyjazny komunikat dla użytkownika zamiast technicznych szczegółów.
 */
export default function Error({ error, reset }: ErrorProps) {
  useEffect(() => {
    console.error("Wystąpił błąd aplikacji:", error);
  }, [error]);

  return (
    <div className="min-h-[calc(100vh-4rem)] flex items-center justify-center px-4 py-16">
      <div className="max-w-md w-full text-center space-y-6">
        {/* Ikona błędu */}
        <div className="mx-auto w-20 h-20 rounded-full bg-destructive/10 flex items-center justify-center">
          <AlertTriangle className="w-10 h-10 text-destructive" />
        </div>

        {/* Nagłówek */}
        <div className="space-y-2">
          <h1 className="text-2xl font-bold text-foreground">
            Ups! Coś poszło nie tak
          </h1>
          <p className="text-muted-foreground">
            Przepraszamy, wystąpił nieoczekiwany błąd. Pracujemy nad jego
            naprawieniem.
          </p>
        </div>

        {/* Kod błędu (jeśli dostępny) */}
        {error.digest && (
          <div className="bg-muted/50 rounded-lg px-4 py-2 inline-block">
            <p className="text-xs text-muted-foreground">
              Kod błędu:{" "}
              <code className="font-mono text-foreground">{error.digest}</code>
            </p>
          </div>
        )}

        {/* Przyciski akcji */}
        <div className="flex flex-col sm:flex-row gap-3 justify-center pt-4">
          <Button onClick={reset} variant="default" className="gap-2">
            <RefreshCw className="w-4 h-4" />
            Spróbuj ponownie
          </Button>
          <Button
            variant="outline"
            className="gap-2"
            onClick={() => window.history.back()}
          >
            <ArrowLeft className="w-4 h-4" />
            Wróć
          </Button>
          <Button
            variant="ghost"
            className="gap-2"
            onClick={() => (window.location.href = "/")}
          >
            <Home className="w-4 h-4" />
            Strona główna
          </Button>
        </div>

        {/* Wskazówka */}
        <p className="text-sm text-muted-foreground pt-4">
          Jeśli problem się powtarza, skontaktuj się z nami.
        </p>
      </div>
    </div>
  );
}
