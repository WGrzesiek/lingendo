"use client";

import {
  Dialog,
  DialogContent,
  DialogHeader,
} from "@/components/ui/dialog";
import { CourseStudyStats } from "./CourseStudyStats";
import {useFlashcardAnswersStats} from "@/features/course/hooks/useFlashcardAnswersStats";

interface CourseStatsModalProps {
  enrollmentId: string;
  open: boolean;
  onOpenChange: (open: boolean) => void;
    completedSessions: number;
}

export const CourseStatsModal = ({
  enrollmentId,
  open,
  onOpenChange,
                                   completedSessions
}: CourseStatsModalProps) => {
  const {data: statistics1, isLoading, isError} = useFlashcardAnswersStats(enrollmentId)

  if(isLoading){
    return <div>Loading...</div>
  }

  if(isError || !statistics1){
    return <div>Error loading statistics</div>
  }
  return (
    <Dialog open={open} onOpenChange={onOpenChange}>
      <DialogContent className="max-w-4xl max-h-[90vh] overflow-y-auto">
        <DialogHeader className="items-center">
          <div>
            <h2 className="text-2xl font-bold mb-2">Statystyki nauki</h2>
          </div>
        </DialogHeader>
        <CourseStudyStats statistics={statistics1} />
      </DialogContent>
    </Dialog>
  );
};
