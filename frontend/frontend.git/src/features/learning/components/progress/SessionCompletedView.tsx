"use client";

import { Card } from "@/components/ui/card";
import { Button } from "@/components/ui/button";
import { Badge } from "@/components/ui/badge";
import { CheckCircle2, ArrowLeft, BookOpen } from "lucide-react";
import { useRouter } from "next/navigation";

type SessionCompletedViewProps = {
    title?: string;
    description?: string;
    courseHref?: string;
};

export function SessionCompletedView({
                                         title = "Sesja zakończona 🎉",
                                         description = "Świetna robota! W tej sesji nie ma już dostępnych fiszek do nauki.",
                                         courseHref,
                                     }: SessionCompletedViewProps) {
    const router = useRouter();

    return (
        <div className="min-h-screen bg-gradient-to-br from-background via-accent/5 to-background">
            <div className="container mx-auto px-4 py-10">
                <div className="max-w-xl mx-auto space-y-6">
                    <Card className="p-8 md:p-10 rounded-2xl">
                        <div className="flex flex-col items-center text-center gap-4">
                            <div className="relative">
                                <div className="absolute inset-0 blur-2xl opacity-30 bg-primary rounded-full" />
                                <CheckCircle2 className="relative w-16 h-16" />
                            </div>

                            <Badge variant="secondary" className="px-3 py-1">
                                Koniec sesji
                            </Badge>

                            <h1 className="text-3xl md:text-4xl font-bold tracking-tight">
                                {title}
                            </h1>

                            <p className="text-muted-foreground text-base md:text-lg">
                                {description}
                            </p>

                            <div className="w-full pt-2 grid grid-cols-1 sm:grid-cols-2 gap-3">
                                <Button
                                    size="lg"
                                    variant="outline"
                                    className="gap-2"
                                    onClick={() => router.back()}
                                >
                                    <ArrowLeft className="w-4 h-4" />
                                    Powrót
                                </Button>

                                {courseHref ? (
                                    <Button
                                        size="lg"
                                        className="gap-2"
                                        onClick={() => router.push(courseHref)}
                                    >
                                        <BookOpen className="w-4 h-4" />
                                        Do kursu
                                    </Button>
                                ) : (
                                    <Button
                                        size="lg"
                                        className="gap-2"
                                        onClick={() => router.back()}
                                    >
                                        <BookOpen className="w-4 h-4" />
                                        Zamknij
                                    </Button>
                                )}
                            </div>
                        </div>
                    </Card>

                    <div className="text-center text-sm text-muted-foreground">
                        Możesz wrócić do kursu, zrobić powtórkę albo rozpocząć kolejną sesję.
                    </div>
                </div>
            </div>
        </div>
    );
}
