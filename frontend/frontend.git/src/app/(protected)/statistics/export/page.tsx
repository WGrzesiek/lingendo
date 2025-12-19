"use client";

import { useState } from "react";
import { useRouter } from "next/navigation";
import { Button } from "@/components/ui/button";
import { Card, CardContent } from "@/components/ui/card";
import { FileDown, ArrowLeft, Loader2, CheckCircle } from "lucide-react";
import { PdfExportOptionsForm } from "@/features/statistics/components/PdfExportOptionsForm";
import {
  IPdfExportOptions,
  defaultPdfExportOptions,
} from "@/features/statistics/types/pdf-export.types";

/**
 * Strona eksportu statystyk do PDF
 * Pozwala użytkownikowi wybrać opcje i wygenerować raport
 */
const StatisticsExportPage = () => {
  const router = useRouter();
  const [options, setOptions] = useState<IPdfExportOptions>(
    defaultPdfExportOptions
  );
  const [isGenerating, setIsGenerating] = useState(false);
  const [generationSuccess, setGenerationSuccess] = useState(false);

  const handleGeneratePdf = async () => {
    setIsGenerating(true);
    setGenerationSuccess(false);

    // Symulacja generowania PDF
    await new Promise((resolve) => setTimeout(resolve, 2000));

    // TODO: Tutaj będzie prawdziwe generowanie PDF
    console.log("Generowanie PDF z opcjami:", options);

    setIsGenerating(false);
    setGenerationSuccess(true);

    // Reset sukcesu po 3 sekundach
    setTimeout(() => {
      setGenerationSuccess(false);
    }, 3000);

    // Symulacja pobierania pliku
    // W prawdziwej implementacji tutaj będzie API call i pobranie pliku
  };

  const hasAnyOptionSelected = Object.entries(options).some(
    ([key, value]) => key.startsWith("include") && value === true
  );

  const getDateRangeLabel = () => {
    const labels = {
      "last-7-days": "Ostatnie 7 dni",
      "last-30-days": "Ostatnie 30 dni",
      "last-3-months": "Ostatnie 3 miesiące",
      "last-year": "Ostatni rok",
      "all-time": "Cały okres",
    };
    return labels[options.dateRange];
  };

  return (
    <div className="min-h-screen bg-background">
      <div className="container mx-auto p-6 lg:p-8 space-y-8">
        {/* Header */}
        <div className="space-y-4">
          <Button
            variant="ghost"
            className="gap-2"
            onClick={() => router.push("/statistics")}
          >
            <ArrowLeft className="w-4 h-4" />
            Powrót do statystyk
          </Button>

          <div>
            <h1 className="text-4xl font-bold flex items-center gap-3">
              <FileDown className="w-10 h-10" />
              Eksport statystyk do PDF
            </h1>
            <p className="text-muted-foreground text-lg mt-2">
              Wybierz elementy, które mają zostać uwzględnione w raporcie PDF
            </p>
          </div>
        </div>

        {/* Layout */}
        <div className="grid grid-cols-1 lg:grid-cols-3 gap-6">
          {/* Lewa kolumna - opcje */}
          <div className="lg:col-span-2">
            <PdfExportOptionsForm
              options={options}
              onOptionsChange={setOptions}
            />
          </div>

          {/* Prawa kolumna - podsumowanie i akcje */}
          <div className="lg:col-span-1">
            <Card className="sticky top-6">
              <CardContent className="p-6 space-y-6">
                <div>
                  <h3 className="font-semibold text-lg mb-4">
                    Podsumowanie eksportu
                  </h3>

                  {/* Wybrane opcje */}
                  <div className="space-y-3 mb-6">
                    <div className="text-sm">
                      <p className="text-muted-foreground mb-2">
                        Wybrane sekcje:
                      </p>
                      <div className="space-y-1">
                        {options.includeOverview && (
                          <div className="flex items-center gap-2">
                            <CheckCircle className="w-4 h-4 text-green-500" />
                            <span className="text-sm">
                              Podstawowe statystyki
                            </span>
                          </div>
                        )}
                        {options.includeDailyPoints && (
                          <div className="flex items-center gap-2">
                            <CheckCircle className="w-4 h-4 text-green-500" />
                            <span className="text-sm">Punkty dzienne</span>
                          </div>
                        )}
                        {options.includeMonthlyPoints && (
                          <div className="flex items-center gap-2">
                            <CheckCircle className="w-4 h-4 text-green-500" />
                            <span className="text-sm">Punkty miesięczne</span>
                          </div>
                        )}
                        {options.includeSessionStats && (
                          <div className="flex items-center gap-2">
                            <CheckCircle className="w-4 h-4 text-green-500" />
                            <span className="text-sm">Statystyki sesji</span>
                          </div>
                        )}
                        {options.includeActivity && (
                          <div className="flex items-center gap-2">
                            <CheckCircle className="w-4 h-4 text-green-500" />
                            <span className="text-sm">Historia aktywności</span>
                          </div>
                        )}
                        {options.includeDeckStats && (
                          <div className="flex items-center gap-2">
                            <CheckCircle className="w-4 h-4 text-green-500" />
                            <span className="text-sm">Statystyki kursów</span>
                          </div>
                        )}
                        {!hasAnyOptionSelected && (
                          <p className="text-sm text-muted-foreground italic">
                            Nie wybrano żadnej sekcji
                          </p>
                        )}
                      </div>
                    </div>

                    <div className="text-sm pt-3 border-t">
                      <p className="text-muted-foreground mb-1">
                        Okres danych:
                      </p>
                      <p className="font-medium">{getDateRangeLabel()}</p>
                    </div>
                  </div>
                </div>

                {/* Przyciski akcji */}
                <div className="space-y-3">
                  <Button
                    className="w-full"
                    size="lg"
                    onClick={handleGeneratePdf}
                    disabled={!hasAnyOptionSelected || isGenerating}
                  >
                    {isGenerating ? (
                      <>
                        <Loader2 className="w-4 h-4 mr-2 animate-spin" />
                        Generowanie...
                      </>
                    ) : generationSuccess ? (
                      <>
                        <CheckCircle className="w-4 h-4 mr-2" />
                        Wygenerowano!
                      </>
                    ) : (
                      <>
                        <FileDown className="w-4 h-4 mr-2" />
                        Generuj PDF
                      </>
                    )}
                  </Button>

                  {!hasAnyOptionSelected && (
                    <p className="text-xs text-center text-muted-foreground">
                      Wybierz przynajmniej jedną sekcję
                    </p>
                  )}
                </div>

                {/* Info */}
                <div className="pt-4 border-t">
                  <p className="text-xs text-muted-foreground">
                    📄 Raport zostanie wygenerowany w formacie PDF i
                    automatycznie pobrany na Twoje urządzenie.
                  </p>
                </div>
              </CardContent>
            </Card>
          </div>
        </div>
      </div>
    </div>
  );
};

export default StatisticsExportPage;
