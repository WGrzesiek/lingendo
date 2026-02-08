import HeroSection from "@/components/home/hero-section/HeroSection";
import { FeaturesSection } from "@/components/home/features/FeaturesSection";
import { HowItWorksSection } from "@/components/home/how-it-works/HowItWorksSection";
import { StatsSection } from "@/components/home/stats/StatsSection";
import { TestimonialsSection } from "@/components/home/testimonials/TestimonialsSection";
import CtaSection from "@/components/home/cta/CtaSection";
import { PricingSection } from "@/components/pricing/PricingSection";

export default function Home() {
    return (
        <main className="">
            <HeroSection />
            <FeaturesSection />
            <HowItWorksSection />
            <StatsSection />
            <TestimonialsSection />
            <CtaSection />
            <PricingSection />
        </main>
    );
}