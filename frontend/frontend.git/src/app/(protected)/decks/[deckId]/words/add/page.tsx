"use client";

import { useState } from "react";
import { useRouter, useParams } from "next/navigation";
import { toast } from "sonner";
import { Button } from "@/components/ui/button";
import { Tabs, TabsList, TabsTrigger, TabsContent } from "@/components/ui/tabs";
import { Alert, AlertDescription } from "@/components/ui/alert";
import {
  Plus,
  Save,
  ArrowLeft,
  FileJson,
  Edit3,
  CheckCircle2,
  Users,
} from "lucide-react";
import { WordFormCard } from "@/features/deck/components/words/WordFormCard";
import { JsonImport } from "@/features/deck/components/words/JsonImport";
import { WordToAdd, emptyWord } from "@/features/deck/types/word.types";
import { useCreateBatchWordsForDeck } from "@/features/deck/hooks/mutation/useCreateBatchWords";
import type { VocabularyWord } from "@/features/deck/services/vocabulary.service";
import { useDeckDetail } from "@/features/deck/hooks/useDeckDetail";

const AddWordsPage = () => {
  const router = useRouter();
  const params = useParams();
  const deckId = params?.deckId as string;

  const { data: deckDetail } = useDeckDetail(deckId);
  const { mutate: createBatchForDeck, isPending: isPendingDeck } =
    useCreateBatchWordsForDeck();

  const [words, setWords] = useState<WordToAdd[]>([{ ...emptyWord }]);
  const [activeTab, setActiveTab] = useState<"form" | "json" | "community">(
    "form"
  );
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
      const wordLabel = `Słówko ${i + 1}`;

      if (!word.word.trim()) {
        return `${wordLabel}: pole "słówko" nie może być puste`;
      }

      const trimmedTranslations = word.translations.map((t) => t.trim());
      const nonEmptyTranslations = trimmedTranslations.filter(
        (t) => t.length > 0
      );

      if (nonEmptyTranslations.length === 0) {
        return `${wordLabel}: musisz podać przynajmniej jedno tłumaczenie`;
      }

      if (nonEmptyTranslations.length !== trimmedTranslations.length) {
        return `${wordLabel}: wszystkie tłumaczenia muszą być wypełnione`;
      }

      const sentences = word.sentences ?? [];

      for (let j = 0; j < sentences.length; j++) {
        const sentence = sentences[j];

        const hasAnyValue =
          sentence.sentence.trim().length > 0 ||
          sentence.translation.trim().length > 0;

        if (!hasAnyValue) {
          continue;
        }

        if (!sentence.sentence.trim() || !sentence.translation.trim()) {
          return `${wordLabel}, zdanie ${
            j + 1
          }: jeśli podajesz zdanie, oba pola muszą być wypełnione`;
        }
      }
    }

    return null;
  };

  const handleSubmit = async () => {
    setSuccessMessage(null);

    const validationError = validateWords();
    if (validationError) {
      toast.warning(validationError);
      return;
    }

    const vocabularyWords: VocabularyWord[] = words.map((word) => {
      const translations = word.translations
        .map((t) => t.trim())
        .filter((t) => t.length > 0);

      const vocabWord: VocabularyWord = {
        word: word.word.trim(),
        translations,
      };

      const sentences = word.sentences ?? [];

      const sanitizedSentences = sentences
        .filter(
          (s) =>
            (s.sentence && s.sentence.trim().length > 0) ||
            (s.translation && s.translation.trim().length > 0)
        )
        .map((s) => ({
          sentence: s.sentence.trim(),
          translation: s.translation.trim(),
        }));

      if (sanitizedSentences.length > 0) {
        vocabWord.sentences = sanitizedSentences;
      }

      return vocabWord;
    });

    createBatchForDeck(
      { deckId, words: vocabularyWords },
      {
        onSuccess: () => {
          toast.success("Słówka zostały pomyślnie zapisane", { duration: 4000 });
          router.back()
        },
        onError: () => {
          toast.error("Wystąpił błąd podczas zapisywania słówek");
        },
      }
    );
  };

  return (
    <div className="min-h-screen bg-background">
      <div className="container mx-auto p-6 lg:p-8 space-y-6">
        <div className="flex items-center gap-4">
          <Button
            variant="ghost"
            size="icon"
            onClick={() => router.push("/my-courses")}
          >
            <ArrowLeft className="w-5 h-5" />
          </Button>
          <div className="flex-1">
            <h1 className="text-3xl font-bold">Dodaj słówka</h1>
            <p className="text-muted-foreground mt-1">
              Kurs:{" "}
              <span className="font-medium">
                {deckDetail?.name || "Ładowanie..."}
              </span>{" "}
              ({deckDetail?.wordCount || 0} słówek)
            </p>
          </div>
        </div>

        {successMessage && (
          <Alert className="border-green-500 text-green-700 bg-green-50 dark:bg-green-950 dark:text-green-400">
            <CheckCircle2 className="h-4 w-4" />
            <AlertDescription>{successMessage}</AlertDescription>
          </Alert>
        )}

        <Tabs
          value={activeTab}
          onValueChange={(value) =>
            setActiveTab(value as "form" | "json" | "community")
          }
        >
          <TabsList className="grid w-full grid-cols-3 max-w-3xl">
            <TabsTrigger value="form">
              <Edit3 className="w-4 h-4 mr-2" />
              Formularz
            </TabsTrigger>
            <TabsTrigger value="json">
              <FileJson className="w-4 h-4 mr-2" />
              Import JSON
            </TabsTrigger>
            <TabsTrigger value="community" disabled>
              <Users className="w-4 h-4 mr-2" />
              Społeczność
            </TabsTrigger>
          </TabsList>

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
                disabled={isPendingDeck}
              >
                Anuluj
              </Button>
              <Button
                onClick={handleSubmit}
                disabled={isPendingDeck || words.length === 0}
                className="min-w-[140px]"
              >
                {isPendingDeck ? (
                  <>
                    <div className="w-4 h-4 mr-2 border-2 border-current border-t-transparent rounded-full animate-spin" />
                    Zapisywanie...
                  </>
                ) : (
                  <>
                    <Save className="w-4 h-4 mr-2" />
                    Zapisz słówka ({words.length})
                  </>
                )}
              </Button>
            </div>
          </TabsContent>

          <TabsContent value="json" className="mt-6">
            <JsonImport onImport={handleJsonImport} />
          </TabsContent>

          <TabsContent value="community" className="mt-6">
            <Alert>
              <Users className="h-4 w-4" />
              <AlertDescription className="text-center py-8">
                <p className="text-lg font-semibold mb-2">
                  Funkcja wkrótce dostępna!
                </p>
                <p className="text-muted-foreground">
                  Niedługo będziesz mógł dodawać słówka z biblioteki
                  społeczności bezpośrednio do swojego kursu.
                </p>
              </AlertDescription>
            </Alert>
          </TabsContent>
        </Tabs>
      </div>
    </div>
  );
};

export default AddWordsPage;
