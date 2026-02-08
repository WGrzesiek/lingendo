"use client";

import { GradientCtaBanner } from "@/components/home/cta/GradientCtaBanner";
import { useRouter } from "next/navigation";

export default function CtaSection() {
  const router = useRouter();

  return (
    <GradientCtaBanner
      title="Poznaj rdzeń Lingendo"
      highlight="Skuteczna nauka słownictwa: powtórki, kontekst i cele"
      ctaLabel="Wypróbuj za darmo"
      onCtaClick={() => router.push("/signup")}
    />
  );
}
