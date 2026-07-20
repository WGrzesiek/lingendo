import Link from "next/link";
import { BookOpen, FlaskConical, MapPin } from "lucide-react";
import { FooterLinkGroup } from "./FooterLinkGroup";
import { FooterSocials } from "./FooterSocials";

export function Footer() {
  return (
    <footer className="relative border-t border-border/50 bg-gradient-to-b from-background to-primary/[0.03]">
      <div className="absolute inset-x-0 top-0 h-px bg-gradient-to-r from-transparent via-primary/50 to-transparent" />

      <div className="mx-auto max-w-6xl px-6 py-14">
        <div className="grid gap-10 md:grid-cols-2 lg:grid-cols-4">
          <div className="space-y-5 lg:col-span-2">
            <Link href="/" className="inline-flex items-center gap-2.5">
              <span className="rounded-xl bg-primary/10 p-2">
                <BookOpen className="size-5 text-primary" />
              </span>
              <span className="text-xl font-bold tracking-tight">Lingendo</span>
            </Link>
            <p className="max-w-md leading-relaxed text-muted-foreground">
              Demonstracyjna aplikacja do nauki słownictwa z fiszkami,
              powtórkami przestrzennymi i przykładami generowanymi przez AI.
            </p>
            <div className="flex items-center gap-2 text-sm text-muted-foreground">
              <FlaskConical className="size-4 text-primary" />
              <span>Projekt portfolio — funkcje i dane mogą się zmieniać</span>
            </div>
          </div>

          <FooterLinkGroup
            title="Aplikacja"
            links={[
              { label: "Funkcje", href: "/#features" },
              { label: "Jak to działa", href: "/how-it-works" },
              { label: "FAQ", href: "/faq" },
              { label: "Wypróbuj demo", href: "/signup" },
            ]}
          />
          <FooterLinkGroup
            title="Projekt"
            links={[
              { label: "O twórcy", href: "/#creator" },
              { label: "Portfolio", href: "https://gwawrzen.pl" },
              {
                label: "Kod na GitHubie",
                href: "https://github.com/WGrzesiek/lingendo",
              },
            ]}
          />
        </div>

        <div className="my-9 border-t border-border/50" />
        <div className="flex flex-col gap-5 sm:flex-row sm:items-center sm:justify-between">
          <div className="flex flex-col gap-2 text-sm text-muted-foreground sm:flex-row sm:items-center sm:gap-5">
            <p>© {new Date().getFullYear()} Grzegorz Wawrzeń</p>
            <span className="flex items-center gap-2">
              <MapPin className="size-4" /> Polska
            </span>
          </div>
          <FooterSocials />
        </div>
      </div>
    </footer>
  );
}
