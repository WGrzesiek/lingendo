"use client";

import { SectionHeader } from "@/components/common/SectionHeader";
import { PricingTable } from "@/components/pricing/PricingTable";
import { type Tier } from "@/components/pricing/TierCard";

const TIERS: Tier[] = [
  {
    id: "basic",
    name: "Basic",
    tagline: "Darmowy plan dla startujących z Lingendo",
    priceMonthly: 0,
    priceYearly: 0,
    ctaLabel: "Załóż darmowe konto",
    features: [
      { label: "Fiszki i powtórki (SR)", included: true },
      { label: "Do 3 własnych zestawów", included: true },
      { label: "Podstawowe statystyki", included: true },
      { label: "Tryb offline", included: false },
      { label: "Import/eksport CSV", included: false },
      { label: "Wsparcie e-mail", included: false },
    ],
  },
  {
    id: "premium",
    name: "Premium",
    tagline: "Kursy społeczności, znajomych i pełny dostęp",
    priceMonthly: 19,
    priceYearly: 180,
    ctaLabel: "Wybierz Premium",
    highlighted: true,
    features: [
      { label: "Wszystko z Basic", included: true },
      { label: "Nielimitowane zestawy i talie", included: true },
      { label: "Kursy społeczności i znajomych", included: true },
      { label: "Zaawansowane statystyki i cele", included: true },
      { label: "Tryb offline + Import/eksport CSV", included: true },
      { label: "Wsparcie e-mail", included: true },
    ],
  },
  {
    id: "student",
    name: "Student",
    tagline: "Dla uczniów korzystających z talii nauczyciela",
    priceMonthly: 0,
    priceYearly: 0,
    ctaLabel: "Dołącz jako uczeń",
    features: [
      { label: "Dostęp do talii nauczyciela", included: true },
      { label: "Fiszki i powtórki (SR)", included: true },
      { label: "Śledzenie postępów przez nauczyciela", included: true },
      { label: "Własne zestawy (ograniczone)", included: true },
      { label: "Podstawowe statystyki", included: true },
      { label: "Wsparcie przez nauczyciela", included: true },
    ],
  },
  {
    id: "teacher",
    name: "Teacher",
    tagline: "Dla nauczycieli i szkół językowych",
    priceMonthly: 49,
    priceYearly: 480,
    ctaLabel: "Zostań nauczycielem",
    features: [
      { label: "Panel nauczyciela", included: true },
      { label: "Tworzenie i udostępnianie talii", included: true },
      { label: "Zarządzanie uczniami", included: true },
      { label: "Śledzenie postępów uczniów", included: true },
      { label: "Priorytetowe wsparcie", included: true },
      { label: "Fakturowanie zbiorcze", included: true },
    ],
  },
];

export function PricingSection() {
  return (
    <section id="pricing" className="mx-auto max-w-6xl px-4 py-16">
      <SectionHeader
        eyebrow="Cennik"
        title="Wybierz plan dopasowany do Twojej nauki"
        subtitle="Zacznij za darmo z Basic. Przejdź na Premium po więcej funkcji. Uczniowie i nauczyciele mają dedykowane plany."
      />
      <div className="mt-10">
        <PricingTable
          tiers={TIERS}
          defaultBilling="monthly"
          onSelect={(id) => console.log("Selected plan:", id)}
        />
      </div>
    </section>
  );
}
