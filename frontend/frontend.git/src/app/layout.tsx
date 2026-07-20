import type { Metadata } from "next";
import { Inter } from "next/font/google";
import "../styles/globals.css";
import { Providers } from "@/components/providers/Providers";
import { Toaster } from "@/components/ui/sonner";

const inter = Inter({
  subsets: ["latin", "latin-ext"], // latin-ext dla polskich znaków
  variable: "--font-inter",
  display: "swap", // lepszy performance
});

export const metadata: Metadata = {
  title: "Lingendo",
  description:
    "Ucz się języków efektywnie z wykorzystaniem algorytmów powtórek",
  keywords: [
    "nauka języków",
    "fiszki",
    "spaced repetition",
    "angielski",
    "polski",
  ],
  authors: [{ name: "Grzegorz Wawrzeń" }],
  openGraph: {
    title: "Lingendo",
    description:
      "Ucz się języków efektywnie z wykorzystaniem algorytmów powtórek",
    type: "website",
  },
};

export default function RootLayout({
  children,
}: Readonly<{
  children: React.ReactNode;
}>) {
  return (
    <html lang="pl" suppressHydrationWarning>
      <body className={`${inter.variable} antialiased`}>
        <Providers>
          {children}
          <Toaster />
        </Providers>
      </body>
    </html>
  );
}
