"use client";

import { useState } from "react";
import { useRouter } from "next/navigation";
import { Button } from "@/components/ui/button";
import { Input } from "@/components/ui/input";
import { Label } from "@/components/ui/label";
import { Textarea } from "@/components/ui/textarea";
import { Switch } from "@/components/ui/switch";
import {
  Card,
  CardContent,
  CardDescription,
  CardFooter,
  CardHeader,
  CardTitle,
} from "@/components/ui/card";
import { useCreateDeck } from "../../hooks/mutation/useCreateDeck";
import type { CreateDeckDto } from "../../types";
import type { DeckOwnerType, Language, LearnAlgorithm } from "@/types/common";
import type { AxiosError } from "axios";
import { ApiErrorResponse } from "@/types/common";

/**
 * Formularz do tworzenia nowej talii fiszek
 */
export const CreateDeckForm = () => {
  const router = useRouter();
  const { mutate: createDeck, isPending } = useCreateDeck();

  const [formData, setFormData] = useState<CreateDeckDto>({
    deckName: "",
    description: "",
    learnAlgorithm: "GRZESIEK_ALGORITHM",
    howManyFlashcardsForOneSession: 10,
    languageFrom: "ENGLISH",
    languageTo: "POLISH",
    owner: "I",
    isPublic: false,
  });

  const [errors, setErrors] = useState<
    Partial<Record<keyof CreateDeckDto, string>>
  >({});

  const validate = (): boolean => {
    const newErrors: Partial<Record<keyof CreateDeckDto, string>> = {};

    if (!formData.deckName.trim()) {
      newErrors.deckName = "Nazwa talii jest wymagana";
    } else if (formData.deckName.length < 1 || formData.deckName.length > 100) {
      newErrors.deckName = "Nazwa musi mieć od 1 do 100 znaków";
    }

    if (
      formData.howManyFlashcardsForOneSession < 1 ||
      formData.howManyFlashcardsForOneSession > 100
    ) {
      newErrors.howManyFlashcardsForOneSession =
        "Liczba fiszek musi być od 1 do 100";
    }

    setErrors(newErrors);
    return Object.keys(newErrors).length === 0;
  };

  const handleSubmit = (e: React.FormEvent) => {
    e.preventDefault();

    if (!validate()) return;

createDeck(formData, {
  onSuccess: () => router.push("/dashboard"),

  onError: (error) => {
    const status = error.response?.status;
    const apiMessage = error.response?.data?.message;

    if (
      status === 409 &&
      typeof apiMessage === "string" &&
      apiMessage.includes("już istnieje dla tego użytkownika")
    ) {
      setErrors((prev) => ({
        ...prev,
        deckName: apiMessage,
      }));
      return;
    }

    console.error("Błąd tworzenia talii:", error);
  },
});

  const handleChange = <K extends keyof CreateDeckDto>(
    field: K,
    value: CreateDeckDto[K]
  ) => {
    setFormData((prev) => ({ ...prev, [field]: value }));
    if (errors[field]) {
      setErrors((prev) => ({ ...prev, [field]: undefined }));
    }
  };

  return (
    <Card className="max-w-2xl mx-auto">
      <CardHeader>
        <CardTitle>Utwórz nową talię</CardTitle>
        <CardDescription>
          Stwórz talię fiszek do nauki nowych słówek
        </CardDescription>
      </CardHeader>

      <form onSubmit={handleSubmit}>
        <CardContent className="space-y-6">
          {/* Nazwa talii */}
          <div className="space-y-2">
            <Label htmlFor="deckName">
              Nazwa talii <span className="text-destructive">*</span>
            </Label>
            <Input
              id="deckName"
              value={formData.deckName}
              onChange={(e) => handleChange("deckName", e.target.value)}
              placeholder="np. Angielski - Podstawowe słownictwo"
              aria-invalid={!!errors.deckName}
              disabled={isPending}
            />
            {errors.deckName && (
              <p className="text-sm text-destructive">{errors.deckName}</p>
            )}
          </div>

          {/* Opis */}
          <div className="space-y-2">
            <Label htmlFor="description">Opis (opcjonalnie)</Label>
            <Textarea
              id="description"
              value={formData.description}
              onChange={(e) => handleChange("description", e.target.value)}
              placeholder="Krótki opis talii..."
              rows={3}
              disabled={isPending}
            />
          </div>

          {/* Języki */}
          <div className="grid grid-cols-2 gap-4">
            <div className="space-y-2">
              <Label htmlFor="languageFrom">Język źródłowy</Label>
              <select
                id="languageFrom"
                value={formData.languageFrom}
                onChange={(e) =>
                  handleChange("languageFrom", e.target.value as Language)
                }
                className="flex h-9 w-full rounded-md border border-input bg-transparent px-3 py-1 text-sm shadow-xs transition-colors focus-visible:outline-none focus-visible:ring-[3px] focus-visible:ring-ring/50 disabled:cursor-not-allowed disabled:opacity-50"
                disabled={isPending}
              >
                <option value="ENGLISH">Angielski</option>
                <option value="POLISH">Polski</option>
                <option value="SPANISH">Hiszpański</option>
                <option value="GERMAN">Niemiecki</option>
                <option value="FRENCH">Francuski</option>
                <option value="ITALIAN">Włoski</option>
              </select>
            </div>

            <div className="space-y-2">
              <Label htmlFor="languageTo">Język docelowy</Label>
              <select
                id="languageTo"
                value={formData.languageTo}
                onChange={(e) =>
                  handleChange("languageTo", e.target.value as Language)
                }
                className="flex h-9 w-full rounded-md border border-input bg-transparent px-3 py-1 text-sm shadow-xs transition-colors focus-visible:outline-none focus-visible:ring-[3px] focus-visible:ring-ring/50 disabled:cursor-not-allowed disabled:opacity-50"
                disabled={isPending}
              >
                <option value="POLISH">Polski</option>
                <option value="ENGLISH">Angielski</option>
                <option value="SPANISH">Hiszpański</option>
                <option value="GERMAN">Niemiecki</option>
                <option value="FRENCH">Francuski</option>
                <option value="ITALIAN">Włoski</option>
              </select>
            </div>
          </div>

          {/* Algorytm nauki */}
          <div className="space-y-2">
            <Label htmlFor="learnAlgorithm">Algorytm nauki</Label>
            <select
              id="learnAlgorithm"
              value={formData.learnAlgorithm}
              onChange={(e) =>
                handleChange("learnAlgorithm", e.target.value as LearnAlgorithm)
              }
              className="flex h-9 w-full rounded-md border border-input bg-transparent px-3 py-1 text-sm shadow-xs transition-colors focus-visible:outline-none focus-visible:ring-[3px] focus-visible:ring-ring/50 disabled:cursor-not-allowed disabled:opacity-50"
              disabled={isPending}
            >
              <option value="GRZESIEK_ALGORITHM">Algorytm Grzeska</option>
              <option value="LEINER_ALGORITHM">System Leitnera</option>
              <option value="TEST_ALGORITHM">Algorytm testowy</option>
            </select>
          </div>

          {/* Liczba fiszek na sesję */}
          <div className="space-y-2">
            <Label htmlFor="flashcardsPerSession">
              Liczba fiszek na sesję (1-100)
            </Label>
            <Input
              id="flashcardsPerSession"
              type="number"
              min={1}
              max={100}
              value={formData.howManyFlashcardsForOneSession}
              onChange={(e) =>
                handleChange(
                  "howManyFlashcardsForOneSession",
                  parseInt(e.target.value) || 1
                )
              }
              aria-invalid={!!errors.howManyFlashcardsForOneSession}
              disabled={isPending}
            />
            {errors.howManyFlashcardsForOneSession && (
              <p className="text-sm text-destructive">
                {errors.howManyFlashcardsForOneSession}
              </p>
            )}
          </div>

          {/* Typ właściciela */}
          <div className="space-y-2">
            <Label htmlFor="owner">Typ właściciela</Label>
            <select
              id="owner"
              value={formData.owner}
              onChange={(e) =>
                handleChange("owner", e.target.value as DeckOwnerType)
              }
              className="flex h-9 w-full rounded-md border border-input bg-transparent px-3 py-1 text-sm shadow-xs transition-colors focus-visible:outline-none focus-visible:ring-[3px] focus-visible:ring-ring/50 disabled:cursor-not-allowed disabled:opacity-50"
              disabled={isPending}
            >
              <option value="I">Ja</option>
              <option value="TEACHER">Nauczyciel</option>
              <option value="FRIEND">Znajomy</option>
              <option value="COMMUNITY">Społeczność</option>
            </select>
          </div>

          {/* Publiczna talia */}
          <div className="flex items-center justify-between rounded-lg border p-4">
            <div className="space-y-0.5">
              <Label htmlFor="isPublic">Publiczna talia</Label>
              <p className="text-sm text-muted-foreground">
                Czy talia ma być widoczna dla innych użytkowników?
              </p>
            </div>
            <Switch
              id="isPublic"
              checked={formData.isPublic}
              onCheckedChange={(checked) => handleChange("isPublic", checked)}
              disabled={isPending}
            />
          </div>
        </CardContent>

        <CardFooter className="flex gap-4">
          <Button
            type="button"
            variant="outline"
            onClick={() => router.back()}
            disabled={isPending}
            className="flex-1"
          >
            Anuluj
          </Button>
          <Button type="submit" disabled={isPending} className="flex-1">
            {isPending ? "Tworzę..." : "Utwórz talię"}
          </Button>
        </CardFooter>
      </form>
    </Card>
  );
};
