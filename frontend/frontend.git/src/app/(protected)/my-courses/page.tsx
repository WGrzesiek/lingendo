"use client";

import { Card, CardHeader, CardTitle, CardContent } from "@/components/ui/card";
import { Button } from "@/components/ui/button";
import { Tabs, TabsList, TabsTrigger, TabsContent } from "@/components/ui/tabs";
import { PlusCircle, BookOpen, Lock, Globe, Plus, Users } from "lucide-react";
import { CreatedDecksList } from "@/features/deck/components/deck/my-deck/CreatedDecksList";
import { useRouter } from "next/navigation";
import { useState, useMemo } from "react";
import {useInfiniteDecksCreatedByMe} from "@/features/deck/hooks/useInfiniteDecksCreatedByMe";

/**
 * Strona "Moje kursy" - kursy utworzone przez użytkownika
 * Wyświetla listę kursów z możliwością filtrowania na publiczne/prywatne/udostępnione
 */
const MyCreatedCoursesPage = () => {
  const router = useRouter();
  const {data: deck, isLoading, isError, fetchNextPage, hasNextPage, isFetchingNextPage } = useInfiniteDecksCreatedByMe();
  const decks = deck ? deck.pages.flatMap(page => page.content) : [];
  console.log(decks);
  const [activeTab, setActiveTab] = useState<
    "all" | "public" | "private" | "shared"
  >("all");


  const filteredDecks = useMemo(() => {
    if (activeTab === "all") return decks;
    if (activeTab === "public")
      return decks.filter((deck) => deck.visibility === "PUBLIC");
    if (activeTab === "private")
      return decks.filter((deck) => deck.visibility === "PRIVATE");
    if (activeTab === "shared")
      return decks.filter((deck) => deck.visibility === "FRIENDS_ONLY" || deck.visibility === "STUDENTS_ONLY");
    return decks;
  }, [activeTab, decks]);

  const publicCount = decks.filter((d) => d.visibility === "PUBLIC").length;
  const privateCount = decks.filter((d) => d.visibility === "PRIVATE").length;
  const sharedCount = decks.filter(
    (d) => d.visibility === "FRIENDS_ONLY" || d.visibility === "STUDENTS_ONLY"
  ).length;

  const handleCreateCourse = () => {
    router.push("/decks/create");
  };

  return (
    <div className="min-h-screen bg-background">
      <div className="container mx-auto p-6 lg:p-8 space-y-8">
        {/* Header */}
        <div className="flex items-center justify-between">
          <div className="space-y-1">
            <h1 className="text-4xl font-bold">Moje kursy</h1>
            <p className="text-muted-foreground text-lg">
              Zarządzaj kursami, które utworzyłeś
            </p>
          </div>
        </div>

        {/* Statystyki szybkiego przeglądu */}
        <div className="grid grid-cols-1 sm:grid-cols-3 gap-4">
          <Card className="border-l-4 border-l-primary">
            <CardContent className="pt-6">
              <div className="flex items-center gap-3">
                <div className="p-3 bg-primary/10 rounded-lg">
                  <BookOpen className="w-6 h-6 text-primary" />
                </div>
                <div>
                  <p className="text-2xl font-bold">
                    {decks.length}
                  </p>
                  <p className="text-sm text-muted-foreground">
                    Wszystkie kursy
                  </p>
                </div>
              </div>
            </CardContent>
          </Card>

          <Card className="border-l-4 border-l-green-500">
            <CardContent className="pt-6">
              <div className="flex items-center gap-3">
                <div className="p-3 bg-green-500/10 rounded-lg">
                  <Globe className="w-6 h-6 text-green-600" />
                </div>
                <div>
                  <p className="text-2xl font-bold">{publicCount}</p>
                  <p className="text-sm text-muted-foreground">
                    Kursy publiczne
                  </p>
                </div>
              </div>
            </CardContent>
          </Card>

          <Card className="border-l-4 border-l-orange-500">
            <CardContent className="pt-6">
              <div className="flex items-center gap-3">
                <div className="p-3 bg-orange-500/10 rounded-lg">
                  <Lock className="w-6 h-6 text-orange-600" />
                </div>
                <div>
                  <p className="text-2xl font-bold">{privateCount}</p>
                  <p className="text-sm text-muted-foreground">
                    Kursy prywatne
                  </p>
                </div>
              </div>
            </CardContent>
          </Card>
        </div>

        {/* Lista kursów z filtrami */}
        <Card className="shadow-sm">
          <CardHeader className="flex flex-row items-center justify-between space-y-0 pb-4">
            <div className="space-y-1">
              <CardTitle className="text-xl">Twoje kursy</CardTitle>
              <p className="text-sm text-muted-foreground">
                Edytuj, dodawaj słówka i monitoruj statystyki
              </p>
            </div>
            <div className="flex gap-2">
              <Button
                size="sm"
                variant="outline"
                className="hidden sm:flex"
                onClick={() => router.push("/decks/words/add")}
              >
                <Plus className="w-4 h-4 mr-2" />
                Dodaj słówka do społecznej bazy
              </Button>
              <Button
                size="sm"
                className="hidden sm:flex"
                onClick={handleCreateCourse}
              >
                <PlusCircle className="w-4 h-4 mr-2" />
                Utwórz nowy kurs
              </Button>
              <Button
                size="icon"
                className="sm:hidden h-8 w-8"
                onClick={handleCreateCourse}
              >
                <PlusCircle className="h-4 w-4" />
              </Button>
            </div>
          </CardHeader>

          <CardContent>
            <Tabs
              defaultValue="all"
              value={activeTab}
              onValueChange={(value) =>
                setActiveTab(value as "all" | "public" | "private" | "shared")
              }
              className="w-full"
            >
              <TabsList className="grid w-full grid-cols-4 mb-6">
                <TabsTrigger value="all">
                  Wszystkie ({decks.length})
                </TabsTrigger>
                <TabsTrigger value="public">
                  <Globe className="w-4 h-4 mr-2" />
                  Publiczne ({publicCount})
                </TabsTrigger>
                <TabsTrigger value="private">
                  <Lock className="w-4 h-4 mr-2" />
                  Prywatne ({privateCount})
                </TabsTrigger>
                <TabsTrigger value="shared">
                  <Users className="w-4 h-4 mr-2" />
                  Udostępnione ({sharedCount})
                </TabsTrigger>
              </TabsList>

              <TabsContent value="all" className="mt-0">
                <CreatedDecksList decks={filteredDecks} />
              </TabsContent>

              <TabsContent value="public" className="mt-0">
                <CreatedDecksList decks={filteredDecks} />
              </TabsContent>

              <TabsContent value="private" className="mt-0">
                <CreatedDecksList decks={filteredDecks} />
              </TabsContent>

              <TabsContent value="shared" className="mt-0">
                <CreatedDecksList decks={filteredDecks} />
              </TabsContent>
            </Tabs>
          </CardContent>
        </Card>
      </div>
    </div>
  );
};

export default MyCreatedCoursesPage;
