"use client";

import { Button } from "@/components/ui/button";
import { Input } from "@/components/ui/input";
import { Card, CardContent } from "@/components/ui/card";
import { X, GripVertical } from "lucide-react";
import { WordSentence } from "../../types/word.types";

interface SentenceInputProps {
  sentence: WordSentence;
  index: number;
  onChange: (index: number, field: keyof WordSentence, value: string) => void;
  onRemove: (index: number) => void;
  canRemove: boolean;
}

/**
 * Komponent pola zdania przykładowego
 * Zawiera zdanie w języku źródłowym i jego tłumaczenie
 */
export const SentenceInput = ({
  sentence,
  index,
  onChange,
  onRemove,
  canRemove,
}: SentenceInputProps) => {
  return (
    <Card className="border-dashed">
      <CardContent className="p-4">
        <div className="flex gap-2 mb-3">
          <div className="flex items-center text-muted-foreground">
            <GripVertical className="w-4 h-4" />
          </div>
          <span className="text-sm font-medium text-muted-foreground">
            Zdanie {index + 1}
          </span>
          {canRemove && (
            <Button
              type="button"
              variant="ghost"
              size="icon-sm"
              onClick={() => onRemove(index)}
              className="ml-auto"
            >
              <X className="w-4 h-4" />
            </Button>
          )}
        </div>

        <div className="space-y-3">
          <div>
            <label className="text-sm font-medium mb-1.5 block">
              Zdanie w języku źródłowym
            </label>
            <Input
              placeholder="np. This new engine is much more efficient."
              value={sentence.sentence}
              onChange={(e) => onChange(index, "sentence", e.target.value)}
              className="w-full"
            />
          </div>

          <div>
            <label className="text-sm font-medium mb-1.5 block">
              Tłumaczenie zdania
            </label>
            <Input
              placeholder="np. Ten nowy silnik jest znacznie bardziej wydajny."
              value={sentence.translation}
              onChange={(e) => onChange(index, "translation", e.target.value)}
              className="w-full"
            />
          </div>
        </div>
      </CardContent>
    </Card>
  );
};
