import { Badge } from "@/components/ui/badge";
import { languageConfig, Language } from "@/types/common";
import { cn } from "@/lib/utils";

export const LanguageBadge = ({ language }: { language: Language }) => {
  const { label, icon: Icon, className } = languageConfig[language];

  return (
    <Badge variant="outline" className={cn("gap-1.5 pr-3", className)}>
      <Icon className="w-3.5 h-3.5" />
      <span>{label}</span>
    </Badge>
  );
};
