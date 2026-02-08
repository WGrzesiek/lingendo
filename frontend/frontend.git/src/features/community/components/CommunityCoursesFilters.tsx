"use client";

import { Card, CardContent, CardHeader, CardTitle } from "@/components/ui/card";
import { Input } from "@/components/ui/input";
import { Button } from "@/components/ui/button";
import {
  Select,
  SelectContent,
  SelectItem,
  SelectTrigger,
  SelectValue,
} from "@/components/ui/select";
import { Search, SlidersHorizontal, X } from "lucide-react";
import { ICommunityCoursesFilters } from "@/features/community/types/community-course.types";
import { deckCategoryConfig } from "@/features/deck/types/deck.types";

interface CommunityCoursesFiltersProps {
  filters: ICommunityCoursesFilters;
  onFiltersChange: (filters: ICommunityCoursesFilters) => void;
  resultsCount: number;
}

/**
 * Komponent filtrów dla kursów społeczności
 */
export const CommunityCoursesFilters = ({
  filters,
  onFiltersChange,
  resultsCount,
}: CommunityCoursesFiltersProps) => {
  const handleSearchChange = (value: string) => {
    onFiltersChange({ ...filters, search: value });
  };

  const handleCategoryChange = (value: string) => {
    onFiltersChange({
      ...filters,
      category: value === "all" ? undefined : value,
    });
  };

  const handleDifficultyChange = (value: string) => {
    onFiltersChange({
      ...filters,
      difficulty:
        value === "all" ? undefined : (value as "EASY" | "MEDIUM" | "HARD"),
    });
  };

  const handleSortChange = (value: string) => {
    onFiltersChange({
      ...filters,
      sortBy: value as "newest" | "oldest",
    });
  };

  const handleClearFilters = () => {
    onFiltersChange({
      search: "",
      category: undefined,
      difficulty: undefined,
      sortBy: "newest",
    });
  };

  const hasActiveFilters =
    filters.search || filters.category || filters.difficulty;

  return (
    <Card>
      <CardHeader>
        <div className="flex items-center justify-between">
          <CardTitle className="flex items-center gap-2">
            <SlidersHorizontal className="w-5 h-5" />
            Filtry
          </CardTitle>
          {hasActiveFilters && (
            <Button
              variant="ghost"
              size="sm"
              onClick={handleClearFilters}
              className="text-muted-foreground"
            >
              <X className="w-4 h-4 mr-2" />
              Wyczyść
            </Button>
          )}
        </div>
        <p className="text-sm text-muted-foreground mt-1">
          Znaleziono: <span className="font-semibold">{resultsCount}</span>{" "}
          {resultsCount === 1 ? "kurs" : resultsCount < 5 ? "kursy" : "kursów"}
        </p>
      </CardHeader>
      <CardContent className="space-y-4">
        {/* Wyszukiwanie */}
        <div className="relative">
          <Search className="absolute left-3 top-1/2 -translate-y-1/2 w-4 h-4 text-muted-foreground" />
          <Input
            type="text"
            placeholder="Szukaj kursu..."
            value={filters.search || ""}
            onChange={(e) => handleSearchChange(e.target.value)}
            className="pl-9"
          />
        </div>

        {/* Kategoria */}
        <div className="space-y-2">
          <label className="text-sm font-medium">Kategoria</label>
          <Select
            value={filters.category || "all"}
            onValueChange={handleCategoryChange}
          >
            <SelectTrigger>
              <SelectValue placeholder="Wszystkie kategorie" />
            </SelectTrigger>
            <SelectContent>
              <SelectItem value="all">Wszystkie kategorie</SelectItem>
              {Object.entries(deckCategoryConfig).map(([key, config]) => (
                <SelectItem key={key} value={key}>
                  {config.label}
                </SelectItem>
              ))}
            </SelectContent>
          </Select>
        </div>

        {/* Poziom trudności */}
        <div className="space-y-2">
          <label className="text-sm font-medium">Poziom trudności</label>
          <Select
            value={filters.difficulty || "all"}
            onValueChange={handleDifficultyChange}
          >
            <SelectTrigger>
              <SelectValue placeholder="Wszystkie poziomy" />
            </SelectTrigger>
            <SelectContent>
              <SelectItem value="all">Wszystkie poziomy</SelectItem>
              <SelectItem value="EASY">Łatwy</SelectItem>
              <SelectItem value="MEDIUM">Średni</SelectItem>
              <SelectItem value="HARD">Trudny</SelectItem>
            </SelectContent>
          </Select>
        </div>

        {/* Sortowanie */}
        <div className="space-y-2">
          <label className="text-sm font-medium">Sortuj według</label>
          <Select
            value={filters.sortBy || "newest"}
            onValueChange={handleSortChange}
          >
            <SelectTrigger>
              <SelectValue placeholder="Sortowanie" />
            </SelectTrigger>
            <SelectContent>
              <SelectItem value="newest">Najnowsze</SelectItem>
              <SelectItem value="oldest">Najstarsze</SelectItem>
            </SelectContent>
          </Select>
        </div>
      </CardContent>
    </Card>
  );
};
