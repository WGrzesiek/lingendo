"use client";

import { Card, CardContent, CardHeader, CardTitle } from "@/components/ui/card";
import { TrendingUp } from "lucide-react";
import { IUserPointsData } from "@/features/statistics/types/statistics.types";

interface PointsChartProps {
  monthlyPoints: IUserPointsData[];
}

/**
 * Wykres punktów w czasie (miesięczne)
 * Prosty wykres słupkowy bez zewnętrznych bibliotek
 */
export const PointsChart = ({ monthlyPoints }: PointsChartProps) => {
  const data = monthlyPoints;
  const maxPoints = Math.max(...data.map((d) => d.points), 1);

  const formatDate = (dateStr: string) => {
    const date = new Date(dateStr);
    return date.toLocaleDateString("pl-PL", { month: "short" });
  };

  return (
    <Card>
      <CardHeader>
        <CardTitle className="flex items-center gap-2">
          <TrendingUp className="w-5 h-5" />
          Punkty w czasie
        </CardTitle>
        <p className="text-sm text-muted-foreground mt-1">
          Ostatnie 12 miesięcy
        </p>
      </CardHeader>
      <CardContent>
        <div className="space-y-4">
          {/* Wykres słupkowy */}
          <div className="relative h-64 flex items-end justify-between gap-1">
            {data.map((item, index) => {
              const heightPercent = (item.points / maxPoints) * 100;
              return (
                <div
                  key={index}
                  className="flex-1 flex flex-col items-center gap-2"
                >
                  {/* Słupek */}
                  <div className="relative w-full flex items-end justify-center h-56">
                    <div
                      className="w-full bg-primary rounded-t transition-all hover:bg-primary/80 cursor-pointer group relative"
                      style={{ height: `${heightPercent}%` }}
                    >
                      {/* Tooltip */}
                      <div className="absolute bottom-full mb-2 left-1/2 -translate-x-1/2 opacity-0 group-hover:opacity-100 transition-opacity bg-popover text-popover-foreground text-xs rounded px-2 py-1 whitespace-nowrap shadow-md z-10">
                        {item.points} pkt
                      </div>
                    </div>
                  </div>
                  {/* Label daty */}
                  <p className="text-xs text-muted-foreground text-center">
                    {formatDate(item.date)}
                  </p>
                </div>
              );
            })}
          </div>

          {/* Statystyki podsumowania */}
          <div className="grid grid-cols-3 gap-4 pt-4 border-t">
            <div className="text-center">
              <p className="text-sm text-muted-foreground">Suma</p>
              <p className="text-xl font-bold">
                {data
                  .reduce((sum, d) => sum + d.points, 0)
                  .toLocaleString("pl-PL")}
              </p>
            </div>
            <div className="text-center">
              <p className="text-sm text-muted-foreground">Średnia</p>
              <p className="text-xl font-bold">
                {Math.round(
                  data.reduce((sum, d) => sum + d.points, 0) / data.length
                ).toLocaleString("pl-PL")}
              </p>
            </div>
            <div className="text-center">
              <p className="text-sm text-muted-foreground">Maks.</p>
              <p className="text-xl font-bold">
                {maxPoints.toLocaleString("pl-PL")}
              </p>
            </div>
          </div>
        </div>
      </CardContent>
    </Card>
  );
};
