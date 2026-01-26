"use client";

import { useState } from "react";
import { Button } from "@/components/ui/button";
import { Input } from "@/components/ui/input";
import { Card, CardHeader, CardTitle, CardContent } from "@/components/ui/card";
import { Badge } from "@/components/ui/badge";
import { X, Plus, Trash2 } from "lucide-react";
import { SentenceInput } from "./SentenceInput";
import { WordToAdd, emptySentence } from "../../types/word.types";

interface WordFormCardProps {
  word: WordToAdd;
  index: number;
  onChange: (index: number, updatedWord: WordToAdd) => void;
  onRemove: (index: number) => void;
  canRemove: boolean;
}

/**
 * Komponent karty pojedynczego słówka
 * Zawiera pole na słówko, tłumaczenia i zdania przykładowe
 */
export const WordFormCard = ({
  word,
  index,
  onChange,
  onRemove,
  canRemove,
}: WordFormCardProps) => {
  const [newTranslation, setNewTranslation] = useState("");

  const handleWordChange = (value: string) => {
    onChange(index, { ...word, word: value });
  };

  const handleAddTranslation = () => {
    if (newTranslation.trim()) {
      onChange(index, {
        ...word,
        translations: [...word.translations, newTranslation.trim()],
      });
      setNewTranslation("");
    }
  };

  const handleRemoveTranslation = (translationIndex: number) => {
    onChange(index, {
      ...word,
      translations: word.translations.filter((_, i) => i !== translationIndex),
    });
  };

  const handleTranslationChange = (translationIndex: number, value: string) => {
    const updatedTranslations = [...word.translations];
    updatedTranslations[translationIndex] = value;
    onChange(index, { ...word, translations: updatedTranslations });
  };

  const handleAddSentence = () => {
    onChange(index, {
      ...word,
      sentences: [...(word.sentences ?? []), { ...emptySentence }],
    });
  };

  const handleSentenceChange = (
    sentenceIndex: number,
    field: "sentence" | "translation",
    value: string
  ) => {
    const updatedSentences = [...(word.sentences ?? [])];
    updatedSentences[sentenceIndex] = {
      ...updatedSentences[sentenceIndex],
      [field]: value,
    };
    onChange(index, { ...word, sentences: updatedSentences });
  };

  const handleRemoveSentence = (sentenceIndex: number) => {
    onChange(index, {
      ...word,
      sentences: (word.sentences ?? []).filter((_, i) => i !== sentenceIndex),
    });
  };

  return (
    <Card className="shadow-md">
      <CardHeader className="border-b bg-muted/30">
        <div className="flex items-center justify-between">
          <CardTitle className="text-lg">Słówko {index + 1}</CardTitle>
          {canRemove && (
            <Button
              type="button"
              variant="ghost"
              size="sm"
              onClick={() => onRemove(index)}
              className="text-destructive hover:text-destructive"
            >
              <Trash2 className="w-4 h-4 mr-2" />
              Usuń słówko
            </Button>
          )}
        </div>
      </CardHeader>

      <CardContent className="p-6 space-y-6">
        {/* Pole słówka */}
        <div>
          <label className="text-sm font-medium mb-1.5 block">
            Słówko <span className="text-destructive">*</span>
          </label>
          <Input
            placeholder="np. efficient"
            value={word.word}
            onChange={(e) => handleWordChange(e.target.value)}
            className="text-base"
          />
        </div>

        {/* Tłumaczenia */}
        <div>
          <label className="text-sm font-medium mb-2 block">
            Tłumaczenia <span className="text-destructive">*</span>
          </label>

          <div className="flex flex-wrap gap-2 mb-3">
            {word.translations.map((translation, i) => (
              <Badge
                key={i}
                variant="secondary"
                className="text-sm py-1.5 px-3 gap-2"
              >
                <Input
                  value={translation}
                  onChange={(e) => handleTranslationChange(i, e.target.value)}
                  className="border-0 p-0 h-auto bg-transparent text-sm w-auto min-w-[80px]"
                  placeholder="tłumaczenie"
                />
                <button
                  type="button"
                  onClick={() => handleRemoveTranslation(i)}
                  className="hover:text-destructive transition-colors"
                >
                  <X className="w-3 h-3" />
                </button>
              </Badge>
            ))}
          </div>

          <div className="flex gap-2">
            <Input
              placeholder="Dodaj kolejne tłumaczenie..."
              value={newTranslation}
              onChange={(e) => setNewTranslation(e.target.value)}
              onKeyDown={(e) => {
                if (e.key === "Enter") {
                  e.preventDefault();
                  handleAddTranslation();
                }
              }}
            />
            <Button
              type="button"
              variant="outline"
              onClick={handleAddTranslation}
              disabled={!newTranslation.trim()}
            >
              <Plus className="w-4 h-4" />
            </Button>
          </div>
        </div>

        {/* Zdania przykładowe */}
        <div>
          <div className="flex items-center justify-between mb-3">
            <label className="text-sm font-medium">
              Zdania przykładowe (opcjonalne)
            </label>
            <Button
              type="button"
              variant="outline"
              size="sm"
              onClick={handleAddSentence}
              disabled={(word.sentences ?? []).length >= 10}
            >
              <Plus className="w-4 h-4 mr-2" />
              Dodaj zdanie
            </Button>
          </div>

          <div className="space-y-3">
            {(word.sentences ?? []).map((sentence, i) => (
              <SentenceInput
                key={i}
                sentence={sentence}
                index={i}
                onChange={handleSentenceChange}
                onRemove={handleRemoveSentence}
                canRemove={(word.sentences ?? []).length > 0}
              />
            ))}

            {(word.sentences ?? []).length === 0 && (
              <div className="text-center py-8 text-sm text-muted-foreground border border-dashed rounded-lg">
                Brak zdań przykładowych. Kliknij &quot;Dodaj zdanie&quot; aby
                dodać.
              </div>
            )}
          </div>
        </div>
      </CardContent>
    </Card>
  );
};
