"use client";

import { useState, useMemo } from "react";
import { Card, CardContent, CardHeader, CardTitle } from "@/components/ui/card";
import { Skeleton } from "@/components/ui/skeleton";
import { Globe, Sparkles, AlertCircle } from "lucide-react";
import { CommunityCoursesFilters } from "@/features/community/components/CommunityCoursesFilters";
import { CommunityCourseCard } from "@/features/community/components/CommunityCourseCard";
import {
  ICommunityCourse,
  ICommunityCoursesFilters,
} from "@/features/community/types/community-course.types";
import { usePublicDecks } from "@/features/community/hooks/usePublicDecks";
import { useMyDeckStats } from "@/features/deck/hooks/useMyDeckStats";
import type { ICreatedDeckListItem } from "@/features/deck/types/created-deck.types";


const mapDeckToCommunityCourse = (
  deck: ICreatedDeckListItem
): ICommunityCourse => ({
  id: deck.id,
  title: deck.name,
  description: deck.deckDescription,
  wordCount: deck.wordCount,
  difficulty: deck.deckDifficulty,
  category: deck.deckCategory,
  visibility: deck.visibility,
  createdAt: deck.createdAt,
  updatedAt: deck.updatedAt,
});

/**
 * Strona przeglądania kursów społeczności
 * Kursy wyglądają tak samo jak na /dashboard z dodatkowymi filtrami
 */
const CommunityCoursesPage = () => {
  const [filters, setFilters] = useState<ICommunityCoursesFilters>({
    search: "",
    category: undefined,
    difficulty: undefined,
    sortBy: "newest",
  });

  const { data: decksData, isLoading, isError } = usePublicDecks({ size: 50 });

  const deckIds = useMemo(() => {
    if (!decksData?.content) return [];
    return decksData.content.map((deck) => deck.id);
  }, [decksData]);

  const { data: statsData } = useMyDeckStats(deckIds);

  const courses = useMemo(() => {
    if (!decksData?.content) return [];
    return decksData.content.map(mapDeckToCommunityCourse);
  }, [decksData]);

  const filteredAndSortedCourses = useMemo(() => {
    let result = [...courses];

    if (filters.search) {
      const searchLower = filters.search.toLowerCase();
      result = result.filter(
        (course) =>
          course.title.toLowerCase().includes(searchLower) ||
          course.description?.toLowerCase().includes(searchLower)
      );
    }


    if (filters.category) {
      result = result.filter((course) => course.category === filters.category);
    }


    if (filters.difficulty) {
      result = result.filter(
        (course) => course.difficulty === filters.difficulty
      );
    }


    switch (filters.sortBy) {
      case "newest":
        result.sort(
          (a, b) =>
            new Date(b.createdAt).getTime() - new Date(a.createdAt).getTime()
        );
        break;
      case "oldest":
        result.sort(
          (a, b) =>
            new Date(a.createdAt).getTime() - new Date(b.createdAt).getTime()
        );
        break;
    }

    return result;
  }, [courses, filters]);

  const handleEnroll = (courseId: string) => {
    console.log("Zapisuję na kurs:", courseId);
    // TODO: Implementacja zapisu na kurs
  };

  return (
    <div className="min-h-screen bg-background">
      <div className="container mx-auto p-6 lg:p-8 space-y-8">
        {/* Header */}
        <div className="space-y-1">
          <h1 className="text-4xl font-bold flex items-center gap-3">
            <Globe className="w-10 h-10" />
            Kursy społeczności
          </h1>
          <p className="text-muted-foreground text-lg">
            Przeglądaj i dołączaj do kursów tworzonych przez innych użytkowników
          </p>
        </div>

        {/* Layout z filtrami i kursami */}
        <div className="grid grid-cols-1 lg:grid-cols-4 gap-6">
          {/* Filtry - lewa kolumna */}
          <div className="lg:col-span-1">
            <CommunityCoursesFilters
              filters={filters}
              onFiltersChange={setFilters}
              resultsCount={filteredAndSortedCourses.length}
            />
          </div>

          {/* Lista kursów - prawa kolumna */}
          <div className="lg:col-span-3">
            <Card>
              <CardHeader>
                <CardTitle className="flex items-center gap-2">
                  <Sparkles className="w-5 h-5" />
                  Dostępne kursy
                </CardTitle>
              </CardHeader>
              <CardContent>
                {isLoading ? (
                  <div className="grid grid-cols-1 md:grid-cols-2 xl:grid-cols-3 gap-4">
                    {[...Array(6)].map((_, i) => (
                      <div key={i} className="space-y-3 p-4 border rounded-lg">
                        <Skeleton className="h-5 w-3/4" />
                        <Skeleton className="h-4 w-full" />
                        <Skeleton className="h-4 w-2/3" />
                        <div className="flex gap-2">
                          <Skeleton className="h-5 w-16" />
                          <Skeleton className="h-5 w-16" />
                        </div>
                        <Skeleton className="h-9 w-full" />
                      </div>
                    ))}
                  </div>
                ) : isError ? (
                  <div className="text-center py-16">
                    <div className="inline-flex items-center justify-center w-16 h-16 rounded-full bg-destructive/10 mb-4">
                      <AlertCircle className="w-8 h-8 text-destructive" />
                    </div>
                    <h3 className="text-xl font-semibold mb-2">
                      Błąd ładowania
                    </h3>
                    <p className="text-muted-foreground max-w-md mx-auto">
                      Nie udało się pobrać kursów. Spróbuj odświeżyć stronę.
                    </p>
                  </div>
                ) : filteredAndSortedCourses.length === 0 ? (
                  <div className="text-center py-16">
                    <div className="inline-flex items-center justify-center w-16 h-16 rounded-full bg-muted mb-4">
                      <Globe className="w-8 h-8 text-muted-foreground" />
                    </div>
                    <h3 className="text-xl font-semibold mb-2">Brak kursów</h3>
                    <p className="text-muted-foreground max-w-md mx-auto">
                      {courses.length === 0
                        ? "Nie ma jeszcze żadnych publicznych kursów."
                        : "Nie znaleziono kursów spełniających wybrane kryteria. Spróbuj zmienić filtry."}
                    </p>
                  </div>
                ) : (
                  <div className="grid grid-cols-1 md:grid-cols-2 xl:grid-cols-3 gap-4">
                    {filteredAndSortedCourses.map((course) => (
                      <CommunityCourseCard
                        key={course.id}
                        course={course}
                        stats={statsData?.[course.id]}
                        onEnroll={handleEnroll}
                      />
                    ))}
                  </div>
                )}
              </CardContent>
            </Card>
          </div>
        </div>
      </div>
    </div>
  );
};

export default CommunityCoursesPage;
