"use client";

import { AnimatedContainer } from "@/components/common/effects/AnimatedContainer";
import { SectionHeader } from "@/components/common/SectionHeader";
import { Card } from "@/components/ui/card";
import { BookMarked, CalendarClock, Sparkles } from "lucide-react";

const LOOP_STEPS = [
  {
    icon: BookMarked,
    eyebrow: "01 · Materiał",
    title: "Zapisujesz słowo",
    description:
      "Dodajesz je do własnej talii razem z tłumaczeniem i tym, czego naprawdę chcesz się nauczyć.",
  },
  {
    icon: Sparkles,
    eyebrow: "02 · Kontekst",
    title: "AI podpowiada użycie",
    description:
      "Generujesz przykładowe zdanie, sprawdzasz je i uczysz się słowa w kontekście zamiast w oderwaniu.",
  },
  {
    icon: CalendarClock,
    eyebrow: "03 · Pamięć",
    title: "Słowo wraca na czas",
    description:
      "Po każdej odpowiedzi Lingendo aktualizuje postęp i planuje następną powtórkę bez ręcznego układania kolejki.",
  },
];

export function LearningLoopSection() {
  return (
    <section className="relative isolate overflow-hidden border-y border-primary/10 bg-primary/[0.025] px-4 py-20 sm:py-28">
      <div className="pointer-events-none absolute left-1/2 top-0 -z-10 h-72 w-72 -translate-x-1/2 rounded-full bg-lime-300/15 blur-3xl" />
      <div className="pointer-events-none absolute inset-0 -z-10 bg-[linear-gradient(to_right,var(--border)_1px,transparent_1px),linear-gradient(to_bottom,var(--border)_1px,transparent_1px)] bg-[size:48px_48px] opacity-20 [mask-image:linear-gradient(to_bottom,transparent,black_20%,black_80%,transparent)]" />

      <div className="mx-auto max-w-6xl">
        <SectionHeader
          eyebrow="Codzienny cykl nauki"
          title="Od nowego słowa do trwałej pamięci"
          subtitle="Każdy element ma swoje miejsce: własny materiał, kontekst wspierany przez AI i powtórka zaplanowana na podstawie Twoich odpowiedzi."
        />

        <div className="relative mt-12 grid gap-6 lg:grid-cols-3">
          <div className="absolute left-[16.66%] right-[16.66%] top-10 hidden h-px bg-gradient-to-r from-transparent via-primary/40 to-transparent lg:block" />
          {LOOP_STEPS.map((step) => (
            <AnimatedContainer key={step.title} variant="slide" className="h-full">
              <Card className="relative h-full overflow-hidden border-primary/10 bg-card/80 p-7 shadow-sm backdrop-blur-sm transition-all hover:-translate-y-1 hover:border-primary/30 hover:shadow-lg hover:shadow-primary/5">
                <div className="absolute right-0 top-0 h-24 w-24 rounded-bl-full bg-primary/5" />
                <div className="relative flex size-12 items-center justify-center rounded-2xl border border-primary/15 bg-primary/10 text-primary shadow-sm">
                  <step.icon className="size-6" />
                </div>
                <p className="mt-7 text-xs font-semibold uppercase tracking-[0.18em] text-primary">
                  {step.eyebrow}
                </p>
                <h3 className="mt-2 text-xl font-semibold">{step.title}</h3>
                <p className="mt-3 leading-relaxed text-muted-foreground">
                  {step.description}
                </p>
              </Card>
            </AnimatedContainer>
          ))}
        </div>

        <p className="mx-auto mt-10 max-w-3xl text-center text-sm leading-relaxed text-muted-foreground">
          To środowisko demonstracyjne projektu portfolio. Możesz sprawdzić
          podstawowy przepływ aplikacji, ale nie wprowadzaj danych wrażliwych —
          zawartość może być okresowo resetowana.
        </p>
      </div>
    </section>
  );
}
