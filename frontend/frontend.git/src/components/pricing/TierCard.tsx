"use client";

import { Card, CardContent, CardHeader } from "@/components/ui/card";
import { Button } from "@/components/ui/button";
import { Check, Minus } from "lucide-react";
import { Badge } from "@/components/ui/badge";
import { cn } from "@/lib/utils";

export type Tier = {
  id: string;
  name: string;
  tagline?: string;
  priceMonthly: number;
  priceYearly: number;
  ctaLabel: string;
  highlighted?: boolean;
  features: Array<{ label: string; included: boolean }>;
};

type Props = {
  tier: Tier;
  billing: "monthly" | "yearly";
  onSelect?: (id: string) => void;
  className?: string;
};

export function TierCard({ tier, billing, onSelect }: Props) {
  const price = billing === "monthly" ? tier.priceMonthly : tier.priceYearly;
  const per = billing === "monthly" ? "/msc" : "/rok";

  return (
    <Card
      className={cn(
        "flex h-full flex-col border-foreground/10",
        tier.highlighted &&
          "ring-1 ring-lime-400/50 shadow-[0_0_0_1px_rgba(163,230,53,0.3)]"
      )}
    >
      <CardHeader className="space-y-2">
        <div className="flex items-center justify-between">
          <h3 className="text-lg font-semibold">{tier.name}</h3>
          {tier.highlighted && (
            <Badge className="bg-lime-400 text-black">Polecany</Badge>
          )}
        </div>
        {tier.tagline && (
          <p className="text-sm text-muted-foreground">{tier.tagline}</p>
        )}
        <div className="mt-2">
          <span className="text-3xl font-bold tracking-tight">{price} zł</span>
          <span className="ml-1 text-sm text-muted-foreground">{per}</span>
        </div>
      </CardHeader>

      <CardContent className="mt-2 flex flex-1 flex-col">
        <ul className="space-y-2 text-sm">
          {tier.features.map((f) => (
            <li key={f.label} className="flex items-start gap-2">
              {f.included ? (
                <Check className="mt-0.5 h-4 w-4 text-lime-500" />
              ) : (
                <Minus className="mt-0.5 h-4 w-4 text-zinc-400" />
              )}
              <span className={f.included ? "" : "text-muted-foreground"}>
                {f.label}
              </span>
            </li>
          ))}
        </ul>

        <Button
          size="lg"
          className={cn(
            "mt-6",
            tier.highlighted ? "bg-lime-400 text-black hover:bg-lime-300" : ""
          )}
          variant={tier.highlighted ? "default" : "outline"}
          onClick={() => onSelect?.(tier.id)}
        >
          {tier.ctaLabel}
        </Button>
      </CardContent>
    </Card>
  );
}
