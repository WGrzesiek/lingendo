"use client";

import { Card, CardContent, CardHeader } from "@/components/ui/card";
import { Separator } from "@/components/ui/separator";
import { HeroBadge } from "./HeroBadge";

type Props = {
  word: string;
  translation: string;
  example: string;
  badge: string;
};

export default function HeroFlashcard({
  word,
  translation,
  example,
  badge,
}: Props) {
  return (
    <div className="grid place-items-center p-4 w-full">
      <Card
        className="
          w-full max-w-[320px] sm:max-w-[380px]
          rounded-3xl
          border
        "
      >
        <CardHeader className="pb-2">
          <div className="text-[11px] font-medium text-muted-foreground flex justify-end">
            <HeroBadge text={badge} />
          </div>
          <h2 className="mt-1 text-4xl font-extrabold tracking-tight text-foreground flex justify-center">
            {word}
          </h2>
          <div className="mt-2 text-xl text-foreground/80 flex justify-center">
            {translation}
          </div>
        </CardHeader>

        <CardContent className="pt-2 pb-6">
          <Separator className="my-4 opacity-60" />
          <p className="text-center text-lg leading-relaxed text-foreground/80">
            {example}
          </p>
        </CardContent>
      </Card>
    </div>
  );
}
