import { Card } from "@/components/ui/card";
import { Users, CheckCircle, TrendingUp, Eye } from "lucide-react";
import type { DeckStats } from "@/features/deck/types/deck-details.types";

interface DeckDetailsStatsProps {
  stats: DeckStats;
}

/**
 * Statystyki decka - tylko dla właściciela
 * Pokazuje liczby uczniów, ukończeń, postępu
 */
export const DeckDetailsStats = ({ stats }: DeckDetailsStatsProps) => {
  return (
    <div className="grid grid-cols-2 lg:grid-cols-5 gap-4">
      <Card className="p-6 border-l-4 border-l-blue-500">
        <div className="flex items-center gap-3">
          <div className="p-3 bg-blue-500/10 rounded-lg">
            <Users className="w-6 h-6 text-blue-600" />
          </div>
          <div>
            <p className="text-2xl font-bold">{stats.totalStudents}</p>
            <p className="text-sm text-muted-foreground">Wszystkich uczniów</p>
          </div>
        </div>
      </Card>

      <Card className="p-6 border-l-4 border-l-green-500">
        <div className="flex items-center gap-3">
          <div className="p-3 bg-green-500/10 rounded-lg">
            <CheckCircle className="w-6 h-6 text-green-600" />
          </div>
          <div>
            <p className="text-2xl font-bold">{stats.completedStudents}</p>
            <p className="text-sm text-muted-foreground">Ukończyło</p>
          </div>
        </div>
      </Card>

      <Card className="p-6 border-l-4 border-l-orange-500">
        <div className="flex items-center gap-3">
          <div className="p-3 bg-orange-500/10 rounded-lg">
            <Users className="w-6 h-6 text-orange-600" />
          </div>
          <div>
            <p className="text-2xl font-bold">{stats.activeStudents}</p>
            <p className="text-sm text-muted-foreground">Aktywnych</p>
          </div>
        </div>
      </Card>

      <Card className="p-6 border-l-4 border-l-purple-500">
        <div className="flex items-center gap-3">
          <div className="p-3 bg-purple-500/10 rounded-lg">
            <TrendingUp className="w-6 h-6 text-purple-600" />
          </div>
          <div>
            <p className="text-2xl font-bold">{stats.averageProgress}%</p>
            <p className="text-sm text-muted-foreground">Średni postęp</p>
          </div>
        </div>
      </Card>

      <Card className="p-6 border-l-4 border-l-indigo-500">
        <div className="flex items-center gap-3">
          <div className="p-3 bg-indigo-500/10 rounded-lg">
            <Eye className="w-6 h-6 text-indigo-600" />
          </div>
          <div>
            <p className="text-2xl font-bold">{stats.totalViews}</p>
            <p className="text-sm text-muted-foreground">Wyświetleń</p>
          </div>
        </div>
      </Card>
    </div>
  );
};
