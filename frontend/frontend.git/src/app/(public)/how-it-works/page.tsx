"use client";

import { Button } from "@/components/ui/button";
import { Card } from "@/components/ui/card";
import Link from "next/link";
import {
  ArrowRight,
  BookOpen,
  Brain,
  Target,
  Repeat,
  Sparkles,
  Users,
  Trophy,
} from "lucide-react";
import { AnimatedContainer } from "@/components/common/effects/AnimatedContainer";

const steps = [
  {
    number: "01",
    title: "Twórz lub wybierz zestawy",
    description:
      "Twórz własne fiszki ze słownictwem lub wybierz gotowe zestawy ze społeczności. Każda fiszka zawiera słowo, tłumaczenie i przykładowe zdanie.",
    icon: BookOpen,
    color: "from-blue-500 to-cyan-500",
    img: "/kursy_spol.png",
  },
  {
    number: "02",
    title: "Ucz się z AI",
    description:
      "Lingendo automatycznie generuje kontekstowe zdania i przykłady użycia. AI dostosowuje materiał do Twojego poziomu zaawansowania.",
    icon: Sparkles,
    color: "from-purple-500 to-pink-500",
  },
  {
    number: "03",
    title: "Powtarzaj inteligentnie",
    description:
      "Algorytm Spaced Repetition (SR) planuje powtórki w optymalnych momentach. Uczysz się efektywniej, bo powtarzasz tylko to, co wymaga utrwalenia.",
    icon: Repeat,
    color: "from-green-500 to-emerald-500",
    img: "/powt.png",
  },
  {
    number: "04",
    title: "Śledź postępy",
    description:
      "Szczegółowe statystyki pokazują Twój progres. Wyznaczaj cele, zdobywaj odznaki i rywalizuj ze znajomymi na tablicy wyników.",
    icon: Target,
    color: "from-orange-500 to-amber-500",
    img: "/staty.png",
  },
];

const features = [
  {
    icon: Brain,
    title: "Algorytm Spaced Repetition",
    description:
      "Naukowy system powtórek, który pokazuje słówka dokładnie wtedy, gdy zaczynasz je zapominać.",
  },
  {
    icon: Sparkles,
    title: "Zdania generowane przez AI",
    description:
      "Każde słowo w kontekście - AI tworzy naturalne przykłady użycia dostosowane do Twojego poziomu.",
  },
  {
    icon: Users,
    title: "Społeczność i nauczyciele",
    description:
      "Dołącz do kursów społeczności lub ucz się pod okiem nauczyciela w trybie Student.",
  },
  {
    icon: Trophy,
    title: "Gamifikacja i motywacja",
    description:
      "Serie nauki, odznaki, cele dzienne i tablice wyników sprawiają, że nauka staje się grą.",
  },
];

export default function HowItWorksPage() {
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
              Jak działa Lingendo
            </span>
            <h1 className="text-4xl md:text-5xl lg:text-6xl font-bold tracking-tight mb-6">
              Ucz się słówek{" "}
              <span className="text-primary">mądrzej, nie więcej</span>
            </h1>
            <p className="text-lg md:text-xl text-muted-foreground leading-relaxed">
              Lingendo łączy sprawdzone techniki nauki z nowoczesną technologią
              AI. Zobacz, jak proste kroki prowadzą do realnych efektów.
            </p>
          </AnimatedContainer>
        </div>
      </section>

      {/* Steps Section */}
      <section className="py-16 md:py-24">
        <div className="container mx-auto px-6">
          <div className="grid gap-8 md:gap-12">
            {steps.map((step, index) => (
              <AnimatedContainer
                key={step.number}
                variant="slide"
                className={`flex flex-col ${
                  index % 2 === 1 ? "md:flex-row-reverse" : "md:flex-row"
                } items-center gap-8 md:gap-16`}
              >
                <div className="flex-1 space-y-4">
                  <div className="flex items-center gap-4">
                    <span
                      className={`text-5xl md:text-6xl font-bold bg-gradient-to-r ${step.color} bg-clip-text text-transparent`}
                    >
                      {step.number}
                    </span>
                    <div
                      className={`p-3 rounded-xl bg-gradient-to-r ${step.color}`}
                    >
                      <step.icon className="w-6 h-6 text-white" />
                    </div>
                  </div>
                  <h2 className="text-2xl md:text-3xl font-bold">
                    {step.title}
                  </h2>
                  <p className="text-muted-foreground text-lg leading-relaxed">
                    {step.description}
                  </p>
                </div>
                <div className="flex-1">
                  <Card className="p-8 bg-gradient-to-br from-muted/50 to-muted/20 border-muted">
                    <div
                      className={`w-full aspect-video rounded-lg bg-gradient-to-r ${step.color} opacity-100`}
                    >
                        {step.img && (
                            <img src={step.img}/>
                        )}
                    </div>
                  </Card>
                </div>
              </AnimatedContainer>
            ))}
          </div>
        </div>
      </section>

      {/* Features Grid */}
      <section className="py-16 md:py-24 bg-muted/30">
        <div className="container mx-auto px-6">
          <AnimatedContainer variant="slide" className="text-center mb-12">
            <h2 className="text-3xl md:text-4xl font-bold mb-4">
              Dlaczego Lingendo działa?
            </h2>
            <p className="text-muted-foreground text-lg max-w-2xl mx-auto">
              Wykorzystujemy sprawdzone techniki naukowe i nowoczesne
              technologie, by Twoja nauka była maksymalnie efektywna.
            </p>
          </AnimatedContainer>

          <div className="grid md:grid-cols-2 gap-6">
            {features.map((feature, index) => (
              <AnimatedContainer key={index} variant="zoom">
                <Card className="p-6 h-full hover:shadow-lg transition-shadow">
                  <div className="flex gap-4">
                    <div className="p-3 rounded-xl bg-primary/10 h-fit">
                      <feature.icon className="w-6 h-6 text-primary" />
                    </div>
                    <div>
                      <h3 className="text-xl font-semibold mb-2">
                        {feature.title}
                      </h3>
                      <p className="text-muted-foreground">
                        {feature.description}
                      </p>
                    </div>
                  </div>
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
              Gotowy, by zacząć?
            </h2>
            <p className="text-muted-foreground text-lg mb-8">
              Dołącz do tysięcy użytkowników, którzy już uczą się z Lingendo.
              Załóż darmowe konto i przekonaj się sam.
            </p>
            <div className="flex flex-col sm:flex-row gap-4 justify-center">
              <Button size="lg" className="h-12 px-8" asChild>
                <Link href="/signup">
                  Załóż darmowe konto
                  <ArrowRight className="ml-2 h-5 w-5" />
                </Link>
              </Button>
              <Button size="lg" variant="outline" className="h-12 px-8" asChild>
                <Link href="/pricing">Zobacz cennik</Link>
              </Button>
            </div>
          </AnimatedContainer>
        </div>
      </section>
    </main>
  );
}
