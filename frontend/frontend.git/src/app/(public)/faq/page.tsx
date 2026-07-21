"use client";

import { useState } from "react";
import Link from "next/link";
import {
  ArrowUpRight,
  BookOpen,
  ChevronDown,
  FlaskConical,
  Github,
  HelpCircle,
  Search,
  Sparkles,
} from "lucide-react";
import { AnimatedContainer } from "@/components/common/effects/AnimatedContainer";
import { Button } from "@/components/ui/button";
import { Card } from "@/components/ui/card";
import { Input } from "@/components/ui/input";
import { cn } from "@/lib/utils";
import { BackgroundWithGreen } from "@/components/common/BackgroundWithGreen";

interface FAQItem {
  question: string;
  answer: string;
}

interface FAQCategory {
  id: string;
  name: string;
  icon: React.ElementType;
  items: FAQItem[];
}

const faqCategories: FAQCategory[] = [
  {
    id: "getting-started",
    name: "Pierwsze kroki",
    icon: BookOpen,
    items: [
      {
        question: "Jak założyć konto w Lingendo?",
        answer:
          "W formularzu rejestracji podaj imię, nazwisko, nazwę użytkownika, e-mail i hasło. Publiczna rejestracja tworzy zwykłe konto demonstracyjne — nie wybierasz planu ani roli administratora lub nauczyciela.",
      },
      {
        question: "Czy korzystanie z wersji demonstracyjnej jest płatne?",
        answer:
          "Nie. Lingendo nie ma obecnie płatnych planów ani aktywnej sprzedaży. Cennik został usunięty, a demo służy prezentacji projektu i testowaniu rozwijanych funkcji.",
      },
      {
        question: "Jak utworzyć pierwszą talię?",
        answer:
          "Po zalogowaniu przejdź do sekcji „Moje talie” i wybierz utworzenie nowej talii. Następnie możesz dodać słowa i rozpocząć sesję nauki.",
      },
      {
        question: "Czy mogę importować lub eksportować słówka?",
        answer:
          "Nie w obecnej wersji demonstracyjnej. Import i eksport nie są jeszcze częścią potwierdzonego podstawowego przepływu aplikacji.",
      },
    ],
  },
  {
    id: "learning",
    name: "Nauka i powtórki",
    icon: Sparkles,
    items: [
      {
        question: "Jak działają powtórki przestrzenne?",
        answer:
          "Po odpowiedzi aplikacja aktualizuje postęp fiszki i planuje jej kolejne pojawienie się. Trudniejsze słowa wracają wcześniej, a dobrze zapamiętane — później.",
      },
      {
        question: "Do czego Lingendo wykorzystuje AI?",
        answer:
          "AI pomaga generować zdania pokazujące słowo w kontekście. Wygenerowane treści mogą zawierać błędy, dlatego warto je zweryfikować przed dodaniem do materiału.",
      },
      {
        question: "Czy aplikacja działa offline?",
        answer:
          "Nie. Obecna wersja webowa wymaga połączenia z internetem, ponieważ dane, sesje nauki i generowanie treści obsługują usługi backendowe.",
      },
      {
        question: "Gdzie zobaczę swój postęp?",
        answer:
          "Podsumowanie znajduje się na dashboardzie i w sekcji statystyk. Zakres metryk będzie porządkowany wraz z rozwojem zwykłego przepływu użytkownika.",
      },
    ],
  },
  {
    id: "demo",
    name: "O środowisku demo",
    icon: FlaskConical,
    items: [
      {
        question: "Czy mogę przechowywać tu ważne dane?",
        answer:
          "Nie. To publiczne środowisko demonstracyjne, więc nie należy wprowadzać danych wrażliwych ani materiałów, których utrata byłaby problemem. Dane mogą być okresowo resetowane.",
      },
      {
        question: "Gdzie działa aplikacja?",
        answer:
          "Frontend i usługi Lingendo są wdrażane na prywatnym klastrze k3s utrzymywanym przez autora projektu.",
      },
      {
        question: "Czy panel nauczyciela jest już gotowym produktem?",
        answer:
          "Nie. Repozytorium zawiera funkcje nauczyciela i ucznia, ale publiczna rejestracja skupia się teraz na zwykłym koncie. Ten obszar będzie rozwijany po uporządkowaniu podstawowego przepływu nauki.",
      },
      {
        question: "Gdzie zgłosić błąd lub zobaczyć kod?",
        answer:
          "Kod projektu jest dostępny w repozytorium WGrzesiek/lingendo na GitHubie. Więcej informacji o autorze i pozostałych projektach znajdziesz na gwawrzen.pl.",
      },
    ],
  },
];

function FAQAccordion({ item }: { item: FAQItem }) {
  const [isOpen, setIsOpen] = useState(false);

  return (
    <div className="border-b border-border px-3 last:border-0">
      <button
        type="button"
        className="flex w-full items-start justify-between gap-4 py-5 text-left transition-colors hover:text-primary"
        onClick={() => setIsOpen((current) => !current)}
        aria-expanded={isOpen}
      >
        <span className="font-medium">{item.question}</span>
        <ChevronDown
          className={cn(
            "size-5 shrink-0 text-muted-foreground transition-transform",
            isOpen && "rotate-180"
          )}
        />
      </button>
      <div
        className={cn(
          "overflow-hidden transition-all duration-300",
          isOpen ? "max-h-96 pb-5" : "max-h-0"
        )}
      >
        <p className="leading-relaxed text-muted-foreground">{item.answer}</p>
      </div>
    </div>
  );
}

export default function FAQPage() {
  const [searchQuery, setSearchQuery] = useState("");
  const [selectedCategory, setSelectedCategory] = useState<string | null>(null);

  const filteredCategories = faqCategories
    .map((category) => ({
      ...category,
      items: category.items.filter(
        (item) =>
          item.question.toLowerCase().includes(searchQuery.toLowerCase()) ||
          item.answer.toLowerCase().includes(searchQuery.toLowerCase())
      ),
    }))
    .filter((category) => category.items.length > 0);

  const displayCategories = selectedCategory
    ? filteredCategories.filter((category) => category.id === selectedCategory)
    : filteredCategories;

  return (
    <main className="relative isolate min-h-screen overflow-hidden">
      <BackgroundWithGreen />
      <section className="relative z-10 overflow-hidden py-20 md:py-28">
        <div className="absolute inset-0 bg-gradient-to-b from-primary/5 via-background to-background" />
        <AnimatedContainer
          variant="slide"
          className="container relative mx-auto max-w-3xl px-6 text-center"
        >
          <span className="mb-6 inline-block rounded-full bg-primary/10 px-4 py-1.5 text-sm font-medium text-primary">
            Informacje o demo
          </span>
          <h1 className="mb-6 text-4xl font-bold tracking-tight md:text-5xl lg:text-6xl">
            Najczęstsze <span className="text-primary">pytania</span>
          </h1>
          <p className="mb-8 text-lg leading-relaxed text-muted-foreground md:text-xl">
            Konkretnie o funkcjach, ograniczeniach i statusie projektu.
          </p>
          <div className="relative mx-auto max-w-xl">
            <Search className="absolute left-4 top-1/2 size-5 -translate-y-1/2 text-muted-foreground" />
            <Input
              type="search"
              placeholder="Szukaj w FAQ…"
              value={searchQuery}
              onChange={(event) => setSearchQuery(event.target.value)}
              className="h-12 pl-12 text-base"
            />
          </div>
        </AnimatedContainer>
      </section>

      <section className="relative z-10 pb-8">
        <div className="container mx-auto flex flex-wrap justify-center gap-3 px-6">
          <Button
            variant={selectedCategory === null ? "default" : "outline"}
            size="sm"
            onClick={() => setSelectedCategory(null)}
          >
            Wszystkie
          </Button>
          {faqCategories.map((category) => (
            <Button
              key={category.id}
              variant={selectedCategory === category.id ? "default" : "outline"}
              size="sm"
              onClick={() => setSelectedCategory(category.id)}
            >
              <category.icon className="mr-2 size-4" />
              {category.name}
            </Button>
          ))}
        </div>
      </section>

      <section className="container relative z-10 mx-auto max-w-4xl px-6 pb-24 pt-8">
        {displayCategories.length === 0 ? (
          <div className="py-12 text-center">
            <HelpCircle className="mx-auto mb-4 size-12 text-muted-foreground" />
            <h2 className="text-xl font-semibold">Brak wyników</h2>
            <Button
              variant="outline"
              className="mt-5"
              onClick={() => setSearchQuery("")}
            >
              Wyczyść wyszukiwanie
            </Button>
          </div>
        ) : (
          <div className="space-y-8">
            {displayCategories.map((category) => (
              <AnimatedContainer key={category.id} variant="slide">
                <div className="mb-4 flex items-center gap-3">
                  <span className="rounded-lg bg-primary/10 p-2">
                    <category.icon className="size-5 text-primary" />
                  </span>
                  <h2 className="text-xl font-bold">{category.name}</h2>
                </div>
                <Card className="divide-y divide-border">
                  {category.items.map((item) => (
                    <FAQAccordion key={item.question} item={item} />
                  ))}
                </Card>
              </AnimatedContainer>
            ))}
          </div>
        )}
      </section>

      <section className="relative z-10 border-t border-primary/5 bg-muted/30 py-16 backdrop-blur-[2px] md:py-20">
        <div className="container mx-auto max-w-2xl px-6 text-center">
          <h2 className="text-3xl font-bold md:text-4xl">Zajrzyj za kulisy</h2>
          <p className="mt-4 text-lg text-muted-foreground">
            Architektura i kod Lingendo są częścią publicznego portfolio autora.
          </p>
          <div className="mt-8 flex flex-col justify-center gap-4 sm:flex-row">
            <Button size="lg" asChild>
              <Link
                href="https://gwawrzen.pl"
                target="_blank"
                rel="noopener noreferrer"
              >
                Portfolio <ArrowUpRight className="ml-2 size-5" />
              </Link>
            </Button>
            <Button size="lg" variant="outline" asChild>
              <Link
                href="https://github.com/WGrzesiek/lingendo"
                target="_blank"
                rel="noopener noreferrer"
              >
                <Github className="mr-2 size-5" /> Kod projektu
              </Link>
            </Button>
          </div>
        </div>
      </section>
    </main>
  );
}
