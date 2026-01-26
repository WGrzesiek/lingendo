"use client";

import { SectionHeader } from "@/components/common/SectionHeader";
import { Card } from "@/components/ui/card";
import { Star, Quote } from "lucide-react";
import { AnimatedContainer } from "@/components/common/effects/AnimatedContainer";

interface Testimonial {
  id: string;
  name: string;
  role: string;
  content: string;
  rating: number;
  avatarInitials: string;
}

const TESTIMONIALS: Testimonial[] = [
  {
    id: "1",
    name: "Anna Kowalska",
    role: "Studentka anglistyki",
    content:
      "Lingendo całkowicie zmieniło moje podejście do nauki słówek. System powtórek sprawia, że naprawdę zapamiętuję słowa, a nie tylko je przeklikuję.",
    rating: 5,
    avatarInitials: "AK",
  },
  {
    id: "2",
    name: "Michał Nowak",
    role: "Programista, B2",
    content:
      "Jako programista cenię sobie efektywność. Lingendo pozwala mi uczyć się w 15 minut dziennie podczas przerwy na kawę, a postępy są widoczne.",
    rating: 5,
    avatarInitials: "MN",
  },
  {
    id: "3",
    name: "Katarzyna Wiśniewska",
    role: "Nauczycielka języka angielskiego",
    content:
      "Używam Lingendo z moimi uczniami od pół roku. Panel nauczyciela jest świetny - widzę postępy każdego ucznia i mogę dostosować materiał.",
    rating: 5,
    avatarInitials: "KW",
  },
  {
    id: "4",
    name: "Piotr Zieliński",
    role: "Manager, przygotowanie do IELTS",
    content:
      "Przygotowywałem się do IELTS i potrzebowałem poszerzyć słownictwo. Dzięki Lingendo zdałem na 7.5! Szczególnie pomocne były zdania z kontekstem.",
    rating: 5,
    avatarInitials: "PZ",
  },
  {
    id: "5",
    name: "Ewa Dąbrowska",
    role: "Mama dwójki dzieci",
    content:
      "Wróciłam do nauki angielskiego po latach przerwy. Aplikacja jest tak prosta, że mogę się uczyć nawet gdy dzieci śpią. 10 minut dziennie robi różnicę!",
    rating: 5,
    avatarInitials: "ED",
  },
  {
    id: "6",
    name: "Tomasz Lewandowski",
    role: "Student medycyny",
    content:
      "Muszę znać terminologię medyczną po angielsku. Stworzyłem własne talie z terminami i Lingendo pomaga mi je systematycznie powtarzać.",
    rating: 5,
    avatarInitials: "TL",
  },
];

function StarRating({ rating }: { rating: number }) {
  return (
    <div className="flex gap-0.5">
      {[...Array(5)].map((_, i) => (
        <Star
          key={i}
          className={`h-4 w-4 ${
            i < rating ? "fill-lime-400 text-lime-400" : "text-foreground/20"
          }`}
        />
      ))}
    </div>
  );
}

function AvatarCircle({ initials }: { initials: string }) {
  return (
    <div className="flex h-10 w-10 items-center justify-center rounded-full border border-foreground/10 bg-lime-400/10 text-xs font-semibold text-lime-400">
      {initials}
    </div>
  );
}

export function TestimonialsSection() {
  return (
    <section className="mx-auto max-w-6xl px-4 py-16">
      <SectionHeader
        eyebrow="Opinie użytkowników"
        title="Dołącz do tysięcy zadowolonych użytkowników"
        subtitle="Zobacz, co mówią osoby, które już uczą się z Lingendo."
      />

      <div className="mt-12 grid grid-cols-1 gap-6 sm:grid-cols-2 lg:grid-cols-3">
        {TESTIMONIALS.map((testimonial) => (
          <AnimatedContainer
            key={testimonial.id}
            variant="fade"
            className="h-full"
          >
            <Card className="relative h-full border-foreground/10 p-6 transition-all hover:border-foreground/20">
              {/* Cytat ikona */}
              <Quote className="absolute right-4 top-4 h-8 w-8 text-foreground/5" />

              {/* Gwiazdki */}
              <StarRating rating={testimonial.rating} />

              {/* Treść opinii */}
              <p className="mt-4 text-sm leading-relaxed text-muted-foreground">
                &ldquo;{testimonial.content}&rdquo;
              </p>

              {/* Autor */}
              <div className="mt-6 flex items-center gap-3 border-t border-foreground/5 pt-4">
                <AvatarCircle initials={testimonial.avatarInitials} />
                <div>
                  <p className="text-sm font-semibold">{testimonial.name}</p>
                  <p className="text-xs text-muted-foreground">
                    {testimonial.role}
                  </p>
                </div>
              </div>
            </Card>
          </AnimatedContainer>
        ))}
      </div>
    </section>
  );
}
