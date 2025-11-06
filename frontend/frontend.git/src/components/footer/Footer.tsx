"use client";

import { FooterLinkGroup } from "./FooterLinkGroup";
import { FooterSocials } from "./FooterSocials";
import Image from "next/image";

export function Footer() {
  return (
    <footer className="border-t border-border bg-background/80 backdrop-blur-md mt-20">
      <div className="mx-auto max-w-6xl px-6 py-12">
        <div className="grid grid-cols-1 gap-10 sm:grid-cols-2 lg:grid-cols-4">
          <div className="space-y-4">
            <div className="flex items-center gap-2">
              <Image src="/avatar.png" alt="Lingendo" width={28} height={28} />
              <span className="text-lg font-semibold">Lingendo</span>
            </div>
            <p className="text-sm text-muted-foreground">
              Ucz się mądrzej, nie więcej — powtórki, kontekst, motywacja.
            </p>
          </div>

          <FooterLinkGroup
            title="Produkt"
            links={[
              { label: "Funkcje", href: "#features" },
              { label: "Cennik", href: "#pricing" },
              { label: "Demo", href: "#demo" },
            ]}
          />
          <FooterLinkGroup
            title="Dla użytkowników"
            links={[
              { label: "Logowanie", href: "/login" },
              { label: "Rejestracja", href: "/signup" },
              { label: "FAQ", href: "/faq" },
            ]}
          />
          <FooterLinkGroup
            title="O nas"
            links={[
              { label: "Zespół", href: "/about" },
              { label: "Kontakt", href: "/contact" },
              { label: "Polityka prywatności", href: "/privacy" },
            ]}
          />
        </div>

        <div className="mt-10 border-t border-border/40 pt-6 flex flex-col gap-4 md:flex-row md:items-center md:justify-between">
          <p className="text-xs text-muted-foreground">
            © {new Date().getFullYear()} Lingendo. Wszelkie prawa zastrzeżone.
          </p>
          <FooterSocials />
        </div>
      </div>
    </footer>
  );
}
