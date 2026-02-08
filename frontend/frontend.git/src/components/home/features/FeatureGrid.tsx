"use client";

import { FeatureCard } from "./FeatureCard";
import { type LucideIcon } from "lucide-react";

export type FeatureItem = {
  icon: LucideIcon;
  title: string;
  desc: string;
  footer?: string;
};

type Props = {
  items: FeatureItem[];
  className?: string;
};

export function FeatureGrid({ items, className = "" }: Props) {
  return (
    <div
      className={`grid grid-cols-1 gap-4 sm:grid-cols-2 lg:grid-cols-3 ${className}`}
    >
      {items.map((it) => (
        <FeatureCard key={it.title} {...it} />
      ))}
    </div>
  );
}
