"use client";

import { SectionHeader } from "@/components/common/SectionHeader";
import { AnimatedContainer } from "@/components/common/effects/AnimatedContainer";
import { Users, BookOpen, Target, Sparkles } from "lucide-react";

interface Stat {
  icon: React.ElementType;
  value: string;
  label: string;
  description: string;
}

const STATS: Stat[] = [
  {
    icon: Users,
    value: "5,000+",
    label: "Aktywnych użytkowników",
    description: "Uczących się każdego dnia",
  },
  {
    icon: BookOpen,
    value: "500,000+",
    label: "Wygenerowanych fiszek",
    description: "Z przykładami i zdaniami",
  },
  {
    icon: Target,
    value: "92%",
    label: "Skuteczność nauki",
    description: "Słowa zapamiętane trwale",
  },
  {
    icon: Sparkles,
    value: "15 min",
    label: "Średni czas nauki",
    description: "Dziennie wystarcza",
  },
];

export function StatsSection() {
  return (
    <section className="relative overflow-hidden bg-foreground/[0.02] py-16">
      {/* Delikatny gradient w tle */}
      <div className="absolute inset-0 bg-gradient-to-b from-transparent via-lime-400/[0.02] to-transparent" />

      <div className="relative mx-auto max-w-6xl px-4">
        <SectionHeader
          eyebrow="Lingendo w liczbach"
          title="Razem uczymy się więcej"
          subtitle="Dołącz do społeczności, która każdego dnia poszerza swoje słownictwo."
        />

        <div className="mt-12 grid grid-cols-2 gap-6 lg:grid-cols-4">
          {STATS.map((stat) => (
            <AnimatedContainer
              key={stat.label}
              variant="zoom"
              className="group text-center"
            >
              {/* Ikona */}
              <div className="mx-auto mb-4 flex h-14 w-14 items-center justify-center rounded-2xl bg-lime-400/10 ring-1 ring-lime-400/20 transition-all group-hover:bg-lime-400/20 group-hover:ring-lime-400/40">
                <stat.icon className="h-7 w-7 text-lime-400" />
              </div>

              {/* Wartość */}
              <div className="text-3xl font-bold tracking-tight sm:text-4xl">
                {stat.value}
              </div>

              {/* Label */}
              <div className="mt-1 text-sm font-medium">{stat.label}</div>

              {/* Opis */}
              <div className="mt-1 text-xs text-muted-foreground">
                {stat.description}
              </div>
            </AnimatedContainer>
          ))}
        </div>
      </div>
    </section>
  );
}
