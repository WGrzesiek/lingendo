
import { Badge } from "@/components/ui/badge";
import { cn } from "@/lib/utils";
import {deckVisibilityConfig, DeckVisibilityType} from "@/features/deck/types";

export const DeckVisibilityBadge = ({ visibility }: { visibility: DeckVisibilityType }) => {
    const { label, icon: Icon, className } = deckVisibilityConfig[visibility];

    return (
        <Badge variant="outline" className={cn("gap-1.5 pr-3", className)}>
            <Icon className="h-3.5 w-3.5" />
            <span>{label}</span>
        </Badge>
    );
};