import { Card } from "@/components/ui/card";
import { Button } from "@/components/ui/button";
import { Badge } from "@/components/ui/badge";
import { Label } from "@/components/ui/label";
import {
  Calendar,
  Settings as SettingsIcon,
  PlusCircle,
  Frown,
} from "lucide-react";
import {
  algorithms,
  REVIEW_SCHEDULE_LABELS,
  reviewSchedules,
} from "@/types/learning";
import { useEffect, useState } from "react";

import {
  Dialog,
  DialogTrigger,
  DialogContent,
  DialogHeader,
  DialogTitle,
  DialogDescription,
  DialogFooter,
} from "@/components/ui/dialog";
import { Input } from "@/components/ui/input";
import {
  Select,
  SelectTrigger,
  SelectContent,
  SelectItem,
  SelectValue,
} from "@/components/ui/select";
import { useCourseSettings } from "@/features/course/hooks/useCourseSettings";
import { Skeleton } from "@/components/ui/skeleton";
import {
  useUpdateReviewSchedule,
  useUpdateLearnAlgorithm,
  useUpdateFlashcardsPerSession,
} from "@/features/deckEnrollment/hooks/hooks";
import {router} from "next/client";
import {toast} from "sonner";

interface CourseSettingsProps {
  enrollmentId: string;
}
const WordListSkeleton = () => (
  <div className="space-y-4">
    {[1, 2, 3].map((i) => (
      <div key={i} className="p-4 border rounded-xl space-y-3">
        <div className="flex justify-between">
          <div className="space-y-2 w-2/3">
            <Skeleton className="h-6 w-1/3" />
            <Skeleton className="h-4 w-full" />
          </div>
          <Skeleton className="h-9 w-24 rounded-md" />
        </div>
        <Skeleton className="h-2 w-full rounded-full" />
      </div>
    ))}
  </div>
);

const EmptyState = () => (
  <div className="flex flex-col items-center justify-center py-12 text-center border rounded-xl border-dashed bg-muted/20">
    <div className="bg-muted p-3 rounded-full mb-3">
      <PlusCircle className="w-6 h-6 text-muted-foreground" />
    </div>
    <h3 className="font-semibold text-lg">Brak słówek</h3>
    <p className="text-sm text-muted-foreground max-w-xs mb-4">
      Wygląda na to, że ten kurs nie zawiera jeszcze żadnych słówek.
    </p>
    <Button variant="outline">Przeglądaj kursy</Button>
  </div>
);
/**
 * Panel ustawień kursu
 * Pokazuje aktualny algorytm nauki i ustawienia sesji
 */
export const CourseSettings = ({ enrollmentId }: CourseSettingsProps) => {
  const { data, isLoading, isError } = useCourseSettings(enrollmentId);

  const [id, setId] = useState<string>("");
  const [wordsPerSession, setWordsPerSession] = useState<number>(0);
  const [value, setValue] = useState<number>(0);
  const [open, setOpen] = useState(false);
  const { mutate: mutateLimit, isPending: isLimitPending } =
    useUpdateFlashcardsPerSession();
  const handleSaveUFPS = () => {
    mutateLimit(
      {
        enrollmentId: id,
        data: { limit: value },
      },
      {
        onSuccess: () => {
          setWordsPerSession(value);
          setOpen(false);
        },
      }
    );
  };

  const [currentAlgorithm, setCurrentAlgorithm] = useState<string>("");
  const [algorithmDialogOpen, setAlgorithmDialogOpen] = useState(false);
  const [selectedAlgorithm, setSelectedAlgorithm] = useState<
    (typeof algorithms)[number] | null
  >(null);

  const { mutate: mutateAlgorithm, isPending: isAlgorithmPending } =
    useUpdateLearnAlgorithm();

  const handleAlgorithmClick = (algorithm: (typeof algorithms)[number]) => {
    if (algorithm.inDevelopment) return;
    if (algorithm.id === currentAlgorithm) return;

    setSelectedAlgorithm(algorithm);
    setAlgorithmDialogOpen(true);
  };

  const handleConfirmAlgorithmChange = () => {
    if (!selectedAlgorithm) return;

    mutateAlgorithm(
      {
        enrollmentId: id,
        data: { learnAlgorithm: selectedAlgorithm.id },
      },
      {
        onSuccess: () => {
          setCurrentAlgorithm(selectedAlgorithm.id);
          setAlgorithmDialogOpen(false);
          toast.success("Algorytm nauki został zmieniony, odśwież stronę.", {duration: 4000});
          router.reload()
        },
      }
    );
  };

  const [openReviewScheduleDialog, setOpenReviewScheduleDialog] =
    useState(false);
  const [reviewSchedule, setReviewSchedule] = useState<reviewSchedules>("AUTO");
  const [selectedReviewSchedule, setSelectedReviewSchedule] =
    useState<reviewSchedules>("AUTO");
  const { mutate: mutateReviewSchedule, isPending: isReviewSchedulePending } =
    useUpdateReviewSchedule();

  const handleConfirmReviewScheduleChange = () => {
    mutateReviewSchedule(
      {
        enrollmentId: id,
        data: { reviewSchedule: selectedReviewSchedule },
      },
      {
        onSuccess: () => {
          setReviewSchedule(selectedReviewSchedule);
          setOpenReviewScheduleDialog(false);
        },
      }
    );
  };
  const handleReviewScheduleButtonClick = () => {
    setSelectedReviewSchedule(reviewSchedule);
    setOpenReviewScheduleDialog(true);
  };

  useEffect(() => {
    if (data) {
      setId(data.enrollmentId);
      setCurrentAlgorithm(data.algorithm);
      setWordsPerSession(data.wordsPerSession);
      setValue(data.wordsPerSession);
      setReviewSchedule(data.reviewSchedule);
      setSelectedReviewSchedule(data.reviewSchedule);
    }
  }, [data]);

  if (isLoading) return <WordListSkeleton />;

  if (!data) return <EmptyState />;

  if (isError) {
    return (
      <div className="p-6 border border-destructive/50 rounded-lg bg-destructive/10 text-destructive flex items-center gap-3">
        <Frown className="h-5 w-5" />
        <span>
          Nie udało się załadować listy kursów. Spróbuj odświeżyć stronę.
        </span>
      </div>
    );
  }

  return (
    <Card className="p-6 space-y-6">
      <div className="flex items-center justify-between">
        <h2 className="text-2xl font-bold">Ustawienia</h2>

        <SettingsIcon className="w-4 h-4" />
      </div>

      <div>
        <Label className="text-sm font-semibold mb-3 block">
          Algorytm nauki
        </Label>

        <div className="space-y-2">
          {algorithms.map((algorithm) => {
            const Icon = algorithm.icon;
            const isActive = algorithm.id === currentAlgorithm;

            return (
              <div
                key={algorithm.id}
                onClick={() => handleAlgorithmClick(algorithm)}
                className={`p-4 border rounded-lg transition-all relative
                ${
                  isActive
                    ? "border-primary bg-primary/5"
                    : "hover:bg-accent/50"
                }
                ${
                  algorithm.inDevelopment
                    ? "opacity-60 cursor-not-allowed"
                    : "cursor-pointer"
                }
              `}
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

                      {algorithm.inDevelopment && (
                        <Badge variant="secondary" className="text-xs">
                          W rozwoju
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

        {/* Dialog potwierdzenia zmiany algorytmu */}
        <Dialog
          open={algorithmDialogOpen}
          onOpenChange={setAlgorithmDialogOpen}
        >
          <DialogContent>
            <DialogHeader>
              <DialogTitle>Zmiana algorytmu nauki</DialogTitle>
              <DialogDescription>
                {selectedAlgorithm && (
                  <>
                    Zmieniasz algorytm nauki na{" "}
                    <span className="font-semibold">
                      {selectedAlgorithm.name}
                    </span>
                    .
                  </>
                )}
                <br />
                <span className="mt-2 block">
                  Zmiana algorytmu może spowodować ustawienie fiszek na pierwszy
                  krok w nowym algorytmie oraz zmianę sposobu planowania
                  powtórek w tym kursie.
                </span>
                <span className="mt-1 block font-medium">
                  Czy na pewno chcesz kontynuować?
                </span>
              </DialogDescription>
            </DialogHeader>

            <DialogFooter className="gap-2">
              <Button
                variant="ghost"
                onClick={() => setAlgorithmDialogOpen(false)}
                disabled={isAlgorithmPending}
              >
                Anuluj
              </Button>
              <Button
                onClick={handleConfirmAlgorithmChange}
                disabled={isAlgorithmPending}
              >
                {isAlgorithmPending ? "Zmienianie..." : "Zmień algorytm"}
              </Button>
            </DialogFooter>
          </DialogContent>
        </Dialog>
      </div>

      <div className="pt-4 border-t space-y-4">
        {/* Słówka na sesję */}
        <Dialog open={open} onOpenChange={setOpen}>
          <div className="flex items-center justify-between">
            <div className="flex items-center gap-2">
              <Calendar className="w-4 h-4 text-muted-foreground" />
              <Label className="text-sm">Słówek na sesję</Label>
            </div>
            <DialogTrigger asChild>
              <Badge
                variant="secondary"
                onClick={() => setValue(wordsPerSession)}
                className="cursor-pointer"
              >
                {wordsPerSession}
              </Badge>
            </DialogTrigger>

            <DialogContent>
              <DialogHeader>
                <DialogTitle>Zmień liczbę fiszek</DialogTitle>
              </DialogHeader>

              <Input
                type="number"
                min={1}
                max={100}
                value={value}
                onChange={(e) => setValue(Number(e.target.value))}
              />

              <DialogFooter>
                <Button
                  variant="ghost"
                  onClick={() => setOpen(false)}
                  disabled={isLimitPending}
                >
                  Anuluj
                </Button>

                <Button onClick={handleSaveUFPS} disabled={isLimitPending}>
                  {isLimitPending ? "Zapisywanie..." : "Zapisz"}
                </Button>
              </DialogFooter>
            </DialogContent>
          </div>
        </Dialog>

        {/* Harmonogram powtórek */}
        <Dialog
          open={openReviewScheduleDialog}
          onOpenChange={setOpenReviewScheduleDialog}
        >
          <div className="flex items-center justify-between">
            <div className="flex items-center gap-2">
              <Calendar className="w-4 h-4 text-muted-foreground" />
              <Label className="text-sm">Harmonogram powtórek</Label>
            </div>

            <DialogTrigger asChild>
              <button
                type="button"
                onClick={handleReviewScheduleButtonClick}
                className="inline-flex items-center rounded-full border px-2.5 py-1 text-xs font-medium bg-background hover:bg-muted transition"
              >
                {REVIEW_SCHEDULE_LABELS[reviewSchedule]}
              </button>
            </DialogTrigger>
          </div>

          <DialogContent>
            <DialogHeader>
              <DialogTitle>Harmonogram powtórek</DialogTitle>
              <DialogDescription>
                Wybierz, jak intensywne mają być powtórki dla tego kursu.
              </DialogDescription>
            </DialogHeader>

            <div className="space-y-2">
              <Label>Tryb powtórek</Label>
              <Select
                value={selectedReviewSchedule}
                onValueChange={(value) =>
                  setSelectedReviewSchedule(value as reviewSchedules)
                }
              >
                <SelectTrigger>
                  <SelectValue placeholder="Wybierz tryb" />
                </SelectTrigger>
                <SelectContent>
                  <SelectItem value="AUTO">
                    Automatyczny (zalecany) — 7, 14, 21 dni
                  </SelectItem>
                  <SelectItem value="LIGHT">Lekki — 3, 7 dni</SelectItem>
                  <SelectItem value="NORMAL">
                    Normalny — 7, 14, 30 dni
                  </SelectItem>
                  <SelectItem value="INTENSE">
                    Intensywny — 1, 3, 7 dni
                  </SelectItem>
                </SelectContent>
              </Select>
            </div>

            <DialogFooter>
              <Button
                type="button"
                variant="ghost"
                onClick={() => setOpenReviewScheduleDialog(false)}
                disabled={isReviewSchedulePending}
              >
                Anuluj
              </Button>
              <Button
                type="button"
                onClick={handleConfirmReviewScheduleChange}
                disabled={isReviewSchedulePending}
              >
                {isReviewSchedulePending ? "Zapisywanie..." : "Zapisz"}
              </Button>
            </DialogFooter>
          </DialogContent>
        </Dialog>
      </div>
    </Card>
  );
};
