import type { Metadata, Viewport } from "next";
import { Inter } from "next/font/google";
import "../styles/globals.css";
import { Providers } from "@/components/providers/Providers";
import { Toaster } from "@/components/ui/sonner";

const SITE_URL = "https://www.lingendo.app";
const SITE_DESCRIPTION =
  "Lingendo to demonstracyjna platforma do nauki słownictwa z wykorzystaniem AI, fiszek i powtórek przestrzennych. Projekt portfolio Grzegorza Wawrzenia.";

const inter = Inter({
  subsets: ["latin", "latin-ext"],
  variable: "--font-inter",
  display: "swap",
});

export const metadata: Metadata = {
  metadataBase: new URL(SITE_URL),
  applicationName: "Lingendo",
  title: {
    default: "Lingendo — nauka słownictwa wspierana przez AI",
    template: "%s | Lingendo",
  },
  description: SITE_DESCRIPTION,
  keywords: [
    "nauka słownictwa",
    "fiszki",
    "powtórki przestrzenne",
    "spaced repetition",
    "nauka języków",
    "AI w nauce języków",
    "projekt portfolio",
  ],
  authors: [
    {
      name: "Grzegorz Wawrzeń",
      url: "https://gwawrzen.pl",
    },
  ],
  creator: "Grzegorz Wawrzeń",
  publisher: "Grzegorz Wawrzeń",
  category: "education",
  alternates: {
    canonical: "/",
  },
  manifest: "/manifest.webmanifest",
  icons: {
    icon: [
      { url: "/favicon.ico", type: "image/x-icon" },
      { url: "/icon.svg", type: "image/svg+xml" },
    ],
  },
  openGraph: {
    type: "website",
    locale: "pl_PL",
    url: SITE_URL,
    siteName: "Lingendo",
    title: "Lingendo — nauka słownictwa wspierana przez AI",
    description: SITE_DESCRIPTION,
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
    title: "Lingendo — nauka słownictwa wspierana przez AI",
    description: SITE_DESCRIPTION,
    images: ["/opengraph-image"],
  },
  robots: {
    index: true,
    follow: true,
    googleBot: {
      index: true,
      follow: true,
      "max-image-preview": "large",
      "max-snippet": -1,
      "max-video-preview": -1,
    },
  },
};

export const viewport: Viewport = {
  colorScheme: "light dark",
  themeColor: [
    { media: "(prefers-color-scheme: light)", color: "#f8fcf9" },
    { media: "(prefers-color-scheme: dark)", color: "#111111" },
  ],
};

const structuredData = {
  "@context": "https://schema.org",
  "@graph": [
    {
      "@type": "WebSite",
      "@id": `${SITE_URL}/#website`,
      url: SITE_URL,
      name: "Lingendo",
      description: SITE_DESCRIPTION,
      inLanguage: "pl-PL",
    },
    {
      "@type": "SoftwareApplication",
      "@id": `${SITE_URL}/#application`,
      name: "Lingendo",
      url: SITE_URL,
      applicationCategory: "EducationalApplication",
      operatingSystem: "Web",
      description: SITE_DESCRIPTION,
      isAccessibleForFree: true,
      codeRepository: "https://github.com/WGrzesiek/lingendo",
      author: {
        "@type": "Person",
        name: "Grzegorz Wawrzeń",
        url: "https://gwawrzen.pl",
      },
      offers: {
        "@type": "Offer",
        price: "0",
        priceCurrency: "PLN",
        description: "Publiczne środowisko demonstracyjne projektu portfolio",
      },
    },
  ],
};

export default function RootLayout({
  children,
}: Readonly<{
  children: React.ReactNode;
}>) {
  return (
    <html lang="pl" suppressHydrationWarning>
      <body className={`${inter.variable} antialiased`}>
        <script
          type="application/ld+json"
          dangerouslySetInnerHTML={{
            __html: JSON.stringify(structuredData).replace(/</g, "\\u003c"),
          }}
        />
        <Providers>
          {children}
          <Toaster />
        </Providers>
      </body>
    </html>
  );
}
