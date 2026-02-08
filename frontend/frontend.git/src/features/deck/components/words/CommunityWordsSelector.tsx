// "use client";
//
// import { useState, useMemo } from "react";
// import { Button } from "@/components/ui/button";
// import { Input } from "@/components/ui/input";
// import { Card, CardHeader, CardTitle, CardContent } from "@/components/ui/card";
// import { Alert, AlertDescription } from "@/components/ui/alert";
// import { Search, Filter, CheckCircle2, Users } from "lucide-react";
// import { CommunityWordCard } from "./CommunityWordCard";
// import { CommunityWord } from "../../types/community-word.types";
//
// interface CommunityWordsSelectorProps {
//   onSelect: (wordIds: string[]) => void;
//   isAdding?: boolean;
// }
//
// /**
//  * Zmockowane dane słówek społeczności
//  */
// const mockCommunityWords: CommunityWord[] = [
//   {
//     id: "comm-1",
//     word: "accomplish",
//     translations: ["osiągnąć", "dokonać", "zrealizować"],
//     sentences: [
//       {
//         sentence: "She accomplished all her goals this year.",
//         translation: "Osiągnęła wszystkie swoje cele w tym roku.",
//       },
//       {
//         sentence: "We need to accomplish this task by Friday.",
//         translation: "Musimy zrealizować to zadanie do piątku.",
//       },
//     ],
//     usageCount: 156,
//     author: "Anna Kowalska",
//     createdAt: "2024-11-15T10:30:00Z",
//   },
//   {
//     id: "comm-2",
//     word: "resilient",
//     translations: ["odporny", "elastyczny", "prężny"],
//     sentences: [
//       {
//         sentence: "Children are surprisingly resilient.",
//         translation: "Dzieci są zaskakująco odporne.",
//       },
//     ],
//     usageCount: 89,
//     author: "Jan Nowak",
//     createdAt: "2024-12-01T14:20:00Z",
//   },
//   {
//     id: "comm-3",
//     word: "fundamental",
//     translations: ["podstawowy", "fundamentalny"],
//     sentences: [
//       {
//         sentence: "These are fundamental principles of democracy.",
//         translation: "To są podstawowe zasady demokracji.",
//       },
//       {
//         sentence: "Reading is fundamental to learning.",
//         translation: "Czytanie jest podstawą nauki.",
//       },
//       {
//         sentence: "We need to address the fundamental issues.",
//         translation: "Musimy zająć się podstawowymi problemami.",
//       },
//     ],
//     usageCount: 234,
//     author: "Maria Wiśniewska",
//     createdAt: "2024-10-22T09:15:00Z",
//   },
//   {
//     id: "comm-4",
//     word: "profound",
//     translations: ["głęboki", "dogłębny"],
//     sentences: [
//       {
//         sentence: "His words had a profound effect on me.",
//         translation: "Jego słowa miały na mnie głęboki wpływ.",
//       },
//     ],
//     usageCount: 67,
//     author: "Piotr Zieliński",
//     createdAt: "2024-12-05T16:45:00Z",
//   },
//   {
//     id: "comm-5",
//     word: "endeavor",
//     translations: ["staranie", "wysiłek", "dążenie"],
//     sentences: [
//       {
//         sentence: "We will endeavor to complete the project on time.",
//         translation: "Będziemy dążyć do ukończenia projektu na czas.",
//       },
//       {
//         sentence: "This is a worthwhile endeavor.",
//         translation: "To jest warte wysiłku przedsięwzięcie.",
//       },
//     ],
//     usageCount: 45,
//     author: "Katarzyna Lewandowska",
//     createdAt: "2024-11-28T11:00:00Z",
//   },
//   {
//     id: "comm-6",
//     word: "persevere",
//     translations: ["wytrwać", "nie poddawać się"],
//     sentences: [
//       {
//         sentence: "You must persevere despite the difficulties.",
//         translation: "Musisz wytrwać pomimo trudności.",
//       },
//     ],
//     usageCount: 123,
//     author: "Tomasz Wójcik",
//     createdAt: "2024-12-10T08:30:00Z",
//   },
// ];
//
// /**
//  * Komponent wyboru słówek ze społeczności
//  * Zawiera filtry wyszukiwania i możliwość zaznaczania słówek
//  */
// export const CommunityWordsSelector = ({
//   onSelect,
//   isAdding = false,
// }: CommunityWordsSelectorProps) => {
//   const [selectedIds, setSelectedIds] = useState<Set<string>>(new Set());
//   const [searchWord, setSearchWord] = useState("");
//   const [searchTranslation, setSearchTranslation] = useState("");
//
//   // Filtrowanie słówek
//   const filteredWords = useMemo(() => {
//     return mockCommunityWords.filter((word) => {
//       const matchesWord =
//         !searchWord ||
//         word.word.toLowerCase().includes(searchWord.toLowerCase());
//
//       const matchesTranslation =
//         !searchTranslation ||
//         word.translations.some((t) =>
//           t.toLowerCase().includes(searchTranslation.toLowerCase())
//         );
//
//       return matchesWord && matchesTranslation;
//     });
//   }, [searchWord, searchTranslation]);
//
//   const handleToggle = (wordId: string) => {
//     const newSelected = new Set(selectedIds);
//     if (newSelected.has(wordId)) {
//       newSelected.delete(wordId);
//     } else {
//       newSelected.add(wordId);
//     }
//     setSelectedIds(newSelected);
//   };
//
//   const handleSelectAll = () => {
//     if (selectedIds.size === filteredWords.length) {
//       setSelectedIds(new Set());
//     } else {
//       setSelectedIds(new Set(filteredWords.map((w) => w.id)));
//     }
//   };
//
//   const handleAddSelected = () => {
//     onSelect(Array.from(selectedIds));
//   };
//
//   const handleClearFilters = () => {
//     setSearchWord("");
//     setSearchTranslation("");
//   };
//
//   return (
//     <div className="space-y-6">
//       {/* Filtry wyszukiwania */}
//       <Card>
//         <CardHeader>
//           <CardTitle className="flex items-center gap-2 text-lg">
//             <Filter className="w-5 h-5" />
//             Filtry wyszukiwania
//           </CardTitle>
//         </CardHeader>
//         <CardContent className="space-y-4">
//           <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
//             <div>
//               <label className="text-sm font-medium mb-2 block">
//                 Szukaj po słówku
//               </label>
//               <div className="relative">
//                 <Search className="absolute left-3 top-1/2 -translate-y-1/2 w-4 h-4 text-muted-foreground" />
//                 <Input
//                   placeholder="np. accomplish"
//                   value={searchWord}
//                   onChange={(e) => setSearchWord(e.target.value)}
//                   className="pl-10"
//                 />
//               </div>
//             </div>
//
//             <div>
//               <label className="text-sm font-medium mb-2 block">
//                 Szukaj po tłumaczeniu
//               </label>
//               <div className="relative">
//                 <Search className="absolute left-3 top-1/2 -translate-y-1/2 w-4 h-4 text-muted-foreground" />
//                 <Input
//                   placeholder="np. osiągnąć"
//                   value={searchTranslation}
//                   onChange={(e) => setSearchTranslation(e.target.value)}
//                   className="pl-10"
//                 />
//               </div>
//             </div>
//           </div>
//
//           {(searchWord || searchTranslation) && (
//             <div className="flex items-center justify-between pt-2 border-t">
//               <p className="text-sm text-muted-foreground">
//                 Znaleziono:{" "}
//                 <span className="font-bold">{filteredWords.length}</span> słówek
//               </p>
//               <Button variant="ghost" size="sm" onClick={handleClearFilters}>
//                 Wyczyść filtry
//               </Button>
//             </div>
//           )}
//         </CardContent>
//       </Card>
//
//       {/* Akcje i statystyki */}
//       <div className="flex items-center justify-between">
//         <div className="flex items-center gap-4">
//           <p className="text-sm text-muted-foreground">
//             Zaznaczono:{" "}
//             <span className="font-bold text-foreground">
//               {selectedIds.size}
//             </span>{" "}
//             z {filteredWords.length}
//           </p>
//         </div>
//         <div className="flex gap-2">
//           <Button variant="outline" size="sm" onClick={handleSelectAll}>
//             {selectedIds.size === filteredWords.length &&
//             filteredWords.length > 0
//               ? "Odznacz wszystkie"
//               : "Zaznacz wszystkie"}
//           </Button>
//           <Button
//             onClick={handleAddSelected}
//             disabled={selectedIds.size === 0 || isAdding}
//             size="sm"
//           >
//             {isAdding ? (
//               <>
//                 <div className="w-4 h-4 mr-2 border-2 border-current border-t-transparent rounded-full animate-spin" />
//                 Dodawanie...
//               </>
//             ) : (
//               <>
//                 <CheckCircle2 className="w-4 h-4 mr-2" />
//                 Dodaj zaznaczone ({selectedIds.size})
//               </>
//             )}
//           </Button>
//         </div>
//       </div>
//
//       {/* Lista słówek */}
//       {filteredWords.length > 0 ? (
//         <div className="space-y-3">
//           {filteredWords.map((word) => (
//             <CommunityWordCard
//               key={word.id}
//               word={word}
//               isSelected={selectedIds.has(word.id)}
//               onToggle={handleToggle}
//             />
//           ))}
//         </div>
//       ) : (
//         <Alert>
//           <Search className="h-4 w-4" />
//           <AlertDescription>
//             Nie znaleziono słówek pasujących do podanych filtrów. Spróbuj
//             zmienić kryteria wyszukiwania.
//           </AlertDescription>
//         </Alert>
//       )}
//
//       {/* Informacja o społeczności */}
//       {filteredWords.length > 0 && (
//         <div className="text-center text-xs text-muted-foreground pt-4 border-t">
//           <Users className="w-4 h-4 inline-block mr-1" />
//           Słówka pochodzą z biblioteki społeczności Lingendo
//         </div>
//       )}
//     </div>
//   );
// };
