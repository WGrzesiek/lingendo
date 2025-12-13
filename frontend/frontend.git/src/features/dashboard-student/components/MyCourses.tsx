import { Card, CardHeader, CardTitle, CardContent } from "@/components/ui/card";
import { Button } from "@/components/ui/button";
import { Plus } from "lucide-react";
import { DecksForDashboard } from "@/features/deck/components/deck/DeckForDashboard";
import { useRouter } from "next/navigation";

export const MyCourses = () => {
  const router = useRouter();
  const handleCreate = () => {
    router.push("/decks/create");
  };
  return (
    <Card className="shadow-sm">
      <CardHeader className="flex flex-row items-center justify-between space-y-0 pb-4">
        <div className="space-y-1">
          <CardTitle className="text-xl">Moje kursy</CardTitle>
          <p className="text-sm text-muted-foreground">
            Kontynuuj naukę tam, gdzie skończyłeś
          </p>
        </div>
        <Button size="sm" className="hidden sm:flex" onClick={handleCreate}>
          <Plus className="w-4 h-4 mr-2" />
          Utwórz nowy
        </Button>
        <Button
          size="icon"
          className="sm:hidden h-8 w-8"
          onClick={handleCreate}
        >
          <Plus className="h-4 w-4" />
        </Button>
      </CardHeader>

      <CardContent>
        <DecksForDashboard />
      </CardContent>
    </Card>
  );
};
