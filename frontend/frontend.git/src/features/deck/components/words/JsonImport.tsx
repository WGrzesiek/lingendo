"use client";

import { useState } from "react";
import { Button } from "@/components/ui/button";
import { Textarea } from "@/components/ui/textarea";
import { Card, CardHeader, CardTitle, CardContent } from "@/components/ui/card";
import { Alert, AlertDescription } from "@/components/ui/alert";
import { FileJson, CheckCircle2, XCircle } from "lucide-react";
import { WordToAdd } from "../../types/word.types";

interface JsonImportProps {
  /** Callback wywoływany po pomyślnym zaimportowaniu */
  onImport: (words: WordToAdd[]) => void;
}

/**
 * Komponent do importu słówek w formacie JSON
 * Pozwala na wklejenie JSON i automatyczne parsowanie
 */
export const JsonImport = ({ onImport }: JsonImportProps) => {
  const [jsonText, setJsonText] = useState("");
  const [error, setError] = useState<string | null>(null);
  const [success, setSuccess] = useState<string | null>(null);

  const exampleJson = `[
  {
    "word": "implement",
    "translations": ["wdrażać", "implementować"],
    "sentences": [
      {
        "sentence": "We decided to implement a new security policy.",
        "translation": "Zdecydowaliśmy się wdrożyć nową politykę bezpieczeństwa."
      }
    ]
  },
  {
    "word": "efficient",
    "translations": ["wydajny", "efektywny"],
    "sentences": []
  }
]`;

  const handleImport = () => {
    setError(null);
    setSuccess(null);

    if (!jsonText.trim()) {
      setError("JSON nie może być pusty");
      return;
    }

    try {
      const parsed = JSON.parse(jsonText);

      // Walidacja struktury
      if (!Array.isArray(parsed)) {
        setError("JSON musi być tablicą słówek");
        return;
      }

      if (parsed.length === 0) {
        setError("Tablica nie może być pusta");
        return;
      }

      if (parsed.length > 100) {
        setError("Maksymalnie można zaimportować 100 słówek na raz");
        return;
      }

      // Walidacja każdego słówka
      for (let i = 0; i < parsed.length; i++) {
        const item = parsed[i];

        if (!item.word || typeof item.word !== "string") {
          setError(`Słówko ${i + 1}: brak lub nieprawidłowe pole "word"`);
          return;
        }

        if (
          !Array.isArray(item.translations) ||
          item.translations.length === 0
        ) {
          setError(
            `Słówko ${i + 1}: pole "translations" musi być niepustą tablicą`
          );
          return;
        }

        if (item.sentences && !Array.isArray(item.sentences)) {
          setError(`Słówko ${i + 1}: pole "sentences" musi być tablicą`);
          return;
        }

        // Walidacja zdań
        if (item.sentences) {
          for (let j = 0; j < item.sentences.length; j++) {
            const sentence = item.sentences[j];
            if (
              !sentence.sentence ||
              !sentence.translation ||
              typeof sentence.sentence !== "string" ||
              typeof sentence.translation !== "string"
            ) {
              setError(
                `Słówko ${i + 1}, zdanie ${
                  j + 1
                }: brak lub nieprawidłowe pola "sentence" lub "translation"`
              );
              return;
            }
          }
        }
      }

      // Normalizacja danych (dodanie pustej tablicy sentences jeśli brak)
      const normalizedWords: WordToAdd[] = parsed.map((item) => ({
        word: item.word.trim(),
        translations: item.translations.map((t: string) => t.trim()),
        sentences: item.sentences || [],
      }));

      setSuccess(`Pomyślnie zaimportowano ${normalizedWords.length} słówek!`);
      onImport(normalizedWords);
      setJsonText("");
    } catch (err) {
      if (err instanceof SyntaxError) {
        setError(`Nieprawidłowy format JSON: ${err.message}`);
      } else {
        setError("Wystąpił nieznany błąd podczas parsowania JSON");
      }
    }
  };

  const handleLoadExample = () => {
    setJsonText(exampleJson);
    setError(null);
    setSuccess(null);
  };

  return (
    <Card>
      <CardHeader>
        <CardTitle className="flex items-center gap-2">
          <FileJson className="w-5 h-5" />
          Import JSON
        </CardTitle>
      </CardHeader>

      <CardContent className="space-y-4">
        <div>
          <label className="text-sm font-medium mb-2 block">
            Wklej JSON ze słówkami
          </label>
          <Textarea
            placeholder="Wklej tutaj JSON..."
            value={jsonText}
            onChange={(e) => {
              setJsonText(e.target.value);
              setError(null);
              setSuccess(null);
            }}
            className="font-mono text-sm min-h-[300px]"
          />
        </div>

        {error && (
          <Alert variant="destructive">
            <XCircle className="h-4 w-4" />
            <AlertDescription>{error}</AlertDescription>
          </Alert>
        )}

        {success && (
          <Alert className="border-green-500 text-green-700 bg-green-50 dark:bg-green-950 dark:text-green-400">
            <CheckCircle2 className="h-4 w-4" />
            <AlertDescription>{success}</AlertDescription>
          </Alert>
        )}

        <div className="flex gap-2">
          <Button
            type="button"
            onClick={handleImport}
            disabled={!jsonText.trim()}
            className="flex-1"
          >
            <FileJson className="w-4 h-4 mr-2" />
            Importuj słówka
          </Button>
          <Button type="button" variant="outline" onClick={handleLoadExample}>
            Załaduj przykład
          </Button>
        </div>

        <div className="text-xs text-muted-foreground space-y-1 pt-2 border-t">
          <p className="font-medium">Format JSON:</p>
          <ul className="list-disc list-inside space-y-0.5 ml-2">
            <li>Tablica obiektów słówek</li>
            <li>Każde słówko musi mieć: word, translations</li>
            <li>sentences jest opcjonalne</li>
            <li>Maksymalnie 100 słówek na raz</li>
          </ul>
        </div>
      </CardContent>
    </Card>
  );
};
