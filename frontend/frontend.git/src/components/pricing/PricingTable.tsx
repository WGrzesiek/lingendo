"use client";

import * as React from "react";
import { TierCard, type Tier } from "./TierCard";
import { BillingToggle } from "./BillingToggle";

type Props = {
  tiers: Tier[];
  defaultBilling?: "monthly" | "yearly";
  onSelect?: (id: string) => void;
};

export function PricingTable({
  tiers,
  defaultBilling = "monthly",
  onSelect,
}: Props) {
  const [billing, setBilling] = React.useState<"monthly" | "yearly">(
    defaultBilling
  );

  return (
    <div className="mx-auto max-w-6xl px-4">
      <BillingToggle
        value={billing}
        onChange={setBilling}
        yearlyNote="-20%"
        className="mb-8"
      />

      <div className="grid grid-cols-1 gap-4 md:grid-cols-3">
        {tiers.map((t) => (
          <TierCard key={t.id} tier={t} billing={billing} onSelect={onSelect} />
        ))}
      </div>

      <p className="mt-4 text-center text-xs text-muted-foreground">
        Ceny brutto w PLN. Możesz anulować w dowolnym momencie. Zniżka roczna
        naliczana z góry.
      </p>
    </div>
  );
}
