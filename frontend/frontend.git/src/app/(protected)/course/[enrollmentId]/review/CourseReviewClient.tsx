"use client";

import { Card } from "@/components/ui/card";
import { Button } from "@/components/ui/button";
import {
  ArrowLeft,
  Clock,
  AlertCircle,
  Target, CheckCircle2,
} from "lucide-react";
import { useRouter } from "next/navigation";
import {useReviewHeader} from "@/features/review/hooks/useReviewHeader";

import React from "react";
import {ReviewWordList} from "@/features/review/components/ReviewWordList";

export default function CourseReviewClient({ enrollmentId }: { enrollmentId: string }) {

  const router = useRouter();
  const {data: reviewHeader, isLoading: isLoadingReviewHeader, isError: isErrorLoadingReviewHeader} = useReviewHeader(enrollmentId)
  const handleStartReview = () => {
    router.push(`/review/${enrollmentId}`);
  };

  if (isErrorLoadingReviewHeader) {
    return <div>Wystąpił błąd podczas ładowania danych powtórek.</div>;
  }
  if(isLoadingReviewHeader) {
    return <div>Ładowanie danych powtórek...</div>;
  }
  if (!reviewHeader) {
  <div>asdadasd</div>
  }
  const countToReview = reviewHeader.counters.wordsForToday + reviewHeader.counters.overdueWords;
  return (
    <div className="min-h-screen bg-background">
      <div className="container mx-auto p-6 lg:p-8 space-y-6">
        {/* Header */}
        <div className="flex items-center justify-between">
          <Button
            variant="ghost"
            size="lg"
            className="gap-2"
            onClick={() => router.back()}
          >
            <ArrowLeft className="w-5 h-5" />
            Powrót do kursu
          </Button>
        </div>

        {/* Title */}
        <div>
          <h1 className="text-4xl font-bold mb-2">Słówka do powtórki</h1>
          <p className="text-lg text-muted-foreground">
            Odśwież swoją wiedzę i utrwal poznane słówka
          </p>
        </div>

        {/* Stats Cards */}
        <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-4">
          <Card className="p-6">
            <div className="flex items-center gap-4">
              <div className="p-3 bg-primary/10 rounded-lg">
                <Target className="w-6 h-6 text-primary" />
              </div>
              <div>
                <p className="text-sm text-muted-foreground">Do powtórki</p>
                <p className="text-3xl font-bold">{reviewHeader.counters.totalWordsToReview}</p>
              </div>
            </div>
          </Card>

          <Card className="p-6">
            <div className="flex items-center gap-4">
              <div className="p-3 bg-blue-500/10 rounded-lg">
                <Clock className="w-6 h-6 text-blue-600" />
              </div>
              <div>
                <p className="text-sm text-muted-foreground">Na dziś</p>
                <p className="text-3xl font-bold">{reviewHeader.counters.wordsForToday}</p>
              </div>
            </div>
          </Card>

          <Card className="p-6">
            <div className="flex items-center gap-4">
              <div className="p-3 bg-orange-500/10 rounded-lg">
                <AlertCircle className="w-6 h-6 text-orange-600" />
              </div>
              <div>
                <p className="text-sm text-muted-foreground">Zaległe</p>
                <p className="text-3xl font-bold">{reviewHeader.counters.overdueWords}</p>
              </div>
            </div>
          </Card>

          {/*<Card className="p-6">*/}
          {/*  <div className="flex items-center gap-4">*/}
          {/*    <div className="p-3 bg-green-500/10 rounded-lg">*/}
          {/*      <TrendingUp className="w-6 h-6 text-green-600" />*/}
          {/*    </div>*/}
          {/*  </div>*/}
          {/*</Card>*/}
        </div>

        {/* Start Button */}
        <Card className="p-6 bg-gradient-to-r from-primary/10 to-primary/5 border-primary/20">
          {countToReview === 0 ? (
              <div className="flex flex-col md:flex-row items-center justify-between gap-4 p-6 rounded-xl border border-green-500/30 bg-green-500/5">
                <div className="flex items-center gap-4">
                  <div className="p-3 rounded-full bg-green-500/10">
                    <CheckCircle2 className="w-6 h-6 text-green-600" />
                  </div>

                  <div>
                    <h3 className="text-xl font-bold mb-1">
                      Wszystko powtórzone 🎉
                    </h3>
                    <p className="text-muted-foreground">
                      Na dziś nie masz już słówek do powtórki. Świetna robota!
                    </p>
                  </div>
                </div>
              </div>
          ) : (
              <div className="flex flex-col md:flex-row items-center justify-between gap-4">
                <div>
                  <h3 className="text-xl font-bold mb-1">Gotowy do powtórki?</h3>
                  <p className="text-muted-foreground">
                    Powtórzysz {countToReview} słówek w trybie pisania
                  </p>
                </div>

                <Button size="lg" className="gap-2" onClick={handleStartReview}>
                  <Target className="w-5 h-5" />
                  Rozpocznij powtórkę
                </Button>
              </div>
          )}

        </Card>
        <div className="lg:col-span-2">
          <ReviewWordList enrollmentId={enrollmentId} />
        </div>
        {/* Words List */}
        {/*<Card className="p-6">*/}
        {/*  <div className="mb-6 flex items-center justify-between">*/}
        {/*    <div>*/}
        {/*      <h2 className="text-2xl font-bold mb-1">Lista słówek</h2>*/}
        {/*      <p className="text-muted-foreground">*/}
        {/*        Wszystkie słówka czekające na powtórkę*/}
        {/*      </p>*/}
        {/*    </div>*/}
        {/*    <Badge variant="secondary" className="text-lg px-4 py-2">*/}
        {/*      {words.length} słówek*/}
        {/*    </Badge>*/}
        {/*  </div>*/}

        {/*  <div className="space-y-4">*/}
        {/*    {words.map((word) => (*/}
        {/*      <ReviewWordCard key={word.id} word={word} />*/}
        {/*    ))}*/}
        {/*  </div>*/}
        {/*</Card>*/}
      </div>
    </div>
  );
};

