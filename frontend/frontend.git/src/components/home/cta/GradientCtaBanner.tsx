"use client";

import { Button } from "@/components/ui/button";
import { ArrowRight } from "lucide-react";

type Props = {
  title: string;
  highlight: string;
  ctaLabel: string;
  onCtaClick: () => void;
};

export function GradientCtaBanner({
  title,
  highlight,
  ctaLabel,
  onCtaClick,
}: Props) {
  return (
    <section>
      <div className="relative mx-auto max-w-4xl px-4 text-center">
        <h2 className="text-3xl font-bold leading-tight tracking-tight md:text-4xl">
          {title}
          <br />
          <span className="inline-block mt-2">
            <span className="bg-lime-300/40 px-2 py-1 rounded-md leading-relaxed [box-decoration-break:clone] [-webkit-box-decoration-break:clone]">
              <span className="text-lime-400">{highlight}</span>
            </span>
          </span>
        </h2>

        <div className="mt-8 flex justify-center">
          <Button
            size="lg"
            onClick={onCtaClick}
            className="h-12 px-6 text-base"
          >
            {ctaLabel}
            <ArrowRight className="ml-2 h-4 w-4" />
          </Button>
        </div>
      </div>
    </section>
  );
}
