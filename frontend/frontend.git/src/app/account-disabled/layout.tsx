import type { Metadata } from "next";

export const metadata: Metadata = {
  title: "Konto nieaktywne",
  robots: { index: false, follow: false },
};

export default function AccountDisabledLayout({
  children,
}: Readonly<{ children: React.ReactNode }>) {
  return children;
}
