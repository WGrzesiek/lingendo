import type { Metadata } from "next";

const description =
  "Odpowiedzi na pytania o funkcje, ograniczenia i status demonstracyjnego projektu Lingendo — platformy do nauki słownictwa z AI.";

export const metadata: Metadata = {
  title: "FAQ — informacje o wersji demonstracyjnej",
  description,
  alternates: { canonical: "/faq" },
  openGraph: {
    type: "website",
    locale: "pl_PL",
    siteName: "Lingendo",
    title: "FAQ Lingendo",
    description,
    url: "/faq",
    images: [
      {
        url: "/opengraph-image",
        width: 1200,
        height: 630,
        alt: "Lingendo — demonstracyjna platforma do nauki słownictwa z AI",
      },
    ],
  },
  twitter: {
    card: "summary_large_image",
    title: "FAQ Lingendo",
    description,
    images: ["/opengraph-image"],
  },
};

export default function FAQLayout({
  children,
}: Readonly<{ children: React.ReactNode }>) {
  return children;
}
