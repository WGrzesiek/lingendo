"use client";

import { SectionHeader } from "@/components/common/SectionHeader";
import { PricingTable } from "@/components/pricing/PricingTable";
import { type Tier } from "@/components/pricing/TierCard";

const TIERS: Tier[] = [
  {
    id: "free",
    name: "Free",
    tagline: "Dla startujących z Lingendo",
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
    id: "pro",
    name: "Pro",
    tagline: "Dla osób, które chcą regularnego postępu",
    priceMonthly: 19,
    priceYearly: 180,
    ctaLabel: "Wybierz Pro",
    highlighted: true,
    features: [
      { label: "Wszystko z Free", included: true },
      { label: "Nielimitowane zestawy i talie", included: true },
      { label: "Zaawansowane statystyki i cele", included: true },
      { label: "Tryb offline", included: true },
      { label: "Import/eksport CSV", included: true },
      { label: "Wsparcie e-mail", included: true },
    ],
  },
  {
    id: "teams",
    name: "Teams",
    tagline: "Dla szkół i zespołów",
    priceMonthly: 49,
    priceYearly: 480,
    ctaLabel: "Skontaktuj się z nami",
    features: [
      { label: "Wszystko z Pro", included: true },
      { label: "Panel nauczyciela / lidera", included: true },
      { label: "Talie współdzielone", included: true },
      { label: "SSO / SCIM (opcjonalnie)", included: true },
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
        subtitle="Zacznij za darmo. Przejdź na Pro, gdy będziesz gotowy. Dla szkół i firm mamy plan Teams."
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
