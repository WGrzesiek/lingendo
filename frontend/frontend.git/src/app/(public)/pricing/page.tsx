"use client";

import { Button } from "@/components/ui/button";
import { Card } from "@/components/ui/card";
import { Badge } from "@/components/ui/badge";
import { Switch } from "@/components/ui/switch";
import Link from "next/link";
import { useState } from "react";
import {
  ArrowRight,
  Check,
  X,
  Sparkles,
  GraduationCap,
  Users,
  BookOpen,
} from "lucide-react";
import { AnimatedContainer } from "@/components/common/effects/AnimatedContainer";
import { cn } from "@/lib/utils";

interface Feature {
  label: string;
  included: boolean;
}

interface Tier {
  id: string;
  name: string;
  icon: React.ElementType;
  tagline: string;
  priceMonthly: number;
  priceYearly: number;
  ctaLabel: string;
  ctaHref: string;
  highlighted?: boolean;
  features: Feature[];
}

const TIERS: Tier[] = [
  {
    id: "basic",
    name: "Basic",
    icon: BookOpen,
    tagline: "Darmowy plan dla startujących z Lingendo",
    priceMonthly: 0,
    priceYearly: 0,
    ctaLabel: "Załóż darmowe konto",
    ctaHref: "/signup",
    features: [
      { label: "Fiszki i powtórki (Spaced Repetition)", included: true },
      { label: "Do 3 własnych zestawów", included: true },
      { label: "Podstawowe statystyki", included: true },
      { label: "Społeczność - przeglądanie kursów", included: true },
      { label: "Tryb offline", included: false },
      { label: "Import/eksport CSV", included: false },
      { label: "Zaawansowane statystyki i cele", included: false },
      { label: "Wsparcie e-mail", included: false },
    ],
  },
  {
    id: "premium",
    name: "Premium",
    icon: Sparkles,
    tagline: "Pełny dostęp dla zaawansowanych użytkowników",
    priceMonthly: 19,
    priceYearly: 180,
    ctaLabel: "Wybierz Premium",
    ctaHref: "/signup?plan=premium",
    highlighted: true,
    features: [
      { label: "Wszystko z Basic", included: true },
      { label: "Nielimitowane zestawy i talie", included: true },
      { label: "Kursy społeczności i znajomych", included: true },
      { label: "Zaawansowane statystyki i cele", included: true },
      { label: "Tryb offline", included: true },
      { label: "Import/eksport CSV", included: true },
      { label: "Priorytetowe wsparcie e-mail", included: true },
      { label: "Bez reklam", included: true },
    ],
  },
  {
    id: "student",
    name: "Student",
    icon: GraduationCap,
    tagline: "Dla uczniów korzystających z talii nauczyciela",
    priceMonthly: 0,
    priceYearly: 0,
    ctaLabel: "Dołącz jako uczeń",
    ctaHref: "/signup?type=student",
    features: [
      { label: "Dostęp do talii nauczyciela", included: true },
      { label: "Fiszki i powtórki (Spaced Repetition)", included: true },
      { label: "Śledzenie postępów przez nauczyciela", included: true },
      { label: "Własne zestawy (do 2)", included: true },
      { label: "Podstawowe statystyki", included: true },
      { label: "Komunikacja z nauczycielem", included: true },
      { label: "Kursy społeczności", included: false },
      { label: "Tryb offline", included: false },
    ],
  },
  {
    id: "teacher",
    name: "Teacher",
    icon: Users,
    tagline: "Dla nauczycieli i szkół językowych",
    priceMonthly: 49,
    priceYearly: 480,
    ctaLabel: "Zostań nauczycielem",
    ctaHref: "/signup?type=teacher",
    features: [
      { label: "Panel nauczyciela", included: true },
      { label: "Tworzenie i udostępnianie talii", included: true },
      { label: "Zarządzanie uczniami (do 50)", included: true },
      { label: "Śledzenie postępów uczniów", included: true },
      { label: "Przypisywanie zadań i terminów", included: true },
      { label: "Priorytetowe wsparcie", included: true },
      { label: "Fakturowanie zbiorcze", included: true },
      { label: "Raporty i eksporty", included: true },
    ],
  },
];

const faqs = [
  {
    q: "Czy mogę zmienić plan później?",
    a: "Tak! Możesz w każdej chwili przejść na wyższy plan lub wrócić do darmowego. Twoje dane i postępy zostaną zachowane.",
  },
  {
    q: "Czy są jakieś ukryte opłaty?",
    a: "Nie. Pokazane ceny to pełny koszt. Plan Basic jest darmowy na zawsze, bez żadnych haczków.",
  },
  {
    q: "Jak działa plan Student?",
    a: "Plan Student jest darmowy i wymaga zaproszenia od nauczyciela. Otrzymujesz dostęp do jego talii i może on śledzić Twoje postępy.",
  },
  {
    q: "Czy mogę anulować subskrypcję?",
    a: "Oczywiście. Możesz anulować w każdej chwili. Dostęp do funkcji Premium/Teacher pozostanie aktywny do końca opłaconego okresu.",
  },
];

export default function PricingPage() {
  const [isYearly, setIsYearly] = useState(false);

  return (
    <main className="min-h-screen">
      {/* Hero Section */}
      <section className="relative overflow-hidden py-20 md:py-28">
        <div className="absolute inset-0 bg-gradient-to-b from-primary/5 via-background to-background" />
        <div className="container mx-auto px-6 relative">
          <AnimatedContainer
            variant="slide"
            className="text-center max-w-3xl mx-auto"
          >
            <span className="inline-block px-4 py-1.5 rounded-full bg-primary/10 text-primary text-sm font-medium mb-6">
              Cennik
            </span>
            <h1 className="text-4xl md:text-5xl lg:text-6xl font-bold tracking-tight mb-6">
              Wybierz plan{" "}
              <span className="text-primary">dopasowany do Ciebie</span>
            </h1>
            <p className="text-lg md:text-xl text-muted-foreground leading-relaxed mb-8">
              Zacznij za darmo z Basic. Przejdź na Premium po więcej funkcji.
              Uczniowie i nauczyciele mają dedykowane plany.
            </p>

            {/* Billing Toggle */}
            <div className="flex items-center justify-center gap-4">
              <span
                className={cn(
                  "text-sm font-medium",
                  !isYearly ? "text-foreground" : "text-muted-foreground"
                )}
              >
                Miesięcznie
              </span>
              <Switch checked={isYearly} onCheckedChange={setIsYearly} />
              <span
                className={cn(
                  "text-sm font-medium",
                  isYearly ? "text-foreground" : "text-muted-foreground"
                )}
              >
                Rocznie
              </span>
              {isYearly && (
                <Badge
                  variant="secondary"
                  className="bg-green-500/10 text-green-600"
                >
                  Oszczędzasz 25%
                </Badge>
              )}
            </div>
          </AnimatedContainer>
        </div>
      </section>

      {/* Pricing Cards */}
      <section className="pb-16 md:pb-24">
        <div className="container mx-auto px-6">
          <div className="grid md:grid-cols-2 lg:grid-cols-4 gap-6">
            {TIERS.map((tier, index) => (
              <AnimatedContainer key={tier.id} variant="zoom">
                <Card
                  className={cn(
                    "relative p-6 h-full flex flex-col",
                    tier.highlighted &&
                      "border-primary shadow-lg shadow-primary/10 scale-[1.02]"
                  )}
                >
                  {tier.highlighted && (
                    <Badge className="absolute -top-3 left-1/2 -translate-x-1/2 bg-primary">
                      Najpopularniejszy
                    </Badge>
                  )}

                  <div className="mb-6">
                    <div className="flex items-center gap-3 mb-3">
                      <div
                        className={cn(
                          "p-2 rounded-lg",
                          tier.highlighted ? "bg-primary/10" : "bg-muted"
                        )}
                      >
                        <tier.icon
                          className={cn(
                            "w-5 h-5",
                            tier.highlighted
                              ? "text-primary"
                              : "text-muted-foreground"
                          )}
                        />
                      </div>
                      <h3 className="text-xl font-bold">{tier.name}</h3>
                    </div>
                    <p className="text-sm text-muted-foreground">
                      {tier.tagline}
                    </p>
                  </div>

                  <div className="mb-6">
                    <div className="flex items-baseline gap-1">
                      <span className="text-4xl font-bold">
                        {isYearly
                          ? Math.round(tier.priceYearly / 12)
                          : tier.priceMonthly}
                      </span>
                      <span className="text-muted-foreground">zł/mies.</span>
                    </div>
                    {tier.priceMonthly > 0 && isYearly && (
                      <p className="text-sm text-muted-foreground mt-1">
                        {tier.priceYearly} zł rocznie
                      </p>
                    )}
                  </div>

                  <ul className="space-y-3 mb-8 flex-grow">
                    {tier.features.map((feature, i) => (
                      <li key={i} className="flex items-start gap-3">
                        {feature.included ? (
                          <Check className="w-5 h-5 text-green-500 shrink-0 mt-0.5" />
                        ) : (
                          <X className="w-5 h-5 text-muted-foreground/40 shrink-0 mt-0.5" />
                        )}
                        <span
                          className={cn(
                            "text-sm",
                            !feature.included && "text-muted-foreground/60"
                          )}
                        >
                          {feature.label}
                        </span>
                      </li>
                    ))}
                  </ul>

                  <Button
                    className="w-full"
                    variant={tier.highlighted ? "default" : "outline"}
                    asChild
                  >
                    <Link href={tier.ctaHref}>
                      {tier.ctaLabel}
                      <ArrowRight className="ml-2 h-4 w-4" />
                    </Link>
                  </Button>
                </Card>
              </AnimatedContainer>
            ))}
          </div>
        </div>
      </section>

      {/* FAQ Section */}
      <section className="py-16 md:py-24 bg-muted/30">
        <div className="container mx-auto px-6">
          <AnimatedContainer variant="slide" className="text-center mb-12">
            <h2 className="text-3xl md:text-4xl font-bold mb-4">
              Często zadawane pytania
            </h2>
            <p className="text-muted-foreground text-lg">
              Masz więcej pytań?{" "}
              <Link href="/faq" className="text-primary hover:underline">
                Zobacz pełne FAQ
              </Link>
            </p>
          </AnimatedContainer>

          <div className="max-w-3xl mx-auto grid gap-6">
            {faqs.map((faq, index) => (
              <AnimatedContainer key={index} variant="slide">
                <Card className="p-6">
                  <h3 className="font-semibold mb-2">{faq.q}</h3>
                  <p className="text-muted-foreground">{faq.a}</p>
                </Card>
              </AnimatedContainer>
            ))}
          </div>
        </div>
      </section>

      {/* CTA Section */}
      <section className="py-16 md:py-24">
        <div className="container mx-auto px-6">
          <AnimatedContainer
            variant="slide"
            className="text-center max-w-2xl mx-auto"
          >
            <h2 className="text-3xl md:text-4xl font-bold mb-4">
              Nadal nie wiesz, który plan wybrać?
            </h2>
            <p className="text-muted-foreground text-lg mb-8">
              Zacznij od darmowego Basic i zobacz, jak Lingendo zmieni Twoją
              naukę. Możesz przejść na wyższy plan w każdej chwili.
            </p>
            <Button size="lg" className="h-12 px-8" asChild>
              <Link href="/signup">
                Zacznij za darmo
                <ArrowRight className="ml-2 h-5 w-5" />
              </Link>
            </Button>
          </AnimatedContainer>
        </div>
      </section>
    </main>
  );
}
