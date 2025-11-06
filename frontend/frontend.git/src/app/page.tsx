import HeroSection from "@/components/home/hero-section/HeroSection";
import { FeaturesSection } from "@/components/home/features/FeaturesSection";
import CtaSection from "@/components/home/cta/CtaSection";
import { PricingSection } from "@/components/pricing/PricingSection";

export default function Home() {
  return (
    <main className="">
      <HeroSection />
      <FeaturesSection />
      <CtaSection />
      <PricingSection />
    </main>
  );
}
