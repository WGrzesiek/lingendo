// "use client";
//
// import { useState } from "react";
// import { Button } from "@/components/ui/button";
// import { Badge } from "@/components/ui/badge";
// import { Card } from "@/components/ui/card";
// import { Progress } from "@/components/ui/progress";
// import { ArrowLeft, CheckCircle, XCircle, Trophy } from "lucide-react";
// import { useRouter } from "next/navigation";
// import { ReviewWriteStep } from "@/features/review/components/ReviewWriteStep";
// import type {
//   ReviewWord,
//   ReviewResult,
// } from "@/features/review/types/review.types";
//
// /**
//  * Mock danych sesji powtórek
//  */
// const mockReviewWords: ReviewWord[] = [
//   {
//     id: "word-1",
//     content: {
//       id: "w1",
//       word: "apple",
//       translations: ["jabłko"],
//       sentences: [
//         {
//           id: "s1",
//           sentence: "I eat an apple every day.",
//           translation: "Jem jabłko każdego dnia.",
//         },
//       ],
//       sentencesAI: [
//         {
//           id: "s2",
//           sentence: "The apple is red and delicious.",
//           translation: "Jabłko jest czerwone i pyszne.",
//         },
//       ],
//     },
//     lastReviewAt: "2025-11-08T10:00:00Z",
//     nextReviewAt: "2025-11-06T10:00:00Z",
//     repetitionCount: 5,
//     difficultyLevel: 2,
//   },
//   {
//     id: "word-2",
//     content: {
//       id: "w2",
//       word: "beautiful",
//       translations: ["piękny", "ładny"],
//       sentences: [
//         {
//           id: "s3",
//           sentence: "The weather is beautiful today.",
//           translation: "Pogoda jest dziś piękna.",
//         },
//       ],
//       sentencesAI: [],
//     },
//     lastReviewAt: "2025-11-07T14:00:00Z",
//     nextReviewAt: "2025-11-09T14:00:00Z",
//     repetitionCount: 3,
//     difficultyLevel: 4,
//   },
//   {
//     id: "word-3",
//     content: {
//       id: "w3",
//       word: "house",
//       translations: ["dom", "budynek mieszkalny"],
//       sentences: [
//         {
//           id: "s4",
//           sentence: "This is my house.",
//           translation: "To jest mój dom.",
//         },
//       ],
//       sentencesAI: [
//         {
//           id: "s5",
//           sentence: "I love my house.",
//           translation: "Kocham mój dom.",
//         },
//       ],
//     },
//     lastReviewAt: "2025-11-05T09:00:00Z",
//     nextReviewAt: "2025-11-09T09:00:00Z",
//     repetitionCount: 7,
//     difficultyLevel: 3,
//   },
// ];
//
// /**
//  * Strona sesji powtórki
//  * User powtarza słówka w trybie pisania
//  */
// const ReviewSessionPage = () => {
//   const router = useRouter();
//   const [currentWordIndex, setCurrentWordIndex] = useState(0);
//   const [results, setResults] = useState<ReviewResult[]>([]);
//   const [isCompleted, setIsCompleted] = useState(false);
//
//   const words = mockReviewWords;
//   const currentWord = words[currentWordIndex];
//   const totalWords = words.length;
//   const progress = Math.round((currentWordIndex / totalWords) * 100);
//
//   const handleAnswer = (isCorrect: boolean, responseTimeMs: number) => {
//     const result: ReviewResult = {
//       wordId: currentWord.id,
//       isCorrect,
//       responseTimeMs,
//       answeredAt: new Date().toISOString(),
//     };
//
//     setResults([...results, result]);
//
//     if (currentWordIndex < words.length - 1) {
//       setCurrentWordIndex(currentWordIndex + 1);
//     } else {
//       setIsCompleted(true);
//     }
//   };
//
//   const handleExit = () => {
//     router.back();
//   };
//
//   // Summary po zakończeniu
//   if (isCompleted) {
//     const correctCount = results.filter((r) => r.isCorrect).length;
//     const accuracy = Math.round((correctCount / results.length) * 100);
//     const avgTime = Math.round(
//       results.reduce((acc, r) => acc + r.responseTimeMs, 0) /
//         results.length /
//         1000
//     );
//
//     return (
//       <div className="min-h-screen bg-gradient-to-br from-background via-accent/5 to-background">
//         <div className="container mx-auto p-6 lg:p-8">
//           <div className="max-w-3xl mx-auto space-y-6">
//             <Card className="p-8 md:p-12 text-center space-y-6">
//               <div className="w-20 h-20 bg-primary/10 rounded-full flex items-center justify-center mx-auto">
//                 <Trophy className="w-10 h-10 text-primary" />
//               </div>
//
//               <div>
//                 <h1 className="text-4xl font-bold mb-2">Świetna robota!</h1>
//                 <p className="text-lg text-muted-foreground">
//                   Ukończyłeś sesję powtórek
//                 </p>
//               </div>
//
//               {/* Stats */}
//               <div className="grid grid-cols-1 md:grid-cols-3 gap-4 pt-6">
//                 <div className="p-6 bg-accent/30 rounded-lg">
//                   <p className="text-sm text-muted-foreground mb-1">
//                     Dokładność
//                   </p>
//                   <p className="text-3xl font-bold">{accuracy}%</p>
//                   <p className="text-xs text-muted-foreground mt-1">
//                     {correctCount}/{results.length} poprawnych
//                   </p>
//                 </div>
//
//                 <div className="p-6 bg-accent/30 rounded-lg">
//                   <p className="text-sm text-muted-foreground mb-1">
//                     Średni czas
//                   </p>
//                   <p className="text-3xl font-bold">{avgTime}s</p>
//                   <p className="text-xs text-muted-foreground mt-1">
//                     na słówko
//                   </p>
//                 </div>
//
//                 <div className="p-6 bg-accent/30 rounded-lg">
//                   <p className="text-sm text-muted-foreground mb-1">
//                     Powtórzono
//                   </p>
//                   <p className="text-3xl font-bold">{results.length}</p>
//                   <p className="text-xs text-muted-foreground mt-1">słówek</p>
//                 </div>
//               </div>
//
//               {/* Detailed Results */}
//               <div className="pt-6 border-t">
//                 <h3 className="text-lg font-semibold mb-4">Szczegóły</h3>
//                 <div className="space-y-2">
//                   {results.map((result) => {
//                     const word = words.find((w) => w.id === result.wordId);
//                     return (
//                       <div
//                         key={result.wordId}
//                         className="flex items-center justify-between p-3 bg-accent/20 rounded-lg"
//                       >
//                         <div className="flex items-center gap-3">
//                           {result.isCorrect ? (
//                             <CheckCircle className="w-5 h-5 text-green-600" />
//                           ) : (
//                             <XCircle className="w-5 h-5 text-red-600" />
//                           )}
//                           <span className="font-medium">
//                             {word?.content.word}
//                           </span>
//                           <span className="text-muted-foreground text-sm">
//                             {word?.content.translations[0]}
//                           </span>
//                         </div>
//                         <Badge variant="outline">
//                           {(result.responseTimeMs / 1000).toFixed(1)}s
//                         </Badge>
//                       </div>
//                     );
//                   })}
//                 </div>
//               </div>
//
//               {/* Actions */}
//               <div className="flex flex-col sm:flex-row gap-3 pt-6">
//                 <Button
//                   variant="outline"
//                   size="lg"
//                   className="flex-1"
//                   onClick={handleExit}
//                 >
//                   Powrót do kursu
//                 </Button>
//                 <Button
//                   size="lg"
//                   className="flex-1"
//                   onClick={() => {
//                     setCurrentWordIndex(0);
//                     setResults([]);
//                     setIsCompleted(false);
//                   }}
//                 >
//                   Powtórz jeszcze raz
//                 </Button>
//               </div>
//             </Card>
//           </div>
//         </div>
//       </div>
//     );
//   }
//
//   return (
//     <div className="min-h-screen bg-gradient-to-br from-background via-accent/5 to-background">
//       <div className="container mx-auto p-4 lg:p-8">
//         <div className="max-w-5xl mx-auto space-y-6">
//           {/* Header */}
//           <div className="flex items-center justify-between">
//             <Button
//               variant="ghost"
//               size="lg"
//               className="gap-2"
//               onClick={handleExit}
//             >
//               <ArrowLeft className="w-5 h-5" />
//               Zakończ powtórkę
//             </Button>
//             <Badge variant="secondary" className="text-base px-4 py-2">
//               Tryb: Pisanie
//             </Badge>
//           </div>
//
//           {/* Progress */}
//           <Card className="p-6">
//             <div className="space-y-3">
//               <div className="flex justify-between items-center">
//                 <span className="text-sm font-medium">
//                   Słówko {currentWordIndex + 1} z {totalWords}
//                 </span>
//                 <span className="text-sm text-muted-foreground">
//                   {progress}% ukończone
//                 </span>
//               </div>
//               <Progress value={progress} className="h-2" />
//               <div className="flex gap-2 text-xs text-muted-foreground">
//                 <span className="flex items-center gap-1">
//                   <CheckCircle className="w-3 h-3 text-green-600" />
//                   {results.filter((r) => r.isCorrect).length} poprawnych
//                 </span>
//                 <span className="flex items-center gap-1">
//                   <XCircle className="w-3 h-3 text-red-600" />
//                   {results.filter((r) => !r.isCorrect).length} błędnych
//                 </span>
//               </div>
//             </div>
//           </Card>
//
//           {/* Current Word */}
//           <ReviewWriteStep word={currentWord} onAnswer={handleAnswer} />
//         </div>
//       </div>
//     </div>
//   );
// };
//
// export default ReviewSessionPage;
"use client";
import {useRouter} from "next/navigation";

import type {FlashcardInteractionResult, NextFlashcardRecommendation, TypingAnswer} from "@/features/learning/types/learning.types";
import {isNoMoreFlashcardsError} from "@/lib/api/error";

import {SessionCompletedView} from "@/features/learning/components/progress/SessionCompletedView";
import {Button} from "@/components/ui/button";
import {ArrowLeft} from "lucide-react";
import {StepRenderer} from "@/features/review/components/StepRendererReview";
import {useNextFlashcardRecommendationReview} from "@/features/review/hooks/useNextFlashcardRecommendationReview";
import {useSubmitAnswerMutationReview} from "@/features/review/hooks/useSubmitAnswerReview";
import {ReviewCompletedView} from "@/features/review/components/ReviewCompletedView";

const ReviewSessionPage = ({ params }: { params: { enrollmentId: string} }) => {
  const router = useRouter();
  const enrollmentId = params.enrollmentId;
  const {data, isLoading, isError, error, refetch,} = useNextFlashcardRecommendationReview(enrollmentId);
  const submit = useSubmitAnswerMutationReview();


  const currentFlashcard: NextFlashcardRecommendation | null =
      (data as never) ?? null;
  const noMore = isError && isNoMoreFlashcardsError(error);

  const handleStepComplete = async (answer: TypingAnswer) => {
    if (!currentFlashcard) return;

    try {
      const _result: FlashcardInteractionResult = await submit.mutateAsync({
        flashcardId: currentFlashcard.flashcardId,
        enrollmentId: enrollmentId,
        answer,
      });
    } catch (e) {
      console.error("Submit answer failed:", e);
    }
  };

  if (isLoading) {
    return (
        <div className="min-h-screen flex items-center justify-center text-muted-foreground">
          Ładowanie fiszki...
        </div>
    );
  }

  if (noMore) {
    return <ReviewCompletedView />;
  }

  if (isError) {
    return (
        <div className="min-h-screen flex items-center justify-center">
          <div className="space-y-3 text-center">
            <div className="text-red-600 font-semibold">
              Nie udało się pobrać fiszki
            </div>
            <Button onClick={() => refetch()}>Spróbuj ponownie</Button>
          </div>
        </div>
    );
  }

  if (!currentFlashcard) {
    return (
        <div className="min-h-screen flex items-center justify-center text-muted-foreground">
          Brak danych fiszki.
        </div>
    );
  }

  return (
      <div className="min-h-screen bg-gradient-to-br from-background via-accent/5 to-background">
        <div className="container mx-auto p-4 lg:p-8">
          <div className="max-w-5xl mx-auto space-y-6">
            {/* Header */}
            <div className="flex flex-col gap-4 sm:flex-row sm:items-center sm:justify-between">
              <Button
                  variant="ghost"
                  size="lg"
                  className="gap-2 w-fit hover:bg-accent"
                  onClick={() => router.back()}
                  disabled={submit.isPending}
              >
                <ArrowLeft className="w-5 h-5" />
                Zakończ powtórkę
              </Button>
            </div>



            <StepRenderer
                interactionType={currentFlashcard.interactionType}
                flashcardId={currentFlashcard.flashcardId}
                wordContent={currentFlashcard.content}
                onStepComplete={handleStepComplete}
            />
          </div>
        </div>
      </div>
  );
};

export default ReviewSessionPage;
