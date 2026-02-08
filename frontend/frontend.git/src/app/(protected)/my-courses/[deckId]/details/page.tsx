import DeckDetailsClient from "@/app/(protected)/my-courses/[deckId]/details/DeckDetailsClient";
export default function DeckDetailsPage({params,
                                   }: {
    params: { deckId: string };
}) {
    return <DeckDetailsClient deckId={params.deckId}/>;
}