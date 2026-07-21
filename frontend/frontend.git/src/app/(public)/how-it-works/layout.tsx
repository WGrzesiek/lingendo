import type { Metadata } from "next";

const description =
  "Zobacz, jak Lingendo łączy fiszki, kontekst wspierany przez AI i powtórki przestrzenne w demonstracyjnej platformie do nauki słownictwa.";

export const metadata: Metadata = {
  title: "Jak działa nauka słownictwa",
  description,
  alternates: { canonical: "/how-it-works" },
  openGraph: {
    type: "website",
    locale: "pl_PL",
    siteName: "Lingendo",
    title: "Jak działa Lingendo",
    description,
    url: "/how-it-works",
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
    title: "Jak działa Lingendo",
    description,
    images: ["/opengraph-image"],
  },
};

export default function HowItWorksLayout({
  children,
}: Readonly<{ children: React.ReactNode }>) {
  return children;
}
