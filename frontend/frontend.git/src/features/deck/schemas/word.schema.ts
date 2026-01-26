import { z } from "zod";

/**
 * Schema Zod dla zdania przykładowego
 */
export const wordSentenceSchema = z.object({
  sentence: z
    .string()
    .min(1, "Zdanie nie może być puste")
    .max(500, "Zdanie jest za długie (max 500 znaków)"),
  translation: z
    .string()
    .min(1, "Tłumaczenie zdania nie może być puste")
    .max(500, "Tłumaczenie jest za długie (max 500 znaków)"),
});

/**
 * Schema Zod dla pojedynczego słówka
 */
export const wordToAddSchema = z.object({
  word: z
    .string()
    .min(1, "Słówko nie może być puste")
    .max(100, "Słówko jest za długie (max 100 znaków)")
    .regex(
      /^[^\s].*[^\s]$|^[^\s]$/,
      "Słówko nie może zaczynać się ani kończyć spacją"
    ),
  translations: z
    .array(
      z
        .string()
        .min(1, "Tłumaczenie nie może być puste")
        .max(100, "Tłumaczenie jest za długie (max 100 znaków)")
    )
    .min(1, "Musisz podać przynajmniej jedno tłumaczenie")
    .max(10, "Maksymalnie 10 tłumaczeń"),
  sentences: z
    .array(wordSentenceSchema)
    .max(10, "Maksymalnie 10 zdań przykładowych")
    .optional()
    .default([]),
});

/**
 * Schema Zod dla batch dodawania słówek
 */
export const batchAddWordsSchema = z.object({
  words: z
    .array(wordToAddSchema)
    .min(1, "Musisz dodać przynajmniej jedno słówko")
    .max(100, "Maksymalnie 100 słówek na raz"),
});

/**
 * Schema Zod dla JSON import
 */
export const jsonImportSchema = z.string().min(1, "JSON nie może być pusty");

export type WordSentenceFormData = z.infer<typeof wordSentenceSchema>;
export type WordToAddFormData = z.infer<typeof wordToAddSchema>;
export type BatchAddWordsFormData = z.infer<typeof batchAddWordsSchema>;
