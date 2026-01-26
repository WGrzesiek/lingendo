"use client";

import { FooterLinkGroup } from "./FooterLinkGroup";
import { FooterSocials } from "./FooterSocials";
import Link from "next/link";
import { BookOpen, Sparkles, Mail, MapPin } from "lucide-react";

export function Footer() {
  return (
    <footer className="relative border-t border-border/40 bg-gradient-to-b from-background to-muted/20">
      {/* Decorative gradient line */}
      <div className="absolute top-0 left-0 right-0 h-px bg-gradient-to-r from-transparent via-primary/50 to-transparent" />

      <div className="mx-auto max-w-6xl px-6 py-16">
        {/* Main Footer Content */}
        <div className="grid grid-cols-1 gap-12 md:grid-cols-2 lg:grid-cols-5">
          {/* Brand Section - Takes 2 columns on large screens */}
          <div className="lg:col-span-2 space-y-6">
            <Link href="/" className="inline-flex items-center gap-2.5 group">
              <div className="p-2 rounded-xl bg-primary/10 group-hover:bg-primary/20 transition-colors">
                <BookOpen className="w-5 h-5 text-primary" />
              </div>
              <span className="text-xl font-bold tracking-tight">Lingendo</span>
            </Link>
            <p className="text-muted-foreground leading-relaxed max-w-sm">
              Ucz się mądrzej, nie więcej. Inteligentne fiszki z algorytmem
              Spaced Repetition i wsparciem AI pomagają Ci zapamiętać więcej w
              krótszym czasie.
            </p>
            <div className="flex items-center gap-2 text-sm text-muted-foreground">
              <Sparkles className="w-4 h-4 text-primary" />
              <span>Ponad 5 000 aktywnych użytkowników</span>
            </div>
          </div>

          {/* Links Sections */}
          <FooterLinkGroup
            title="Produkt"
            links={[
              { label: "Jak to działa", href: "/how-it-works" },
              { label: "Cennik", href: "/pricing" },
              { label: "FAQ", href: "/faq" },
              { label: "Funkcje", href: "/#features" },
            ]}
          />
          <FooterLinkGroup
            title="Dla użytkowników"
            links={[
              { label: "Logowanie", href: "/login" },
              { label: "Rejestracja", href: "/signup" },
              { label: "Społeczność", href: "/community" },
              { label: "Dla nauczycieli", href: "/pricing" },
            ]}
          />
          <FooterLinkGroup
            title="Firma"
            links={[
              { label: "O nas", href: "/about" },
              { label: "Kontakt", href: "/contact" },
              { label: "Polityka prywatności", href: "/privacy" },
              { label: "Regulamin", href: "/terms" },
            ]}
          />
        </div>

        {/* Divider */}
        <div className="my-10 border-t border-border/40" />

        {/* Bottom Section */}
        <div className="flex flex-col gap-6 md:flex-row md:items-center md:justify-between">
          <div className="flex flex-col gap-3 md:flex-row md:items-center md:gap-6">
            <p className="text-sm text-muted-foreground">
              © {new Date().getFullYear()} Lingendo. Wszelkie prawa zastrzeżone.
            </p>
            <div className="flex items-center gap-2 text-sm text-muted-foreground">
              <MapPin className="w-4 h-4" />
              <span>Polska</span>
            </div>
            <div className="flex items-center gap-2 text-sm text-muted-foreground">
              <Mail className="w-4 h-4" />
              <a
                href="mailto:support@lingendo.app"
                className="hover:text-foreground transition-colors"
              >
                support@lingendo.app
              </a>
            </div>
          </div>
          <FooterSocials />
        </div>
      </div>
    </footer>
  );
}
