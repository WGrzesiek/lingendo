"use client";

import { SectionHeader } from "@/components/common/SectionHeader";
import { Card } from "@/components/ui/card";
import { UserPlus, BookOpen, Brain, Trophy } from "lucide-react";
import { AnimatedContainer } from "@/components/common/effects/AnimatedContainer";

interface Step {
  number: number;
  icon: React.ElementType;
  title: string;
  description: string;
}

const STEPS: Step[] = [
  {
    number: 1,
    icon: UserPlus,
    title: "Załóż konto",
    description:
      "Zarejestruj się za darmo w kilka sekund. Nie wymagamy karty kredytowej ani zobowiązań.",
  },
  {
    number: 2,
    icon: BookOpen,
    title: "Wybierz lub stwórz talie",
    description:
      "Wybierz z gotowych zestawów lub stwórz własne fiszki. AI pomoże Ci wygenerować przykładowe zdania.",
  },
  {
    number: 3,
    icon: Brain,
    title: "Ucz się codziennie",
    description:
      "System powtórek przestrzennych (SR) planuje optymalne momenty na powtórkę każdego słowa.",
  },
  {
    number: 4,
    icon: Trophy,
    title: "Śledź postępy",
    description:
      "Obserwuj swoje statystyki, buduj serie dni nauki i ciesz się z realnych postępów.",
  },
];

export function HowItWorksSection() {
  return (
    <section className="mx-auto max-w-6xl px-4 py-16">
      <SectionHeader
        eyebrow="Jak to działa"
        title="Cztery proste kroki do płynności"
        subtitle="Lingendo sprawia, że nauka słownictwa staje się naturalnym nawykiem."
      />

      <div className="mt-12 grid grid-cols-1 gap-6 sm:grid-cols-2 lg:grid-cols-4">
        {STEPS.map((step) => (
          <AnimatedContainer
            key={step.number}
            variant="slide"
            className="h-full"
          >
            <Card className="relative h-full border-foreground/10 p-6 transition-all hover:border-lime-400/50 hover:shadow-lg hover:shadow-lime-400/5">
              {/* Numer kroku */}
              <div className="absolute -top-3 -left-3 flex h-8 w-8 items-center justify-center rounded-full bg-lime-400 text-sm font-bold text-black">
                {step.number}
              </div>

              {/* Ikona */}
              <div className="mb-4 flex h-12 w-12 items-center justify-center rounded-xl bg-foreground/5 ring-1 ring-foreground/10">
                <step.icon className="h-6 w-6 text-lime-400" />
              </div>

              {/* Treść */}
              <h3 className="mb-2 text-lg font-semibold">{step.title}</h3>
              <p className="text-sm text-muted-foreground">
                {step.description}
              </p>

              {/* Strzałka łącząca (widoczna tylko na desktop, nie na ostatnim) */}
              {step.number < STEPS.length && (
                <div className="absolute right-0 top-1/2 hidden -translate-y-1/2 translate-x-1/2 lg:block">
                  <div className="h-0.5 w-6 bg-gradient-to-r from-foreground/20 to-transparent" />
                </div>
              )}
            </Card>
          </AnimatedContainer>
        ))}
      </div>
    </section>
  );
}
