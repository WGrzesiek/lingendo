"use client";

import { type LucideIcon } from "lucide-react";

type Props = { icon: LucideIcon };
export function FeatureIcon({ icon: Icon }: Props) {
  return (
    <div className="grid h-10 w-10 place-items-center rounded-xl bg-foreground/5 ring-1 ring-foreground/10">
      <Icon className="h-5 w-5" />
    </div>
  );
}
