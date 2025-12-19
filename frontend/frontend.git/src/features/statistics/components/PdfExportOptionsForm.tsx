"use client";

import { Card, CardContent, CardHeader, CardTitle } from "@/components/ui/card";
import { Checkbox } from "@/components/ui/checkbox";
import { Label } from "@/components/ui/label";
import {
  Select,
  SelectContent,
  SelectItem,
  SelectTrigger,
  SelectValue,
} from "@/components/ui/select";
import { FileText, Calendar } from "lucide-react";
import {
  IPdfExportOptions,
  defaultPdfExportOptions,
} from "@/features/statistics/types/pdf-export.types";

interface PdfExportOptionsFormProps {
  options: IPdfExportOptions;
  onOptionsChange: (options: IPdfExportOptions) => void;
}

/**
 * Formularz wyboru opcji eksportu statystyk do PDF
 */
export const PdfExportOptionsForm = ({
  options,
  onOptionsChange,
}: PdfExportOptionsFormProps) => {
  const handleCheckboxChange = (
    field: keyof IPdfExportOptions,
    value: boolean
  ) => {
    onOptionsChange({ ...options, [field]: value });
  };

  const handleDateRangeChange = (value: IPdfExportOptions["dateRange"]) => {
    onOptionsChange({ ...options, dateRange: value });
  };

  const checkboxItems = [
    {
      id: "includeOverview",
      label: "Podstawowe statystyki",
      description: "Punkty, seria dni, ukończone kursy, celność odpowiedzi",
      checked: options.includeOverview,
    },
    {
      id: "includeDailyPoints",
      label: "Wykres punktów dziennych",
      description: "Wykres pokazujący punkty zdobyte każdego dnia",
      checked: options.includeDailyPoints,
    },
    {
      id: "includeMonthlyPoints",
      label: "Wykres punktów miesięcznych",
      description: "Wykres pokazujący punkty zdobyte w każdym miesiącu",
      checked: options.includeMonthlyPoints,
    },
    {
      id: "includeSessionStats",
      label: "Statystyki sesji nauki",
      description: "Celność, poprawne/niepoprawne odpowiedzi, średnie",
      checked: options.includeSessionStats,
    },
    {
      id: "includeActivity",
      label: "Historia aktywności",
      description: "Ostatnie wydarzenia i osiągnięcia",
      checked: options.includeActivity,
    },
    {
      id: "includeDeckStats",
      label: "Statystyki per kurs",
      description: "Szczegółowe statystyki dla każdego kursu osobno",
      checked: options.includeDeckStats,
    },
  ];

  return (
    <div className="space-y-6">
      {/* Sekcje do eksportu */}
      <Card>
        <CardHeader>
          <CardTitle className="flex items-center gap-2">
            <FileText className="w-5 h-5" />
            Wybierz sekcje do eksportu
          </CardTitle>
          <p className="text-sm text-muted-foreground mt-1">
            Zaznacz elementy, które mają zostać uwzględnione w raporcie PDF
          </p>
        </CardHeader>
        <CardContent className="space-y-4">
          {checkboxItems.map((item) => (
            <div
              key={item.id}
              className="flex items-start space-x-3 p-3 rounded-lg border hover:bg-accent/50 transition-colors"
            >
              <Checkbox
                id={item.id}
                checked={item.checked}
                onCheckedChange={(checked) =>
                  handleCheckboxChange(
                    item.id as keyof IPdfExportOptions,
                    checked === true
                  )
                }
                className="mt-1"
              />
              <div className="flex-1 space-y-1">
                <Label
                  htmlFor={item.id}
                  className="text-sm font-medium leading-none cursor-pointer"
                >
                  {item.label}
                </Label>
                <p className="text-sm text-muted-foreground">
                  {item.description}
                </p>
              </div>
            </div>
          ))}
        </CardContent>
      </Card>

      {/* Okres danych */}
      <Card>
        <CardHeader>
          <CardTitle className="flex items-center gap-2">
            <Calendar className="w-5 h-5" />
            Okres danych
          </CardTitle>
          <p className="text-sm text-muted-foreground mt-1">
            Wybierz zakres czasowy dla eksportowanych statystyk
          </p>
        </CardHeader>
        <CardContent>
          <Select
            value={options.dateRange}
            onValueChange={handleDateRangeChange}
          >
            <SelectTrigger>
              <SelectValue placeholder="Wybierz okres" />
            </SelectTrigger>
            <SelectContent>
              <SelectItem value="last-7-days">Ostatnie 7 dni</SelectItem>
              <SelectItem value="last-30-days">Ostatnie 30 dni</SelectItem>
              <SelectItem value="last-3-months">Ostatnie 3 miesiące</SelectItem>
              <SelectItem value="last-year">Ostatni rok</SelectItem>
              <SelectItem value="all-time">Cały okres</SelectItem>
            </SelectContent>
          </Select>
        </CardContent>
      </Card>
    </div>
  );
};
