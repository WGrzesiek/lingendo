import Link from "next/link";
import { Button } from "@/components/ui/button";
import { Home, Search, ArrowLeft } from "lucide-react";

/**
 * Strona 404 wyświetlana gdy użytkownik próbuje wejść na nieistniejącą stronę.
 */
export default function NotFound() {
  return (
    <div className="min-h-[calc(100vh-4rem)] flex items-center justify-center px-4 py-16">
      <div className="max-w-md w-full text-center space-y-6">
        {/* Numer błędu */}
        <div className="space-y-2">
          <h1 className="text-8xl font-bold text-primary/20">404</h1>
          <h2 className="text-2xl font-bold text-foreground">
            Strona nie została znaleziona
          </h2>
          <p className="text-muted-foreground">
            Przepraszamy, ale strona, której szukasz nie istnieje lub została
            przeniesiona.
          </p>
        </div>

        {/* Ilustracja/ikona */}
        <div className="mx-auto w-24 h-24 rounded-full bg-muted/50 flex items-center justify-center">
          <Search className="w-12 h-12 text-muted-foreground/50" />
        </div>

        {/* Przyciski akcji */}
        <div className="flex flex-col sm:flex-row gap-3 justify-center pt-4">
          <Button asChild variant="default" className="gap-2">
            <Link href="/">
              <Home className="w-4 h-4" />
              Strona główna
            </Link>
          </Button>
          <Button asChild variant="outline" className="gap-2">
            <Link href="/decks">
              <ArrowLeft className="w-4 h-4" />
              Moje talie
            </Link>
          </Button>
        </div>

        {/* Sugestie */}
        <div className="pt-6 space-y-2">
          <p className="text-sm font-medium text-foreground">
            Co mogło pójść nie tak?
          </p>
          <ul className="text-sm text-muted-foreground space-y-1">
            <li>• Adres URL może być nieprawidłowy</li>
            <li>• Strona mogła zostać usunięta</li>
            <li>• Link może być nieaktualny</li>
          </ul>
        </div>
      </div>
    </div>
  );
}
