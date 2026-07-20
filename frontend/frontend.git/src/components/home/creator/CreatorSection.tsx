import Link from "next/link";
import { ArrowUpRight, Github, ServerCog } from "lucide-react";
import { Button } from "@/components/ui/button";

export function CreatorSection() {
  return (
    <section id="creator" className="px-4 py-16 sm:py-24">
      <div className="mx-auto grid max-w-6xl overflow-hidden rounded-3xl border bg-card shadow-sm lg:grid-cols-[0.8fr_1.2fr]">
        <div className="flex min-h-64 items-center justify-center bg-gradient-to-br from-emerald-500/20 via-primary/10 to-background p-8">
          <div className="rounded-3xl border border-primary/20 bg-background/80 p-7 shadow-xl backdrop-blur">
            <ServerCog className="size-12 text-primary" />
            <p className="mt-5 max-w-56 text-sm leading-relaxed text-muted-foreground">
              Frontend, mikroserwisy i dane działają na samodzielnie utrzymywanym
              klastrze k3s.
            </p>
          </div>
        </div>

        <div className="flex flex-col justify-center p-8 sm:p-12">
          <p className="text-sm font-semibold uppercase tracking-[0.2em] text-primary">
            O twórcy
          </p>
          <h2 className="mt-3 text-3xl font-bold tracking-tight sm:text-4xl">
            Projekt tworzony i rozwijany przez Grzegorza Wawrzenia
          </h2>
          <p className="mt-5 max-w-2xl leading-relaxed text-muted-foreground">
            Lingendo powstało jako projekt inżynierski, a dziś jest praktycznym
            laboratorium do rozwoju backendu, frontendu i infrastruktury. Kod i
            środowisko demonstracyjne są rozwijane etapami — bez udawania
            gotowego produktu komercyjnego.
          </p>
          <div className="mt-7 flex flex-col gap-3 sm:flex-row">
            <Button asChild>
              <Link
                href="https://gwawrzen.pl"
                target="_blank"
                rel="noopener noreferrer"
              >
                Zobacz portfolio
                <ArrowUpRight className="ml-2 size-4" />
              </Link>
            </Button>
            <Button variant="outline" asChild>
              <Link
                href="https://github.com/WGrzesiek/lingendo"
                target="_blank"
                rel="noopener noreferrer"
              >
                <Github className="mr-2 size-4" />
                Kod projektu
              </Link>
            </Button>
          </div>
        </div>
      </div>
    </section>
  );
}
