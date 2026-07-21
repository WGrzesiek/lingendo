import HeroSection from "@/components/home/hero-section/HeroSection";
import { FeaturesSection } from "@/components/home/features/FeaturesSection";
import { HowItWorksSection } from "@/components/home/how-it-works/HowItWorksSection";
import CtaSection from "@/components/home/cta/CtaSection";
import { CreatorSection } from "@/components/home/creator/CreatorSection";
import { LearningLoopSection } from "@/components/home/learning-loop/LearningLoopSection";

export default function Home() {
  return (
    <main>
      <HeroSection />
      <FeaturesSection />
      <HowItWorksSection />
      <LearningLoopSection />
      <CtaSection />
      <CreatorSection />
    </main>
  );
}
