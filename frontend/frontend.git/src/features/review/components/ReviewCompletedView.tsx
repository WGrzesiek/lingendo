"use client";

import { Card } from "@/components/ui/card";
import { Button } from "@/components/ui/button";
import { Badge } from "@/components/ui/badge";
import { CheckCircle2, ArrowLeft, BookOpen, Calendar } from "lucide-react";
import { useRouter } from "next/navigation";

type ReviewCompletedViewProps = {
    title?: string;
    description?: string;
    courseHref?: string;
};

export function ReviewCompletedView({
                                         title = "Na dziś wszystko gotowe 🎉",
                                         description = "Nie masz już słówek do powtórki na dziś. Świetna robota — wróć jutro po kolejną porcję!",
                                         courseHref,
                                     }: ReviewCompletedViewProps) {
    const router = useRouter();

    return (
        <div className="min-h-screen bg-gradient-to-br from-background via-accent/5 to-background">
            <div className="container mx-auto px-4 py-10">
                <div className="max-w-xl mx-auto space-y-6">
                    <Card className="p-8 md:p-10 rounded-2xl">
                        <div className="flex flex-col items-center text-center gap-4">
                            {/* Icon */}
                            <div className="relative">
                                <div className="absolute inset-0 blur-2xl opacity-30 bg-primary rounded-full" />
                                <CheckCircle2 className="relative w-16 h-16 text-primary" />
                            </div>

                            <Badge variant="secondary" className="px-3 py-1 flex items-center gap-1">
                                <Calendar className="w-3 h-3" />
                                Powtórki na dziś zakończone
                            </Badge>

                            <h1 className="text-3xl md:text-4xl font-bold tracking-tight">
                                {title}
                            </h1>

                            <p className="text-muted-foreground text-base md:text-lg">
                                {description}
                            </p>

                            {/* Actions */}
                            <div className="w-full pt-2 grid grid-cols-1 sm:grid-cols-2 gap-3">
                                <Button
                                    size="lg"
                                    variant="outline"
                                    className="gap-2"
                                    onClick={() => router.back()}
                                >
                                    <ArrowLeft className="w-4 h-4" />
                                    Wróć
                                </Button>

                                {courseHref ? (
                                    <Button
                                        size="lg"
                                        className="gap-2"
                                        onClick={() => router.push(courseHref)}
                                    >
                                        <BookOpen className="w-4 h-4" />
                                        Przejdź do kursu
                                    </Button>
                                ) : (
                                    <Button
                                        size="lg"
                                        className="gap-2"
                                        onClick={() => router.push("/courses")}
                                    >
                                        <BookOpen className="w-4 h-4" />
                                        Przeglądaj kursy
                                    </Button>
                                )}
                            </div>
                        </div>
                    </Card>

                    <div className="text-center text-sm text-muted-foreground">
                        Nowe słówka do powtórki pojawią się zgodnie z harmonogramem nauki.
                    </div>
                </div>
            </div>
        </div>
    );
}
