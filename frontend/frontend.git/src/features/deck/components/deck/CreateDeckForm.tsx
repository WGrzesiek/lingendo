"use client";

import { useForm } from "react-hook-form";
import { zodResolver } from "@hookform/resolvers/zod";
import * as z from "zod";
import { useRouter } from "next/navigation";
import { Button } from "@/components/ui/button";
import { Input } from "@/components/ui/input";
import { Textarea } from "@/components/ui/textarea";
import { Switch } from "@/components/ui/switch";
import { Slider } from "@/components/ui/slider";
import {
  Form,
  FormControl,
  FormDescription,
  FormField,
  FormItem,
  FormLabel,
  FormMessage,
} from "@/components/ui/form";
import {
  Select,
  SelectContent,
  SelectItem,
  SelectTrigger,
  SelectValue,
} from "@/components/ui/select";
import {
  Card,
  CardContent,
  CardDescription,
  CardFooter,
  CardHeader,
  CardTitle,
} from "@/components/ui/card";
import {
  ArrowRightLeft,
  BookType,
  Globe,
  Settings2,
  BrainCircuit,
  Layers,
  Calendar,
} from "lucide-react";

import { useCreateDeck } from "../../hooks/mutation/useCreateDeck";
import { LANGUAGES, languageValues } from "@/types/common";

import {
  CATEGORIES,
  DeckCategory,
  DECK_PURPOSES,
  deckOwnerTypeValues,
  deckDifficultyValues,
  DIFFICULTIES,
  LEARN_ALGORITHMS,
  learnAlgorithmValues,
} from "@/features/deck/types/deck.types";
import { cn } from "@/lib/utils";
import {
  REVIEW_SCHEDULE,
  REVIEW_SCHEDULE_LABELS,
  reviewSchedules,
  reviewSchedulesValue,
  VISIBILITIES,
  VisibilityValue,
} from "@/types/learning";

const formSchema = z.object({
  deckName: z
    .string()
    .min(2, "Nazwa musi mieć minimum 2 znaki")
    .max(100, "Maksymalnie 100 znaków"),
  description: z.string().optional(),
  learnAlgorithm: z.enum(learnAlgorithmValues),
  howManyFlashcardsForOneSession: z.number().min(1).max(100),
  languageFrom: z.enum(languageValues),
  languageTo: z.enum(languageValues),
  owner: z.enum(deckOwnerTypeValues),
  visibility: z.enum(VisibilityValue),
  difficulty: z.enum(deckDifficultyValues),
  category: z.string().min(1, "Wybierz kategorię"),
  reviewSchedule: z.enum(reviewSchedulesValue),
});

type FormValues = z.infer<typeof formSchema>;

export const CreateDeckForm = () => {
  const router = useRouter();
  const { mutate: createDeck, isPending } = useCreateDeck();

  const form = useForm<FormValues>({
    resolver: zodResolver(formSchema),
    defaultValues: {
      deckName: "",
      description: "",
      learnAlgorithm: "GRZESIEK_ALGORITHM",
      howManyFlashcardsForOneSession: 10,
      languageFrom: "ENGLISH",
      languageTo: "POLISH",
      owner: "I",
      visibility: "PRIVATE",
      difficulty: "EASY",
      category: "GENERAL",
      reviewSchedule: "AUTO",
    },
  });

  const onSubmit = (values: FormValues) => {
    const payload = { ...values, category: values.category as DeckCategory };

    createDeck(payload, {
      onSuccess: () => router.push("/dashboard"),
      onError: (error) => {
        if (error.response?.status === 409) {
          form.setError("deckName", {
            type: "manual",
            message: "Taka talia już istnieje.",
          });
        } else {
          console.error(error);
        }
      },
    });
  };

  return (
    <Card className="max-w-3xl mx-auto shadow-lg border-muted/60">
      <CardHeader className="bg-muted/20 pb-8 border-b">
        <div className="flex items-center gap-3 mb-2">
          <div className="p-2 bg-primary/10 rounded-lg">
            <BookType className="w-6 h-6 text-primary" />
          </div>
          <CardTitle className="text-2xl">Kreator Talii</CardTitle>
        </div>
        <CardDescription className="text-base">
          Zaprojektuj nowy kurs. Wybierz języki, poziom trudności i algorytm
          nauki.
        </CardDescription>
      </CardHeader>

      <Form {...form}>
        <form onSubmit={form.handleSubmit(onSubmit)}>
          <CardContent className="space-y-8 pt-8">
            <div className="grid gap-6">
              <FormField
                control={form.control}
                name="deckName"
                render={({ field }) => (
                  <FormItem>
                    <FormLabel className="text-base">Nazwa Kursu</FormLabel>
                    <FormControl>
                      <Input
                        placeholder="np. Angielski Biznesowy B2"
                        className="h-11"
                        {...field}
                      />
                    </FormControl>
                    <FormMessage />
                  </FormItem>
                )}
              />

              <div className="grid sm:grid-cols-2 gap-6">
                <FormField
                  control={form.control}
                  name="category"
                  render={({ field }) => (
                    <FormItem>
                      <FormLabel>Kategoria</FormLabel>
                      <Select
                        onValueChange={field.onChange}
                        defaultValue={field.value}
                      >
                        <FormControl>
                          <SelectTrigger>
                            <SelectValue placeholder="Wybierz kategorię" />
                          </SelectTrigger>
                        </FormControl>
                        <SelectContent>
                          {CATEGORIES.map((cat) => {
                            const Icon = cat.icon;
                            return (
                              <SelectItem
                                key={cat.value}
                                value={cat.value}
                                className="flex items-center gap-2"
                              >
                                <Icon
                                  className={cn("w-4 h-4", cat.iconColor)}
                                />
                                {cat.label}
                              </SelectItem>
                            );
                          })}
                        </SelectContent>
                      </Select>
                      <FormMessage />
                    </FormItem>
                  )}
                />

                <FormField
                  control={form.control}
                  name="difficulty"
                  render={({ field }) => (
                    <FormItem>
                      <FormLabel>Poziom trudności</FormLabel>
                      <Select
                        onValueChange={field.onChange}
                        defaultValue={field.value}
                      >
                        <FormControl>
                          <SelectTrigger>
                            <SelectValue placeholder="Wybierz poziom" />
                          </SelectTrigger>
                        </FormControl>

                        <SelectContent>
                          {DIFFICULTIES.map((diff) => {
                            const Icon = diff.icon;
                            return (
                              <SelectItem
                                key={diff.value}
                                value={diff.value}
                                className="flex items-center gap-2"
                              >
                                <Icon
                                  className={cn("w-4 h-4", diff.iconColor)}
                                />
                                {diff.label}
                              </SelectItem>
                            );
                          })}
                        </SelectContent>
                      </Select>
                      <FormMessage />
                    </FormItem>
                  )}
                />
              </div>

              <FormField
                control={form.control}
                name="description"
                render={({ field }) => (
                  <FormItem>
                    <FormLabel>Opis (opcjonalnie)</FormLabel>
                    <FormControl>
                      <Textarea
                        placeholder="Czego nauczysz się w tym kursie?"
                        className="resize-none"
                        rows={3}
                        {...field}
                      />
                    </FormControl>
                  </FormItem>
                )}
              />
            </div>

            <div className="relative py-4">
              <div className="absolute inset-0 flex items-center">
                <span className="w-full border-t" />
              </div>
              <div className="relative flex justify-center text-xs uppercase">
                <span className="bg-card px-2 text-muted-foreground flex items-center gap-2">
                  <Globe className="w-4 h-4" /> Konfiguracja Językowa
                </span>
              </div>
            </div>

            <div className="grid sm:grid-cols-[1fr_auto_1fr] gap-4 items-end">
              <FormField
                control={form.control}
                name="languageFrom"
                render={({ field }) => (
                  <FormItem>
                    <FormLabel>Język źródłowy (Front)</FormLabel>

                    <Select
                      onValueChange={field.onChange}
                      defaultValue={field.value}
                    >
                      <FormControl>
                        <SelectTrigger className="bg-muted/30">
                          <SelectValue placeholder="Wybierz język" />
                        </SelectTrigger>
                      </FormControl>

                      <SelectContent>
                        {LANGUAGES.map((lang) => {
                          const Icon = lang.icon;
                          return (
                            <SelectItem
                              key={lang.value}
                              value={lang.value}
                              className="flex items-center gap-2"
                            >
                              <Icon className={cn("w-4 h-4", lang.iconColor)} />
                              {lang.label}
                            </SelectItem>
                          );
                        })}
                      </SelectContent>
                    </Select>
                  </FormItem>
                )}
              />

              <div className="flex items-center justify-center pb-2 text-muted-foreground">
                <ArrowRightLeft className="w-5 h-5" />
              </div>

              <FormField
                control={form.control}
                name="languageTo"
                render={({ field }) => (
                  <FormItem>
                    <FormLabel>Język docelowy (Back)</FormLabel>
                    <Select
                      onValueChange={field.onChange}
                      defaultValue={field.value}
                    >
                      <FormControl>
                        <SelectTrigger className="bg-muted/30">
                          <SelectValue />
                        </SelectTrigger>
                      </FormControl>
                      <SelectContent>
                        {LANGUAGES.map((lang) => {
                          const Icon = lang.icon;
                          return (
                            <SelectItem
                              key={lang.value}
                              value={lang.value}
                              className="flex items-center gap-2"
                            >
                              <Icon className={cn("w-4 h-4", lang.iconColor)} />
                              {lang.label}
                            </SelectItem>
                          );
                        })}
                      </SelectContent>
                    </Select>
                  </FormItem>
                )}
              />
            </div>

            <div className="relative py-4">
              <div className="absolute inset-0 flex items-center">
                <span className="w-full border-t" />
              </div>
              <div className="relative flex justify-center text-xs uppercase">
                <span className="bg-card px-2 text-muted-foreground flex items-center gap-2">
                  <BrainCircuit className="w-4 h-4" /> Algorytmika
                </span>
              </div>
            </div>

            <div className="bg-secondary/20 p-6 rounded-xl space-y-6 border border-secondary/40">
              <div className="grid sm:grid-cols-2 gap-6">
                <FormField
                  control={form.control}
                  name="learnAlgorithm"
                  render={({ field }) => (
                    <FormItem>
                      <FormLabel className="flex items-center gap-2">
                        <Settings2 className="w-4 h-4" /> Metoda nauki
                      </FormLabel>
                      <Select
                        onValueChange={field.onChange}
                        defaultValue={field.value}
                      >
                        <FormControl>
                          <SelectTrigger className="bg-background">
                            <SelectValue placeholder="Wybierz metodę" />
                          </SelectTrigger>
                        </FormControl>
                        <SelectContent>
                          {LEARN_ALGORITHMS.map((alg) => {
                            const Icon = alg.icon;

                            return (
                              <SelectItem
                                key={alg.value}
                                value={alg.value}
                                className="flex items-center gap-2"
                              >
                                <Icon
                                  className={cn("w-4 h-4", alg.iconColor)}
                                />
                                {alg.label}
                              </SelectItem>
                            );
                          })}
                        </SelectContent>
                      </Select>
                      <FormDescription>
                        Określa jak często powtarzane są trudne słowa.
                      </FormDescription>
                    </FormItem>
                  )}
                />

                <FormField
                  control={form.control}
                  name="owner"
                  render={({ field }) => (
                    <FormItem>
                      <FormLabel className="flex items-center gap-2">
                        <Layers className="w-4 h-4" /> Przeznaczenie
                      </FormLabel>
                      <Select
                        onValueChange={field.onChange}
                        defaultValue={field.value}
                      >
                        <FormControl>
                          <SelectTrigger className="bg-background">
                            <SelectValue />
                          </SelectTrigger>
                        </FormControl>
                        <SelectContent>
                          {DECK_PURPOSES.map((purpose) => {
                            const Icon = purpose.icon;
                            const iconColor = purpose.iconColor;
                            return (
                              <SelectItem
                                key={purpose.value}
                                value={purpose.value}
                                className="flex items-center gap-2"
                              >
                                <Icon className={`w-4 h-4 ${iconColor}`} />
                                {purpose.label}
                              </SelectItem>
                            );
                          })}
                        </SelectContent>
                      </Select>
                      <FormDescription>
                        Wybierz Community aby udostępnić talię innym.
                      </FormDescription>
                    </FormItem>
                  )}
                />
              </div>

              <FormField
                control={form.control}
                name="howManyFlashcardsForOneSession"
                render={({ field }) => (
                  <FormItem>
                    <div className="flex justify-between items-center">
                      <FormLabel>Ilość fiszek na sesję</FormLabel>
                      <span className="text-sm font-bold text-primary border px-2 py-0.5 rounded-md bg-background">
                        {field.value}
                      </span>
                    </div>

                    <FormControl>
                      <Slider
                        min={5}
                        max={50}
                        step={5}
                        defaultValue={[field.value]}
                        onValueChange={(vals) => field.onChange(vals[0])}
                        className="py-4"
                      />
                    </FormControl>
                    <FormDescription className="text-xs">
                      Sugerujemy 10-20 fiszek dla optymalnego skupienia.
                    </FormDescription>
                  </FormItem>
                )}
              />

              <FormField
                control={form.control}
                name="visibility"
                render={({ field }) => (
                  <FormItem>
                    <FormLabel className="flex items-center gap-2">
                      <Calendar className="w-4 h-4" /> Widoczność kursu
                    </FormLabel>

                    <Select
                      onValueChange={field.onChange}
                      defaultValue={field.value}
                    >
                      <FormControl>
                        <SelectTrigger className="bg-background">
                          <SelectValue placeholder="Wybierz widoczność" />
                        </SelectTrigger>
                      </FormControl>

                      <SelectContent>
                        {VISIBILITIES.map((vis) => {
                          const Icon = vis.icon;
                          return (
                            <SelectItem
                              key={vis.value}
                              value={vis.value}
                              className="flex items-center gap-2"
                            >
                              <Icon className={cn("w-4 h-4", vis.iconColor)} />
                              {vis.label}
                            </SelectItem>
                          );
                        })}
                      </SelectContent>
                    </Select>

                    <FormDescription>
                      Zdecyduj, czy kurs ma być widoczny publicznie, czy tylko
                      dla Ciebie.
                    </FormDescription>
                    <FormMessage />
                  </FormItem>
                )}
              />
            </div>
            <div className="bg-secondary/20 p-6 rounded-xl space-y-6 border border-secondary/40">
              <div className="grid sm:grid-cols-2 gap-6">
                <FormField
                  control={form.control}
                  name="reviewSchedule"
                  render={({ field }) => (
                    <FormItem>
                      <FormLabel className="flex items-center gap-2">
                        <Calendar className="w-4 h-4" /> Harmonogram powtórek
                      </FormLabel>
                      <Select
                        onValueChange={field.onChange}
                        defaultValue={field.value}
                      >
                        <FormControl>
                          <SelectTrigger className="bg-background">
                            <SelectValue placeholder="Wybierz metodę" />
                          </SelectTrigger>
                        </FormControl>
                        <SelectContent>
                          {REVIEW_SCHEDULE.map((rev) => {
                            return (
                              <SelectItem
                                key={rev.value}
                                value={rev.value}
                                className="flex items-center gap-2"
                              >
                                {rev.label}
                              </SelectItem>
                            );
                          })}
                        </SelectContent>
                      </Select>
                      <FormDescription>
                        Wybierz jak często chcesz powtarzać materiał.
                      </FormDescription>
                    </FormItem>
                  )}
                />
              </div>
            </div>
          </CardContent>

          <CardFooter className="flex justify-end gap-4 pb-8 bg-muted/20 pt-6 border-t">
            <Button
              type="button"
              variant="ghost"
              onClick={() => router.back()}
              disabled={isPending}
            >
              Anuluj
            </Button>
            <Button
              type="submit"
              size="lg"
              disabled={isPending}
              className="min-w-[150px]"
            >
              {isPending ? "Tworzenie..." : "Stwórz Kurs"}
            </Button>
          </CardFooter>
        </form>
      </Form>
    </Card>
  );
};
