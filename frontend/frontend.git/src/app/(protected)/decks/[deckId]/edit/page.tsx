
import EditDeckFormClient from "@/app/(protected)/decks/[deckId]/edit/EditDeckFormClient";

export default function EditDeckForm({params,}: { params: { deckId: string }; }) {
  return <EditDeckFormClient deckId={params.deckId} />;
}