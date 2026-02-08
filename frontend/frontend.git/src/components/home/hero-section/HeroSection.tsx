"use client";

import { BackgroundWithGreen } from "../../common/BackgroundWithGreen";
import { HeroBadge } from "./HeroBadge";
import HeroH1 from "./HeroH1";
import HeroButtons from "./HeroButtons";
import HeroSocialProof from "./HeroSocialProof";
import HeroFlashcard from "./HeroFlashcard";
import { UserProgressBadge } from "./mini-hero/UserProgressBadge";
import { LearningStatsCard } from "./mini-hero/LearningStatsCard";
import { FloatingBadge } from "../../common/badges/FloatingBadge";
import { Lightbulb, BookOpen } from "lucide-react";
import { AnimatedContainer } from "../../common/effects/AnimatedContainer";
export default function HeroSection() {
  return (
    <section className="relative overflow-hidden">
      <BackgroundWithGreen />

      <div className="container mx-auto grid max-w-7xl grid-cols-1 items-center gap-12 px-6 py-20 md:py-28 lg:grid-cols-2">
        <AnimatedContainer className="space-y-6" variant="slide">
          <HeroBadge text="Ucz się mądrzej, nie więcej" />
          <HeroH1 text="Zamień każde słowo w" colorText="Postęp" />

          <p className="text-pretty text-lg leading-relaxed text-muted-foreground md:text-xl">
            Ucz się skutecznie dzięki inteligentnym fiszkom z zdaniami
            tworzonymi przez AI. Lingendo pomaga Ci rozwijać słownictwo
            szybciej, pewniej i z przyjemnością.
          </p>

          <HeroButtons
            leftButtonText="Rozpocznij naukę"
            rightButtonText="Dowiedz się więcej"
          />
          <HeroSocialProof
            firstText="Używany przez 5 000+ użytkowników"
            secondText="500 000+ wygenerowanych fiszek"
          />
        </AnimatedContainer>

        <AnimatedContainer variant="zoom">
          <div className="relative aspect-[4/3]">
            <HeroFlashcard
              word="ability"
              translation="umiejętność"
              example="She has the ability to solve complex problems."
              badge="Lingendo · A2"
            />
            <FloatingBadge
              icon={Lightbulb}
              text="Nowe słowo dnia:"
              highlightText='"ability"'
              position="top-right"
            />
            <FloatingBadge
              icon={BookOpen}
              text="Rozpocznij naukę"
              position="right-center"
            />
            <UserProgressBadge />
            <LearningStatsCard />
          </div>
        </AnimatedContainer>
      </div>
    </section>
  );
}
