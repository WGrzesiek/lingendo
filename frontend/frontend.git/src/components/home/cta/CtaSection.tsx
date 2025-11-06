"use client";

import { GradientCtaBanner } from "@/components/home/cta/GradientCtaBanner";

export default function CtaSection() {
  return (
    <GradientCtaBanner
      title="Poznaj rdzeń Lingendo"
      highlight="Skuteczna nauka słownictwa: powtórki, kontekst i cele"
      ctaLabel="Wypróbuj za darmo"
      onCtaClick={() => console.log("open trial modal")}
    />
  );
}
