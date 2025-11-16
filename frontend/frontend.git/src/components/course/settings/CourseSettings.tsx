import { Card } from "@/components/ui/card";
import { Button } from "@/components/ui/button";
import { Badge } from "@/components/ui/badge";
import { Label } from "@/components/ui/label";
import {
  Brain,
  Layers,
  Shuffle,
  BookOpen,
  Calendar,
  Settings as SettingsIcon,
} from "lucide-react";

interface CourseSettingsProps {
  course: {
    algorithm: "spaced-repetition" | "leitner" | "random";
    wordsPerSession: number;
    isOwner: boolean;
  };
}

/**
 * Panel ustawień kursu
 * Pokazuje aktualny algorytm nauki i ustawienia sesji
 */
export const CourseSettings = ({ course }: CourseSettingsProps) => {
  const algorithms = [
    {
      id: "spaced-repetition",
      name: "Powtarzanie Interwałowe",
      description:
        "Optymalizuje czas powtórek na podstawie krzywej zapominania",
      icon: Brain,
      color: "text-purple-600",
      bgColor: "bg-purple-500/10",
    },
    {
      id: "leitner",
      name: "System Leitnera",
      description:
        "Fiszki przenoszą się między pudełkami w zależności od odpowiedzi",
      icon: Layers,
      color: "text-blue-600",
      bgColor: "bg-blue-500/10",
    },
    {
      id: "random",
      name: "Losowy",
      description: "Losowa kolejność słówek bez zaplanowanych powtórek",
      icon: Shuffle,
      color: "text-green-600",
      bgColor: "bg-green-500/10",
    },
  ];

  return (
    <Card className="p-6 space-y-6">
      <div className="flex items-center justify-between">
        <h2 className="text-2xl font-bold">Ustawienia</h2>
        {course.isOwner && (
          <Button variant="ghost" size="sm">
            <SettingsIcon className="w-4 h-4" />
          </Button>
        )}
      </div>

      <div>
        <Label className="text-sm font-semibold mb-3 block">
          Algorytm nauki
        </Label>
        <div className="space-y-2">
          {algorithms.map((algorithm) => {
            const Icon = algorithm.icon;
            const isActive = algorithm.id === course.algorithm;
            return (
              <div
                key={algorithm.id}
                className={`p-4 border rounded-lg transition-all cursor-pointer ${
                  isActive
                    ? "border-primary bg-primary/5"
                    : "hover:bg-accent/50"
                }`}
              >
                <div className="flex items-start gap-3">
                  <div className={`p-2 ${algorithm.bgColor} rounded-lg`}>
                    <Icon className={`w-5 h-5 ${algorithm.color}`} />
                  </div>
                  <div className="flex-1">
                    <div className="flex items-center gap-2 mb-1">
                      <h3 className="font-semibold">{algorithm.name}</h3>
                      {isActive && (
                        <Badge variant="default" className="text-xs">
                          Aktywny
                        </Badge>
                      )}
                    </div>
                    <p className="text-sm text-muted-foreground">
                      {algorithm.description}
                    </p>
                  </div>
                </div>
              </div>
            );
          })}
        </div>
      </div>

      <div className="pt-4 border-t space-y-4">
        <div className="flex items-center justify-between">
          <div className="flex items-center gap-2">
            <BookOpen className="w-4 h-4 text-muted-foreground" />
            <Label className="text-sm">Słówek na sesję</Label>
          </div>
          <div className="flex items-center gap-2">
            <Badge variant="secondary">{course.wordsPerSession}</Badge>
            {course.isOwner && (
              <Button variant="ghost" size="sm">
                Zmień
              </Button>
            )}
          </div>
        </div>

        <div className="flex items-center justify-between">
          <div className="flex items-center gap-2">
            <Calendar className="w-4 h-4 text-muted-foreground" />
            <Label className="text-sm">Harmonogram powtórek</Label>
          </div>
          <Badge variant="outline">Automatyczny</Badge>
        </div>
      </div>

      {course.isOwner && (
        <Button variant="outline" className="w-full">
          Zaawansowane ustawienia
        </Button>
      )}
    </Card>
  );
};
