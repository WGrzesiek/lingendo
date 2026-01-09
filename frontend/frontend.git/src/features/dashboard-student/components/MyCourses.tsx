import { Card, CardHeader, CardTitle, CardContent } from "@/components/ui/card";
import { DecksForDashboard } from "@/features/deck/components/deck/DeckForDashboard";

export const MyCourses = () => {
  return (
    <Card className="shadow-sm">
      <CardHeader className="flex flex-row items-center justify-between space-y-0 pb-4">
        <div className="space-y-1">
          <CardTitle className="text-xl">Kursy w trakcie nauki</CardTitle>
          <p className="text-sm text-muted-foreground">
            Kontynuuj naukę tam, gdzie skończyłeś
          </p>
        </div>
      </CardHeader>

      <CardContent>
        <DecksForDashboard />
      </CardContent>
    </Card>
  );
};
