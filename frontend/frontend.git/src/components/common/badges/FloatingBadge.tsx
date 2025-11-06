import { Button } from "@/components/ui/button";
import { LucideIcon } from "lucide-react";

type FloatingBadgeProps = {
  icon: LucideIcon;
  text: string;
  highlightText?: string;
  position?: "top-right" | "right-center" | "top-left" | "bottom-right";
  rotation?: number;
  variant?: "default" | "outline" | "secondary";
  onClick?: () => void;
};

export function FloatingBadge({
  icon: Icon,
  text,
  highlightText,
  position = "top-right",
  rotation = 3,
  variant = "outline",
  onClick,
}: FloatingBadgeProps) {
  const positionClasses = {
    "top-right": "right-5 top-2 -translate-y-1/2",
    "right-center": "-right-4 top-2/5 -translate-y-1/2",
    "top-left": "left-5 top-2 -translate-y-1/2",
    "bottom-right": "right-5 bottom-2",
  };

  return (
    <div
      className={`absolute hidden md:block ${positionClasses[position]}`}
      style={{ transform: `rotate(${rotation}deg)` }}
    >
      <Button className="shadow-lg" variant={variant} onClick={onClick}>
        <Icon className="mr-2 h-4 w-4" /> {text}
        {highlightText && (
          <span className="ml-1 font-semibold">{highlightText}</span>
        )}
      </Button>
    </div>
  );
}
