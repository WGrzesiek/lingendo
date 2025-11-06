"use client";

import * as React from "react";
import { Switch } from "@/components/ui/switch";
import { Label } from "@/components/ui/label";

type Props = {
  value: "monthly" | "yearly";
  onChange: (v: "monthly" | "yearly") => void;
  yearlyNote?: string;
  className?: string;
};

export function BillingToggle({
  value,
  onChange,
  yearlyNote = "-20%",
  className = "",
}: Props) {
  const checked = value === "yearly";
  return (
    <div className={`flex items-center justify-center gap-3 ${className}`}>
      <Label className={checked ? "text-muted-foreground" : "text-foreground"}>
        Miesięcznie
      </Label>
      <Switch
        checked={checked}
        onCheckedChange={(c) => onChange(c ? "yearly" : "monthly")}
        aria-label="Przełącz rozliczenie: miesięczne / roczne"
      />
      <div className="flex items-center gap-2">
        <Label
          className={!checked ? "text-muted-foreground" : "text-foreground"}
        >
          Rocznie
        </Label>
        <span className="rounded-full bg-lime-200/60 px-2 py-0.5 text-xs font-medium text-lime-900 dark:bg-lime-900/30 dark:text-lime-300">
          {yearlyNote}
        </span>
      </div>
    </div>
  );
}
