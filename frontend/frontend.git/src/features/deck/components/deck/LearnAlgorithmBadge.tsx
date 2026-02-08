import {
  learnAlgorithmConfig,
  LearnAlgorithm,
} from "@/features/deck/types/deck.types";
import { Badge } from "@/components/ui/badge";
import { cn } from "@/lib/utils";

export const LearnAlgorithmBadge = ({
  algorithm,
}: {
  algorithm: LearnAlgorithm;
}) => {
  const { label, className, icon: Icon } = learnAlgorithmConfig[algorithm];

  return (
    <Badge variant="outline" className={cn("gap-1.5 pr-3", className)}>
      <Icon className="h-3.5 w-3.5" />
      <span>{label}</span>
    </Badge>
  );
};
