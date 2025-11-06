import { ArrowRight } from "lucide-react";
import { Button } from "@/components/ui/button";

export default function HeroButtons({
  leftButtonText,
  rightButtonText,
}: {
  leftButtonText: string;
  rightButtonText: string;
}) {
  return (
    <div className="flex flex-col gap-4 sm:flex-row sm:items-center">
      <Button size="lg" className="h-12 px-6 text-base">
        {leftButtonText}
        <ArrowRight className="ml-2 h-5 w-5" />
      </Button>
      <Button size="lg" variant="secondary" className="h-12 px-6 text-base">
        {rightButtonText}
      </Button>
    </div>
  );
}
