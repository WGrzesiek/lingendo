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
  Layers3,
  Users,
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
    title: "Przykłady tworzone przez AI",
    desc: "Do słów możesz generować zdania, które pokazują ich znaczenie w kontekście i ułatwiają zapamiętywanie.",
    footer: "AI wspiera tworzenie materiału, a nie zastępuje nauki.",
  },
  {
    icon: Layers3,
    title: "Własne talie",
    desc: "Twórz zestawy słownictwa, dodawaj fiszki i porządkuj materiał według języka, poziomu oraz tematu.",
    footer: "Materiał dopasowany do tego, czego faktycznie się uczysz.",
  },
  {
    icon: LineChart,
    title: "Przejrzyste statystyki",
    desc: "Śledź postęp: opanowane słowa, skuteczność odpowiedzi, tempo nauki. Liczą się fakty, nie tylko odhaczone lekcje.",
    footer: "Jedno miejsce do oceny postępu i regularności.",
  },
  {
    icon: BookOpenCheck,
    title: "Nauka i powtórki",
    desc: "Osobne sesje nauki i powtórek prowadzą przez materiał krok po kroku, bez konieczności ręcznego planowania kolejności.",
    footer: "Skupiasz się na odpowiedzi, aplikacja pilnuje kolejki.",
  },
  {
    icon: Users,
    title: "Udostępnianie i społeczność",
    desc: "Przeglądaj publiczne talie, zapisuj się do wybranych zestawów i udostępniaj własne materiały innym użytkownikom.",
    footer: "Funkcje społecznościowe są nadal rozwijane.",
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
