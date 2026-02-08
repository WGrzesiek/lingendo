import {
  DeckCategory,
  deckCategoryConfig,
} from "@/features/deck/types/deck.types";
import { Badge } from "@/components/ui/badge";
import { cn } from "@/lib/utils";

export const DeckCategoryBadge = ({ category }: { category: DeckCategory }) => {
  const { label, className, icon: Icon } = deckCategoryConfig[category];

  return (
    <Badge
      variant="outline"
      className={cn("gap-1.5 pl-2 pr-3 font-normal", className)}
    >
      <Icon className="w-3.5 h-3.5" />
      <span>{label}</span>
    </Badge>
  );
};
