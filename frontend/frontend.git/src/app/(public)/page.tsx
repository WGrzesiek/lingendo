import HeroSection from "@/components/home/hero-section/HeroSection";
import { FeaturesSection } from "@/components/home/features/FeaturesSection";
import { HowItWorksSection } from "@/components/home/how-it-works/HowItWorksSection";
import CtaSection from "@/components/home/cta/CtaSection";
import { CreatorSection } from "@/components/home/creator/CreatorSection";

export default function Home() {
  return (
    <main>
      <HeroSection />
      <FeaturesSection />
      <HowItWorksSection />
      <CreatorSection />
      <CtaSection />
    </main>
  );
}
