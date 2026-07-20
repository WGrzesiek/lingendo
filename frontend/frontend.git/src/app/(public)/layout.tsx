import { DemoBanner } from "@/components/common/DemoBanner";
import { Footer } from "@/components/footer/Footer";
import { Navbar } from "@/components/menu/navbar";

export default function PublicLayout({
  children,
}: Readonly<{ children: React.ReactNode }>) {
  return (
    <div className="min-h-screen bg-background">
      <DemoBanner />
      <Navbar />
      <div className="pt-24">{children}</div>
      <Footer />
    </div>
  );
}
