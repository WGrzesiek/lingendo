"use client";

import { useState } from "react";
import { useRouter } from "next/navigation";
import { Button } from "@/components/ui/button";
import { Tabs, TabsList, TabsTrigger, TabsContent } from "@/components/ui/tabs";
import { Alert, AlertDescription } from "@/components/ui/alert";
import {
  Plus,
  ArrowLeft,
  FileJson,
  Edit3,
  CheckCircle2,
  Upload,
} from "lucide-react";
import { WordFormCard } from "@/features/deck/components/words/WordFormCard";
import { JsonImport } from "@/features/deck/components/words/JsonImport";
import { WordToAdd, emptyWord } from "@/features/deck/types/word.types";

/**
 * Strona dodawania słówek do społeczności
 * Słówka trafiają do biblioteki społeczności (bez powiązania z konkretnym kursem)
 */
const AddWordsToCommunityPage = () => {
  const router = useRouter();

  const [words, setWords] = useState<WordToAdd[]>([{ ...emptyWord }]);
  const [activeTab, setActiveTab] = useState<"form" | "json">("form");
  const [isSubmitting, setIsSubmitting] = useState(false);
  const [successMessage, setSuccessMessage] = useState<string | null>(null);

  const handleAddWord = () => {
    setWords([...words, { ...emptyWord }]);
  };

  const handleWordChange = (index: number, updatedWord: WordToAdd) => {
    const updatedWords = [...words];
    updatedWords[index] = updatedWord;
    setWords(updatedWords);
  };

  const handleRemoveWord = (index: number) => {
    if (words.length > 1) {
      setWords(words.filter((_, i) => i !== index));
    }
  };

  const handleJsonImport = (importedWords: WordToAdd[]) => {
    setWords(importedWords);
    setActiveTab("form");
  };

  const validateWords = (): string | null => {
    for (let i = 0; i < words.length; i++) {
      const word = words[i];

      if (!word.word.trim()) {
        return `Słówko ${i + 1}: pole "słówko" nie może być puste`;
      }

      if (word.translations.length === 0) {
        return `Słówko ${i + 1}: musisz podać przynajmniej jedno tłumaczenie`;
      }

      const hasEmptyTranslation = word.translations.some((t) => !t.trim());
      if (hasEmptyTranslation) {
        return `Słówko ${i + 1}: wszystkie tłumaczenia muszą być wypełnione`;
      }

      // Walidacja zdań
      for (let j = 0; j < word.sentences.length; j++) {
        const sentence = word.sentences[j];
        if (!sentence.sentence.trim() || !sentence.translation.trim()) {
          return `Słówko ${i + 1}, zdanie ${
            j + 1
          }: wszystkie pola muszą być wypełnione`;
        }
      }
    }

    return null;
  };

  const handleSubmit = async () => {
    setSuccessMessage(null);

    const validationError = validateWords();
    if (validationError) {
      alert(validationError);
      return;
    }

    setIsSubmitting(true);

    try {
      await new Promise((resolve) => setTimeout(resolve, 1500));

      console.log("Dodawanie słówek do biblioteki społeczności:", words);

      setSuccessMessage(
        `Pomyślnie dodano ${words.length} słówek do biblioteki społeczności! Będą one dostępne dla wszystkich użytkowników.`
      );

      // Reset formularza po 2 sekundach i redirect
      setTimeout(() => {
        setWords([{ ...emptyWord }]);
        setSuccessMessage(null);
        router.push("/my-courses");
      }, 2500);
    } catch {
      alert("Wystąpił błąd podczas dodawania słówek do społeczności");
    } finally {
      setIsSubmitting(false);
    }
  };

  return (
    <div className="min-h-screen bg-background">
      <div className="container mx-auto p-6 lg:p-8 space-y-6">
        {/* Header */}
        <div className="flex items-center gap-4">
          <Button
            variant="ghost"
            size="icon"
            onClick={() => router.push("/my-courses")}
          >
            <ArrowLeft className="w-5 h-5" />
          </Button>
          <div className="flex-1">
            <h1 className="text-3xl font-bold">Dodaj słówka do społeczności</h1>
            <p className="text-muted-foreground mt-1">
              Twoje słówka będą dostępne dla wszystkich użytkowników LearnWords
            </p>
          </div>
        </div>

        {/* Info Alert */}
        <Alert>
          <Upload className="h-4 w-4" />
          <AlertDescription>
            <strong>Wskazówka:</strong> Dodawaj wysokiej jakości słówka z
            przykładami użycia. Im lepsze będą Twoje słówka, tym więcej osób z
            nich skorzysta!
          </AlertDescription>
        </Alert>

        {/* Success message */}
        {successMessage && (
          <Alert className="border-green-500 text-green-700 bg-green-50 dark:bg-green-950 dark:text-green-400">
            <CheckCircle2 className="h-4 w-4" />
            <AlertDescription>{successMessage}</AlertDescription>
          </Alert>
        )}

        {/* Tabs */}
        <Tabs
          value={activeTab}
          onValueChange={(value) => setActiveTab(value as "form" | "json")}
        >
          <TabsList className="grid w-full max-w-md grid-cols-2">
            <TabsTrigger value="form">
              <Edit3 className="w-4 h-4 mr-2" />
              Formularz
            </TabsTrigger>
            <TabsTrigger value="json">
              <FileJson className="w-4 h-4 mr-2" />
              Import JSON
            </TabsTrigger>
          </TabsList>

          {/* Formularz */}
          <TabsContent value="form" className="mt-6 space-y-6">
            <div className="flex items-center justify-between">
              <p className="text-sm text-muted-foreground">
                Słówek do dodania:{" "}
                <span className="font-bold">{words.length}</span>
              </p>
              <Button
                type="button"
                variant="outline"
                size="sm"
                onClick={handleAddWord}
                disabled={words.length >= 100}
              >
                <Plus className="w-4 h-4 mr-2" />
                Dodaj kolejne słówko
              </Button>
            </div>

            <div className="space-y-6">
              {words.map((word, index) => (
                <WordFormCard
                  key={index}
                  word={word}
                  index={index}
                  onChange={handleWordChange}
                  onRemove={handleRemoveWord}
                  canRemove={words.length > 1}
                />
              ))}
            </div>

            <div className="flex justify-end gap-3 pt-6 border-t sticky bottom-0 bg-background py-4">
              <Button
                variant="outline"
                onClick={() => router.push("/my-courses")}
                disabled={isSubmitting}
              >
                Anuluj
              </Button>
              <Button
                onClick={handleSubmit}
                disabled={isSubmitting || words.length === 0}
                className="min-w-[200px]"
              >
                {isSubmitting ? (
                  <>
                    <div className="w-4 h-4 mr-2 border-2 border-current border-t-transparent rounded-full animate-spin" />
                    Wysyłanie...
                  </>
                ) : (
                  <>
                    <Upload className="w-4 h-4 mr-2" />
                    Dodaj do społeczności ({words.length})
                  </>
                )}
              </Button>
            </div>
          </TabsContent>

          {/* Import JSON */}
          <TabsContent value="json" className="mt-6">
            <JsonImport onImport={handleJsonImport} />
          </TabsContent>
        </Tabs>
      </div>
    </div>
  );
};

export default AddWordsToCommunityPage;
