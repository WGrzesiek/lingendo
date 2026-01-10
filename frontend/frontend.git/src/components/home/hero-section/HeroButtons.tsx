"use client";

import { ArrowRight } from "lucide-react";
import { Button } from "@/components/ui/button";
import Link from "next/link";

interface HeroButtonsProps {
  leftButtonText: string;
  rightButtonText: string;
  leftButtonHref?: string;
  rightButtonHref?: string;
}

export default function HeroButtons({
  leftButtonText,
  rightButtonText,
  leftButtonHref = "/signup",
  rightButtonHref = "/how-it-works",
}: HeroButtonsProps) {
  return (
    <div className="flex flex-col gap-4 sm:flex-row sm:items-center">
      <Button size="lg" className="h-12 px-6 text-base" asChild>
        <Link href={leftButtonHref}>
          {leftButtonText}
          <ArrowRight className="ml-2 h-5 w-5" />
        </Link>
      </Button>
      <Button
        size="lg"
        variant="secondary"
        className="h-12 px-6 text-base"
        asChild
      >
        <Link href={rightButtonHref}>{rightButtonText}</Link>
      </Button>
    </div>
  );
}
