"use client";

import { useState, useMemo } from "react";
import { Card, CardContent, CardHeader, CardTitle } from "@/components/ui/card";
import { TrendingUp } from "lucide-react";
import { LeaderboardSearch } from "@/features/leaderboard/components/LeaderboardSearch";
import { CurrentUserPosition } from "@/features/leaderboard/components/CurrentUserPosition";
import { LeaderboardList } from "@/features/leaderboard/components/LeaderboardList";
import { ILeaderboardEntry } from "@/features/leaderboard/types/leaderboard.types";

// Mock data - pełny ranking użytkowników
const mockCurrentUserId = "user-15";

const mockLeaderboardEntries: ILeaderboardEntry[] = [
  {
    userId: "user-1",
    rank: 1,
    displayName: "Anna Kowalska",
    points: 15420,
    completedCourses: 42,
    isActive: true,
  },
  {
    userId: "user-2",
    rank: 2,
    displayName: "Piotr Wiśniewski",
    points: 14890,
    completedCourses: 38,
    isActive: true,
  },
  {
    userId: "user-3",
    rank: 3,
    displayName: "Maria Nowak",
    points: 13275,
    completedCourses: 35,
    isActive: true,
  },
  {
    userId: "user-4",
    rank: 4,
    displayName: "Jan Kamiński",
    points: 12640,
    completedCourses: 33,
    isActive: true,
  },
  {
    userId: "user-5",
    rank: 5,
    displayName: "Ewa Lewandowska",
    points: 11890,
    completedCourses: 31,
    isActive: true,
  },
  {
    userId: "user-6",
    rank: 6,
    displayName: "Tomasz Zieliński",
    points: 11245,
    completedCourses: 29,
    isActive: true,
  },
  {
    userId: "user-7",
    rank: 7,
    displayName: "Katarzyna Wójcik",
    points: 10750,
    completedCourses: 28,
    isActive: true,
  },
  {
    userId: "user-8",
    rank: 8,
    displayName: "Michał Kowalczyk",
    points: 10340,
    completedCourses: 26,
    isActive: true,
  },
  {
    userId: "user-9",
    rank: 9,
    displayName: "Agnieszka Kamińska",
    points: 9875,
    completedCourses: 25,
    isActive: true,
  },
  {
    userId: "user-10",
    rank: 10,
    displayName: "Paweł Lewandowski",
    points: 9420,
    completedCourses: 24,
    isActive: true,
  },
  {
    userId: "user-11",
    rank: 11,
    displayName: "Monika Zielińska",
    points: 9120,
    completedCourses: 23,
    isActive: true,
  },
  {
    userId: "user-12",
    rank: 12,
    displayName: "Krzysztof Szymański",
    points: 8840,
    completedCourses: 22,
    isActive: true,
  },
  {
    userId: "user-13",
    rank: 13,
    displayName: "Joanna Woźniak",
    points: 8560,
    completedCourses: 21,
    isActive: true,
  },
  {
    userId: "user-14",
    rank: 14,
    displayName: "Adam Dąbrowski",
    points: 8280,
    completedCourses: 20,
    isActive: true,
  },
  {
    userId: "user-15",
    rank: 15,
    displayName: "Ty (Jan Nowak)",
    points: 8000,
    completedCourses: 19,
    isActive: true,
  },
  {
    userId: "user-16",
    rank: 16,
    displayName: "Beata Kozłowska",
    points: 7720,
    completedCourses: 18,
    isActive: true,
  },
  {
    userId: "user-17",
    rank: 17,
    displayName: "Marcin Jankowski",
    points: 7440,
    completedCourses: 17,
    isActive: true,
  },
  {
    userId: "user-18",
    rank: 18,
    displayName: "Aleksandra Mazur",
    points: 7160,
    completedCourses: 16,
    isActive: true,
  },
  {
    userId: "user-19",
    rank: 19,
    displayName: "Grzegorz Krawczyk",
    points: 6880,
    completedCourses: 15,
    isActive: true,
  },
  {
    userId: "user-20",
    rank: 20,
    displayName: "Natalia Piotrowska",
    points: 6600,
    completedCourses: 14,
    isActive: true,
  },
  {
    userId: "user-21",
    rank: 21,
    displayName: "Wojciech Grabowski",
    points: 6320,
    completedCourses: 13,
    isActive: true,
  },
  {
    userId: "user-22",
    rank: 22,
    displayName: "Magdalena Pawłowska",
    points: 6040,
    completedCourses: 12,
    isActive: true,
  },
  {
    userId: "user-23",
    rank: 23,
    displayName: "Damian Michalski",
    points: 5760,
    completedCourses: 11,
    isActive: true,
  },
  {
    userId: "user-24",
    rank: 24,
    displayName: "Karolina Król",
    points: 5480,
    completedCourses: 10,
    isActive: true,
  },
  {
    userId: "user-25",
    rank: 25,
    displayName: "Robert Wieczorek",
    points: 5200,
    completedCourses: 9,
    isActive: true,
  },
  {
    userId: "user-26",
    rank: 26,
    displayName: "Sylwia Adamczyk",
    points: 4920,
    completedCourses: 8,
    isActive: false,
  },
  {
    userId: "user-27",
    rank: 27,
    displayName: "Łukasz Dudek",
    points: 4640,
    completedCourses: 7,
    isActive: true,
  },
  {
    userId: "user-28",
    rank: 28,
    displayName: "Weronika Nowakowska",
    points: 4360,
    completedCourses: 6,
    isActive: true,
  },
  {
    userId: "user-29",
    rank: 29,
    displayName: "Bartosz Witkowski",
    points: 4080,
    completedCourses: 5,
    isActive: false,
  },
  {
    userId: "user-30",
    rank: 30,
    displayName: "Dominika Walczak",
    points: 3800,
    completedCourses: 4,
    isActive: true,
  },
];

/**
 * Strona pełnego rankingu użytkowników
 * Wzorowana na mini rankingu z dashboard
 */
const LeaderboardPage = () => {
  const [searchQuery, setSearchQuery] = useState("");

  // Znajdź aktualnego użytkownika
  const currentUser = mockLeaderboardEntries.find(
    (entry) => entry.userId === mockCurrentUserId
  )!;

  // Znajdź użytkownika powyżej
  const userAbove = mockLeaderboardEntries.find(
    (entry) => entry.rank === currentUser.rank - 1
  );

  // Filtrowanie po nazwie
  const filteredEntries = useMemo(() => {
    if (!searchQuery.trim()) {
      return mockLeaderboardEntries;
    }

    const searchLower = searchQuery.toLowerCase();
    return mockLeaderboardEntries.filter((entry) =>
      entry.displayName.toLowerCase().includes(searchLower)
    );
  }, [searchQuery]);

  return (
    <div className="min-h-screen bg-background">
      <div className="container mx-auto p-6 lg:p-8 space-y-8">
        {/* Header */}
        <div className="space-y-1">
          <h1 className="text-4xl font-bold flex items-center gap-3">
            <TrendingUp className="w-10 h-10" />
            Ranking użytkowników
          </h1>
          <p className="text-muted-foreground text-lg">
            Zobacz jak wypadasz na tle innych użytkowników platformy
          </p>
        </div>

        {/* Layout - pozycja użytkownika + ranking */}
        <div className="grid grid-cols-1 lg:grid-cols-3 gap-6">
          {/* Lewa kolumna - pozycja użytkownika i wyszukiwanie */}
          <div className="lg:col-span-1 space-y-6">
            <CurrentUserPosition
              currentUser={currentUser}
              userAbove={userAbove}
            />
            <LeaderboardSearch
              searchQuery={searchQuery}
              onSearchChange={setSearchQuery}
              resultsCount={filteredEntries.length}
              totalUsers={mockLeaderboardEntries.length}
            />
          </div>

          {/* Prawa kolumna - pełna lista rankingu */}
          <div className="lg:col-span-2">
            <Card>
              <CardHeader>
                <CardTitle className="flex items-center gap-2">
                  <TrendingUp className="w-5 h-5" />
                  Pełny ranking
                </CardTitle>
                <p className="text-sm text-muted-foreground mt-1">
                  Ranking jest aktualizowany co godzinę
                </p>
              </CardHeader>
              <CardContent>
                <LeaderboardList
                  entries={filteredEntries}
                  currentUserId={mockCurrentUserId}
                />
              </CardContent>
            </Card>
          </div>
        </div>
      </div>
    </div>
  );
};

export default LeaderboardPage;
