import type { Metadata } from "next";

export const metadata: Metadata = {
  title: "Rejestracja",
  description: "Załóż konto w demonstracyjnej platformie Lingendo.",
  alternates: { canonical: "/signup" },
  robots: { index: false, follow: false },
};

export default function SignupLayout({
  children,
}: Readonly<{ children: React.ReactNode }>) {
  return children;
}
