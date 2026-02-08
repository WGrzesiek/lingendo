"use client";

import { SectionHeader } from "@/components/common/SectionHeader";
import {
  FeatureGrid,
  type FeatureItem,
} from "@/components/home/features/FeatureGrid";
import {
  Brain,
  Repeat2,
  LineChart,
  BookOpenCheck,
  Target,
  Shield,
} from "lucide-react";

const FEATURES: FeatureItem[] = [
  {
    icon: Repeat2,
    title: "Powtórki oparte na nauce",
    desc: "Lingendo planuje kolejne powtórki w optymalnych odstępach (spaced repetition), dzięki czemu zapamiętujesz trwale i bez przeładowania.",
    footer: "Algorytm SR dopasowany do Twojego tempa.",
  },
  {
    icon: Brain,
    title: "Kontekst i przykłady",
    desc: "Każde słowo widzisz w zdaniu i realnym użyciu. Szybciej rozumiesz znaczenie i łatwiej przychodzi mówienie.",
    footer: "Fiszki z dźwiękiem i transkrypcją — opcjonalnie.",
  },
  {
    icon: Target,
    title: "Cele i nawyki",
    desc: "Ustal dzienny cel (np. 15 słów) i buduj serię. Lingendo przypomina o nauce i dba o regularność.",
    footer: "Motywujące „streaki” i powiadomienia.",
  },
  {
    icon: LineChart,
    title: "Przejrzyste statystyki",
    desc: "Śledź postęp: opanowane słowa, skuteczność odpowiedzi, tempo nauki. Liczą się fakty, nie tylko odhaczone lekcje.",
    footer: "Panel postępu dostępny na wszystkich urządzeniach.",
  },
  {
    icon: BookOpenCheck,
    title: "Zestawy i ścieżki",
    desc: "Gotowe talie według poziomu (A1–C1), branży i celu (rozmowa, praca, podróże) — lub twórz własne.",
    footer: "Import/eksport własnych talii w minutę.",
  },
  {
    icon: Shield,
    title: "Prywatność i bezpieczeństwo",
    desc: "Twoje dane są bezpieczne. Hosting w UE, zgodność z RODO i przejrzyste ustawienia prywatności.",
    footer: "Logowanie bezpieczne, opcjonalnie 2FA.",
  },
];

export function FeaturesSection() {
  return (
    <section className="mx-auto max-w-6xl px-4 py-16">
      <SectionHeader
        eyebrow="Dlaczego Lingendo"
        title="Ucz się mądrzej, nie więcej"
        subtitle="Lingendo łączy powtórki przestrzenne, kontekst i cele, aby zamieniać słówka w realny postęp."
      />
      <FeatureGrid items={FEATURES} className="mt-10" />
    </section>
  );
}
