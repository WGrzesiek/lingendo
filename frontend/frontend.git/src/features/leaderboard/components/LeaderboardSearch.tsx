"use client";

import { Card, CardContent, CardHeader, CardTitle } from "@/components/ui/card";
import { Input } from "@/components/ui/input";
import { Search } from "lucide-react";

interface LeaderboardSearchProps {
  searchQuery: string;
  onSearchChange: (value: string) => void;
  resultsCount: number;
  totalUsers: number;
}

/**
 * Komponent wyszukiwania użytkownika w rankingu
 */
export const LeaderboardSearch = ({
  searchQuery,
  onSearchChange,
  resultsCount,
  totalUsers,
}: LeaderboardSearchProps) => {
  return (
    <Card>
      <CardHeader>
        <CardTitle className="flex items-center gap-2 text-lg">
          <Search className="w-5 h-5" />
          Wyszukaj użytkownika
        </CardTitle>
      </CardHeader>
      <CardContent className="space-y-3">
        <div className="relative">
          <Search className="absolute left-3 top-1/2 -translate-y-1/2 w-4 h-4 text-muted-foreground" />
          <Input
            type="text"
            placeholder="Wpisz nazwę użytkownika..."
            value={searchQuery}
            onChange={(e) => onSearchChange(e.target.value)}
            className="pl-9"
          />
        </div>

        <p className="text-sm text-muted-foreground">
          {searchQuery ? (
            <>
              Znaleziono: <span className="font-semibold">{resultsCount}</span>{" "}
              z {totalUsers} użytkowników
            </>
          ) : (
            <>
              Wyświetlono: <span className="font-semibold">{totalUsers}</span>{" "}
              użytkowników
            </>
          )}
        </p>
      </CardContent>
    </Card>
  );
};
