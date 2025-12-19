"use client";

import { useState, useMemo } from "react";
import { Card, CardContent, CardHeader, CardTitle } from "@/components/ui/card";
import { Globe, Sparkles } from "lucide-react";
import { CommunityCoursesFilters } from "@/features/community/components/CommunityCoursesFilters";
import { CommunityCourseCard } from "@/features/community/components/CommunityCourseCard";
import {
  ICommunityCourse,
  ICommunityCoursesFilters,
} from "@/features/community/types/community-course.types";

// Mock data - kursy społeczności
const mockCommunityCourses: ICommunityCourse[] = [
  {
    id: "comm-1",
    title: "Angielski biznesowy w praktyce",
    description:
      "Poznaj język biznesu i bądź pewny siebie w rozmowach z klientami i partnerami. Kurs obejmuje negocjacje, prezentacje i korespondencję biznesową.",
    author: "Maria Nowak",
    studentsCount: 1250,
    rating: 4.8,
    ratingsCount: 342,
    lessonsCount: 35,
    difficulty: "MEDIUM",
    category: "BUSINESS",
    createdAt: "2024-03-15T10:00:00Z",
    updatedAt: "2024-12-01T14:30:00Z",
    totalWords: 420,
  },
  {
    id: "comm-2",
    title: "Hiszpański dla podróżników",
    description:
      "Wszystko czego potrzebujesz podczas podróży po krajach hiszpańskojęzycznych. Od zamawiania jedzenia po rezerwację hotelu.",
    author: "Carlos Rodriguez",
    studentsCount: 890,
    rating: 4.9,
    ratingsCount: 215,
    lessonsCount: 28,
    difficulty: "EASY",
    category: "TOURISM",
    createdAt: "2024-05-20T08:00:00Z",
    updatedAt: "2024-11-28T09:15:00Z",
    totalWords: 320,
  },
  {
    id: "comm-3",
    title: "Programowanie - terminologia angielska",
    description:
      "Słownictwo IT niezbędne w pracy programisty. Frameworki, algorytmy, wzorce projektowe i dokumentacja techniczna.",
    author: "Jan Kowalski",
    studentsCount: 2100,
    rating: 4.7,
    ratingsCount: 567,
    lessonsCount: 42,
    difficulty: "HARD",
    category: "TECHNOLOGY",
    createdAt: "2024-01-10T12:00:00Z",
    updatedAt: "2024-12-10T16:00:00Z",
    totalWords: 580,
  },
  {
    id: "comm-4",
    title: "Niemiecki od podstaw",
    description:
      "Kompletny kurs dla początkujących. Gramatyka, wymowa, podstawowe zwroty i konwersacje.",
    author: "Anna Schmidt",
    studentsCount: 650,
    rating: 4.6,
    ratingsCount: 189,
    lessonsCount: 50,
    difficulty: "EASY",
    category: "OTHER",
    createdAt: "2024-06-05T09:30:00Z",
    updatedAt: "2024-12-05T11:20:00Z",
    totalWords: 650,
  },
  {
    id: "comm-5",
    title: "Medycyna - słownictwo specjalistyczne",
    description:
      "Terminologia medyczna po angielsku. Anatomia, choroby, procedury medyczne i komunikacja z pacjentami.",
    author: "Dr. Piotr Lewandowski",
    studentsCount: 420,
    rating: 4.9,
    ratingsCount: 145,
    lessonsCount: 38,
    difficulty: "HARD",
    category: "MEDICINE",
    createdAt: "2024-04-12T14:00:00Z",
    updatedAt: "2024-11-30T10:45:00Z",
    totalWords: 495,
  },
  {
    id: "comm-6",
    title: "Francuski - kultura i sztuka",
    description:
      "Poznaj język francuski przez pryzmat kultury, literatury i sztuki. Dla miłośników francuskiej kultury.",
    author: "Sophie Dubois",
    studentsCount: 780,
    rating: 4.8,
    ratingsCount: 234,
    lessonsCount: 32,
    difficulty: "MEDIUM",
    category: "CULTURE",
    createdAt: "2024-07-18T11:00:00Z",
    updatedAt: "2024-12-08T15:30:00Z",
    totalWords: 385,
  },
  {
    id: "comm-7",
    title: "Fizyka i matematyka po angielsku",
    description:
      "Naukowe słownictwo z zakresu fizyki i matematyki. Równania, teorie i doświadczenia.",
    author: "Prof. Andrzej Wiśniewski",
    studentsCount: 315,
    rating: 4.5,
    ratingsCount: 87,
    lessonsCount: 45,
    difficulty: "HARD",
    category: "SCIENCE",
    createdAt: "2024-08-22T13:00:00Z",
    updatedAt: "2024-12-03T09:00:00Z",
    totalWords: 520,
  },
  {
    id: "comm-8",
    title: "Włoski dla początkujących",
    description:
      "Podstawy języka włoskiego z naciskiem na praktyczne użycie w codziennych sytuacjach.",
    author: "Marco Rossi",
    studentsCount: 945,
    rating: 4.7,
    ratingsCount: 298,
    lessonsCount: 30,
    difficulty: "EASY",
    category: "OTHER",
    createdAt: "2024-09-15T10:30:00Z",
    updatedAt: "2024-12-11T14:00:00Z",
    totalWords: 360,
  },
  {
    id: "comm-9",
    title: "Biznes międzynarodowy - negocjacje",
    description:
      "Zaawansowane techniki negocjacyjne w biznesie międzynarodowym. Case studies i praktyczne ćwiczenia.",
    author: "Robert Johnson",
    studentsCount: 567,
    rating: 4.9,
    ratingsCount: 178,
    lessonsCount: 25,
    difficulty: "HARD",
    category: "BUSINESS",
    createdAt: "2024-10-01T08:00:00Z",
    updatedAt: "2024-12-12T12:00:00Z",
    totalWords: 310,
  },
  {
    id: "comm-10",
    title: "Technologia - AI i Machine Learning",
    description:
      "Terminologia związana ze sztuczną inteligencją i uczeniem maszynowym. Dla inżynierów i entuzjastów AI.",
    author: "Dr. Katarzyna Nowak",
    studentsCount: 1830,
    rating: 4.8,
    ratingsCount: 456,
    lessonsCount: 40,
    difficulty: "HARD",
    category: "TECHNOLOGY",
    createdAt: "2024-02-28T15:00:00Z",
    updatedAt: "2024-12-09T17:00:00Z",
    totalWords: 475,
  },
];

/**
 * Strona przeglądania kursów społeczności
 * Kursy wyglądają tak samo jak na /dashboard z dodatkowymi filtrami
 */
const CommunityCoursesPage = () => {
  const [filters, setFilters] = useState<ICommunityCoursesFilters>({
    search: "",
    category: undefined,
    difficulty: undefined,
    sortBy: "popular",
  });

  // Filtrowanie i sortowanie kursów
  const filteredAndSortedCourses = useMemo(() => {
    let result = [...mockCommunityCourses];

    // Filtr wyszukiwania
    if (filters.search) {
      const searchLower = filters.search.toLowerCase();
      result = result.filter(
        (course) =>
          course.title.toLowerCase().includes(searchLower) ||
          course.description.toLowerCase().includes(searchLower) ||
          course.author.toLowerCase().includes(searchLower)
      );
    }

    // Filtr kategorii
    if (filters.category) {
      result = result.filter((course) => course.category === filters.category);
    }

    // Filtr trudności
    if (filters.difficulty) {
      result = result.filter(
        (course) => course.difficulty === filters.difficulty
      );
    }

    // Sortowanie
    switch (filters.sortBy) {
      case "popular":
        result.sort((a, b) => b.studentsCount - a.studentsCount);
        break;
      case "rating":
        result.sort((a, b) => b.rating - a.rating);
        break;
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
  }, [filters]);

  const handleEnroll = (courseId: string) => {
    // Symulacja zapisu na kurs
    console.log("Zapisuję na kurs:", courseId);
    // TODO: API call
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
                {filteredAndSortedCourses.length === 0 ? (
                  <div className="text-center py-16">
                    <div className="inline-flex items-center justify-center w-16 h-16 rounded-full bg-muted mb-4">
                      <Globe className="w-8 h-8 text-muted-foreground" />
                    </div>
                    <h3 className="text-xl font-semibold mb-2">Brak kursów</h3>
                    <p className="text-muted-foreground max-w-md mx-auto">
                      Nie znaleziono kursów spełniających wybrane kryteria.
                      Spróbuj zmienić filtry.
                    </p>
                  </div>
                ) : (
                  <div className="grid grid-cols-1 md:grid-cols-2 xl:grid-cols-3 gap-4">
                    {filteredAndSortedCourses.map((course) => (
                      <CommunityCourseCard
                        key={course.id}
                        course={course}
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
