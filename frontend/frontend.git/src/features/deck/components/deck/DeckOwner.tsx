import { DeckOwnerType } from "@/types/common";
import { Badge } from "@/components/ui/badge";

const ownerLabels: Record<DeckOwnerType, string> = {
  I: "Własny",
  TEACHER: "Nauczyciela",
  FRIEND: "Znajomego",
  COMMUNITY: "Społeczności",
};

export const DeckOwner = ({ owner }: { owner: DeckOwnerType }) => {
  return (
    <Badge variant="outline" className="text-xs">
      {ownerLabels[owner]}
    </Badge>
  );
};
