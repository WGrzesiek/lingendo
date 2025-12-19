import {useQuery} from "@tanstack/react-query";
import {getMyDeckStats, getMyDeckStatsBody} from "@/features/deck/services/deck.service";

export const useMyDeckStats = (deckIds: string[]) => {
    return useQuery({
        queryKey: ["my-deck-stats", deckIds],
        queryFn: () => getMyDeckStats({ deckIds }),
        enabled: deckIds.length > 0,
    });
}
