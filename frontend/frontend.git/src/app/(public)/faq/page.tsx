"use client";

import { Button } from "@/components/ui/button";
import { Card } from "@/components/ui/card";
import { Input } from "@/components/ui/input";
import Link from "next/link";
import { useState } from "react";
import {
  ArrowRight,
  ChevronDown,
  Search,
  BookOpen,
  CreditCard,
  Settings,
  Users,
  Sparkles,
  HelpCircle,
} from "lucide-react";
import { AnimatedContainer } from "@/components/common/effects/AnimatedContainer";
import { cn } from "@/lib/utils";

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
          "Wystarczy kliknąć przycisk 'Zarejestruj się' w prawym górnym rogu strony. Możesz założyć konto za pomocą e-maila lub zalogować się przez Google. Rejestracja jest darmowa i zajmuje mniej niż minutę.",
      },
      {
        question: "Czy Lingendo jest darmowe?",
        answer:
          "Tak! Plan Basic jest całkowicie darmowy i pozwala na korzystanie z podstawowych funkcji: tworzenie do 3 zestawów fiszek, powtórki z algorytmem Spaced Repetition i podstawowe statystyki. Jeśli potrzebujesz więcej, możesz przejść na plan Premium.",
      },
      {
        question: "Jak stworzyć pierwszy zestaw fiszek?",
        answer:
          "Po zalogowaniu przejdź do sekcji 'Moje kursy' i kliknij 'Utwórz nowy zestaw'. Możesz dodawać słówka ręcznie, importować z CSV lub skorzystać z biblioteki słów społeczności. AI automatycznie wygeneruje przykładowe zdania dla każdego słowa.",
      },
      {
        question: "Czy mogę importować słówka z innych aplikacji?",
        answer:
          "Tak, w planie Premium możesz importować słówka z plików CSV. Obsługujemy również eksport danych, więc Twoje słówka zawsze pozostaną Twoje.",
      },
    ],
  },
  {
    id: "learning",
    name: "Nauka i powtórki",
    icon: Sparkles,
    items: [
      {
        question: "Jak działa algorytm Spaced Repetition?",
        answer:
          "Spaced Repetition (SR) to naukowa metoda nauki, która pokazuje słówka w optymalnych odstępach czasowych. Słowa, które znasz dobrze, pojawiają się rzadziej, a te trudniejsze - częściej. Dzięki temu uczysz się efektywniej i nie tracisz czasu na powtarzanie tego, co już umiesz.",
      },
      {
        question: "Ile czasu dziennie powinienem poświęcić na naukę?",
        answer:
          "Regularna, krótka nauka jest lepsza niż sporadyczne długie sesje. Zalecamy 10-15 minut dziennie. Lingendo pomoże Ci utrzymać serię nauki i przypomni o powtórkach.",
      },
      {
        question: "Czy mogę uczyć się offline?",
        answer:
          "Tak, w planie Premium możesz pobierać zestawy na urządzenie i uczyć się bez dostępu do internetu. Postępy zsynchronizują się automatycznie po połączeniu.",
      },
      {
        question: "Jak AI generuje zdania przykładowe?",
        answer:
          "Lingendo wykorzystuje zaawansowane modele językowe do generowania naturalnych, kontekstowych zdań dla każdego słowa. Zdania są dostosowane do Twojego poziomu zaawansowania i pokazują słowo w typowym użyciu.",
      },
    ],
  },
  {
    id: "pricing",
    name: "Płatności i plany",
    icon: CreditCard,
    items: [
      {
        question: "Jakie są różnice między planami?",
        answer:
          "Plan Basic jest darmowy i zawiera podstawowe funkcje. Plan Premium (19 zł/mies.) daje nielimitowane zestawy, tryb offline, zaawansowane statystyki i priorytetowe wsparcie. Plan Teacher (49 zł/mies.) to wszystko z Premium plus panel nauczyciela i zarządzanie uczniami.",
      },
      {
        question: "Czy mogę anulować subskrypcję?",
        answer:
          "Tak, możesz anulować w każdej chwili z poziomu ustawień konta. Dostęp do funkcji Premium pozostanie aktywny do końca opłaconego okresu rozliczeniowego.",
      },
      {
        question: "Jakie metody płatności są akceptowane?",
        answer:
          "Akceptujemy karty płatnicze (Visa, Mastercard), BLIK oraz przelewy online przez PayU. Dla firm oferujemy również faktury VAT.",
      },
      {
        question: "Czy jest zniżka na płatność roczną?",
        answer:
          "Tak! Płacąc rocznie oszczędzasz 25% w porównaniu do płatności miesięcznej. Premium roczny to 180 zł (15 zł/mies.), a Teacher roczny to 480 zł (40 zł/mies.).",
      },
    ],
  },
  {
    id: "teacher-student",
    name: "Nauczyciele i uczniowie",
    icon: Users,
    items: [
      {
        question: "Jak zostać nauczycielem w Lingendo?",
        answer:
          "Zarejestruj się i wybierz plan Teacher. Po aktywacji konta otrzymasz dostęp do panelu nauczyciela, gdzie możesz tworzyć talie, zapraszać uczniów i śledzić ich postępy.",
      },
      {
        question: "Jak dołączyć do kursu nauczyciela jako uczeń?",
        answer:
          "Potrzebujesz zaproszenia od nauczyciela. Może on wysłać Ci link lub kod zaproszenia. Po dołączeniu automatycznie otrzymasz dostęp do jego talii i będzie on mógł śledzić Twoje postępy.",
      },
      {
        question: "Czy nauczyciel widzi wszystkie moje dane?",
        answer:
          "Nauczyciel widzi tylko postępy w nauce jego talii: ile słówek znasz, kiedy ostatnio się uczyłeś i jakie masz wyniki. Nie ma dostępu do Twoich prywatnych zestawów ani innych danych osobowych.",
      },
      {
        question: "Ilu uczniów może mieć nauczyciel?",
        answer:
          "W standardowym planie Teacher możesz mieć do 50 uczniów. Jeśli potrzebujesz więcej (np. dla szkoły językowej), skontaktuj się z nami - oferujemy indywidualne pakiety.",
      },
    ],
  },
  {
    id: "account",
    name: "Konto i ustawienia",
    icon: Settings,
    items: [
      {
        question: "Jak zmienić hasło?",
        answer:
          "Przejdź do Ustawień konta > Bezpieczeństwo > Zmień hasło. Jeśli zapomniałeś hasła, użyj opcji 'Nie pamiętam hasła' na stronie logowania.",
      },
      {
        question: "Czy mogę usunąć konto?",
        answer:
          "Tak, możesz usunąć konto w Ustawieniach > Konto > Usuń konto. Pamiętaj, że ta operacja jest nieodwracalna i wszystkie Twoje dane zostaną trwale usunięte.",
      },
      {
        question: "Jak wyeksportować swoje dane?",
        answer:
          "W planie Premium możesz eksportować swoje zestawy do formatu CSV. Przejdź do zestawu i wybierz opcję Eksportuj. Możesz również poprosić o pełny eksport danych zgodnie z RODO.",
      },
      {
        question: "Czy mogę zmienić adres e-mail?",
        answer:
          "Tak, przejdź do Ustawień > Konto > Zmień e-mail. Otrzymasz wiadomość weryfikacyjną na nowy adres, którą musisz potwierdzić.",
      },
    ],
  },
  {
    id: "other",
    name: "Inne pytania",
    icon: HelpCircle,
    items: [
      {
        question: "Jak mogę się z Wami skontaktować?",
        answer:
          "Możesz napisać do nas na support@lingendo.app lub skorzystać z formularza kontaktowego na stronie /contact. Użytkownicy Premium i Teacher mają priorytetowe wsparcie z czasem odpowiedzi do 24h.",
      },
      {
        question: "Czy Lingendo jest dostępne na telefon?",
        answer:
          "Obecnie Lingendo działa jako aplikacja webowa, która jest w pełni responsywna i działa świetnie na telefonach. Natywna aplikacja mobilna jest w przygotowaniu.",
      },
      {
        question: "W jakich językach mogę się uczyć?",
        answer:
          "Lingendo obsługuje naukę słownictwa w dowolnym języku. Najpopularniejsze kombinacje to angielski-polski, niemiecki-polski i hiszpański-polski, ale możesz tworzyć zestawy dla dowolnej pary języków.",
      },
      {
        question: "Czy moje dane są bezpieczne?",
        answer:
          "Tak, bezpieczeństwo danych jest dla nas priorytetem. Używamy szyfrowania SSL, bezpiecznych metod autoryzacji i regularnie tworzymy kopie zapasowe. Więcej informacji znajdziesz w naszej Polityce Prywatności.",
      },
    ],
  },
];

function FAQAccordion({ item }: { item: FAQItem }) {
  const [isOpen, setIsOpen] = useState(false);

  return (
    <div className="border-b border-border last:border-0 px-3">
      <button
        className="w-full py-5 flex items-start justify-between gap-4 text-left hover:text-primary transition-colors"
        onClick={() => setIsOpen(!isOpen)}
      >
        <span className="font-medium">{item.question}</span>
        <ChevronDown
          className={cn(
            "w-5 h-5 shrink-0 text-muted-foreground transition-transform",
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
        <p className="text-muted-foreground leading-relaxed">{item.answer}</p>
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
    ? filteredCategories.filter((c) => c.id === selectedCategory)
    : filteredCategories;

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
              Centrum pomocy
            </span>
            <h1 className="text-4xl md:text-5xl lg:text-6xl font-bold tracking-tight mb-6">
              Jak możemy <span className="text-primary">Ci pomóc?</span>
            </h1>
            <p className="text-lg md:text-xl text-muted-foreground leading-relaxed mb-8">
              Znajdź odpowiedzi na najczęściej zadawane pytania lub skontaktuj
              się z nami.
            </p>

            {/* Search */}
            <div className="relative max-w-xl mx-auto">
              <Search className="absolute left-4 top-1/2 -translate-y-1/2 w-5 h-5 text-muted-foreground" />
              <Input
                type="text"
                placeholder="Szukaj w FAQ..."
                value={searchQuery}
                onChange={(e) => setSearchQuery(e.target.value)}
                className="pl-12 h-12 text-base"
              />
            </div>
          </AnimatedContainer>
        </div>
      </section>

      {/* Category Pills */}
      <section className="pb-8">
        <div className="container mx-auto px-6">
          <div className="flex flex-wrap justify-center gap-3">
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
                variant={
                  selectedCategory === category.id ? "default" : "outline"
                }
                size="sm"
                onClick={() => setSelectedCategory(category.id)}
              >
                <category.icon className="w-4 h-4 mr-2" />
                {category.name}
              </Button>
            ))}
          </div>
        </div>
      </section>

      {/* FAQ Content */}
      <section className="py-8 pb-24">
        <div className="container mx-auto px-6 max-w-4xl">
          {displayCategories.length === 0 ? (
            <AnimatedContainer variant="slide" className="text-center py-12">
              <HelpCircle className="w-12 h-12 text-muted-foreground mx-auto mb-4" />
              <h3 className="text-xl font-semibold mb-2">Brak wyników</h3>
              <p className="text-muted-foreground mb-6">
                Nie znaleźliśmy odpowiedzi pasującej do &quot;{searchQuery}
                &quot;
              </p>
              <Button variant="outline" onClick={() => setSearchQuery("")}>
                Wyczyść wyszukiwanie
              </Button>
            </AnimatedContainer>
          ) : (
            <div className="space-y-8">
              {displayCategories.map((category) => (
                <AnimatedContainer key={category.id} variant="slide">
                  <div className="flex items-center gap-3 mb-4">
                    <div className="p-2 rounded-lg bg-primary/10">
                      <category.icon className="w-5 h-5 text-primary" />
                    </div>
                    <h2 className="text-xl font-bold">{category.name}</h2>
                  </div>
                  <Card className="divide-y divide-border">
                    {category.items.map((item, index) => (
                      <FAQAccordion key={index} item={item} />
                    ))}
                  </Card>
                </AnimatedContainer>
              ))}
            </div>
          )}
        </div>
      </section>

      {/* Contact CTA */}
      <section className="py-16 md:py-24 bg-muted/30">
        <div className="container mx-auto px-6">
          <AnimatedContainer
            variant="slide"
            className="text-center max-w-2xl mx-auto"
          >
            <h2 className="text-3xl md:text-4xl font-bold mb-4">
              Nie znalazłeś odpowiedzi?
            </h2>
            <p className="text-muted-foreground text-lg mb-8">
              Nasz zespół wsparcia jest gotowy, by Ci pomóc. Napisz do nas, a
              odpowiemy najszybciej jak to możliwe.
            </p>
            <div className="flex flex-col sm:flex-row gap-4 justify-center">
              <Button size="lg" className="h-12 px-8" asChild>
                <Link href="mailto:support@lingendo.app">
                  Napisz do nas
                  <ArrowRight className="ml-2 h-5 w-5" />
                </Link>
              </Button>
              <Button size="lg" variant="outline" className="h-12 px-8" asChild>
                <Link href="/signup">Załóż konto</Link>
              </Button>
            </div>
          </AnimatedContainer>
        </div>
      </section>
    </main>
  );
}
