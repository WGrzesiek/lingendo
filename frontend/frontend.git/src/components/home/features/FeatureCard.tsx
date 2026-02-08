"use client";

import { Card, CardContent, CardHeader } from "@/components/ui/card";
import { FeatureIcon } from "./FeatureIcon";
import { type LucideIcon } from "lucide-react";

type Props = {
  icon: LucideIcon;
  title: string;
  desc: string;
  footer?: string;
};

export function FeatureCard({ icon, title, desc, footer }: Props) {
  return (
    <Card className="h-full border-foreground/10">
      <CardHeader className="flex flex-row items-center gap-3 pb-2">
        <FeatureIcon icon={icon} />
        <h3 className="text-lg font-semibold">{title}</h3>
      </CardHeader>
      <CardContent className="space-y-3">
        <p className="text-sm text-muted-foreground">{desc}</p>
        {footer && <p className="text-xs text-foreground/70">{footer}</p>}
      </CardContent>
    </Card>
  );
}
