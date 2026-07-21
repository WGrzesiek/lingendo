import { AuthBoundary } from "@/components/auth/AuthBoundary";
import { DemoBanner } from "@/components/common/DemoBanner";
import { Navbar } from "@/components/menu/navbar";
import type { Metadata } from "next";

export const metadata: Metadata = {
  robots: {
    index: false,
    follow: false,
    noarchive: true,
  },
};

export default function ProtectedLayout({
  children,
}: Readonly<{ children: React.ReactNode }>) {
  return (
    <AuthBoundary>
      <div className="min-h-screen bg-background">
        <DemoBanner />
        <Navbar />
        <div className="min-h-[calc(100vh-6rem)] bg-gradient-to-br from-primary/[0.04] via-background to-background pt-24">
          {children}
        </div>
      </div>
    </AuthBoundary>
  );
}
